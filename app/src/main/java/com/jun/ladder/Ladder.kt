package com.jun.ladder

/**
 * rows : 가로줄 행 개수
 * cols : 세로 기둥(참가자) 개수
 * hBars[row][col] == true  ⇒ row 행의 col‑col+1 사이 가로줄 존재
 * winnerCol : 맨 아래 당첨 열(0‑based)
 */
data class Ladder(
    val rows: Int,
    val cols: Int,
    val hBars: Array<BooleanArray>,
    val winnerCol: Int           // 단 1명 당첨
)