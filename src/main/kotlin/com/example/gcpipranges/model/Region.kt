package com.example.gcpipranges.model

enum class Region(val code: String, val scopePrefixes: List<String>) {
    EU("EU", listOf("europe-")),
    US("US", listOf("us-")),
    ME("ME", listOf("me-")),
    NA("NA", listOf("northamerica-", "us-")),
    SA("SA", listOf("southamerica-")),
    AS("AS", listOf("asia-")),
    AF("AF", listOf("africa-")),
    AUS("AUS", listOf("australia-"));
    
    companion object {
        fun fromString(code: String): Region? = 
            values().find { it.code.equals(code, ignoreCase = true) }
    }
}
