package installer

import com.izforge.izpack.api.data.InstallData
import com.izforge.izpack.api.data.PanelActionConfiguration
import com.izforge.izpack.api.handler.AbstractUIHandler
import com.izforge.izpack.data.PanelAction

class InstallStuff: PanelAction {

    override fun executeAction(adata: InstallData, handler: AbstractUIHandler) {
        println("Install Stuff")
        println(adata)
    }

    override fun initialize(configuration: PanelActionConfiguration) {
        println("Initialize Install Stuff")
        println(configuration.properties["INSTALL_PATH"])
    }

}