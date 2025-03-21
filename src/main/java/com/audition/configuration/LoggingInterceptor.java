package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private transient ObjectMapper objectMapper;

    @Autowired
    private transient AuditionLogger auditionLogger;

    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution requestExecution) throws IOException {
        logRequest(request, body);
        final ClientHttpResponse response = requestExecution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        try {
            if (LOG.isInfoEnabled()) {
                final String requestBody = new String(body, StandardCharsets.UTF_8);
                auditionLogger.info(LOG, String.format("RestTemplate Request: Method=%s, URI=%s, Headers=%s, Body=%s",
                        request.getMethod(),
                        request.getURI(),
                        request.getHeaders(),
                        objectMapper.readTree(requestBody)));
            }
        } catch (IOException e) {
            auditionLogger.warn(LOG, "Failed to log request body");
        }
    }

    private void logResponse(final ClientHttpResponse response) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
            if (LOG.isInfoEnabled()) {
                auditionLogger.info(LOG, String.format("RestTemplate Response: Status=%s, Headers=%s, Body=%s",
                        response.getStatusCode(),
                        response.getHeaders(),
                        objectMapper.readTree(reader.lines().collect(Collectors.joining("\n")))));
            }
        } catch (IOException e) {
            auditionLogger.warn(LOG, "Failed to log response body");
        }
    }
}
