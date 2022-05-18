package com.github.blarc.gitlab.template.lint.plugin.gitlab.http

class HttpClientException : RuntimeException {
    constructor() : super() {}
    constructor(msg: String?) : super(msg) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
    constructor(cause: Throwable?) : super(cause) {}
}
