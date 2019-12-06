package triton.image

import org.w3c.dom.Document
import java.awt.Dimension
import java.awt.Graphics2D
import org.apache.batik.dom.GenericDOMImplementation
import org.apache.batik.svggen.SVGGraphics2D
import java.io.File
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


/**
 * Реализация отрисовки волн в вектор
 */
class SvgWavesPainter(
        /**
         * Масштаб
         */
        scale: Double = 4.0,

        /**
         * Конфигурация сетки
         */
        gridConfig: GridConfig = GridConfig()
) : AbstractWavesPainter<Document>(scale, gridConfig) {


    override fun newImage(imageSize: Dimension): Pair<Document, Graphics2D> {
        // Get a DOMImplementation.
        val domImpl = GenericDOMImplementation.getDOMImplementation()

        // Create an instance of org.w3c.dom.Document.
        val svgNS = "http://www.w3.org/2000/svg"
        val document = domImpl.createDocument(svgNS, "svg", null)

        // Create an instance of the SVG Generator.
        val svgGenerator = SVGGraphics2D(document)
        svgGenerator.svgCanvasSize = imageSize
        return Pair(document, svgGenerator)
    }

    fun createImageAsString(waves: List<UByteArray>, title: String): String {
        return writeToString(createImage(waves, title))
    }

    override fun flush(image: Document, graphics: Graphics2D) {
        (graphics as SVGGraphics2D)
                .getRoot(image.documentElement)
    }


    fun writeToString(document: Document): String {
        val domSource = DOMSource(document)
        val writer = StringWriter()
        val result = StreamResult(writer)
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.transform(domSource, result)
        return writer.toString()
    }

    fun writeToFile(document: Document, file: File) {
        file
                .outputStream()
                .use {
                    val domSource = DOMSource(document)
                    val result = StreamResult(it)
                    val tf = TransformerFactory.newInstance()
                    val transformer = tf.newTransformer()
                    transformer.transform(domSource, result)
                }
    }

}