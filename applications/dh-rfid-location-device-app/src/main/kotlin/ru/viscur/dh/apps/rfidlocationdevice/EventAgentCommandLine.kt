package ru.viscur.dh.apps.rfidlocationdevice

import picocli.CommandLine.*
import java.util.concurrent.Callable
import kotlin.system.exitProcess

/**
 * Описание параметров командной строки
 * @author zhalninde
 */
@Command(
        name = "agent",
        header = ["Эмулятор сообщений от датчиков для отслеживания местонахождения медперсонала"]
)
class EventAgentCommandLine(private val rfidLocationCommandService: RfidLocationCommandService) : Callable<Int> {

    @Option(names = ["-i", "--app-id"], description = ["Идентификатор экземпляра приложения"])
    var appId: String? = null

    @Option(names = ["-r", "--replay"], description = ["Прочитать заданные логи и повторить сообщения из них"])
    var replay: Boolean = false

    @Option(names = ["-o", "--output"], description = ["Имя файла лога сообщений"])
    var output: String? = null

    @Option(names = ["-g", "--generate"])
    var generate: String? = null

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Отобразить информацию по использованию"])
    var help = false

    @Parameters(paramLabel = "FILE", description = ["Файлы логов сигналов датчиков"])
    var files: Array<String>? = null

    override fun call(): Int {
        when {
            help -> exitProcess(0)
            else -> rfidLocationCommandService.execute(appId, replay, generate, output, files)
        }
        return 0
    }
}
