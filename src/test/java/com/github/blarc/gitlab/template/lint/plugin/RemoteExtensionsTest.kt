package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.git.gitlabUrl
import git4idea.repo.GitRemote
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class RemoteExtensionsTest {

    companion object {
        @JvmStatic
        fun urlsProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                GitRemote("test", listOf("git@gitlab.medius.si:medius/ci-tools/medius-cd.git"), listOf(), listOf(), listOf()),
                "http://gitlab.medius.si/api/v4",
            )
        )
    }

    @ParameterizedTest
    @MethodSource("urlsProvider")
    fun testNormaliseUrl(remote: GitRemote, expectedNormalisedUrl: String) {
        assertEquals(expectedNormalisedUrl, remote.gitlabUrl.toString())
    }

}
