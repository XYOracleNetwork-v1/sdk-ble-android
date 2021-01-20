package network.xyo.ble.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.ble.devices.xy.XY2BluetoothDevice
import network.xyo.ble.devices.xy.XY3BluetoothDevice
import network.xyo.ble.devices.xy.XY4BluetoothDevice
import network.xyo.ble.generic.devices.XYBluetoothDevice
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.sample.R
import network.xyo.ble.sample.XYDeviceData
import network.xyo.ble.sample.databinding.FragmentGenericAttributeBinding

@kotlin.ExperimentalUnsignedTypes
class GenericAttributeFragment : XYDeviceFragment<FragmentGenericAttributeBinding>() {

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentGenericAttributeBinding {
        return FragmentGenericAttributeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGattRefresh.setOnClickListener {
            setGattValues()
        }
    }

    override fun onResume() {
        super.onResume()

        if (deviceData?.serviceChanged.isNullOrEmpty()) {
            setGattValues()
        } else {
            updateUI()
        }
    }

    private fun updateUI() {
        activity?.runOnUiThread {
            binding.textServiceChanged.text = deviceData?.serviceChanged
        }
    }

    private fun setGattValues() {
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
                val x2 = (device as? XY2BluetoothDevice)
                x2?.let {
                    getXY2Values(x2)
                }
            }
            else -> {
                binding.textServiceChanged.text = getString(R.string.unknown_device)
            }

        }
    }

    private fun getXY4Values(device: XY4BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.serviceChanged = device.genericAttributeService.serviceChanged.get().format()
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
                    it.serviceChanged = device.genericAttributeService.serviceChanged.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    private fun getXY2Values(device: XY2BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.serviceChanged = device.genericAttributeService.serviceChanged.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    companion object {

        fun newInstance() =
                GenericAttributeFragment()

        fun newInstance (device: XYBluetoothDevice?, deviceData : XYDeviceData?) : GenericAttributeFragment {
            val frag = GenericAttributeFragment()
            frag.device = device
            frag.deviceData = deviceData
            return frag
        }
    }
}
