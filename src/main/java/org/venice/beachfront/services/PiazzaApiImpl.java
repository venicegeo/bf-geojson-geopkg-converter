package org.venice.beachfront.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
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

    @Autowired
    public PiazzaApiImpl(
            @Value("${piazza.url}") String piazzaUrl,
            PiazzaApi.HttpRequestFactory requestFactory
        ) {
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

    public DefaultFeatureCollection geoJSONtoFeatureCollection(byte[] geoJSON) {
        DefaultFeatureCollection fc;
        try {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(geoJSON));
            fc = (DefaultFeatureCollection)new FeatureJSON().readFeatureCollection(reader);
        } catch (IOException e) {
            System.err.printf("Failed to convert json to feature collection: %s\n", new String(geoJSON));
            fc = null;
        }
        return fc;
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