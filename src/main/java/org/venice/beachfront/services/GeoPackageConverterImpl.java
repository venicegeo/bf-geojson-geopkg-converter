package org.venice.beachfront.services;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.stereotype.Service;

@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
        public byte[] geoJSONToGeoPackage(SimpleFeatureCollection fc) {
            return "hello, world!".getBytes();
        }

}