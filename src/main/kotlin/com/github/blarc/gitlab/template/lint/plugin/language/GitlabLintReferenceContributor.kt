package com.github.blarc.gitlab.template.lint.plugin.language

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.psi.YAMLScalar
import java.net.URI


class GitlabLintReferenceContributor : PsiReferenceContributor() {
    private val schema: Schema = SchemaStore().loadSchema(URI.create("https://gitlab.com/gitlab-org/gitlab/-/raw/master/app/assets/javascripts/editor/schema/ci.json"))
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(YAMLScalar::class.java)
                .withLanguage(YAMLLanguage.INSTANCE),
            GitlabLintReferenceProvider(schema),
            PsiReferenceRegistrar.DEFAULT_PRIORITY
        )
    }
}