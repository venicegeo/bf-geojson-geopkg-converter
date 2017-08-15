package org.venice.beachfront.services;

import org.venice.beachfront.services.PiazzaApi;
import org.venice.beachfront.services.PiazzaApiImpl;

import com.github.kevinsawicki.http.HttpRequest;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.concurrent.ExecutionException;

import org.mockito.Mockito;

public class PiazzaApiImplTest extends TestCase {
    protected PiazzaApi piazzaApi;
    private String mockId = "Feature-ID-1234";
    private String mockPzKey = "Pz-Key-5678";
    private String mockPiazzaUrl = "http://fake-url.localdomain";
    private String mockRequestBody = this.mockId + " " + this.mockPzKey;
    private HttpRequest mockHttpRequest;

    public void setUp() {
        this.mockHttpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(this.mockHttpRequest.basic(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(this.mockHttpRequest);
        Mockito.when(this.mockHttpRequest.bytes())
            .thenReturn(this.mockRequestBody.getBytes());

        PiazzaApi.HttpRequestFactory factory = Mockito.mock(PiazzaApi.HttpRequestFactory.class);
        Mockito.when(factory.getHttpRequest(Mockito.anyString()))
            .thenReturn(this.mockHttpRequest);

        this.piazzaApi = new PiazzaApiImpl(this.mockPiazzaUrl, factory);
    }

    public void testGetUrlForItemId() {
        String url = this.piazzaApi.getUrlForItemId("foo");
        String expected = this.mockPiazzaUrl + "/file/foo";
        Assert.assertEquals(expected, url);
    }

    public void testGetGeoJSON_correctResult() throws ExecutionException, InterruptedException {
        byte[] result = this.piazzaApi.getGeoJSON(this.mockId, this.mockPzKey).get();
        Assert.assertEquals(this.mockRequestBody, new String(result));
    }

    public void testGetGeoJSON_noDataIdError() throws InterruptedException {
        try {
            this.piazzaApi.getGeoJSON(null, this.mockPzKey).get();
            Assert.fail("Expected error passing in null data ID, got no error.");
        } catch (ExecutionException e) {
            Assert.assertEquals(PiazzaApi.DataIdNotSpecifiedException.class, e.getCause().getClass());
        }

        try {
            this.piazzaApi.getGeoJSON("", this.mockPzKey).get();
            Assert.fail("Expected error passing in empty data ID, got no error.");
        } catch (ExecutionException e) {
            Assert.assertEquals(PiazzaApi.DataIdNotSpecifiedException.class, e.getCause().getClass());
        }
    }

    public void testGetGeoJSON_noApiKeyError() throws InterruptedException {
        try {
            this.piazzaApi.getGeoJSON(this.mockId, null).get();
            Assert.fail("Expected error passing in null API key, got no error.");
        } catch (ExecutionException e) {
            Assert.assertEquals(PiazzaApi.ApiKeyNotSpecifiedException.class, e.getCause().getClass());
        }

        try {
            this.piazzaApi.getGeoJSON(this.mockId, null).get();
            Assert.fail("Expected error passing in null API key, got no error.");
        } catch (ExecutionException e) {
            Assert.assertEquals(PiazzaApi.ApiKeyNotSpecifiedException.class, e.getCause().getClass());
        }
    }
}