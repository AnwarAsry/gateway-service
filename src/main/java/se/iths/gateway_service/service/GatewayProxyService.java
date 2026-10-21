package se.iths.gateway_service.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GatewayProxyService {

    private final String authServiceUrl;
    private final String productServiceUrl;
    private final String orderServiceUrl;
    private final HttpClient httpClient;


    public GatewayProxyService(
            @Value("${app.auth-service-url}") String authServerUrl,
            @Value("${app.product-service-url}") String productServiceUrl,
            @Value("${app.order-service-url}") String orderServiceUrl,
            HttpClient httpClient) {
        this.authServiceUrl = authServerUrl;
        this.productServiceUrl = productServiceUrl;
        this.orderServiceUrl = orderServiceUrl;
        this.httpClient = httpClient;
    }

    // Get all products from product-service
    public ResponseEntity<String> getAllProducts(String authorization) {
        return forwardGet(productServiceUrl, "/products", authorization);
    }

    public ResponseEntity<String> createProduct(
            String requestBody,
            String authorization,
            String contentType) {
        return forwardPost(productServiceUrl, "/products", requestBody, authorization, contentType);
    }

    public ResponseEntity<String> createOrder(
            String requestBody,
            String authorization,
            String contentType) {
        return forwardPost(orderServiceUrl, "/orders", requestBody, authorization, contentType);
    }

    private ResponseEntity<String> forwardPost(
            String baseUrl,
            String path,
            String requestBody,
            String authorization,
            String contentType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(
                        URI.create(baseUrl.replaceAll("/+$", "") + path))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        if (contentType != null && !contentType.isBlank()) {
            builder.header(HttpHeaders.CONTENT_TYPE, contentType);
        }

        return send(builder, authorization);
    }

    private ResponseEntity<String> forwardGet(
            String baseUrl,
            String path,
            String authorization) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl.replaceAll("/+$", "") + path)).GET();

        return send(builder, authorization);
    }

    private ResponseEntity<String> send(HttpRequest.Builder builder, String authorization) {
        if (authorization != null && !authorization.isBlank()) {
            builder.header(
                    HttpHeaders.AUTHORIZATION,
                    authorization
            );
        }
        try {
            HttpResponse<String> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            ResponseEntity.BodyBuilder result =
                    ResponseEntity.status(response.statusCode());

            response.headers().firstValue(HttpHeaders.CONTENT_TYPE)
                    .ifPresent(value -> result.header(
                            HttpHeaders.CONTENT_TYPE, value
                    ));
            return result.body(response.body());
        } catch (IOException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Could not reach service");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Interrupted");
        }

    }
}
