package com.tu.utech

data class DollarResponse(
    val bmx: Bmx
)

data class Bmx(
    val series: List<Serie>
)

data class Serie(
    val idSerie: String,
    val titulo: String,
    val datos: List<Data>
)

data class Data(
    val fecha: String,
    val dato: String
)
