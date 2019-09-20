package network.xyo.ble.sample

import android.app.Application
import android.os.Build
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.xyo.ble.devices.*
import network.xyo.ble.scanner.XYSmartScan
import network.xyo.ble.scanner.XYSmartScanLegacy
import network.xyo.ble.scanner.XYSmartScanModern
import network.xyo.base.XYBase

@kotlin.ExperimentalUnsignedTypes
class XYApplication : Application() {
    private var _scanner: XYSmartScan? = null
    val scanner: XYSmartScan
        get() {
            if (_scanner == null) {
                _scanner = if (Build.VERSION.SDK_INT >= 21) {
                    XYSmartScanModern(this.applicationContext)
                } else {
                    XYSmartScanLegacy(this.applicationContext)
                }
            }
            return _scanner!!
        }

    override fun onCreate() {
        super.onCreate()

        XYAppleBluetoothDevice.enable(true)
        XYIBeaconBluetoothDevice.enable(true)
        XYFinderBluetoothDevice.enable(true)
        XY4BluetoothDevice.enable(true)
        XY3BluetoothDevice.enable(true)
        XY2BluetoothDevice.enable(true)
        XYGpsBluetoothDevice.enable(true)
        XYBluetoothDevice.enable(true)

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            log.error("Exception Thread: $t")
            log.error(e)
        }

        GlobalScope.launch {
            scanner.start().await()
        }
    }

    override fun onTerminate() {
        log.info("onTerminate")
        GlobalScope.launch {
            scanner.stop().await()
        }
        super.onTerminate()
    }

    companion object : XYBase()
}