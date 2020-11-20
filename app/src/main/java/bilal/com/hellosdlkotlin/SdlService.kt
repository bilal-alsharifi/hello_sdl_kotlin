package bilal.com.hellosdlkotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import com.smartdevicelink.managers.SdlManager
import com.smartdevicelink.managers.SdlManagerListener
import com.smartdevicelink.managers.file.filetypes.SdlArtwork
import com.smartdevicelink.managers.lifecycle.LifecycleConfigurationUpdate
import com.smartdevicelink.protocol.enums.FunctionID
import com.smartdevicelink.proxy.RPCNotification
import com.smartdevicelink.proxy.rpc.OnHMIStatus
import com.smartdevicelink.proxy.rpc.enums.AppHMIType
import com.smartdevicelink.proxy.rpc.enums.FileType
import com.smartdevicelink.proxy.rpc.enums.HMILevel
import com.smartdevicelink.proxy.rpc.enums.Language
import com.smartdevicelink.proxy.rpc.listeners.OnRPCNotificationListener
import com.smartdevicelink.transport.*
import com.smartdevicelink.transport.enums.TransportType
import java.util.*


/**
 * Created by Bilal Alsharifi on 2019-08-05.
 */
class SdlService : Service() {

    var sdlManager: SdlManager? = null
    var TAG = "SdlService"

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterForeground()
        }


        if(sdlManager == null){
            // Create SDlManagerListener
            val listener = object : SdlManagerListener {

                override fun onStart() {
                    sdlManager?.addOnRPCNotificationListener(FunctionID.ON_HMI_STATUS, object : OnRPCNotificationListener(){
                        override fun onNotified(notification: RPCNotification?) {
                            val onHMIStatus : OnHMIStatus = notification as OnHMIStatus
                            if (onHMIStatus.hmiLevel == HMILevel.HMI_FULL && onHMIStatus.firstRun){
                                sdlManager?.screenManager?.beginTransaction()
                                sdlManager?.screenManager?.textField1 = "Welcome to Hello Sdl Kotlin"
                                sdlManager?.screenManager?.textField2 = "My dudes ;)"
                                sdlManager?.screenManager?.primaryGraphic = SdlArtwork("image1", FileType.GRAPHIC_PNG, R.drawable.image1, false)
                                sdlManager?.screenManager?.commit(null)
                            }
                        }
                    })
                }

                override fun onDestroy() {
                    this@SdlService.stopSelf()
                }

                override fun onError(info: String, e: Exception)
                {

                }

                override fun managerShouldUpdateLifecycle(language: Language?, hmiLanguage: Language?): LifecycleConfigurationUpdate {
                    return LifecycleConfigurationUpdate()
                }
            }


            // Create sdlManager
            val builder = SdlManager.Builder(this, Config.APP_ID, Config.APP_NAME, listener)

            // Set transport config
            var transportConfig: BaseTransportConfig? = null
            when (Config.TRANSPORT_TYPE) {
                TransportType.TCP -> transportConfig = TCPTransportConfig(Config.CORE_PORT, Config.CORE_IP, false)
                TransportType.MULTIPLEX -> transportConfig = MultiplexTransportConfig(baseContext, Config.APP_ID, MultiplexTransportConfig.FLAG_MULTI_SECURITY_MED)
            }
            builder.setTransportType(transportConfig!!)


            // // Set App HMI Types
            val appType : Vector <AppHMIType> = Vector()
            appType.add(AppHMIType.MEDIA)
            builder.setAppTypes(appType)


            // Set app icon
            val appIconArtwork = SdlArtwork("icon", FileType.GRAPHIC_PNG, R.drawable.ic_launcher, false)
            builder.setAppIcon(appIconArtwork)


            // Build SdlManager
            sdlManager = builder.build()
            sdlManager?.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        sdlManager?.dispose()
    }

    private fun enterForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("MyApp", "SdlService", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val serviceNotification = Notification.Builder(this, channel.id)
                .setContentTitle("Connected through SDL")
                .setSmallIcon(R.drawable.ic_launcher)
                .build()
            val FOREGROUND_SERVICE_ID = 100
            startForeground(FOREGROUND_SERVICE_ID, serviceNotification)
        }
    }
}