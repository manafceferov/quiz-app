package com.quiz.dto.ai

data class AiQuizResponse(
    var topicName: String? = null,
    var questions: List<AiQuestion>? = null
)