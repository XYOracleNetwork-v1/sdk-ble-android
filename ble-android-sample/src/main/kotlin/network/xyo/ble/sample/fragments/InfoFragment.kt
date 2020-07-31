@file:Suppress("SpellCheckingInspection")

package network.xyo.ble.sample.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.coroutines.*
import network.xyo.base.XYBase
import network.xyo.ble.devices.apple.XYIBeaconBluetoothDevice
import network.xyo.ble.devices.xy.XY3BluetoothDevice
import network.xyo.ble.devices.xy.XY4BluetoothDevice
import network.xyo.ble.devices.xy.XYFinderBluetoothDevice
import network.xyo.ble.generic.devices.XYBluetoothDevice
import network.xyo.ble.generic.gatt.peripheral.XYBluetoothResult
import network.xyo.ble.sample.R
import network.xyo.ble.sample.XYDeviceData

@kotlin.ExperimentalUnsignedTypes
class InfoFragment : XYDeviceFragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_startTest.setOnClickListener(this)
        button_connect.setOnClickListener(this)
        button_disconnect.setOnClickListener(this)
        button_find.setOnClickListener(this)
        button_stop_find.setOnClickListener(this)
        button_stay_awake.setOnClickListener(this)
        button_fall_asleep.setOnClickListener(this)
        button_lock.setOnClickListener(this)
        button_unlock.setOnClickListener(this)
        button_enable_notify.setOnClickListener(this)
        button_stayConnected.setOnCheckedChangeListener(this)

        when (device) {
            is XY4BluetoothDevice -> {
                button_enable_notify.visibility = VISIBLE
                button_disable_notify.visibility = VISIBLE
            }
            is XY3BluetoothDevice -> {
                button_enable_notify.visibility = VISIBLE
                button_disable_notify.visibility = VISIBLE
            }

            else -> {
                button_enable_notify.visibility = GONE
                button_disable_notify.visibility = GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        log.info("onResume: InfoFragment")
        updateAdList()
        updateUI()

        device?.addListener("info", object: XYBluetoothDevice.Listener() {
            override fun entered(device: XYBluetoothDevice) {
                super.entered(device)
                log.info("Entered")
            }

            override fun detected(device: XYBluetoothDevice) {
                super.detected(device)
                updateUI()
            }

            override fun connectionStateChanged(device: XYBluetoothDevice, newState: Int) {
                super.connectionStateChanged(device, newState)
                updateUI()
            }

            override fun exited(device: XYBluetoothDevice) {
                super.exited(device)
                log.info("Exited")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        device?.removeListener("info")
    }

    override fun update() {
        updateUI()
    }

    private fun updateUI() {
        activity?.runOnUiThread {
            log.info("update")

            if (device != null) {


                text_family.text = device?.name
                text_rssi.text = device?.rssi.toString()

                (device as? XYIBeaconBluetoothDevice)?.let {
                    text_power.text = it.power.toString()
                    text_major.text = String.format(getString(R.string.hex_placeholder), it.major.toInt().toString(16))
                    text_minor.text = String.format(getString(R.string.hex_placeholder), it.minor.toInt().toString(16))
                }

                text_pulse_count.text = device?.detectCount.toString()
                text_enter_count.text = device?.enterCount.toString()
                text_exit_count.text = device?.exitCount.toString()
                text_avg_gap_size.text = device?.averageDetectGap.toString()
                text_last_gap_size.text = device?.lastDetectGap.toString()
                text_max_gap_size.text = device?.maxDetectTime.toString()
            }

            if (device?.connected == true) {
                button_connect?.visibility = GONE
                button_disconnect?.visibility = VISIBLE
            } else {
                button_connect?.visibility = VISIBLE
                button_disconnect?.visibility = GONE
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.button_stayConnected -> {
                GlobalScope.launch {
                    device?.stayConnected = isChecked
                    updateUI()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_connect -> {
                connect()
            }
            R.id.button_disconnect -> {
                disconnect()
            }
            R.id.button_find -> {
                find()
            }
            R.id.button_stop_find -> {
                stopFind()
            }
            R.id.button_stay_awake -> {
                wake()
            }
            R.id.button_fall_asleep -> {
                sleep()
            }
            R.id.button_lock -> {
                lock()
            }
            R.id.button_unlock -> {
                unlock()
            }
            R.id.button_enable_notify -> {
                enableButtonNotify(true)
            }
            R.id.button_disable_notify -> {
                enableButtonNotify(false)
            }
            R.id.button_startTest -> {
                //testRefreshGatt()
            }
        }
    }

    /*private fun testRefreshGatt() {
        GlobalScope.launch {
            val connection = device?.connectGatt()?.await()
            val device: XYBluetoothDevice? = activity?.device
            val connection = device?.connect()?.await()
            if (connection?.error != null) {
                showToast("Connection failed")
                return@launch
            }
            val result = device?.refreshGatt()?.await()
            showToast(result.toString())
        }
    }*/

    private fun connect() {
        GlobalScope.launch {
            val result = device?.connect()
            val error = result?.error
            if (error != XYBluetoothResult.ErrorCode.None) {
                log.error(error.toString())
            } else {
                log.info("Connected")
            }
            updateUI()
        }
    }

    private fun disconnect() {
        GlobalScope.launch {
            device?.disconnect()
            updateUI()
        }
    }

    private fun find() {
        log.info("beepButton: got xyDevice")
        activity?.runOnUiThread {
            button_find?.isEnabled = false
        }

        GlobalScope.launch {
            (device as? XYFinderBluetoothDevice)?.find()
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_find?.isEnabled = true }

            }
        }
    }

    private fun stopFind() {
        log.info("beepButton: got xyDevice")
        activity?.runOnUiThread {
            button_find?.isEnabled = false
        }

        GlobalScope.launch {
            (device as? XYFinderBluetoothDevice)?.stopFind()
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_find?.isEnabled = true }
            }
        }
    }

    private fun wake() {
        log.info("stayAwakeButton: onClick")
        activity?.runOnUiThread {
            button_stay_awake?.isEnabled = false
        }

        GlobalScope.launch {
            val stayAwake = (device as? XYFinderBluetoothDevice)?.stayAwake()
            if (stayAwake == null) {
                log.info("Stay Awake Failed to Complete Call")
            } else {
                log.info("Stay Awake Set")
            }
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_stay_awake?.isEnabled = true }
            }
        }
    }

    private fun sleep() {
        log.info("fallAsleepButton: onClick")
        activity?.runOnUiThread {
            button_fall_asleep?.isEnabled = false
        }

        GlobalScope.launch {
            val fallAsleep = (device as? XYFinderBluetoothDevice)?.fallAsleep()
            if (fallAsleep == null) {
                log.error("Fall Asleep Failed to Complete Call")
            } else {
                log.info("Fall Asleep Set")
            }
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_fall_asleep?.isEnabled = true }
            }
        }
    }

    private fun lock() {
        log.info("lockButton: onClick")
        activity?.runOnUiThread {
            button_lock?.isEnabled = false
        }

        GlobalScope.launch {
            val locked = (device as? XYFinderBluetoothDevice)?.lock()
            when {
                locked == null -> log.error("Device does not support Lock")
                locked.error == XYBluetoothResult.ErrorCode.None -> {
                    log.info("Locked: ${locked.value}")
                    updateStayAwakeEnabledStates()
                }
                else -> log.error("Lock Error: ${locked.error}")
            }
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_lock?.isEnabled = true }
            }
        }
    }

    private fun unlock() {
        log.info("unlockButton: onClick")
        activity?.runOnUiThread {
            button_unlock?.isEnabled = false
        }

        GlobalScope.launch {
            val unlocked = (device as? XYFinderBluetoothDevice)?.unlock()
            when {
                unlocked == null -> log.error("Device does not support Unlock")
                unlocked.error == XYBluetoothResult.ErrorCode.None -> {
                    log.info("Unlocked: ${unlocked.value}")
                    updateStayAwakeEnabledStates()
                }
                else -> log.error("Unlock Error: ${unlocked.error}")
            }
            activity?.runOnUiThread {
                this@InfoFragment.isVisible.let { button_unlock?.isEnabled = true }
            }
        }
    }


    private suspend fun updateStayAwakeEnabledStates() {
        return GlobalScope.async {
            log.info("updateStayAwakeEnabledStates")
            val xy4 = device as? XY4BluetoothDevice
            if (xy4 != null) {
                val stayAwake = xy4.primary.stayAwake.get()
                log.info("updateStayAwakeEnabledStates: ${stayAwake.value}")
                activity?.runOnUiThread {
                    this@InfoFragment.isVisible.let {
                        if (stayAwake.value != 0x0.toUByte()) {
                            button_fall_asleep?.isEnabled = true
                            button_stay_awake?.isEnabled = false
                        } else {
                            button_fall_asleep?.isEnabled = false
                            button_stay_awake?.isEnabled = true
                        }
                    }
                }
            } else {
                log.error("updateStayAwakeEnabledStates: Not an XY4!", false)
            }
            return@async
        }.await()
    }

    private fun enableButtonNotify(enable: Boolean) {
        GlobalScope.launch {
            val xy4 = device as? XY4BluetoothDevice
            if (xy4 != null) {
                val notify = xy4.primary.buttonState.enableNotify(enable)
                log.info(notify.toString())

            } else {
                val xy3 = device as? XY3BluetoothDevice
                if (xy3 != null) {
                    val notify = xy3.controlService.button.enableNotify(enable)
                    log.info(notify.toString())
                }
            }
        }
    }

    private fun updateAdList() {
        activity?.runOnUiThread {
            var txt = ""
            device?.let { device ->
                for (i in 0 until device.ads.size()) {
                    val key = device.ads.keyAt(i)
                    txt = txt + device.ads[key].data?.toHex() + "\r\n"
                }
                adList?.text = txt
            }
        }
    }

    private val hexChars = "0123456789ABCDEF".toCharArray()
    private fun ByteArray.toHex(): String {
        val result = StringBuffer()

        forEach {
            val octet = it.toInt()
            val firstIndex = (octet and 0xF0).ushr(4)
            val secondIndex = octet and 0x0F
            result.append(hexChars[firstIndex])
            result.append(hexChars[secondIndex])
        }

        return result.toString()
    }

    companion object : XYBase() {
        fun newInstance() = InfoFragment()

        fun newInstance (device: XYBluetoothDevice?, deviceData : XYDeviceData?) : InfoFragment {
            val frag = InfoFragment()
            frag.device = device
            frag.deviceData = deviceData
            return frag
        }
    }
}
