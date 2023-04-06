package ai.kore.androidsdk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

internal object KoreAiService {
    //private val _baseUrl = "https://bots.kore.ai/chatbot/v2/webhook/" + KoreAiSDK.botId
    private val _baseUrl = ""

    @OptIn(DelicateCoroutinesApi::class)
    fun sendMessage(message: String): LiveData<String?> {
        val returnData = MutableLiveData<String?>()
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val connection = _setupConnection(message)
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    returnData.postValue(connection.inputStream.bufferedReader().readText())
                } else {
                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                }
            } catch (ex: Exception) {
                returnData.postValue(null)
            }
        }
        return returnData
    }

    private fun _setupConnection(message: String): HttpURLConnection {
        val url = URL(_baseUrl)
        val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.setReadTimeout(10000)
        httpURLConnection.setConnectTimeout(15000)
        httpURLConnection.setRequestMethod("POST")
        httpURLConnection.setRequestProperty("Content-Type", "application/json")
        httpURLConnection.setRequestProperty("Authorization", "Bearer ${_createJwtToken()}")
        httpURLConnection.setRequestProperty("Accept", "application/json")
        httpURLConnection.doInput = true
        httpURLConnection.doOutput = true

        val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
        outputStreamWriter.write(_body(message))
        outputStreamWriter.flush()

        return httpURLConnection
    }

    private fun _createJwtToken(): String? {
        val headers : HashMap<String, Any?> = HashMap<String, Any?>()
        headers.put("alg", "HS256")
        headers.put("typ", "JWT")

        val claims : HashMap<String, Any?> = HashMap<String, Any?>()
        //claims.put("appId", KoreAiSDK.clientId)

        return try {
            Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                //.signWith(Keys.hmacShaKeyFor(KoreAiSDK.clientSecret!!.toByteArray()), SignatureAlgorithm.HS256)
                .compact()
        } catch (_: Exception) {
            null
        }
    }

    private fun _body(message: String): String {
        return "{" +
                "    \"message\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"val\": \"" + message + "\"\n" +
                "    },\n" +
                "    \"from\": {\n" +
                "        \"id\": \"" + KoreAiSDK.userId + "\"\n" +
                "    }\n" +
                "}"
    }
}