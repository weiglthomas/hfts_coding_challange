package com.example.gcpipranges.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.function.client.WebClientException

@ControllerAdvice
class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(ex.message)
    }
    
    @ExceptionHandler(WebClientException::class)
    fun handleWebClientException(ex: WebClientException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Unable to fetch GCP IP ranges: ${ex.message}")
    }
    
}
