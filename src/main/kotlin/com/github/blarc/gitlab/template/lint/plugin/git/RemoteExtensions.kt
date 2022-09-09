package com.github.blarc.gitlab.template.lint.plugin.git

import git4idea.repo.GitRemote
import java.net.URI
import java.net.URL

val GitRemote.httpUrl: URI? get() {
    val url = firstUrl ?: return null

    val normalisedUrl = normaliseUrl(url)

    return URI(normalisedUrl.protocol, normalisedUrl.host, normalisedUrl.path, normalisedUrl.query)
}
val GitRemote.gitlabUrl : URI? get() {
    val url = firstUrl ?: return null

    val normalisedUrl = normaliseUrl(url)

    return URI(normalisedUrl.protocol, normalisedUrl.host, "/api/v4", null)
}

private fun normaliseUrl(url: String): URL {
    var tmpUrl = url
    tmpUrl = tmpUrl
        .trim()
        .removeSuffix(".git")

    // Do not try to remove the port if the URL uses the SSH protocol in the SCP syntax. For example
    // 'git@github.com:foo.git'. This syntax does not support port definitions. Attempting to remove the port
    // will result in an invalid URL when the repository name is made up of digits.
    if (!tmpUrl.startsWith("git@")) {
        tmpUrl = tmpUrl.replace(":\\d{1,5}".toRegex(), ""); // remove the port
    }

    if (!tmpUrl.startsWith("http")) {
        tmpUrl = tmpUrl
            .replace("git@", "")
            .replace("ssh://", "")
            .replace("git://", "")
            .replace(":", "/")

        tmpUrl = "http://".plus(tmpUrl)
    }

    return URL(tmpUrl)
}
