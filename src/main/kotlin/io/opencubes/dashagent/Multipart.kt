package io.opencubes.dashagent

import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class Multipart {
    internal val builder = MultipartEntityBuilder.create()

    fun add(name: String, filename: String, stream: InputStream, contentType: String = "application/octet-stream"): Multipart {
        builder.addBinaryBody(name, stream, ContentType.create(contentType), filename)
        return this
    }
    fun add(name: String, text: String, contentType: String = "text/plain"): Multipart {
        builder.addTextBody(name, text, ContentType.create(contentType))
        return this
    }

    fun addJson(name: String, json: String) = add(name, json, "application/json")
    fun add(name: String, file: File, contentType: String = Files.probeContentType(file.toPath())) =
            add(name, file.name, file.inputStream(), contentType)
    fun add(name: String, path: Path, contentType: String = Files.probeContentType(path)) =
            add(name, path.toFile().name, path.toFile().inputStream(), contentType)

    fun contentType(contentType: String): Multipart {
        builder.seContentType(ContentType.create(contentType))
        return this
    }
}