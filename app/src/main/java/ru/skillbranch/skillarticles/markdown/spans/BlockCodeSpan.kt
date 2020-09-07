package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.*
import android.text.Spanned
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import ru.skillbranch.skillarticles.markdown.Element


class BlockCodeSpan(
    @ColorInt
    private val textColor: Int,
    @ColorInt
    private val bgColor: Int,
    @Px
    private val cornerRadius: Float,
    @Px
    private val padding: Float,
    private val type: Element.BlockCode.Type
) : ReplacementSpan() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var rect = RectF()
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                /*paint.forBackground {
                   rect.set(0f,
                       top + padding,
                        canvas.width.toFloat(),
                        bottom - padding)
                   canvas.drawRoundRect(
                       rect,
                       cornerRadius,
                       cornerRadius,
                       paint
                   )
                }*/

                paint.forText {
                    canvas.drawText(text, 0, text.length, padding, y + padding, paint)
                }
            }
            Element.BlockCode.Type.START -> {}
            Element.BlockCode.Type.MIDDLE -> {}
            Element.BlockCode.Type.END -> {}
        }
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        when (type) {
            Element.BlockCode.Type.SINGLE -> {
                paint.forText {
                    val measureText = paint.measureText(text.toString(), start, end)
                    fm?.ascent = (measureText * 0.85f- 2*padding).toInt()
                    fm?.descent = (measureText * 0.85f + 2*padding).toInt()
                }

                return 0
            }
            Element.BlockCode.Type.START -> {return 0}
            Element.BlockCode.Type.MIDDLE -> {return 0}
            Element.BlockCode.Type.END -> {return 0}
            else -> error("Invalid Type")
        }
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color
        color = textColor
        block()
        color = oldColor
    }

    private inline fun Paint.forBackground(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style

        color = bgColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }
}
