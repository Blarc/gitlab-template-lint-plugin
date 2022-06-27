package com.github.blarc.gitlab.template.lint.plugin.language.highlighter

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlLexerAdapter
import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType


class GitlabYamlSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        val SEPARATOR: TextAttributesKey = createTextAttributesKey("SIMPLE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val KEY: TextAttributesKey = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD)
        val VALUE: TextAttributesKey = createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING)
        val COMMENT: TextAttributesKey = createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
        private val SEPARATOR_KEYS = arrayOf(SEPARATOR)
        private val KEY_KEYS = arrayOf(KEY)
        private val VALUE_KEYS = arrayOf(VALUE)
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val EMPTY_KEYS = arrayOfNulls<TextAttributesKey>(0)
    }

    override fun getHighlightingLexer(): Lexer {
        return GitlabYamlLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<out TextAttributesKey?> {
            when (tokenType) {
                GitlabYamlTokenTypes.SEPARATOR -> {
                    return SEPARATOR_KEYS;
                }
                GitlabYamlTokenTypes.KEY -> {
                    return KEY_KEYS;
                }
                GitlabYamlTokenTypes.VALUE -> {
                    return VALUE_KEYS;
                }
                GitlabYamlTokenTypes.COMMENT -> {
                    return COMMENT_KEYS;
                }
                TokenType.BAD_CHARACTER -> {
                    return BAD_CHAR_KEYS;
                }
                else -> return EMPTY_KEYS
            }
    }
}
