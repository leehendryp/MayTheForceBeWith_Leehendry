package com.leehendryp.maytheforcebewithleehendry.core

import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.CLIENT_ERROR
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.REDIRECT
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.SERVER_ERROR
import com.leehendryp.maytheforcebewithleehendry.core.ResponseType.SUCCESS
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
abstract class BaseNetworkTest {

    private val mockServer = MockWebServer()
    lateinit var api: StarWarsApi

    @Before
    open fun setUp() {
        val okHttpClient = OkHttpClient.Builder()
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(mockServer.url("/").toString())
            .build()

        api = retrofit.create(StarWarsApi::class.java)
    }

    @After
    open fun tearDown() = mockServer.shutdown()

    fun setResponse(type: ResponseType) {
        when (type) {
            SUCCESS -> setResponse(200)
            REDIRECT -> setResponse(300)
            CLIENT_ERROR -> setResponse(400)
            SERVER_ERROR -> setResponse(500)
        }
    }

    private fun setResponse(httpCode: Int) = with(MockResponse()) {
        setResponseCode(httpCode)
        setResponseBodyFrom(httpCode)
        mockServer.enqueue(this)
    }

    fun setResponse(type: ResponseType, jsonFileName: String) = with(MockResponse()) {
        setResponseCodeFrom(type)
        setBody(getStringJson(jsonFileName))
        mockServer.enqueue(this)
    }

    private fun MockResponse.setResponseCodeFrom(type: ResponseType) {
        setResponseCode(
            when (type) {
                SUCCESS -> 200
                REDIRECT -> 300
                CLIENT_ERROR -> 400
                SERVER_ERROR -> 500
            }
        )
    }

    private fun MockResponse.setResponseBodyFrom(httpCode: Int) {
        setBody(if (httpCode >= 400 || httpCode == 204) "" else "{}")
    }
}

enum class ResponseType {
    SUCCESS,
    REDIRECT,
    CLIENT_ERROR,
    SERVER_ERROR
}