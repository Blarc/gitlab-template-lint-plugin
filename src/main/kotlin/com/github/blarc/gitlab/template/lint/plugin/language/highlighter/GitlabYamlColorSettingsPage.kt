package com.github.blarc.gitlab.template.lint.plugin.language.highlighter

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlFileType
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class GitlabYamlColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("Key", GitlabYamlSyntaxHighlighter.KEY),
        AttributesDescriptor("Separator", GitlabYamlSyntaxHighlighter.SEPARATOR),
        AttributesDescriptor("Value", GitlabYamlSyntaxHighlighter.VALUE),
        AttributesDescriptor("Bad value", GitlabYamlSyntaxHighlighter.BAD_CHARACTER)
//        It is supported to group related attributes like operators or braces by separating the nodes with //, e.g.:
//        AttributesDescriptor("Operators//Plus", MySyntaxHighlighter.PLUS),
//        AttributesDescriptor("Operators//Minus", MySyntaxHighlighter.MINUS),
//        AttributesDescriptor("Operators//Advanced//Sigma", MySyntaxHighlighter.SIGMA),
//        AttributesDescriptor("Operators//Advanced//Pi", MySyntaxHighlighter.PI),
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> {
        return DESCRIPTORS
    }

    override fun getColorDescriptors(): Array<ColorDescriptor> {
        return ColorDescriptor.EMPTY_ARRAY
    }

    override fun getDisplayName(): String {
        return "Gitlab YAML"
    }

    override fun getIcon(): Icon? {
        return GitlabYamlFileType.INSTANCE.icon
    }

    override fun getHighlighter(): SyntaxHighlighter {
        return GitlabYamlSyntaxHighlighter()
    }

    override fun getDemoText(): String {
        return "# You are reading the \".properties\" entry.\n" +
                "! The exclamation mark can also mark text as comments.\n" +
                "website = https://en.wikipedia.org/\n" +
                "language = English\n" +
                "# The backslash below tells the application to continue reading\n" +
                "# the value onto the next line.\n" +
                "message = Welcome to \\\n" +
                "          Wikipedia!\n" +
                "# Add spaces to the key\n" +
                "key\\ with\\ spaces = This is the value that could be looked up with the key \"key with spaces\".\n" +
                "# Unicode\n" +
                "tab : \\u0009";
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? {
        return null
    }
}
