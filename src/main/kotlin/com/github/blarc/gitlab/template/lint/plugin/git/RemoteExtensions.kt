package com.github.blarc.gitlab.template.lint.plugin.git

import git4idea.repo.GitRemote
import java.net.URI
import java.net.URL

val GitRemote.httpUrl : URI? get() {
    var url = firstUrl ?: return null

    url = url
        .trim()
        .removeSuffix(".git")

    // Do not try to remove the port if the URL uses the SSH protocol in the SCP syntax. For example
    // 'git@github.com:foo.git'. This syntax does not support port definitions. Attempting to remove the port
    // will result in an invalid URL when the repository name is made up of digits.
    if (!url.startsWith("git@")) {
        url = url.replace(":\\d{1,5}".toRegex(), ""); // remove the port
    }

    if (!url.startsWith("http")) {
        url = url
            .replace("git@", "")
            .replace("ssh://", "")
            .replace("git://", "")
            .replace(":", "/")

        url = "http://".plus(url)
    }

    val normalisedUrl = URL(url)

    return URI(normalisedUrl.protocol, normalisedUrl.host, normalisedUrl.path, normalisedUrl.query)
}
