package network.xyo.ble.sample.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tx_power.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.ble.devices.xy.XY2BluetoothDevice
import network.xyo.ble.devices.xy.XY3BluetoothDevice
import network.xyo.ble.devices.xy.XY4BluetoothDevice
import network.xyo.ble.generic.devices.XYBluetoothDevice
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.sample.R
import network.xyo.ble.sample.XYDeviceData
import network.xyo.ui.ui


@kotlin.ExperimentalUnsignedTypes
class TxPowerFragment : XYDeviceFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_tx_power, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_tx_refresh.setOnClickListener {
            setTxValues()
        }
    }

    override fun onResume() {
        super.onResume()

        if (deviceData?.txPowerLevel.isNullOrEmpty()) {
            setTxValues()
        } else {
            updateUI()
        }
    }

    private fun updateUI() {
        ui {
            throbber?.hide()

            text_tx_power?.text = deviceData?.txPowerLevel
        }
    }

    private fun setTxValues() {
        ui {
            text_tx_power.text = ""

            when (device) {
                is XY4BluetoothDevice -> {
                    val x4 = (device as? XY4BluetoothDevice)
                    x4?.let {
                        getXY4Values(x4)
                    }
                }
                is XY3BluetoothDevice -> {
                    val x3 = (device as? XY3BluetoothDevice)
                    x3?.let {
                        getXY3Values(x3)
                    }
                }
                is XY2BluetoothDevice -> {
                    text_tx_power.text = getString(R.string.not_supported_x2)
                }
                else -> {
                    text_tx_power.text = getString(R.string.unknown_device)
                }
            }
        }
    }

    private fun getXY4Values(device: XY4BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.txPowerLevel = device.txPowerService.txPowerLevel.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    private fun getXY3Values(device: XY3BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.txPowerLevel = device.txPowerService.txPowerLevel.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    companion object {

        fun newInstance() =
                TxPowerFragment()

        fun newInstance (device: XYBluetoothDevice?, deviceData : XYDeviceData?) : TxPowerFragment {
            val frag = TxPowerFragment()
            frag.device = device
            frag.deviceData = deviceData
            return frag
        }
    }

}
