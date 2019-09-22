package network.xyo.ble.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_device.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.ble.devices.XY2BluetoothDevice
import network.xyo.ble.devices.XY3BluetoothDevice
import network.xyo.ble.devices.XY4BluetoothDevice
import network.xyo.ble.devices.XYBluetoothDevice
import network.xyo.ble.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.sample.R
import network.xyo.ble.sample.XYDeviceData
import network.xyo.ui.ui

@kotlin.ExperimentalUnsignedTypes
class DeviceFragment : XYDeviceFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_device_refresh.setOnClickListener {
            setDeviceValues()
        }
    }

    override fun onPause() {
        super.onPause()
        device?.removeListener("DeviceFragment")
    }

    override fun onResume() {
        super.onResume()

        device?.addListener("DeviceFragment", object: XYBluetoothDevice.Listener() {
            override fun detected(device: XYBluetoothDevice) {
                updateUI()
                super.detected(device)
            }
        })

        if (deviceData?.systemId.isNullOrEmpty()) {
            setDeviceValues()
        } else {
            updateUI()
        }
    }

    fun updateUI() {
        ui {
            throbber?.show()

            text_system_id?.text = deviceData?.systemId
            text_model_number?.text = deviceData?.modelNumberString
            text_serial_number?.text = deviceData?.serialNumberString
            text_firmware_revision?.text = deviceData?.firmwareRevisionString
            text_hardware_revision?.text = deviceData?.hardwareRevisionString
            text_software_revision?.text = deviceData?.softwareRevisionString
            text_mfg_name?.text = deviceData?.manufacturerNameString
            text_ieee?.text = deviceData?.ieeeRegulatoryCertificationDataList
            text_pnp_id?.text = deviceData?.pnpId

            throbber?.hide()
        }
    }

    private fun setDeviceValues() {
        throbber?.show()

        when (device) {
            is XY4BluetoothDevice -> {
                val x4 = (device as? XY4BluetoothDevice)
                x4?.let { getInformationValues(it) }
            }
            is XY3BluetoothDevice -> {
                val x3 = (device as? XY3BluetoothDevice)
                x3?.let { getInformationValues(it) }
            }
            is XY2BluetoothDevice -> {
                val x2 = (device as? XY2BluetoothDevice)
                x2?.let { getInformationValues(it) }
            }
            else -> {
                text_system_id.text = getString(R.string.unknown_device)
            }
        }

        throbber?.hide()
    }

    private fun getInformationValues(device: XY4BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.systemId = device.deviceInformationService.systemId.get().format()
                    it.modelNumberString = device.deviceInformationService.modelNumberString.get().format()
                    it.serialNumberString = device.deviceInformationService.serialNumberString.get().format()
                    it.firmwareRevisionString = device.deviceInformationService.firmwareRevisionString.get().format()
                    it.hardwareRevisionString = device.deviceInformationService.hardwareRevisionString.get().format()
                    it.softwareRevisionString = device.deviceInformationService.softwareRevisionString.get().format()
                    it.manufacturerNameString = device.deviceInformationService.manufacturerNameString.get().format()
                    it.ieeeRegulatoryCertificationDataList = device.deviceInformationService.ieeeRegulatoryCertificationDataList.get().format()
                    it.pnpId = device.deviceInformationService.pnpId.get().format()
                }

                return@connection XYBluetoothResult(true)
            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }


    private fun getInformationValues(device: XY3BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.systemId = device.deviceInformationService.systemId.get().format()
                    it.modelNumberString = device.deviceInformationService.modelNumberString.get().format()
                    it.serialNumberString = device.deviceInformationService.serialNumberString.get().format()
                    it.firmwareRevisionString = device.deviceInformationService.firmwareRevisionString.get().format()
                    it.hardwareRevisionString = device.deviceInformationService.hardwareRevisionString.get().format()
                    it.softwareRevisionString = device.deviceInformationService.softwareRevisionString.get().format()
                    it.manufacturerNameString = device.deviceInformationService.manufacturerNameString.get().format()
                    it.ieeeRegulatoryCertificationDataList = device.deviceInformationService.ieeeRegulatoryCertificationDataList.get().format()
                    it.pnpId = device.deviceInformationService.pnpId.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    private fun getInformationValues(device: XY2BluetoothDevice) {
        GlobalScope.launch {
            var hasConnectionError = true

            device.connection {
                hasConnectionError = false

                deviceData?.let {
                    it.systemId = device.deviceInformationService.systemId.get().format()
                    it.modelNumberString = device.deviceInformationService.modelNumberString.get().format()
                    it.serialNumberString = device.deviceInformationService.serialNumberString.get().format()
                    it.firmwareRevisionString = device.deviceInformationService.firmwareRevisionString.get().format()
                    it.hardwareRevisionString = device.deviceInformationService.hardwareRevisionString.get().format()
                    it.softwareRevisionString = device.deviceInformationService.softwareRevisionString.get().format()
                    it.manufacturerNameString = device.deviceInformationService.manufacturerNameString.get().format()
                    it.ieeeRegulatoryCertificationDataList = device.deviceInformationService.ieeeRegulatoryCertificationDataList.get().format()
                    it.pnpId = device.deviceInformationService.pnpId.get().format()
                }

                return@connection XYBluetoothResult(true)

            }

            updateUI()
            checkConnectionError(hasConnectionError)
        }
    }

    companion object {

        fun newInstance() =
                DeviceFragment()

        fun newInstance (device: XYBluetoothDevice?, deviceData : XYDeviceData?) : DeviceFragment {
            val frag = DeviceFragment()
            frag.device = device
            frag.deviceData = deviceData
            return frag
        }
    }

}
