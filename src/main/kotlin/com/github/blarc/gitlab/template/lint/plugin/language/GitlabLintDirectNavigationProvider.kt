package com.github.blarc.gitlab.template.lint.plugin.language;

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabFile
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.navigation.DirectNavigationProvider
import com.intellij.openapi.components.service
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import net.jimblackler.jsonschemafriend.Schema
import net.jimblackler.jsonschemafriend.SchemaStore
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLPsiElement
import org.jetbrains.yaml.psi.YAMLScalar
import java.net.URI

class GitlabLintDirectNavigationProvider : DirectNavigationProvider {
    private val schema: Schema = SchemaStore().loadSchema(URI.create("https://gitlab.com/gitlab-org/gitlab/-/raw/master/app/assets/javascripts/editor/schema/ci.json"))

    override fun getNavigationElement(element: PsiElement): PsiElement? {
        if (PlatformPatterns.psiElement(YAMLScalar::class.java).withLanguage(YAMLLanguage.INSTANCE).accepts(element)) {
            element.containingFile.virtualFile ?: return null

            if (!GitlabLintUtils.isGitlabYaml(element.containingFile.virtualFile)) {
                return null
            }

            val schemaForPath = schemaForPath(schema, pathForKey(element as YAMLScalar))

            schemaForPath ?: return null

            val psiFileFactory = PsiFileFactory.getInstance(element.project)

            val resolvedFile = resolveFile(schemaForPath.uri.toString(), element) ?: return null

            return psiFileFactory.createFileFromText(
                resolvedFile.fileName,
                element.language,
                resolvedFile.content
            )

        }

        return null
    }

    private fun pathForKey(keyValue: YAMLPsiElement): List<String> {
        // Get the tree of keys leading up to this one
        val keys: MutableList<String> = ArrayList()
        var currentKey: YAMLPsiElement? = keyValue
        while (currentKey != null) {
            currentKey.name?.let { keys.add(it) }
            currentKey = PsiTreeUtil.getParentOfType(currentKey, YAMLKeyValue::class.java)
        }

        // We have iterated from the inside out, so flip this around to get it in the correct direction for the ModelProvider
        keys.reverse()
        return keys.toList()
    }

    private fun schemaForPath(schema: Schema, path: List<String>): Schema? {
        if (path.isEmpty() && schema.pattern != null) {
            return schema
        }

        if (path.isNotEmpty() && schema.properties != null && schema.properties.containsKey(path[0])) {
            return schemaForPath(schema.properties[path[0]]!!, path.subList(1, path.size))
        }

        if (schema.items != null) {
            return schemaForPath(schema.items, path)
        }

        if (schema.oneOf != null) {
            return schema.oneOf.firstNotNullOfOrNull { schemaForPath(it, path) }
        }

        return null
    }

    private fun resolveFile(schema: String, element: YAMLScalar): GitlabFile? {
        val value = element.textValue
        val pipeline = element.project.service<Pipeline>()

        GitlabLintSchema.IncludeItem.values()
            .filter { it.value == schema }
            .first() {
                return pipeline.runResolveInclude(element.containingFile, value, it, getProperties(element)) as GitlabFile?
            }
        return null
    }

    private fun getProperties(element: PsiElement): Map<String, String> {
        return findParent(element).children
            .filterIsInstance<YAMLKeyValue>()
            .associate {
                it.keyText to it.valueText
            }
    }

    private fun findParent(element: PsiElement): PsiElement {
        if (element.children.isEmpty()) {
            return findParent(element.parent)
        }
        if (element.children.first() is YAMLKeyValue) {
            return element
        }
        return findParent(element.parent)
    }
}
