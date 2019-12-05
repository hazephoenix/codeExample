package installer

import com.izforge.izpack.api.data.InstallData
import com.izforge.izpack.api.data.PanelActionConfiguration
import com.izforge.izpack.api.handler.AbstractUIHandler
import com.izforge.izpack.data.PanelAction
import java.net.InetAddress

class ConfigPanelAction: PanelAction {

    override fun initialize(configuration: PanelActionConfiguration) {
        println("Initialize config panel")
        val compName = InetAddress.getLocalHost()
        configuration.addProperty("arm.uuid", compName.hostName)
    }

    override fun executeAction(adata: InstallData, handler: AbstractUIHandler) {
        println(adata.installPath)
        println(adata.getVariable("arm.uuid"))
    }

}