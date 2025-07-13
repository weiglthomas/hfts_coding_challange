package com.example.gcpipranges.service

import com.example.gcpipranges.model.GcpResponse
import com.example.gcpipranges.model.IpPrefix
import com.example.gcpipranges.model.Region
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
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
    
    private suspend fun fetchGcpData(): GcpResponse {
        return webClient.get()
            .uri(gcpUrl)
            .retrieve()
            .bodyToMono(GcpResponse::class.java)
            .awaitSingle()
    }
    
    private fun filterByRegion(prefixes: List<IpPrefix>, region: String): List<IpPrefix> {
    }
    
    private fun filterByIpVersion(prefixes: List<IpPrefix>, ipVersion: String?): List<String> {
    }
    
}
