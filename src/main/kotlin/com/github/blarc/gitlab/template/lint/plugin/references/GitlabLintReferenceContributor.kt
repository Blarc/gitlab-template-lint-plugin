package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.psi.YAMLQuotedText
import org.jetbrains.yaml.psi.YAMLSequenceItem


class GitlabLintReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        val pattern = PlatformPatterns.psiElement(YAMLQuotedText::class.java)
            .withLanguage(YAMLLanguage.INSTANCE)
            .withParent(YAMLSequenceItem::class.java)

        registrar.registerReferenceProvider(
            pattern,
            GitlabLintReferenceProvider(),
            PsiReferenceRegistrar.DEFAULT_PRIORITY
        )
    }
}