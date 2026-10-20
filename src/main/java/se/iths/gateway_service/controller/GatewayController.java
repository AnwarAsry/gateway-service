package se.iths.gateway_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.gateway_service.service.GatewayProxyService;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    private final GatewayProxyService proxyService;

    public GatewayController(GatewayProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<String> getSingleProduct(@PathVariable String id, HttpServletRequest request) {
        return proxyService.getProductById(id, request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @PostMapping("/products")
    public ResponseEntity<String> createProduct(
            @RequestBody String requestBody,
            HttpServletRequest request) {
        return proxyService.createProduct(
                requestBody,
                request.getHeader(HttpHeaders.AUTHORIZATION),
                request.getHeader(HttpHeaders.CONTENT_TYPE));
    }

    @PostMapping("/orders")
    public ResponseEntity<String> createOrder(
            @RequestBody String requestBody,
            HttpServletRequest request) {
        return proxyService.createOrder(
                requestBody,
                request.getHeader(HttpHeaders.AUTHORIZATION),
                request.getHeader(HttpHeaders.CONTENT_TYPE));
    }
}
