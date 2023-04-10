package com.github.blarc.gitlab.template.lint.plugin.references

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLScalar
import java.net.URI


class GitlabLintReferenceContributor : PsiReferenceContributor() {
    private val schema: Schema = SchemaStore().loadSchema(URI.create("https://gitlab.com/gitlab-org/gitlab/-/raw/master/app/assets/javascripts/editor/schema/ci.json"))
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {

//        val pattern = PlatformPatterns.psiElement(YAMLScalar::class.java)
//            .withLanguage(YAMLLanguage.INSTANCE)
//            .withParent(YAMLSequenceItem::class.java)
//            .withSuperParent(3, PlatformPatterns.psiElement(YAMLKeyValue::class.java)
//                .withLanguage(YAMLLanguage.INSTANCE)
//                .withName("file")
//            )
//            .withSuperParent(7, PlatformPatterns.psiElement(YAMLKeyValue::class.java)
//                .withLanguage(YAMLLanguage.INSTANCE)
//                .withName("include")
//            )

        val pattern = PlatformPatterns.psiElement(YAMLScalar::class.java)
            .withLanguage(YAMLLanguage.INSTANCE)
            .withAncestor(
                30, PlatformPatterns.psiElement(YAMLKeyValue::class.java)
                    .withLanguage(YAMLLanguage.INSTANCE)
                    .withName("include")
            )


        val singlePattern = PlatformPatterns.psiElement(YAMLScalar::class.java)
            .withLanguage(YAMLLanguage.INSTANCE)
            .withParent(
                PlatformPatterns.psiElement(YAMLKeyValue::class.java)
                    .withLanguage(YAMLLanguage.INSTANCE)
                    .withName("include")
            )


        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(YAMLScalar::class.java)
                .withLanguage(YAMLLanguage.INSTANCE),
            GitlabLintReferenceProvider(schema),
            PsiReferenceRegistrar.DEFAULT_PRIORITY
        )
    }
}