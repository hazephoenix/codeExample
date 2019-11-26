package ru.viscur.dh.apps.paramedicdevice.device

import fr.opensagres.xdocreport.converter.ConverterTypeTo
import fr.opensagres.xdocreport.converter.ConverterTypeVia
import fr.opensagres.xdocreport.converter.Options
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry
import fr.opensagres.xdocreport.template.TemplateEngineKind
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.printing.PDFPageable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.service.RouteSheetBuilder
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.TaskType
import java.awt.RenderingHints
import java.awt.print.PrinterJob
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.annotation.PostConstruct
import javax.print.PrintServiceLookup

/**
 * Печать маршрутного листа
 *
 * @property printerName    наименование принтера в системе, задается в application.properties
 */
@Component
@Profile("!fake-device")
class RoutePrinter(
        @Value("\${route.printer.name:RoutePrinter}")
        private val printerName: String,
        private val eventPublisher: ApplicationEventPublisher,
        private val routeSheetBuilder: RouteSheetBuilder
) {

    private val log: Logger = LoggerFactory.getLogger(RoutePrinter::class.java)

    private lateinit var template: InputStream

    @PostConstruct
    fun postCreate() {
        template = RoutePrinter::class.java.classLoader.getResourceAsStream("RouteSheet.odt")!!
    }

    @EventListener(TaskRequested::class)
    fun eventListener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.PrintRoute) {
            eventPublisher.publishEvent(TaskStarted(task))
            try {
                check(task.payload != null) { "Payload for route print is required!" }
                eventPublisher.publishEvent(TaskStarted(task))
                printRoute(task.payload!!["patientId"] as String)
                task.payload = null
                eventPublisher.publishEvent(TaskComplete(task))
            } catch (e: Exception) {
                log.error(e.message, e)
                eventPublisher.publishEvent(TaskError(task))
            }
        }
    }

    private fun printRoute(patientId: String) {
        if (log.isDebugEnabled) {
            log.debug("Print route sheet for patientId[$patientId]")
        }
        val routeSheet = XDocReportRegistry.getRegistry()
                .loadReport(template, TemplateEngineKind.Velocity)
        val options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM)
        val data = routeSheetBuilder.build(patientId)
        val context = routeSheet.createContext(data)
        ByteArrayOutputStream().use { os ->
            routeSheet.convert(context, options, os)
            PDDocument.load(os.toByteArray()).use { doc ->
                val renderingHints = RenderingHints(null)
                renderingHints[RenderingHints.KEY_INTERPOLATION] = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                renderingHints[RenderingHints.KEY_RENDERING] = RenderingHints.VALUE_RENDER_QUALITY
                renderingHints[RenderingHints.KEY_ANTIALIASING] = RenderingHints.VALUE_ANTIALIAS_OFF

                val pageable = PDFPageable(doc).apply { this.renderingHints = renderingHints }
                val printService = PrintServiceLookup.lookupPrintServices(null, null)
                        .find { printerName == it.name }
                val job = PrinterJob.getPrinterJob().apply { setPageable(pageable); setPrintService(printService) }
                job.print()
            }
        }
        if (log.isDebugEnabled) {
            log.debug("Print complete!")
        }
    }
}