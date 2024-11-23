package com.rocketseat.redirectUrlShortener;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String BUCKET_NAME = "url-shortener-storagerrm";
    private final S3Client s3Client = S3Client.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String rawPath = (String) input.get("rawPath");
        String shortUrlCode = getShortUrlCode(rawPath);

        UrlData urlData = fetchUrlDataFromS3(shortUrlCode);

        long currentTimestamp = System.currentTimeMillis() / 1000;
        return createResponse(urlData, currentTimestamp);
    }

    private String getShortUrlCode(String rawPath) {
        String shortUrlCode = rawPath.replace("/", "");
        if (shortUrlCode == null || shortUrlCode.isEmpty()) {
            throw new IllegalArgumentException("Invalid input: 'shortUrlCode' is required");
        }
        return shortUrlCode;
    }

    private UrlData fetchUrlDataFromS3(String shortUrlCode) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(shortUrlCode + ".json")
                .build();

        InputStream s3ObjectStream;
        try {
            s3ObjectStream = s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching URL data from s3: " + e.getMessage(), e);
        }

        try {
            return objectMapper.readValue(s3ObjectStream, UrlData.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing URL data: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> createResponse(UrlData urlData, long currentTimestamp) {
        Map<String, Object> response = new HashMap<>();
        if (currentTimestamp <= urlData.getExpirationTime()) {
            response.put("statusCode", 302);
            Map<String, String> headers = new HashMap<>();
            headers.put("Location", urlData.getOriginalUrl());
            response.put("headers", headers);
        } else {
            response.put("statusCode", 410);
            response.put("body", "This URL expired");
        }
        return response;
    }
}