package io.opencubes.dashagent

import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.impl.client.HttpClientBuilder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

class DashAgent(val method: String, var url: URL) {

    private val headers = mutableListOf<Header>()
    private var content: HttpEntity? = null

    constructor(method: String, url: String) : this(method, URL(url))

    fun query(name: String, value: String): DashAgent {
        url = URL("${
            url.protocol
        }://${
            url.host
        }${
            if (url.port != -1) url.port.toString() else ""
        }${
            url.path ?: '/'
        }${
            (if (url.query == null) "?" else "&") + "$name=$value"
        }")
        return this
    }

    fun query(nameValue: Pair<String, String>) = query(nameValue.first, nameValue.second)

    fun set(name: String, vararg values: String): DashAgent {
        val header = headers.find { it.name == name }
        when (header) {
            null -> headers += Header(name, *values)
            else -> header.add(*values)
        }

        return this
    }

    fun set(nameValue: Pair<String, String>) = set(nameValue.first, nameValue.second)

    fun content(multipart: Multipart): DashAgent {
        content = multipart.builder.build()
        return this
    }

    fun content(stream: InputStream, contentType: String = "application/octet-stream"): DashAgent {
        content = BasicHttpEntity().apply {
            setContentType(contentType)
            content = stream
        }
        return this
    }
    fun content(text: String, contentType: String?) =
            content(ByteArrayInputStream(text.toByteArray()), contentType ?: "text/plain")
    fun contentJson(json: String) = content(json, "application/json")
    fun content(file: File, contentType: String = Files.probeContentType(file.toPath())) =
            content(file.inputStream(), contentType)
    fun content(path: Path, contentType: String = Files.probeContentType(path)) = content(path.toFile(), contentType)

    fun send(): Response {
        val request = object : HttpEntityEnclosingRequestBase() {
            override fun getMethod() = this@DashAgent.method
        }

        request.uri = url.toURI()

        for ((name, value) in headers)
            request.addHeader(name, value)

        if (content != null)
            request.entity = content

        val response = HttpClientBuilder.create().build().execute(request)

        return Response(response)
    }
}
