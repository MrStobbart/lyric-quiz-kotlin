package com.example.marekmeyer.lyricquiz_kotlin.models


data class Quiz(
        var questions: List<Question>,
        var questionCounter: Int = 0
)


