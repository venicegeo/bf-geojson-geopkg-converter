package org.venice.beachfront.services;


import java.util.concurrent.CompletableFuture;

import com.github.kevinsawicki.http.HttpRequest;

@SuppressWarnings({"serial"})
public interface PiazzaApi {
    public CompletableFuture<byte[]> getGeoJSON(String id, String pzKey);
    public String getUrlForItemId(String id);
    public interface HttpRequestFactory {
    public HttpRequest getHttpRequest(String url);
    }
    public class ApiKeyNotSpecifiedException extends RuntimeException {}
    public class DataIdNotSpecifiedException extends RuntimeException {}
}