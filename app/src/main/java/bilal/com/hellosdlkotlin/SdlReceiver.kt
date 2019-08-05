package bilal.com.hellosdlkotlin

import android.content.Context
import android.content.Intent
import android.os.Build
import com.smartdevicelink.transport.SdlBroadcastReceiver

/**
 * Created by Bilal Alsharifi on 2019-08-05.
 */
class SdlReceiver : SdlBroadcastReceiver() {

    override fun onSdlEnabled(context: Context?, intent: Intent?) {
        // Use the provided intent but set the class to the SdlService
        intent?.setClass(context!!, SdlService::class.java)


        // SdlService needs to be foregrounded in Android O and above
        // This will prevent apps in the background from crashing when they try to start SdlService
        // Because Android O doesn't allow background apps to start background services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }

    override fun defineLocalSdlRouterClass(): Class<out SdlRouterService> {
        //Return a local copy of the SdlRouterService located in your project
        return bilal.com.hellosdlkotlin.SdlRouterService::class.java
    }
}