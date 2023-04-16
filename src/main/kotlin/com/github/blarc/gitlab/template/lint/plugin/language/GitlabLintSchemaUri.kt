package com.github.blarc.gitlab.template.lint.plugin.language

object GitlabLintSchema {

    /*
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/0
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/1/properties/local
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/2/properties/file/oneOf/0
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/3/properties/template
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/4/properties/component
    https://gitlab.com/.gitlab-ci.yml#/definitions/include_item/oneOf/5/properties/remote
    */

    const val SCHEMA_URI = "https://gitlab.com/gitlab-org/gitlab/-/raw/master/app/assets/javascripts/editor/schema/ci.json"
    const val SCHEMA_BASE = "https://gitlab.com/.gitlab-ci.yml#"
    const val INCLUDE_ITEM = "$SCHEMA_BASE/definitions/include_item/oneOf"
    enum class IncludeItem(val value: String) {
        BASIC("$INCLUDE_ITEM/0"),
        LOCAL("$INCLUDE_ITEM/1/properties/local"),
        FILE("$INCLUDE_ITEM/2/properties/file/oneOf/0"),
        TEMPLATE("$INCLUDE_ITEM/3/properties/template"),
        COMPONENT("$INCLUDE_ITEM/4/properties/component"),
        REMOTE("$INCLUDE_ITEM/5/properties/remote")
    }
}