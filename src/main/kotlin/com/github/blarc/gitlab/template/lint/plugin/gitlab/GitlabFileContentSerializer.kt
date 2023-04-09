package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.serializer

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = String::class)
class GitlabFileContentSerializer: KSerializer<String> {
    override val descriptor = String.serializer().descriptor

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: String) {
        encoder.encodeString(value)
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): String {
        return decoder.decodeString()
    }

}