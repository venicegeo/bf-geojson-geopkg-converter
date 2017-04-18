package org.venice.beachfront.services;


import org.geotools.feature.DefaultFeatureCollection;

import com.github.kevinsawicki.http.HttpRequest;

import rx.Observable;

@SuppressWarnings({"serial"})
public interface PiazzaApi {
    public Observable<byte[]> getGeoJSON(String id, String pzKey);
    public DefaultFeatureCollection geoJSONtoFeatureCollection(byte[] geoJSON);

    public String getUrlForItemId(String id);
    public interface HttpRequestFactory {
    public HttpRequest getHttpRequest(String url);
    }
    public class ApiKeyNotSpecifiedException extends Exception {}
    public class DataIdNotSpecifiedException extends Exception {}
}