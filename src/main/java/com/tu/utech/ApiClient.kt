package com.tu.utech

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getBanxicoService(): BanxicoService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.banxico.org.mx/SieAPIRest/service/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(BanxicoService::class.java)
}

class Retrofit {

}

class GsonConverterFactory {
    companion object {
        fun create() {

        }
    }

}

fun fetchDollarExchangeRate(context: Context, onResult: (String) -> Unit) {
    val apiKey = "e00773bd4267fac4b464e7fe01e7f807928a4f827e70b24da335c94af7e78035"
    val service = getBanxicoService()
    val call = service.getDollarExchangeRate(apiKey)

    call.enqueue(object : Callback<DollarResponse> {
        override fun onResponse(call: Call<DollarResponse>, response: Response<DollarResponse>) {
            if (response.isSuccessful) {
                val data = response.body()?.bmx?.series?.firstOrNull()?.datos?.firstOrNull()
                val rate = data?.dato ?: "No disponible"
                onResult(rate)
            } else {
                Toast.makeText(context, "Error al obtener el tipo de cambio", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<DollarResponse>, t: Throwable) {
            Toast.makeText(context, "Error en la solicitud", Toast.LENGTH_SHORT).show()
        }
    })
}
