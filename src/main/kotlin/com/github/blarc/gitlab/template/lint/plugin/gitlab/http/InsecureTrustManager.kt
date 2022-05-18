package com.github.blarc.gitlab.template.lint.plugin.gitlab.http

import java.security.cert.X509Certificate
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * A trust manager that does not validate certificate chains.
 */
class InsecureTrustManager : X509TrustManager {
    override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        // noop
    }

    override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
        // noop
    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }

    companion object {
        fun asList(): Array<TrustManager> {
            return arrayOf(InsecureTrustManager())
        }
    }
}
