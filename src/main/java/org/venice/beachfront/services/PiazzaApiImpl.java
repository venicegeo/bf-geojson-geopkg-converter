package org.venice.beachfront.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.kevinsawicki.http.HttpRequest.Base64;

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
	private RestTemplateFactory restTemplateFactory;

	/**
	 * Create a new Piazza API Service.
	 * 
	 * @param piazzaUrl
	 *            The base URL at which the Piazza API is located; injected by
	 *            Spring from the {@code PIAZZA_URL} environment variable
	 * @param requestFactory
	 *            The HTTP request factory to use to create the requests to the
	 *            API
	 */
	@Autowired
	public PiazzaApiImpl(@Value("${PIAZZA_URL}") String piazzaUrl, RestTemplateFactory restTemplateFactory) {
		this.piazzaUrl = piazzaUrl;
		this.restTemplateFactory = restTemplateFactory;
	}

	/**
	 * Query the Piazza API for GeoJSON using the given ID and key.
	 * 
	 * This method is asynchronous and uses a {@link CompletableFuture} to
	 * return its results.
	 * 
	 * @param id
	 *            Piazza data ID to look for
	 * @param pzKey
	 *            API key to use in the query
	 * @return A {@link CompletableFuture} that gets completed with a JSON byte
	 *         array once the request completes
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

			HttpHeaders headers = new HttpHeaders();
			PiazzaApiImpl.applyBasicAuthorizationHeaders(headers, pzKey, "");
			HttpEntity<String> entity = new HttpEntity<>(headers);

			RestTemplate restTemplate = this.restTemplateFactory.getRestTemplate();
			ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new PiazzaApi.HttpRequestFailedException(response);
			}

			return response.getBody();
		});
	}

	/**
	 * Format the API URL for a given item ID.
	 * 
	 * @param id
	 *            Piazza data ID to use
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
	private static RestTemplateFactory getRequestFactory() {
		return new RestTemplateFactory() {
			public RestTemplate getRestTemplate() {
				return new RestTemplate();
			}
		};
	}

	/**
	 * Modify the given {@link HttpHeaders} in-place to include a Basic
	 * authorization header for the given credentials
	 * 
	 * @param headers
	 * @param username
	 * @param password
	 */
	private static void applyBasicAuthorizationHeaders(HttpHeaders headers, String username, String password) {
		String headerValue = "Basic " + Base64.encode(username + ":" + password);
		headers.set("Authorization", headerValue);
	}
}