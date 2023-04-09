package com.github.blarc.gitlab.template.lint.plugin.references

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiReferenceBase
import org.gitlab4j.api.GitLabApi

class GitlabLintReference(element: PsiElement, textRange: TextRange) :
    PsiReferenceBase<PsiElement>(element, textRange) {
    override fun resolve(): PsiElement? {
        val psiFileFactory = PsiFileFactory.getInstance(element.project)

        GitLabApi("hostUrl", AppSettings.instance.getGitlabToken("hostUrl"))

        return psiFileFactory.createFileFromText("filename.yaml", element.language, "hello: world")
    }
}