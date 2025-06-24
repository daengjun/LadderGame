package com.jun.ladder

import kotlin.random.Random

object LadderGen {

    /**
     * @param rows           전체 행 수
     * @param cols           세로 기둥(참가자) 수  (2 이상)
     * @param allowedTop     위쪽 여백(행) – 이 행보다 위는 가로줄 금지
     * @param allowedBottom  아래쪽 여백(행) – 이 행보다 아래는 가로줄 금지
     */
    fun random(
        rows: Int,
        cols: Int,
        allowedTop: Int = 2,
        allowedBottom: Int = rows - 3
    ): Ladder {
        require(cols >= 2) { "cols must be >= 2" }
        require(rows >= 2) { "rows must be >= 2" }

        // 파라미터 보정
        val top    = allowedTop.coerceAtLeast(0)
        val bottom = allowedBottom.coerceAtMost(rows - 1)
        require(top <= bottom) { "allowedTop must be <= allowedBottom" }

        // 가로줄 배열 초기화  [row][col]  → col 은 왼쪽 기둥 인덱스
        val h = Array(rows) { BooleanArray(cols - 1) }
        val rnd = Random.Default

        /*──────────────────────────────────────
         *     인접 열(c, c+1) 사이에 최소 1개 보장
         *──────────────────────────────────────*/
        for (c in 0 until cols - 1) {
            var r: Int
            do {
                r = rnd.nextInt(top, bottom + 1)
            } while (           // 인접 충돌 방지
                (c > 0       && h[r][c - 1]) ||
                (c < cols-2 && h[r][c + 1])
            )
            h[r][c] = true
        }

        /*──────────────────────────────────────
         * 2) 추가 가로줄 랜덤 배치 (난이도 조절)
         *──────────────────────────────────────*/
        val candidates = mutableListOf<Pair<Int, Int>>()
        for (r in top..bottom) {
            for (c in 0 until cols - 1) {
                if (!h[r][c]) candidates += r to c      // 아직 비어 있는 칸만 후보
            }
        }

        candidates.shuffle(rnd)
        val targetCnt = (cols + 2).coerceAtMost(candidates.size) // 기존 로직 유지
        var placed = 0
        for ((row, col) in candidates) {
            if (col > 0       && h[row][col - 1]) continue
            if (col < cols-2 && h[row][col + 1]) continue
            h[row][col] = true
            if (++placed == targetCnt) break
        }

        /*──────────────────────────────────────
         * 3) 승리 열 무작위 선택
         *──────────────────────────────────────*/
        val winner = rnd.nextInt(cols)
        return Ladder(rows, cols, h, winner)
    }

    /** 시작 열 → 최종 도착 열 계산 */
    fun traverse(l: Ladder, start: Int): Int {
        var c = start
        for (r in 0 until l.rows) {
            when {
                c > 0 && l.hBars[r][c - 1] -> c--
                c < l.cols - 1 && l.hBars[r][c] -> c++
            }
        }
        return c
    }
}