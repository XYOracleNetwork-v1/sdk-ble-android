package network.xyo.ble.generic.gatt.peripheral.actions

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import kotlinx.coroutines.*
import network.xyo.base.XYBase
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothGattCallback
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResultErrorCode
import network.xyo.ble.generic.gatt.peripheral.XYThreadSafeBluetoothGatt

class XYBluetoothGattWriteCharacteristic(
    val gatt: XYThreadSafeBluetoothGatt,
    val gattCallback: XYBluetoothGattCallback,
    private val writeType: Int = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
): XYBase() {

    private var _timeout = 15000L

    fun timeout(timeout: Long) {
        _timeout = timeout
    }

    fun completeStartCoroutine(cont: CancellableContinuation<ByteArray?>, value: ByteArray? = null) {
        GlobalScope.launch {
            val idempotent = cont.tryResume(value)
            idempotent?.let { token ->
                cont.completeResume(token)
            }
        }
    }

    suspend fun start(characteristicToWrite: BluetoothGattCharacteristic): XYBluetoothResult<ByteArray?> {
        log.info("writeCharacteristic")
        characteristicToWrite.writeType = writeType

        val listenerName = "XYBluetoothGattWriteCharacteristic${hashCode()}"
        var error: XYBluetoothResultErrorCode = XYBluetoothResultErrorCode.None
        var value: ByteArray? = null

        try {
            withTimeoutOrNull(_timeout) {
                value = suspendCancellableCoroutine { cont ->
                    val listener = object : BluetoothGattCallback() {
                        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                            log.info("onCharacteristicWrite: $status")
                            super.onCharacteristicWrite(gatt, characteristic, status)
                            // since it is always possible to have a rogue callback from a previously timed out call,
                            // make sure it is the one we are looking for
                            if (characteristicToWrite.uuid == characteristic?.uuid) {
                                gattCallback.removeListener(listenerName)
                                if (status == BluetoothGatt.GATT_SUCCESS) {
                                    completeStartCoroutine(cont, characteristicToWrite.value)
                                } else {
                                    error = XYBluetoothResultErrorCode.CharacteristicWriteFailed
                                    if (!isActive) {
                                        return
                                    }

                                    completeStartCoroutine(cont)
                                }
                            }
                        }

                        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                            super.onConnectionStateChange(gatt, status, newState)
                            //since we started as connected, any change in that status results in a failure
                            if (newState != BluetoothGatt.STATE_CONNECTED) {
                                error = XYBluetoothResultErrorCode.Disconnected
                                gattCallback.removeListener(listenerName)
                                if (!isActive) {
                                    return
                                }

                                completeStartCoroutine(cont)
                            }
                        }
                    }
                    gattCallback.addListener(listenerName, listener)
                    GlobalScope.launch {
                        val writeStarted = gatt.writeCharacteristic(characteristicToWrite)
                        if (writeStarted != true) {
                            error = XYBluetoothResultErrorCode.WriteCharacteristicFailedToStart
                            gattCallback.removeListener(listenerName)
                            if (!isActive) {
                                return@launch
                            }

                            completeStartCoroutine(cont)
                        }
                    }
                }
            }
        } catch (ex: TimeoutCancellationException) {
            error = XYBluetoothResultErrorCode.Timeout
            gattCallback.removeListener(listenerName)
            log.error(ex)
        }

        return XYBluetoothResult(value, error)
    }
}
