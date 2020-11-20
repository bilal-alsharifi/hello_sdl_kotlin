package bilal.com.hellosdlkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smartdevicelink.transport.SdlBroadcastReceiver
import com.smartdevicelink.transport.enums.TransportType

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, SdlService::class.java)
        startService(intent)


        if (Config.TRANSPORT_TYPE == TransportType.MULTIPLEX || Config.TRANSPORT_TYPE == TransportType.USB) {
            SdlBroadcastReceiver.queryForConnectedService(this)
        } else {
            val proxyIntent = Intent(this, SdlService::class.java)
            startService(proxyIntent)
        }
    }
}

