package org.venice.beachfront.services;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.github.kevinsawicki.http.HttpRequest;

import rx.subjects.AsyncSubject;
import rx.Observable;

@Service
public class PiazzaApiImpl implements PiazzaApi {
    private String piazzaUrl;

    private PiazzaApi.HttpRequestFactory requestFactory;

    public PiazzaApiImpl(@Autowired @Value("${piazza.url}") String piazzaUrl,
            @Autowired PiazzaApi.HttpRequestFactory requestFactory) {
        this.piazzaUrl = piazzaUrl;
        this.requestFactory = requestFactory;
    }

    public Observable<byte[]> getGeoJSON(String id, String pzKey) {
        AsyncSubject<byte[]> result = AsyncSubject.create();
        new Thread(() -> {
            if (id == null || id.length() < 1) {
                result.onError(new PiazzaApi.DataIdNotSpecifiedException());            
                return;
            }
            if (pzKey == null || pzKey.length() < 1) {
                result.onError(new PiazzaApi.ApiKeyNotSpecifiedException());
                return;
            }
            try {
                String url = this.getUrlForItemId(id);
                HttpRequest request = this.requestFactory.getHttpRequest(url).basic(pzKey, "");
                result.onNext(request.bytes());
                result.onCompleted();
            } catch(Exception e) {
                result.onError(e);
            }
        }).start();
        return result.share();
    }

    public SimpleFeatureCollection geoJSONtoFeatureCollection(byte[] geoJSON) {
        System.err.printf("Convert json to feature collection: %s\n", new String(geoJSON));
        return null;
    }

    public String getUrlForItemId(String id) {
        return String.format("%s/data/%s", this.piazzaUrl, id);
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