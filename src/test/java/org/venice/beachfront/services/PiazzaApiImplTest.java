package org.venice.beachfront.services;

import org.venice.beachfront.services.PiazzaApi;
import org.venice.beachfront.services.PiazzaApi.RestTemplateFactory;
import org.venice.beachfront.services.PiazzaApiImpl;

import junit.framework.TestCase;

import java.util.Base64;
import java.util.concurrent.ExecutionException;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

public class PiazzaApiImplTest extends TestCase {
	protected PiazzaApi piazzaApi;
	private String mockId = "Feature-ID-1234";
	private String mockPzKey = "Pz-Key-5678";
	private String mockAuthorization = "Basic " + Base64.getEncoder().encodeToString((this.mockPzKey + ":").getBytes());
	private String mockPiazzaUrl = "http://fake-url.localdomain";
	private String mockRequestBody = this.mockId + " " + this.mockPzKey;
	private String mockAuthorizationFailedMessage = "Authorization failed";
	private RestTemplate mockRestTemplate;

	public void setUp() {
		this.mockRestTemplate = Mockito.mock(RestTemplate.class);

		// Respond to a proper query with the mock data, or with a 401 on
		// improper auth
		Mockito.when(this.mockRestTemplate.exchange(Mockito.startsWith(this.mockPiazzaUrl + "/file"),
				Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), Mockito.eq(byte[].class)))
				.then(new Answer<ResponseEntity<byte[]>>() {
					public ResponseEntity<byte[]> answer(InvocationOnMock invocation) {
						HttpEntity<String> requestEntity = invocation.getArgument(2);
						if (requestEntity.getHeaders().getFirst("Authorization").equals(mockAuthorization)) {
							return new ResponseEntity<byte[]>(mockRequestBody.getBytes(), HttpStatus.OK);
						}
						return new ResponseEntity<byte[]>(mockAuthorizationFailedMessage.getBytes(),
								HttpStatus.UNAUTHORIZED);
					}
				});

		PiazzaApi.RestTemplateFactory factory = (RestTemplateFactory) Mockito.mock(PiazzaApi.RestTemplateFactory.class);
		Mockito.when(factory.getRestTemplate()).thenReturn(this.mockRestTemplate);

		this.piazzaApi = new PiazzaApiImpl(this.mockPiazzaUrl, factory);
	}

	public void testGetUrlForItemId() {
		String url = this.piazzaApi.getUrlForItemId("foo");
		String expected = this.mockPiazzaUrl + "/file/foo";
		TestCase.assertEquals(expected, url);
	}

	public void testGetGeoJSON_correctResult() throws ExecutionException, InterruptedException {
		byte[] result = this.piazzaApi.getGeoJSON(this.mockId, this.mockPzKey).get();
		TestCase.assertEquals(this.mockRequestBody, new String(result));
	}

	public void testGetGeoJSON_wrongAuthorization() throws InterruptedException {
		try {
			this.piazzaApi.getGeoJSON(this.mockId, "bogusKey").get();
			TestCase.fail("Expected API call to fail, it did not");
		} catch (ExecutionException e) {
			TestCase.assertEquals(e.getCause().getClass(), PiazzaApi.HttpRequestFailedException.class);
			PiazzaApi.HttpRequestFailedException httpExc = (PiazzaApi.HttpRequestFailedException) e.getCause();
			TestCase.assertEquals(this.mockAuthorizationFailedMessage,
					new String(httpExc.getResponseEntity().getBody()));
		}
	}

	public void testGetGeoJSON_noDataIdError() throws InterruptedException {
		try {
			this.piazzaApi.getGeoJSON(null, this.mockPzKey).get();
			TestCase.fail("Expected error passing in null data ID, got no error.");
		} catch (ExecutionException e) {
			TestCase.assertEquals(PiazzaApi.DataIdNotSpecifiedException.class, e.getCause().getClass());
		}

		try {
			this.piazzaApi.getGeoJSON("", this.mockPzKey).get();
			TestCase.fail("Expected error passing in empty data ID, got no error.");
		} catch (ExecutionException e) {
			TestCase.assertEquals(PiazzaApi.DataIdNotSpecifiedException.class, e.getCause().getClass());
		}
	}

	public void testGetGeoJSON_noApiKeyError() throws InterruptedException {
		try {
			this.piazzaApi.getGeoJSON(this.mockId, null).get();
			TestCase.fail("Expected error passing in null API key, got no error.");
		} catch (ExecutionException e) {
			TestCase.assertEquals(PiazzaApi.ApiKeyNotSpecifiedException.class, e.getCause().getClass());
		}

		try {
			this.piazzaApi.getGeoJSON(this.mockId, null).get();
			TestCase.fail("Expected error passing in null API key, got no error.");
		} catch (ExecutionException e) {
			TestCase.assertEquals(PiazzaApi.ApiKeyNotSpecifiedException.class, e.getCause().getClass());
		}
	}
}