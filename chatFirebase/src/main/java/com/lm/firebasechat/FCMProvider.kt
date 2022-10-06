package com.lm.firebasechat

import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

class FCMProvider() {

    fun sendRemoteMessage(token: String, apiKey: String) {
        val headers = java.util.HashMap<String, String>()
        headers["Authorization"] = "key=${apiKey}"
        headers["Content-Type"] = "application/json"
        fCMApi.sendRemoteMessage(
            JSONObject().put("registration_ids", JSONArray().put(token)).toString(),
            headers
        )?.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {}
            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }

    private val fCMApi: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }

    interface ApiInterface {
        @POST("send")
        fun sendRemoteMessage(
            @Body remoteBody: String?, @HeaderMap headers: HashMap<String, String>
        ): Call<String?>?
    }
}