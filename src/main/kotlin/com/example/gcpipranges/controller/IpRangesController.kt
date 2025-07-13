package com.example.gcpipranges.controller

import com.example.gcpipranges.controller.api.ApiConstants
import com.example.gcpipranges.service.IpRangesService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ApiConstants.FULL_IP_RANGES_PATH)
class IpRangesController(private val ipRangesService: IpRangesService) {
    
    @GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun getIpRanges(
        @RequestParam(
            name = ApiConstants.Parameters.REGION,
            defaultValue = ApiConstants.DefaultValues.DEFAULT_REGION
        ) region: String,
        @RequestParam(
            name = ApiConstants.Parameters.IP_VERSION,
            required = false
        ) ipVersion: String?
    ): String {
        val ipRanges = ipRangesService.getIpRanges(region, ipVersion)
        return ipRanges.joinToString("\n")
    }
}
