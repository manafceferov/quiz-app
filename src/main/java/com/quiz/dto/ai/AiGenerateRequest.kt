package com.quiz.dto.ai

import jakarta.validation.constraints.NotBlank

data class AiGenerateRequest(

    @field:NotBlank(message = "Prompt boş ola bilməz")
    var prompt: String? = null
)