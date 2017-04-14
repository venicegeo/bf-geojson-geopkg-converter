package org.venice.beachfront.services;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.stereotype.Service;

import rx.subjects.AsyncSubject;
import rx.Observable;

@Service
public class PiazzaApiImpl implements PiazzaApi {
    public Observable<byte[]> getGeoJSON(String id, String pzKey) {
        AsyncSubject<byte[]> result = AsyncSubject.create();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            String fakeData = String.format("%s %s",id , pzKey);
            result.onNext(fakeData.getBytes());
            result.onCompleted();
        }).start();
        return result.share();
    }

    public SimpleFeatureCollection geoJSONtoFeatureCollection(byte[] geoJSON) {
        System.err.printf("Convert json to feature collection: %s\n", new String(geoJSON));
        return null;
    }
}