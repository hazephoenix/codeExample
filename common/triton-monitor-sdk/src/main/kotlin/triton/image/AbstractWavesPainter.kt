package triton.image

import java.awt.*
import java.awt.geom.AffineTransform
import kotlin.math.ceil

abstract class AbstractWavesPainter<TImage>(
        /**
         * Масштаб
         */
        val scale: Double,

        /**
         * Конфигурация сетки
         */
        val gridConfig: GridConfig = GridConfig()
) {

    protected abstract fun newImage(imageSize: Dimension): Pair<TImage, Graphics2D>
    protected abstract fun flush(image: TImage, graphics: Graphics2D)


    fun createEmptyImage(wavesSize: WavesSize): TImage {
        val (image, graphics) = prepareEmptyImage(wavesSize)
        flush(image, graphics)
        return image
    }

    fun createImage(waves: List<UByteArray>, title: String): TImage {
        val wavesSize = WavesSize(waves.size, waves.first().size)
        val dimension = calcDimension(wavesSize.pointsCount)
        val (image, graphics) = prepareEmptyImage(wavesSize)

        graphics.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        var waveStartX = 0
        var prevPoint: Point? = null

        for (waveIdx in 0 until waves.size) {
            val wave = waves[waveIdx]
            for (pointIdx in 0 until wave.size) {
                graphics.color = Color.BLACK

                val x = (waveStartX + pointIdx).toInt()
                val y = dimension.height - (wave[pointIdx].toInt()).toInt()

                if (prevPoint != null) {
                    graphics.drawLine(prevPoint.x, prevPoint.y, x, y)
                }
                prevPoint = Point(x, y)
            }
            if (prevPoint != null) {
                waveStartX = prevPoint.x
            }
        }
        graphics.font = graphics.font
                .deriveFont(20)
                .deriveFont(Font.BOLD)
        graphics.drawString(title, 6, 21)
        flush(image, graphics)
        return image
    }

    private fun prepareEmptyImage(wavesSize: WavesSize): Pair<TImage, Graphics2D> {
        val dimension = calcDimension(wavesSize.pointsCount)
        val (image, graphics) = newImage(
                Dimension(
                        ceil(dimension.width * scale).toInt(),
                        ceil(dimension.height * scale).toInt()
                )
        )
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        graphics.transform = AffineTransform.getScaleInstance(scale, scale)
        drawGrid(graphics, wavesSize)
        return Pair(image, graphics)
    }


    /**
     * Отрисовка сетки (на бумажном насителе это миллиметровка, мы рисуем что-то подобное)
     */
    private fun drawGrid(g: Graphics2D, wavesSize: WavesSize) {
        val cellStroke = BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
        val bigCellStroke = BasicStroke(2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
        val prevColor = g.color
        try {
            g.color = Color.GRAY
//            g.stroke = BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL)
            // Размер изображения
            val dimension = calcDimension(wavesSize.pointsCount)
            // Сколько времени представляет один пиксел (может быть дробным, в зависимости от scale)
            val pxTime: Double = (1024.0 / wavesSize.waveSize.toDouble()) / scale /* одна волна - 1024мс */
            // Количество мс на маленькую клетку (в соответствии с https://ru.wikihow.com/%D0%BF%D1%80%D0%BE%D1%87%D0%B8%D1%82%D0%B0%D1%82%D1%8C-%D0%AD%D0%9A%D0%93)
            val cellTime = gridConfig.cellTime
            // Количество мс на большую клетку
            val bigCellTime = gridConfig.bigCellCellsCount * cellTime
            // Количество пикселей на малеьнкую клетку
            val cellPx = ceil(cellTime / pxTime).toInt()

            val bigCellPx = cellPx * (bigCellTime / cellTime)

            val chooseStroke = { position: Int ->
                if ((position % bigCellPx) == 0) {
                    g.stroke = bigCellStroke
                } else {
                    g.stroke = cellStroke
                }
            }
            // Рисуем вертикальные линии
            var x = 0
            do {
                chooseStroke(x)
                g.drawLine(x, 0, x, dimension.height)
                x += cellPx
            } while (x < dimension.width)
            var y = 0
            // Рисуем горризонтальные линии
            do {
                chooseStroke(y)
                val screenY = dimension.height - y
                g.drawLine(0, screenY, dimension.width, screenY)
                y += cellPx
            } while (y < dimension.height)

            // Рамку рисуем толще, иначе она пропадает
            g.stroke = bigCellStroke
            g.drawLine(0, 0, 0, dimension.height)
            g.drawLine(0, 0, dimension.width, 0)
            g.drawLine(dimension.width, 0, dimension.width, dimension.height)
            g.drawLine(0, dimension.height, dimension.width, dimension.height)
        } finally {
            g.color = prevColor
        }
    }


    private fun calcDimension(pointsCount: Int): Dimension {
        return Dimension(
                pointsCount,
                255
        )
    }


    class WavesSize(
            /**
             * Количество волн
             */
            val wavesCount: Int,

            /**
             * Размер волны (сколько точек содержит волна)
             */
            val waveSize: Int
    ) {

        /**
         * Сколько всего точек
         */
        val pointsCount: Int = wavesCount * waveSize

    }


    /**
     * Конфигурация сетки (аналог миллимитровки)
     */
    class GridConfig(
            /**
             * Сколько миллисекунд представлено одной клеткой
             */
            val cellTime: Int = 40,

            /**
             * Сколько клеток содержится в большой клетке
             */
            val bigCellCellsCount: Int = 5
    )

}