package com.jun.ladder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withTranslation
import kotlin.math.floor
import kotlin.math.roundToInt

class LadderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var participantNames: List<String> = emptyList()
        set(value) {
            field = value; invalidate()
        }

    var ladder: Ladder? = null
        set(value) {
            field = value; invalidate()
        }

    var path: List<Pair<Float, Float>> = emptyList()
    var progress = 0f
        set(value) {
            field = value; invalidate()
        }

    var colGap = 0f; private set
    var rowGap = 0f; private set
    private var drawTop = 0f; private set

    private val marginPx get() = (24f * resources.displayMetrics.density).roundToInt()

    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; strokeWidth = 6f; strokeCap = Paint.Cap.ROUND
    }
    private val winBox = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9800")            // 주황
        style = Paint.Style.STROKE; strokeWidth = 6f
    }
    private val loseBox = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9800")            // 주황(동일 테두리)
        style = Paint.Style.STROKE; strokeWidth = 6f
    }
    private val fillWhite = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; style = Paint.Style.FILL
    }
    private val txt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; textSize = 36f; textAlign = Paint.Align.CENTER
    }
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9800"); strokeWidth = 6f; strokeCap = Paint.Cap.ROUND
    }

    /* 하단 박스 크기·여백 */
    private val boxHeight = 80f
    private val boxHalfW = 40f
    private val boxGap = 24f
    private val padTopExtra get() = boxHeight + boxGap
    private val padBottomExtra get() = boxHeight + boxGap

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val l = ladder ?: return

        val m = marginPx.toFloat()
        val left = m
        val top = m + padTopExtra
        val right = width - m
        val bottom = height - m - padBottomExtra

        val drawW = right - left
        val drawH = bottom - top
        colGap = drawW / (l.cols - 1)
        rowGap = drawH / (l.rows - 1)
        drawTop = top

        canvas.withTranslation(left, top) {

            // 세로줄
            repeat(l.cols) { c ->
                val x = c * colGap
                drawLine(x, 0f, x, drawH, line)
            }

            // 가로줄
            l.hBars.forEachIndexed { r, row ->
                val y = r * rowGap
                row.forEachIndexed { c, has ->
                    if (has) drawLine(c * colGap, y, (c + 1) * colGap, y, line)
                }
            }

            // 상단 텍스트 + 하단 결과 박스
            repeat(l.cols) { c ->
                val x = c * colGap
                drawText("${c + 1}", x, -boxGap - 16f, txt)
                val by = drawH + boxGap
                val win = c == l.winnerCol
                val leftBx = x - boxHalfW
                val rightBx = x + boxHalfW
                val bottomBx = by + boxHeight

                // 흰 배경 채우기
                drawRect(leftBx, by, rightBx, bottomBx, fillWhite)
                // 테두리
                drawRect(leftBx, by, rightBx, bottomBx, if (win) winBox else loseBox)

                /* ─ 이모지 출력 ─ */
                drawText(
                    if (win) "\uD83E\uDD55" /*🥕*/ else "\uD83D\uDC30" /*🐰*/,
                    x,
                    by + boxHeight - 25f,
                    txt
                )
            }
            drawPathProgress()
        }
    }

    /** 진행률(progress)만큼 빨간 경로 그리기 */
    private fun Canvas.drawPathProgress() {
        if (path.isEmpty()) return

        val t = progress * (path.size - 1)         // 실수 인덱스
        val seg = floor(t).toInt().coerceAtMost(path.size - 2)

        for (i in 0 until seg) {
            val (x1, y1) = path[i]
            val (x2, y2) = path[i + 1]
            drawLine(x1, y1, x2, y2, pathPaint)
        }

        if (seg < path.size - 1) {
            val f = t - seg
            val (sx, sy) = path[seg]
            val (ex, ey) = path[seg + 1]
            drawLine(sx, sy, sx + (ex - sx) * f, sy + (ey - sy) * f, pathPaint)
        }
    }
}