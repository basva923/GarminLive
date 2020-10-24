package com.github.basva923.garminphoneactivity.garmin

import android.content.Context
import android.util.Log
import com.garmin.android.connectiq.ConnectIQ
import com.garmin.android.connectiq.ConnectIQ.IQApplicationInfoListener
import com.garmin.android.connectiq.IQApp
import com.garmin.android.connectiq.IQDevice


class GarminConnection(context: Context) : ConnectIQ.ConnectIQListener,
    ConnectIQ.IQApplicationEventListener, ConnectIQ.IQSendMessageListener,
    IQApplicationInfoListener {
    private val TAG = "PhoneActivityApp"
    private val CONNECT_IQ_UUID = "26bbbf75-e075-4760-b5a3-3ec09a613b59"
    private var _device: IQDevice? = null
    private var _app: IQApp? = null
    private var _connectIQ: ConnectIQ =
        ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS)

    val messageReceivers = mutableSetOf<GarminMessageReceiver>()

    init {
        _connectIQ.initialize(context, true, this)
    }

    fun isConnected(): Boolean {
        return _device != null && _app != null
    }

    fun sendMessage(msg: GarminMessage): Boolean {
        if (isConnected()) {
            Log.d(TAG, "Sending message (${msg.command}) to ${_device?.friendlyName}")
            _connectIQ.sendMessage(_device, _app, msg.toDict(), this)
            return true
        }
        return false
    }

    private fun setupApplication(app: IQApp, device: IQDevice) {
        Log.d(
            TAG,
            "Application ${app.displayName} is installed on ${device.friendlyName}, using this device."
        )
        _app = app
        _device = device

        _connectIQ.registerForAppEvents(_device, _app, this)
    }

    override fun onSdkShutDown() {
        Log.e(TAG, "GARMIN connect sdk shutdown")
    }

    override fun onInitializeError(p0: ConnectIQ.IQSdkErrorStatus?) {
        Log.e(TAG, "GARMIN connect sdk: init failed")
    }

    override fun onSdkReady() {
        Log.w(TAG, "Garmin sdk ready")

        findAndSetupGarminDevice()
    }

    override fun onMessageReceived(
        device: IQDevice?,
        app: IQApp?,
        messages: MutableList<Any>?,
        status: ConnectIQ.IQMessageStatus?
    ) {
        if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
            messages?.forEach {
                Log.d(TAG, "Received message from ${device!!.friendlyName}.")
                val msg = GarminMessage.fromDict(it as Map<String, Any>)
                Log.d(TAG, msg.toString())

                notifyListeners(msg)
            }
        } else {
            Log.e(TAG, "Received error from ${device?.friendlyName}:  $status")
        }
    }

    private fun notifyListeners(msg: GarminMessage) {
        messageReceivers.forEach {
            it.onMessage(msg)
        }
    }

    override fun onMessageStatus(
        device: IQDevice?,
        app: IQApp?,
        status: ConnectIQ.IQMessageStatus?
    ) {
        if (status == ConnectIQ.IQMessageStatus.SUCCESS) {
            Log.d(TAG, "Sent message to ${device?.friendlyName} successfully.")
        } else {
            Log.e(TAG, "Failed to send message to ${device?.friendlyName}: $status")
        }
    }

    private fun findAndSetupGarminDevice() {
        Log.w(TAG, "Found ${_connectIQ.connectedDevices.size} known devices.")
        for (device in _connectIQ.connectedDevices) {

            if (_connectIQ.getDeviceStatus(device) != IQDevice.IQDeviceStatus.CONNECTED) {
                Log.e(TAG, "Failed to connect to ${device.friendlyName}")
                continue
            }

            Log.w(TAG, "Connected to device ${device.friendlyName}")

            registerForConnectionLost(device)
            _device = device
            val app = IQApp(CONNECT_IQ_UUID)
            setupApplication(app, _device!!)
            // Fix for bug https://forums.garmin.com/developer/connect-iq/i/bug-reports/mobile-sdk-not-working-after-latest-garmin-connect-android-app-update
            //            _connectIQ.getApplicationInfo(
//                CONNECT_IQ_UUID,
//                device,
//                this
//            )
            return
        }
    }

    override fun onApplicationInfoReceived(app: IQApp?) {
        Log.w(TAG, "Found application on ${_device!!.friendlyName}: ${app?.displayName}")
        if (app != null && _app == null) {
            setupApplication(app, _device!!)
        }
    }

    override fun onApplicationNotInstalled(app: String?) {
        Log.e(TAG, "Application $app is NOT installed on ${_device!!.friendlyName}.")
    }

    private fun registerForConnectionLost(device: IQDevice?) {
        _connectIQ.unregisterForDeviceEvents(device)

        _connectIQ.registerForDeviceEvents(device) { iqDevice, iqDeviceStatus ->
            if (iqDeviceStatus != IQDevice.IQDeviceStatus.CONNECTED) {
                Log.e(TAG, "Connection lost to ${iqDevice.friendlyName}")
                findAndSetupGarminDevice()
            }
        }
        Log.w(TAG, "Registerd ${device?.friendlyName} for connection loss.")
    }
}