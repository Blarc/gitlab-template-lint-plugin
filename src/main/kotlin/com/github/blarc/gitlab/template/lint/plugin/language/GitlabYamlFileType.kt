package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class GitlabYamlFileType : LanguageFileType(GitlabYamlLanguage.INSTANCE) {

    companion object {
        val INSTANCE = GitlabYamlFileType()
    }

    override fun getName(): String {
        return "Gitlab Yaml"
    }

    override fun getDescription(): String {
        return "Gitlab CI yaml file"
    }

    override fun getDefaultExtension(): String {
        return "gitlab-ci.yml"
    }

    override fun getIcon(): Icon? {
        return GitlabLintIcons.GITLAB_YAML_ICON
    }
}
