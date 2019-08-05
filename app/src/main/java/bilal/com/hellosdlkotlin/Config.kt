package bilal.com.hellosdlkotlin

import com.smartdevicelink.transport.enums.TransportType

/**
 * Created by Bilal Alsharifi on 2019-08-05.
 */
class Config {
    companion object {
        val TRANSPORT_TYPE = TransportType.TCP
        const val APP_NAME = "Hello Sdl Kotlin"
        const val APP_ID = "123876235"
        const val CORE_IP = "m.sdl.tools"
        const val CORE_PORT = 18161
    }
}