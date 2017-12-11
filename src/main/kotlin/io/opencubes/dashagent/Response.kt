package io.opencubes.dashagent

import com.google.gson.Gson
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse
import java.nio.charset.Charset

class Response(res: HttpResponse) {
    val status = res.statusLine.statusCode
    val message = res.statusLine.reasonPhrase
    val headers = mapOf<String, String>(*res.allHeaders.toList().map { it.name to it.value }.toTypedArray())
    val text = IOUtils.toString(res.entity.content, Charset.forName("utf-8"))
    inline fun <reified T> json(): T = Gson().fromJson(text, T::class.java)
    val stream = res.entity.content
}
