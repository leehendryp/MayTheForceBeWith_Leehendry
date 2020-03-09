package com.leehendryp.maytheforcebewithleehendry.core

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import retrofit2.http.Body
import retrofit2.http.POST

const val WEBHOOK_BASE_URL = "https://webhook.site/"
private const val WEBHOOK_ENDPOINT = "03e4c305-73aa-4d2c-8626-557687f2c393"

interface WebhookApi {
    @POST(WEBHOOK_ENDPOINT)
    suspend fun saveFavorite(@Body character: Character)
}