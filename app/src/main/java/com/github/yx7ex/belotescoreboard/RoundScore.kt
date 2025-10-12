package com.github.yx7ex.belotescoreboard

data class RoundScore(
    val roundNumber: Int,
    val team1Score: String, // Changed from Int to String
    val team2Score: String, // Changed from Int to String
    val bid: String
)
