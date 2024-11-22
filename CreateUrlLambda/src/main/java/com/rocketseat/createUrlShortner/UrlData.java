package com.rocketseat.createUrlShortner;

public class UrlData {
    private String originalUrl;
    private long expirationTimeInSeconds;

    public UrlData(String originalUrl, long expirationTimeInSeconds) {
        this.originalUrl = originalUrl;
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }

    // Getters e Setters

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public long getExpirationTimeInSeconds() {
        return expirationTimeInSeconds;
    }

    public void setExpirationTimeInSeconds(long expirationTimeInSeconds) {
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }
}