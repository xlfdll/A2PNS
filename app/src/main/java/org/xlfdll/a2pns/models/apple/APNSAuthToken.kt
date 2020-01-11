package org.xlfdll.a2pns.models.apple

import com.google.gson.annotations.SerializedName

class APNSAuthToken(
    // Must add @SerializedName annotations for all variables used by Gson
    // In release builds, obfuscation will replace all variable names
    // Thus, the default serialized names will not be correct
    @SerializedName("id")
    var id: String,
    @SerializedName("iat")
    var iat: Long,
    @SerializedName("jwt")
    var jwt: String
)