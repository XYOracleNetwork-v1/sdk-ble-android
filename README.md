[logo]: https://cdn.xy.company/img/brand/XYO_full_colored.png

[![logo]](https://xyo.network)

# XYO Bluetooth SDK

![](https://github.com/XYOracleNetwork/sdk-xyo-android/workflows/CI/badge.svg?branch=develop)  ![](https://github.com/XYOracleNetwork/sdk-xyo-android/workflows/Release/badge.svg?branch=master) [![Download](https://api.bintray.com/packages/xyoraclenetwork/xyo/sdk-ble-android/images/download.svg)](https://bintray.com/xyoraclenetwork/xyo/sdk-ble-android/_latestVersion) [![](https://jitpack.io/v/XYOracleNetwork/sdk-ble-android.svg)](https://jitpack.io/#XYOracleNetwork/sdk-ble-android)
[![Maintainability](https://api.codeclimate.com/v1/badges/73ee685fc9e2c61454b2/maintainability)](https://codeclimate.com/github/XYOracleNetwork/sdk-ble-android/maintainability)

[![BCH compliance](https://bettercodehub.com/edge/badge/XYOracleNetwork/sdk-ble-android?branch=master)](https://bettercodehub.com/results/XYOracleNetwork/sdk-ble-android)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=XYOracleNetwork_sdk-ble-android&metric=alert_status)](https://sonarcloud.io/dashboard?id=XYOracleNetwork_sdk-ble-android)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b14446847e614a2fae7152892765dac1)](https://www.codacy.com/app/arietrouw/sdk-ble-android?utm_source=github.com&utm_medium=referral&utm_content=XYOracleNetwork/sdk-ble-android&utm_campaign=Badge_Grade) [![Known Vulnerabilities](https://snyk.io/test/github/XYOracleNetwork/sdk-ble-android/badge.svg?targetFile=build.gradle)](https://snyk.io/test/github/XYOracleNetwork/sdk-ble-android?targetFile=build.gradle)

## Table of Contents

-   [Title](#xyo-bluetooth-sdk)
-   [Description](#description)
-   [Prerequisites](#prerequisites)
-   [Install](#install)
-   [Usage](#usage)
-   [License](#license)
-   [Credits](#credits)

## Description

A robust Bluetooth solution for Android. This BLE SDK was written from ground-up, in Kotlin,
 to help developers with the agonizing issues with Android the BLE stack.
Not only will this SDK make XYO apps better, but bring XYO functionality to existing apps.  In adition to generalized BLE support, the SDK also has specific support for XY spacific hardware.

## Prerequisites

-   JDK 1.8
-   Android SDK

    -   Kotlin
    -   Build Tools 27+

## Install

You can add sdk-ble-android to your existing app by cloning the project and manually adding it
to your build.gradle:

```bash
git clone git@github.com:XYOracleNetwork/sdk-ble-android.git
```

or by using maven:

```gradle
allprojects {
    repositories {
        maven { url "https://dl.bintray.com/xyoraclenetwork/xyo" }
    }
}
```

```gradle
dependencies {
    implementation 'com.github.XYOracleNetwork:sdk-ble-android:1.0.2'
}
```

## Usage

A full Working example is included in the project. Look at the ble-android-sample folder for more information.

Configure and create an instance of the scanner:

```kotlin
// enable the device types you want to listen for
// you can create your own custom listener
// for any type of BLE device by creating a Class that extends XYBluetoothDevice
XYAppleBluetoothDevice.enable(true)     //Apple devices
XYIBeaconBluetoothDevice.enable(true)   //iBeacon device
XYFinderBluetoothDevice.enable(true)    //XY device
XY4BluetoothDevice.enable(true)         //XY4+ Find It device
XY3BluetoothDevice.enable(true)         //XY3 Find It device
XY2BluetoothDevice.enable(true)         //XY2 Find It device
XYGpsBluetoothDevice.enable(true)       //XY GPS device
myCustomDevice.enable(true)

val scanner: XYFilteredSmartScan = XYFilteredSmartScanModern(MyApplication.getAppContext())
```

Add a listener for the XYFilteredSmartScan:

```kotlin
scanner.addListener("myTAG", object : XYFilteredSmartScan.Listener() {
    override fun entered(device: XYBluetoothDevice) {
        super.entered(device)
        checkMyDeviceTypeAddListener(device)
    }

    override fun exited(device: XYBluetoothDevice) {
        super.exited(device)
        doSomethingOnExit()
    }

    override fun detected(device: XYBluetoothDevice) {
        super.detected(device)
        doSomethingOnDetected()
    }

    override fun connectionStateChanged(device: XYBluetoothDevice, newState: Int) {
        super.connectionStateChanged(device, newState)
        doSomethingOnChanged()
    }

})
```

Start scanning for BLE devices:

```kotlin
scanner.start()
```

Once we know the device type, add a specific device type listener

```kotlin
private fun checkMyDeviceTypeAddListener(device: XYBluetoothDevice) {
    (device as? XY4BluetoothDevice)?.addListener("myTAG", object : XY4BluetoothDevice.Listener(){
        override fun buttonSinglePressed(device: XYFinderBluetoothDevice) {
            super.buttonSinglePressed(device)
            doSomethingOnSinglePressed()
        }

        override fun buttonDoublePressed(device: XYFinderBluetoothDevice) {
            super.buttonDoublePressed(device)
            doSomethingOnDoublePressed()
        }

        override fun buttonLongPressed(device: XYFinderBluetoothDevice) {
            super.buttonLongPressed(device)
            doSomethingOnLongPressed()
        }

        override fun entered(device: XYBluetoothDevice) {
            super.entered(device)
            doSomethingOnEntered()
        }

        override fun exited(device: XYBluetoothDevice) {
            super.exited(device)
            doSomethingOnExited()
        }

        override fun detected(device: XYBluetoothDevice) {
            super.detected(device)
            doSomethingOnDetected()
        }

        override fun connectionStateChanged(device: XYBluetoothDevice, newState: Int) {
            super.connectionStateChanged(device, newState)
            doSomethingOnConnectionStateChanged()
        }
    })

    (device as? XY3BluetoothDevice)?.addListener("myTAG", object : XY3BluetoothDevice.Listener(){
        //add your logic here
    })
    (device as? XY2BluetoothDevice)?.addListener("myTAG", object : XY2BluetoothDevice.Listener(){
    //add your logic here
    })
    (device as? XYIBeaconBluetoothDevice)?.addListener("myTAG", object : XYIBeaconBluetoothDevice.Listener(){
    //add your logic here
    })

    //or create a custom listener for a custom BLE device
    (device as? MyCustomBluetoothDevice)?.addListener("myTAG", object : MyCustomBluetoothDevice.Listener(){
    //add your logic here
    })

}
```

Connecting to a BLE device:

```kotlin
fun connectXY4Device(device: XY4BluetoothDevice) {
    device.connection {
        // within this connection, you can do multiple tasks
        // the connection will stay open until all tasks are completed
        // the connection will auto-disconnect after 5 seconds of inactivity.

        var batteryLevel = device.batteryService.level.get().await()
        var firmwareVersion = device.deviceInformationService.firmwareRevisionString.get().await()
        var deviceName = device.genericAccessService.deviceName.get().await()
    }
}
```

Basic callback server use

```kotlin
val myAwesomeReadCharacteristic = XYBluetoothReadCharacteristic(UUID.fromString("01ef8f90-e99f-48ae-87bb-f683b93c692f"))
val myAwesomeWriteCharacteristic = XYBluetoothWriteCharacteristic(UUID.fromString("01ef8f90-e99f-48ae-87bb-f683b93c692f"))

/**
 * Will send "Carter is cool" on every read
 */
myAwesomeReadCharacteristic.addResponder("myResponder", object : XYBluetoothReadCharacteristic.XYBluetoothReadCharacteristicResponder {
    override fun onReadRequest(device: BluetoothDevice?): ByteArray? {
        return "Carter is cool".toByteArray()
    }
})

myAwesomeWriteCharacteristic.addResponder("myResponder", object : XYBluetoothWriteCharacteristic.XYBluetoothWriteCharacteristicResponder {
    override fun onWriteRequest(value: ByteArray, device: BluetoothDevice?): Boolean? {
        if (value.size == 10) {
            return true
        }
        return false
    }
})

val myAwesomeService = XYBluetoothService(UUID.fromString("3079ca44-ae64-4797-b4e5-a31e3304c481"), BluetoothGattService.SERVICE_TYPE_PRIMARY)
myAwesomeService.addCharacteristic(myAwesomeReadCharacteristic)
myAwesomeService.addCharacteristic(myAwesomeWriteCharacteristic)

val server = XYBluetoothGattServer(applicationContext)
server.addService(myAwesomeService).await()
server.startServer()
```

Basic await server use

```kotlin
val myAwesomeReadCharacteristic = XYBluetoothReadCharacteristic(UUID.fromString("01ef8f90-e99f-48ae-87bb-f683b93c692f"))
val myAwesomeWriteCharacteristic = XYBluetoothWriteCharacteristic(UUID.fromString("01ef8f90-e99f-48ae-87bb-f683b93c692f"))

val myAwesomeService = XYBluetoothService(UUID.fromString("3079ca44-ae64-4797-b4e5-a31e3304c481"), BluetoothGattService.SERVICE_TYPE_PRIMARY)
myAwesomeService.addCharacteristic(myAwesomeReadCharacteristic)
myAwesomeService.addCharacteristic(myAwesomeWriteCharacteristic)

val server = XYBluetoothGattServer(applicationContext)
server.addService(myAwesomeService).await()
server.startServer()

/**
 * Will send "0x1337" on next read
 */
myAwesomeReadCharacteristic.waitForReadRequest(byteArrayOf(0x13, 0x37), null).await()
val writeValue = myAwesomeWriteCharacteristic.waitForWriteRequest(null).await()
```

IMPORTANT:

```kotlin
//make sure you stop the scanner when no longer needed
scanner.stop()

//remmove all device listeners when shutting down an activity:
 override fun onStop() {
    super.onStop()
    device!!.removeListener(TAG)
}
```

## License

See the [LICENSE.md](LICENSE) file for license details.

## Credits

Made with 🔥and ❄️ by [XYO](https://www.xyo.network)
