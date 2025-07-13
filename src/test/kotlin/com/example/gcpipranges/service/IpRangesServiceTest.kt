package com.example.gcpipranges.service

import com.example.gcpipranges.model.GcpResponse
import com.example.gcpipranges.model.IpPrefix
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.web.reactive.function.client.WebClient
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IpRangesServiceTest {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var ipRangesService: IpRangesService
    private lateinit var objectMapper: ObjectMapper
    
    companion object {
        
        private val MIXED_PREFIXES = listOf(
            IpPrefix(ipv4Prefix = "192.168.1.0/24", ipv6Prefix = null, scope = "europe-west1"),
            IpPrefix(ipv4Prefix = "10.0.0.0/8", ipv6Prefix = null, scope = "us-central1"),
            IpPrefix(ipv4Prefix = null, ipv6Prefix = "2001:db8::/32", scope = "asia-east1"),
            IpPrefix(ipv4Prefix = "172.16.0.0/12", ipv6Prefix = null, scope = "northamerica-northeast1"),
            IpPrefix(ipv4Prefix = "203.0.113.0/24", ipv6Prefix = null, scope = "southamerica-east1"),
            IpPrefix(ipv4Prefix = "198.51.100.0/24", ipv6Prefix = null, scope = "me-west1"),
            IpPrefix(ipv4Prefix = "198.18.0.0/15", ipv6Prefix = null, scope = "africa-south1"),
            IpPrefix(ipv4Prefix = "192.0.2.0/24", ipv6Prefix = null, scope = "australia-southeast1")
        )
        
        private val EU_TEST_PREFIXES = listOf(
            IpPrefix(ipv4Prefix = "192.168.1.0/24", ipv6Prefix = null, scope = "europe-west1"),
            IpPrefix(ipv4Prefix = "10.0.0.0/8", ipv6Prefix = null, scope = "us-central1"),
            IpPrefix(ipv4Prefix = "172.16.0.0/12", ipv6Prefix = null, scope = "europe-north1")
        )
        
        private val IPV4_IPV6_TEST_PREFIXES = listOf(
            IpPrefix(ipv4Prefix = "192.168.1.0/24", ipv6Prefix = null, scope = "europe-west1"),
            IpPrefix(ipv4Prefix = null, ipv6Prefix = "2001:db8::/32", scope = "europe-west1"),
            IpPrefix(ipv4Prefix = "10.0.0.0/8", ipv6Prefix = "2001:db9::/32", scope = "europe-west1")
        )
    }
    
    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(0)
        
        // Wait for server to be ready
        Thread.sleep(100)
        
        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()
        
        objectMapper = ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerModule(KotlinModule.Builder().build())
        }
        ipRangesService = IpRangesService(webClient, "/")
    }
    
    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `should return EU IP ranges when region is EU`() = runTest {
        // Given
        enqueueMockResponse(EU_TEST_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", null)
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains("192.168.1.0/24"))
        assertTrue(result.contains("172.16.0.0/12"))
    }
    
    @Test
    fun `should filter by EU region`() = runTest {
        // Given
        enqueueMockResponse(EU_TEST_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", null)
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains("192.168.1.0/24"))
        assertTrue(result.contains("172.16.0.0/12"))
    }
    
    @Test
    fun `should filter by IPv4 only`() = runTest {
        // Given
        enqueueMockResponse(IPV4_IPV6_TEST_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", "IPv4")
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains("192.168.1.0/24"))
        assertTrue(result.contains("10.0.0.0/8"))
    }
    
    @Test
    fun `should filter by IPv6 only`() = runTest {
        // Given
        enqueueMockResponse(IPV4_IPV6_TEST_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", "IPv6")
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.contains("2001:db8::/32"))
        assertTrue(result.contains("2001:db9::/32"))
    }
    
    @Test
    fun `should throw exception for invalid region`() = runTest {
        // Given
        enqueueMockResponse(emptyList())
        
        // When & Then
        assertThrows<IllegalArgumentException> {
            ipRangesService.getIpRanges("INVALID", null)
        }
    }
    
    @Test
    fun `should throw exception for invalid IP version`() = runTest {
        // Given
        enqueueMockResponse(emptyList())
        
        // When & Then
        assertThrows<IllegalArgumentException> {
            ipRangesService.getIpRanges("EU", "INVALID")
        }
    }

        @Test
    fun `should handle empty prefixes list`() = runTest {
        // Given
        enqueueMockResponse(emptyList())
        
        // When
        val result = ipRangesService.getIpRanges("EU", null)
        
        // Then
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `should handle mixed IPv4 and IPv6 prefixes`() = runTest {
        // Given
        enqueueMockResponse(MIXED_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", null)
        
        // Then
        assertEquals(1, result.size) // 1 EU-adress in MIXED_PREFIXES
        assertTrue(result.contains("192.168.1.0/24"))
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["EU", "US", "AS", "NA", "SA", "ME", "AF", "AUS"])
    fun `should handle different regions without errors`(region: String) = runTest {
        // Given
        enqueueMockResponse(MIXED_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges(region, null)
        
        // Then
        when (region) {
            "EU" -> assertTrue(result.isNotEmpty())
            "US" -> assertTrue(result.isNotEmpty())
            "AUS" -> assertTrue(result.isNotEmpty())
            "AF" -> assertTrue(result.isNotEmpty())
            "ME" -> assertTrue(result.isNotEmpty())
            "AS" -> assertTrue(result.isNotEmpty())
            else -> {}
        }
    }
    
    @ParameterizedTest
    @ValueSource(strings = ["IPv4", "IPv6"])
    fun `should filter by different IP versions`(ipVersion: String) = runTest {
        // Given
        enqueueMockResponse(MIXED_PREFIXES)
        
        // When
        val result = ipRangesService.getIpRanges("EU", ipVersion)
        
        // Then
        if (ipVersion == "IPv4") {
            assertEquals(1, result.size) // 1 IPv4-adress for EU in MIXED_PREFIXES
            assertTrue(result.all { !it.contains(":") })
        } else {
            assertEquals(0, result.size) // 0 IPv6-adresses for EU in MIXED_PREFIXES
        }
    }

        // Helper methods
    private fun createMockResponse(prefixes: List<IpPrefix>): MockResponse {
        val gcpResponse = GcpResponse(prefixes = prefixes)
        return MockResponse()
            .setBody(objectMapper.writeValueAsString(gcpResponse))
            .addHeader("Content-Type", "application/json")
    }
    
    private fun enqueueMockResponse(prefixes: List<IpPrefix>) {
        mockWebServer.enqueue(createMockResponse(prefixes))
    }
}
