package triton.image

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Реализация отрисовки волн в растер
 */
class RasterWavesPainter(
        /**
         * Масштаб
         */
        scale: Double = 4.0,

        /**
         * Конфигурация сетки
         */
        gridConfig: GridConfig = GridConfig()
) : AbstractWavesPainter<BufferedImage>(scale, gridConfig) {
    override fun newImage(imageSize: Dimension): Pair<BufferedImage, Graphics2D> {
        val image = BufferedImage(
                imageSize.width,
                imageSize.height,
                BufferedImage.TYPE_4BYTE_ABGR
        )
        val graphics = image.createGraphics()
        return Pair(image, graphics)
    }

    override fun flush(image: BufferedImage, graphics: Graphics2D) {
        /* Noting to do*/
    }

}