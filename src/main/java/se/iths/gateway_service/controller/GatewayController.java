package se.iths.gateway_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import se.iths.gateway_service.service.GatewayProxyService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    private final GatewayProxyService proxyService;
    private final ObjectMapper objectMapper;

    public GatewayController(GatewayProxyService proxyService, ObjectMapper objectMapper) {
        this.proxyService = proxyService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody String requestBody, HttpServletRequest request) {
        ResponseEntity<String> response = proxyService.login(
                requestBody, request.getHeader(HttpHeaders.CONTENT_TYPE));

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return response;
        }

        JsonNode json = objectMapper.readTree(response.getBody());
        ResponseCookie cookie = ResponseCookie.from("token", json.get("accessToken").asText())
                .httpOnly(true)
                .secure(false)       // true på https
                .sameSite("Lax")
                .path("/")
                .maxAge(json.get("expiresIn").asLong())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }

    @GetMapping("/auth/me")
    public ResponseEntity<String> me(@CookieValue(name = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return proxyService.me(token);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true).path("/").maxAge(0).build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
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
