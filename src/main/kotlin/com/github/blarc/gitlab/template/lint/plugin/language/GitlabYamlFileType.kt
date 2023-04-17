package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.Icons
import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.yaml.YAMLLanguage
import javax.swing.Icon

class GitlabYamlFileType : LanguageFileType(YAMLLanguage.INSTANCE, true) {
    override fun getName(): String {
        return "Gitlab Yaml"
    }

    override fun getDescription(): String {
        return "Gitlab Yaml"
    }

    override fun getDefaultExtension(): String {
        return "gitlab-ci.yml"
    }

    override fun getIcon(): Icon {
        return Icons.GITLAB_LOGO
    }
}