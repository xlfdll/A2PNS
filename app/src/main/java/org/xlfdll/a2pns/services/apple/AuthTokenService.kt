package org.xlfdll.a2pns.services.apple

import org.xlfdll.a2pns.models.apple.APNSAuthToken
import retrofit2.http.GET
import retrofit2.http.Url

interface AuthTokenService {
    @GET
    suspend fun getAPNSAuthToken(@Url url: String): APNSAuthToken
}