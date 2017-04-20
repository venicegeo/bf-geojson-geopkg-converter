package org.venice.beachfront.services;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.github.kevinsawicki.http.HttpRequest;

@Service
public class PiazzaApiImpl implements PiazzaApi {
    private String piazzaUrl;

    private PiazzaApi.HttpRequestFactory requestFactory;

    @Autowired
    public PiazzaApiImpl(
            @Value("${piazza.url}") String piazzaUrl,
            PiazzaApi.HttpRequestFactory requestFactory
        ) {
        this.piazzaUrl = piazzaUrl;
        this.requestFactory = requestFactory;
    }

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

    public String getUrlForItemId(String id) {
        return String.format("%s/file/%s", this.piazzaUrl, id);
    }

    @Bean
    private static PiazzaApi.HttpRequestFactory getRequestFactory() {
        return new PiazzaApi.HttpRequestFactory() {
            public HttpRequest getHttpRequest(String url) {
                return HttpRequest.get(url);
            }
        };
    }
}