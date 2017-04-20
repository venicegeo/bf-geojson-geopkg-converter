package org.venice.beachfront.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.github.kevinsawicki.http.HttpRequest;


/**
 * Implementation of a Piazza API service for interfacing with a Piazza API
 * 
 * @version 1.0
 */
@Service
public class PiazzaApiImpl implements PiazzaApi {
    /**
     * The URL at which the Piazza API is located.
     */
    private String piazzaUrl;

    /**
     * The HTTP request factory to use
     */
    private PiazzaApi.HttpRequestFactory requestFactory;

    /**
     * Create a new Piazza API Service.
     * 
     * @param piazzaUrl The base URL at which the Piazza API is located;
     *                  injected by Spring from the {@code piazza.url} runtime property
     * @param requestFactory The HTTP request factory to use to create the
     *                       requests to the API
     */
    @Autowired
    public PiazzaApiImpl(
            @Value("${piazza.url}") String piazzaUrl,
            PiazzaApi.HttpRequestFactory requestFactory
        ) {
        this.piazzaUrl = piazzaUrl;
        this.requestFactory = requestFactory;
    }

    
    /**
     * Query the Piazza API for GeoJSON using the given ID and key.
     * 
     * This method is asynchronous and uses a {@link CompletableFuture} to
     * return its results.
     * 
     * @param id Piazza data ID to look for
     * @param pzKey API key to use in the query
     * @return A {@link CompletableFuture} that gets completed with a JSON byte array 
     *         once the request completes
     */
    public CompletableFuture<byte[]> getGeoJSON(String id, String pzKey) {
        return CompletableFuture.supplyAsync(() -> {
            if (id == null || id.length() < 1) {
                throw new PiazzaApi.DataIdNotSpecifiedException();            
            }
            if (pzKey == null || pzKey.length() < 1) {
                throw new PiazzaApi.ApiKeyNotSpecifiedException();
            }
            String url = this.getUrlForItemId(id);
            HttpRequest request = this.requestFactory.getHttpRequest(url).basic(pzKey, "");
            return request.bytes();
        });
    }

    /**
     * Format the API URL for a given item ID.
     * 
     * @param id Piazza data ID to use
     * @return A String containing the appropriate URL for this resource
     */
    public String getUrlForItemId(String id) {
        return String.format("%s/file/%s", this.piazzaUrl, id);
    }

    /**
     * Spring {@link Bean} for injecting a basic HTTP request factory.
     * 
     * @return A {@link PiazzaApi.HttpRequestFactory} object that can create
     *         HTTP requests
     */
    @Bean
    private static PiazzaApi.HttpRequestFactory getRequestFactory() {
        return new PiazzaApi.HttpRequestFactory() {
            public HttpRequest getHttpRequest(String url) {
                return HttpRequest.get(url);
            }
        };
    }
}