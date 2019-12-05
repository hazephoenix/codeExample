package installer

import com.izforge.izpack.panels.userinput.processor.Processor
import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient
import java.net.InetAddress

class ArmUUIDProcessor: Processor {
    override fun process(client: ProcessingClient): String {
        return InetAddress.getLocalHost().hostName
    }
}