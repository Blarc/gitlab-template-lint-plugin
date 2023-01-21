package com.github.blarc.gitlab.template.lint.plugin.extensions

import com.intellij.util.ui.ColumnInfo

fun <T> createColumn(name: String, formatter: (T) -> String) : ColumnInfo<T, String> {
    return object : ColumnInfo<T, String>(name) {
        override fun valueOf(item: T): String {
            return formatter(item)
        }
    }
}