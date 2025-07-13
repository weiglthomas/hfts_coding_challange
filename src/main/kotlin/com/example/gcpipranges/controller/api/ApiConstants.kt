package com.example.gcpipranges.controller.api

object ApiConstants {
    const val BASE_PATH = "/api"
    const val IP_RANGES_PATH = "/ip-ranges"
    const val FULL_IP_RANGES_PATH = "$BASE_PATH$IP_RANGES_PATH"
    
    object Parameters {
        const val REGION = "region"
        const val IP_VERSION = "ipVersion"
    }
    
    object DefaultValues {
        const val DEFAULT_REGION = "ALL"
    }
}
