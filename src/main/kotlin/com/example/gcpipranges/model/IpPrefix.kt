package com.example.gcpipranges.model

data class IpPrefix(
    val ipv4Prefix: String? = null,
    val ipv6Prefix: String? = null,
    val scope: String
)
