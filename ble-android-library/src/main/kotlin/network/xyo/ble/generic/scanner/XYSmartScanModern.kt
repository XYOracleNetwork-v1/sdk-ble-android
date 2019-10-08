package network.xyo.ble.generic.scanner

import android.annotation.TargetApi
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import java.lang.IllegalStateException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.generic.gatt.peripheral.asyncBle
import network.xyo.ble.utilities.XYCallByVersion

@TargetApi(21)
@kotlin.ExperimentalUnsignedTypes
class XYSmartScanModern(context: Context) : XYSmartScan(context) {
    override suspend fun start() = GlobalScope.async {
        log.info("start")
        super.start()

        val result = asyncBle {

            val bluetoothAdapter = bluetoothManager?.adapter

            bluetoothAdapter?.let {
                val scanner = it.bluetoothLeScanner
                if (scanner == null) {
                    log.info("startScan:Failed to get Bluetooth Scanner. Disabled?")
                    return@asyncBle XYBluetoothResult(false)
                } else {
                    // this loop is for Android 7 to prevent getting nuked for scanning too much
                    GlobalScope.launch {
                        //while (started()) {
                            if (status != Status.BluetoothDisabled && status != Status.BluetoothUnavailable) {
                                val filters = ArrayList<ScanFilter>()
                                try {
                                    scanner.startScan(filters, getSettings(), callback)
                                } catch (ex: IllegalStateException) {
                                    log.info("Turning Scanner on after BT disable")
                                }
                                // prevent the pause after a restart from being 5 minutes
                                // 15 minutes
                                /*for (i in 0..180) {
                                    delay(5000) // 5 seconds
                                    if (status != Status.Enabled) {
                                        break
                                    }
                                }
                                try {
                                    scanner.stopScan(callback)
                                } catch (ex: IllegalStateException) {
                                    log.info("Turning Scanner off after BT disable")
                                }
                                delay(1000)*/
                            }/* else {
                                // wait for enabled status
                                delay(5000)
                            }*/
                        //}
                    }
                }

                return@asyncBle XYBluetoothResult(true)
            }

            log.info("Bluetooth Disabled")
            return@asyncBle XYBluetoothResult(false)
        }

        if (result?.error != null) {
            return@async false
        }
        return@async result?.value ?: false
    }.await()

    private val callback = object : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            // log.info("onBatchScanResults: $results")
            results?.let {
                val xyResults = ArrayList<XYScanResult>()
                for (result in it) {
                    xyResults.add(XYScanResultModern(result))
                }
                onScanResult(xyResults)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            log.error("onScanFailed: $errorCode, ${codeToScanFailed(errorCode)}", false)
            if (SCAN_FAILED_APPLICATION_REGISTRATION_FAILED == errorCode && !restartingBluetooth) {
                restartBluetooth()
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            // log.info("onScanResult: $result")
            result?.let {
                val xyResults = ArrayList<XYScanResult>()
                xyResults.add(XYScanResultModern(it))
                onScanResult(xyResults)
            }
        }
    }

    private fun getSettings(): ScanSettings {
        var result: ScanSettings? = null
        XYCallByVersion()
                .add(Build.VERSION_CODES.O) {
                    result = getSettings26()
                }
                .add(Build.VERSION_CODES.M) {
                    result = getSettings23()
                }
                .add(Build.VERSION_CODES.LOLLIPOP) {
                    result = getSettings21()
                }.call()
        return result!!
    }

    // Android 5 and 6
    private fun getSettings21(): ScanSettings {
        return ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setReportDelay(500)
                .build()
    }

    // Android 7 and 7.1
    @TargetApi(Build.VERSION_CODES.M)
    private fun getSettings23(): ScanSettings {
        return ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .build()
    }

    // Android 8 and 9
    @TargetApi(Build.VERSION_CODES.O)
    private fun getSettings26(): ScanSettings {
        return ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .build()
    }

    override suspend fun stop() = GlobalScope.async {
        log.info("stop")
        super.stop()
        val result = asyncBle {
            val bluetoothAdapter = this@XYSmartScanModern.bluetoothAdapter

            if (bluetoothAdapter == null) {
                log.info("stop: Bluetooth Disabled")
                return@asyncBle XYBluetoothResult(false)
            }

            val scanner = bluetoothAdapter.bluetoothLeScanner
            if (scanner == null) {
                log.info("stop:Failed to get Bluetooth Scanner. Disabled?")
                return@asyncBle XYBluetoothResult(false)
            }

            scanner.stopScan(callback)
            return@asyncBle XYBluetoothResult(true)
        }

        if (result?.error != null) {
            return@async false
        }
        return@async result?.value ?: false
    }.await()
}
