package network.xyo.ble.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sensor.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.ble.devices.xy.XY3BluetoothDevice
import network.xyo.ble.generic.devices.XYBluetoothDevice
import network.xyo.ble.sample.R
import network.xyo.ble.sample.XYDeviceData
import network.xyo.ble.services.xy.SensorService
import network.xyo.ui.ui

@kotlin.ExperimentalUnsignedTypes
class SensorFragment : XYDeviceFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_sensor_refresh.setOnClickListener {
            GlobalScope.launch {
                readCharacteristics()
            }
        }
    }

    private suspend fun readCharacteristics() {
        var sensor: SensorService? = null
        (device as? XY3BluetoothDevice)?.let {
            sensor = it.sensorService
        }

        sensor?.let {

            val raw = it.raw.get()
            ui { text_raw.text = raw.display }

            val timeout = it.timeout.get()
            ui { text_timeout.text = timeout.display }

            val threshold = it.threshold.get()
            ui { text_threshold.text = threshold.display }

            val inactive = it.inactive.get()
            ui { text_inactive.text = inactive.display }

            val movementCount = it.movementCount.get()
            ui { text_movement_count.text = movementCount.display }

        }
    }

    companion object {

        fun newInstance() =
                SensorFragment()

        fun newInstance (device: XYBluetoothDevice?, deviceData : XYDeviceData?) : SensorFragment {
            val frag = SensorFragment()
            frag.device = device
            frag.deviceData = deviceData
            return frag
        }
    }

}
