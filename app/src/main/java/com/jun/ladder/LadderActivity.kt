package com.jun.ladder

import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.centerm.myapplication.R


class LadderActivity : AppCompatActivity() {

    private lateinit var ladder: Ladder
    private lateinit var ladderView: LadderView

    private var cols = 0      // 참가 인원(세로 기둥) 수
    private var currCol = 0   // 현재 애니메이션 중인 참가자

    /** 한 참가자 내려가는데 걸리는 시간(ms) */
    private val animDuration = 5000L

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ladder)

        // 전달받은 인원 수
        cols = intent.getIntExtra("cols", 3)

        // 사다리 데이터 생성
        ladder = LadderGen.random(rows = 15, cols = cols)


        // 커스텀 뷰 바인딩
        ladderView = findViewById(R.id.ladderView)
        ladderView.ladder = ladder
        ladderView.participantNames = List(cols) { idx -> "${idx + 1}번" }

        // 레이아웃 완료 후 애니메이션 시작
        ladderView.post { startSequence() }
    }

    /** 1번 → 2번 → 3번 … 순서로 내려가기 */
    private fun startSequence() {
        currCol = 0
        playNext()
    }

    /** 현재 참가자 내려보내고 끝나면 다음 참가자로 재귀 호출 */
    private fun playNext() {
        if (currCol >= cols) {        // 모두 끝났으면
            showResult(); return
        }

        // 현재 참가자의 경로 목록 계산
        ladderView.path = buildPath(currCol)

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = animDuration
            addUpdateListener { ladderView.progress = it.animatedValue as Float }
            doOnEnd { currCol++; playNext() }
            start()
        }
    }

    /** LadderView 의 colGap/rowGap 을 그대로 사용 + 마지막 행 바로 위에서 멈춤 */
    private fun buildPath(startCol: Int): List<Pair<Float, Float>> {
        val colGap = ladderView.colGap
        val rowGap = ladderView.rowGap

        val pts = mutableListOf<Pair<Float, Float>>()
        var c = startCol
        var y = 0f

        /* rows-1 번만 내려가면 y == drawH (결과 박스 바로 위) */
        repeat(ladder.rows - 1) { r ->
            pts += c * colGap to y
            when {
                c > 0 && ladder.hBars[r][c - 1] -> {
                    c--; pts += c * colGap to y
                }

                c < ladder.cols - 1 && ladder.hBars[r][c] -> {
                    c++; pts += c * colGap to y
                }
            }
            y += rowGap
        }
        pts += c * colGap to y
        return pts
    }

    private fun showResult() {
        // 액티비티 벗어나면 호출 x
        if (isFinishing || isDestroyed) return

        val winnerEndCol = ladder.winnerCol
        val winnerIdx = (0 until cols).firstOrNull {
            LadderGen.traverse(ladder, it) == winnerEndCol
        } ?: -1

        val msg = if (winnerIdx >= 0)
            "\uD83E\uDD55 ${winnerIdx + 1} 번 참가자가 당첨입니다!"
        else
            "당첨자를 찾을 수 없습니다 😅"

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_result)
            .setMessage(msg)
            .setPositiveButton(R.string.dialog_btn_ok, null)
            .show()
    }
}