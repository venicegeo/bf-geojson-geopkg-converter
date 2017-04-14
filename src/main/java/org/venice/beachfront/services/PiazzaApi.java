package org.venice.beachfront.services;


import org.geotools.data.simple.SimpleFeatureCollection;

import rx.Observable;

public interface PiazzaApi {
    public Observable<byte[]> getGeoJSON(String id, String pzKey);
    public SimpleFeatureCollection geoJSONtoFeatureCollection(byte[] geoJSON);
}