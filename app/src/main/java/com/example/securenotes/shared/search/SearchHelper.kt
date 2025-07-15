package com.example.securenotes.shared.search

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.example.securenotes.R

data class TextMatch(val start: Int, val end: Int)

class SearchHelper (
    private val context: android.content.Context,
    private val titleEditText: EditText,
    private val contentEditText: EditText,
    private val onMatchChanged: (String) -> Unit
) {

    private var matchPositions: List<TextMatch> = emptyList()
    private var currentMatchIndex = 0
    private var currentQuery = ""

    fun performSearch(query: String) {
        currentQuery = query
        if (query.isBlank()) return

        val titleText = titleEditText.text.toString()
        val contentText = contentEditText.text.toString()

        matchPositions = findAllMatches(titleText, query) +
                findAllMatches(contentText, query, offset = titleText.length + 1)

        if (matchPositions.isEmpty()) {
            onMatchChanged(getMatchIndexText())
            return
        }

        currentMatchIndex = 0
        applyHighlights()
        scrollToMatch()
        onMatchChanged(getMatchIndexText())
    }

    fun goToNextMatch() {
        if (matchPositions.isEmpty()) return
        currentMatchIndex = (currentMatchIndex + 1) % matchPositions.size
        applyHighlights()
        scrollToMatch()
        onMatchChanged(getMatchIndexText())
    }

    fun goToPreviousMatch() {
        if (matchPositions.isEmpty()) return
        currentMatchIndex = (currentMatchIndex - 1 + matchPositions.size) % matchPositions.size
        applyHighlights()
        scrollToMatch()
        onMatchChanged(getMatchIndexText())
    }

    fun clearSearch() {
        val originalTitle = titleEditText.text?.toString() ?: ""
        val originalContent = contentEditText.text?.toString() ?: ""

        titleEditText.setText(originalTitle)
        contentEditText.setText(originalContent)

        matchPositions = emptyList()
        currentMatchIndex = 0
        currentQuery = ""
    }

    fun hasMatches(): Boolean = matchPositions.isNotEmpty()

    fun getMatchIndexText(): String {
        return if (matchPositions.isEmpty()) ""
        else "${currentMatchIndex + 1}/${matchPositions.size}"
    }

    private fun scrollToMatch() {
        val match = matchPositions.getOrNull(currentMatchIndex) ?: return
        val start = match.start

        if (start < titleEditText.text.length) {
            titleEditText.clearFocus()
            animateScroll(titleEditText, start)
        } else {
            val offset = titleEditText.text.length + 1
            val contentStart = start - offset
            contentEditText.clearFocus()
            animateScroll(contentEditText, contentStart)
        }
    }

    private fun animateScroll(editText: EditText, position: Int) {
        editText.post {
            val layout = editText.layout ?: return@post
            val line = layout.getLineForOffset(position)
            val y = layout.getLineTop(line)
            editText.scrollTo(0, y)
        }
    }

    private fun applyHighlights() {
        val titleText = titleEditText.text.toString()
        val contentText = contentEditText.text.toString()
        highlightMatches(titleEditText, titleText, currentQuery)
        highlightMatches(contentEditText, contentText, currentQuery, offset = titleText.length + 1)
    }

    private fun highlightMatches(editText: EditText, text: String, query: String, offset: Int = 0) {
        val spannable = SpannableString(text)
        val regex = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)

        regex.findAll(text).forEach { match ->
            val globalMatch = TextMatch(match.range.first + offset, match.range.last + offset + 1)
            val isCurrent = globalMatch == matchPositions.getOrNull(currentMatchIndex)

            val highlightColor = if (isCurrent)
                ContextCompat.getColor(context, R.color.search_current_match_highlight) // highlighted current
            else
                ContextCompat.getColor(context, R.color.search_other_match_highlight) // other matches

            spannable.setSpan(
                BackgroundColorSpan(highlightColor),
                match.range.first,
                match.range.last + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        editText.setText(spannable)
        editText.clearFocus() // remove cursor
    }

    private fun findAllMatches(text: String, query: String, offset: Int = 0): List<TextMatch> {
        val regex = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)
        return regex.findAll(text).map {
            TextMatch(it.range.first + offset, it.range.last + offset + 1)
        }.toList()
    }
}
