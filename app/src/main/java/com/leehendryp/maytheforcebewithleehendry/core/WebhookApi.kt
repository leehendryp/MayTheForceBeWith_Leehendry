package com.leehendryp.maytheforcebewithleehendry.core

import com.leehendryp.maytheforcebewithleehendry.feed.domain.Character
import retrofit2.http.Body
import retrofit2.http.POST

const val WEBHOOK_BASE_URL = "https://webhook.site/"
private const val WEBHOOK_ENDPOINT = "f78642bb-50b6-44de-b1f3-8153cf6a2c61"

interface WebhookApi {
    @POST(WEBHOOK_ENDPOINT)
    suspend fun saveFavorite(@Body character: Character)
}