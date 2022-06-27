package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlFile
import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class GitlabYamlParserDefinition : ParserDefinition {

    val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    val COMMENTS = TokenSet.create(GitlabYamlTokenTypes.COMMENT)


    val FILE = IFileElementType(GitlabYamlLanguage.INSTANCE)


    override fun createLexer(project: Project?): Lexer {
        return GitlabYamlLexerAdapter()
    }

    override fun createParser(project: Project?): PsiParser {
        return GitlabYamlParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return COMMENTS
    }

    override fun getWhitespaceTokens(): TokenSet {
        return WHITE_SPACES
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return GitlabYamlTokenTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return GitlabYamlFile(viewProvider)
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements {
        return ParserDefinition.SpaceRequirements.MAY
    }
}
