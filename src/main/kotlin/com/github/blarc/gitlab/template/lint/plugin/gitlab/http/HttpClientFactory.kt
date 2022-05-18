package com.github.blarc.gitlab.template.lint.plugin.gitlab.http

import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class HttpClientFactory private constructor() {
    val httpClient: OkHttpClient
        get() = OkHttpClient()
    val insecureHttpClient: OkHttpClient
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, InsecureTrustManager.asList(), SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, InsecureTrustManager.asList()[0] as X509TrustManager)
                .hostnameVerifier { s, sslSession -> true }
                .build()
        } catch (e: NoSuchAlgorithmException) {
            throw HttpClientException("Cannot create insecure HTTP client: " + e.message, e)
        } catch (e: KeyManagementException) {
            throw HttpClientException("Cannot create insecure HTTP client: " + e.message, e)
        }

    companion object {
        val instance = HttpClientFactory()
    }
}
