package com.loess.geosurveymap.kinde

import com.loess.geosurveymap.exceptions.HttpClientException
import org.springframework.http.HttpInputMessage
import org.springframework.http.client.ClientHttpResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

@Service
class KindeUserService(
    //private val restClient: RestClient
) {

//    @Async
//    fun deleteUser(userId: String) {
//        restClient.delete()
//            .uri { uriBuilder ->
//                uriBuilder
//                    .queryParam("id", userId)
//                    .build()
//            }
//            .retrieve()
//            .onStatus({ it.isError }) { _, response ->
//                throw HttpClientException(clientResponse(response))
//            }
//    }

    @Throws(IOException::class)
    fun getBodyAsString(httpInputMessage: HttpInputMessage): String {
        val bodyStream = httpInputMessage.body
        BufferedReader(InputStreamReader(bodyStream, StandardCharsets.UTF_8)).use { reader ->
            return reader.lines().collect(Collectors.joining("\n"))
        }
    }

    private fun clientResponse(response: ClientHttpResponse) =
        "Client error: ${response.statusCode} - ${response.statusText}, ${getBodyAsString(response)}"
}