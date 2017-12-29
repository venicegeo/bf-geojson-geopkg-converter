/**
 * Copyright 2018, RadiantBlue Technologies, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package org.venice.beachfront.services;


import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
     * An interface for a factory class with the purpose of creating {@link RestTemplate}
     * objects. This is necessary for properly mocking HTTP requests in testing.
     */
    public interface RestTemplateFactory {
        /**
         * Create a plain Spring RestTemplate.
         * 
         * @return A new plain RestTemplate
         */
        public RestTemplate getRestTemplate();
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
    
	public class HttpRequestFailedException extends RuntimeException {
		private ResponseEntity<byte[]> responseEntity;
    	public HttpRequestFailedException(ResponseEntity<byte[]> responseEntity) {
    		this.responseEntity = responseEntity;
    	}
    	
    	public ResponseEntity<byte[]> getResponseEntity() { return this.responseEntity; }
    }
}