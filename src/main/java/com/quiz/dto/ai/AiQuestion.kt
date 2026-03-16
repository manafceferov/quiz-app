package com.quiz.dto.ai

data class AiQuestion(
    var questionText: String? = null,
    var answers: List<AiAnswer>? = null
)