package org.venice.beachfront.services;


import java.util.concurrent.CompletableFuture;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * Interface for a Piazza API service for interfacing with a Piazza API
 * 
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public interface PiazzaApi {
    /**
     * Query the Piazza API for GeoJSON using the given ID and key.
     * 
     * This method is to be asynchronous and use a {@link CompletableFuture} to
     * return its results.
     * 
     * @param id Piazza data ID to look for
     * @param pzKey API key to use in the query
     * @return A {@link CompletableFuture} that gets completed with a JSON byte array 
     *         once the request completes
     */
    public CompletableFuture<byte[]> getGeoJSON(String id, String pzKey);

    /**
     * Format the API URL for a given item ID.
     * 
     * @param id Piazza data ID to use
     * @return A String containing the appropriate URL for this resource
     */
    public String getUrlForItemId(String id);

    /**
     * An interface for a factory for creating {@link HttpRequest} objects
     * for a given URL. This is necessary for proper mocking/testing of the
     * HTTP requests.
     */
    public interface HttpRequestFactory {
        /**
         * Create an HTTP request for a given URL.
         * 
         * @param url The URL to create a request for
         * @return A new HTTP request for the given URL
         */
        public HttpRequest getHttpRequest(String url);
    }

    /**
     * Custom exception for handling a missing API key.
     * 
     * Simple subclass of {@link RuntimeException}
     */
    public class ApiKeyNotSpecifiedException extends RuntimeException {}

    /**
     * Custom exception for handling a missing data ID.
     * 
     * Simple subclass of {@link RuntimeException}
     */
    public class DataIdNotSpecifiedException extends RuntimeException {}
}