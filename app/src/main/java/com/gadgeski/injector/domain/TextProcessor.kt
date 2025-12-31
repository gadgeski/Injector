package com.gadgeski.injector.domain

import javax.inject.Inject

class TextProcessor @Inject constructor() {

    fun process(text: String): ProcessedText {
        val title = generateAutoTitle(text)
        // Future decoration logic can go here
        return ProcessedText(
            title = title,
            content = text
        )
    }

    private fun generateAutoTitle(text: String): String {
        val firstLine = text.lines().firstOrNull() ?: ""
        return if (firstLine.length > 30) {
            firstLine.take(30) + "..."
        } else {
            firstLine.ifEmpty { "No Title" }
        }
    }
}

data class ProcessedText(
    val title: String,
    val content: String
)
