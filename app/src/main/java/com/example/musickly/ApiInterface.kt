package com.example.musickly

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {

    @Headers("x-rapidapi-key:3243b75122mshbe1e8df6b83feeap16780cjsnb98a50fd7a39",
            "x-rapidapi-host:deezerdevs-deezer.p.rapidapi.com")
    @GET("search")
    fun getDate(@Query("q") query: String) : Call<MyData>

}
