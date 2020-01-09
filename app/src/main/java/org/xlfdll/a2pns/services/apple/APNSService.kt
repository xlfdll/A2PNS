package org.xlfdll.a2pns.services.apple

import org.xlfdll.a2pns.models.apple.APNSRequest
import retrofit2.http.*

interface APNSService {
    // All parameters need to have annotations (e.g. @Body)
    @Headers("apns-push-type: alert")
    @POST("3/device/{device_token}")
    suspend fun sendNotificationRequest(
        @Path("device_token") deviceToken: String,
        @Header("Authorization") authToken: String,
        @Header("apns-topic") id: String,
        @Body request: APNSRequest
    )
}