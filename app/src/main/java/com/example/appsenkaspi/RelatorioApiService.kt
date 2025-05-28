package com.example.appsenkaspi

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

interface RelatorioApiService {
    @Headers("Content-Type: application/json")
    @POST("/relatorio/pdf")
    suspend fun gerarPdf(@Body dados: RelatorioRequest): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/relatorio/word")
    suspend fun gerarWord(@Body dados: RelatorioRequest): Response<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("/relatorio/excel")
    suspend fun gerarExcel(@Body dados: RelatorioRequest): Response<ResponseBody>

}