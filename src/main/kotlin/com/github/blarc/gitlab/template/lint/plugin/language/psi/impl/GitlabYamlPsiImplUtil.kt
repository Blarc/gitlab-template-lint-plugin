package com.github.blarc.gitlab.template.lint.plugin.language.psi.impl

import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlProperty
import com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlTokenTypes
import com.intellij.lang.ASTNode

public class GitlabYamlPsiImplUtil {

    companion object {

        @JvmStatic
        fun getKey(element: GitlabYamlProperty): String? {
            val keyNode: ASTNode? = element.node.findChildByType(GitlabYamlTokenTypes.KEY)
            return keyNode?.text?.replace("\\\\ ", " ")
        }

        @JvmStatic
        fun getValue(element: GitlabYamlProperty): String? {
            return element.node.findChildByType(GitlabYamlTokenTypes.VALUE)?.text
        }
    }

}
