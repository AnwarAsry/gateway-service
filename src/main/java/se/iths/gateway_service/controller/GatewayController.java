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

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(
            @RequestBody String requestBody,
            HttpServletRequest request) {
        return proxyService.login(
                requestBody,
                request.getHeader(HttpHeaders.CONTENT_TYPE)
        );
    }

    // Mapping for get single product
    @GetMapping("/products/{id}")
    public ResponseEntity<String> getSingleProduct(@PathVariable String id, HttpServletRequest request) {
        return proxyService.getProductById(id, request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    // Mapping for get all products
    @GetMapping("/products")
    public ResponseEntity<String> getProducts(HttpServletRequest request) {
        return proxyService.getAllProducts(request.getHeader(HttpHeaders.AUTHORIZATION));
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
