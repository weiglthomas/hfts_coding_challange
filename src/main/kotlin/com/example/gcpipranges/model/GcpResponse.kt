package com.example.gcpipranges.model

data class GcpResponse(
    val syncToken: String,
    val prefixes: List<IpPrefix>
)
