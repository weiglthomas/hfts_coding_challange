package com.example.gcpipranges.service

import com.example.gcpipranges.model.GcpResponse
import com.example.gcpipranges.model.IpPrefix
import com.example.gcpipranges.model.Region
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class IpRangesService(
    private val webClient: WebClient,
    @Value("\${gcp.ip-ranges.url}") private val gcpUrl: String
) {
    
    suspend fun getIpRanges(region: String, ipVersion: String?): List<String> {
        val gcpData = fetchGcpData()
        val filteredByRegion = filterByRegion(gcpData.prefixes, region)
        return filterByIpVersion(filteredByRegion, ipVersion)
    }
    
    @Cacheable(value = ["gcp-ip-ranges"], key = "#result.syncToken")
    private suspend fun fetchGcpData(): GcpResponse {
        return webClient.get()
            .uri(gcpUrl)
            .retrieve()
            .bodyToMono(GcpResponse::class.java)
            .awaitSingle()
    }
    
    private fun filterByRegion(prefixes: List<IpPrefix>, region: String): List<IpPrefix> {
        val targetRegion = Region.fromString(region)
            ?: throw IllegalArgumentException("Invalid region: $region. Valid regions: ${Region.values().joinToString { it.code }}")
        
        return prefixes.filter { prefix ->
            targetRegion.scopePrefixes.any { scopePrefix ->
                prefix.scope.startsWith(scopePrefix, ignoreCase = true)
            }
        }
    }
    
    private fun filterByIpVersion(prefixes: List<IpPrefix>, ipVersion: String?): List<String> {
        return when (ipVersion?.uppercase()) {
            "IPV4" -> prefixes.mapNotNull { it.ipv4Prefix }
            "IPV6" -> prefixes.mapNotNull { it.ipv6Prefix }
            null -> prefixes.flatMap { listOfNotNull(it.ipv4Prefix, it.ipv6Prefix) }
            else -> throw IllegalArgumentException("Invalid IP version: $ipVersion. Valid values: IPv4, IPv6")
        }
    }
}
