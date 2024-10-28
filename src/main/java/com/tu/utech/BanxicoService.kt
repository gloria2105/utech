package com.tu.utech

import retrofit2.http.Header
import retrofit2.Call
import retrofit2.http.GET

interface BanxicoService {
    @GET("series/SF43718/datos/oportuno")
    fun getDollarExchangeRate(@Header("Bmx-Token") apiKey: String): retrofit2.Call<DollarResponse>
}
