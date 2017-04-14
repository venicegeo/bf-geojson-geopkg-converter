package org.venice.beachfront.services;

import org.geotools.data.simple.SimpleFeatureCollection;

public interface GeoPackageConverter {
    public byte[] geoJSONToGeoPackage(SimpleFeatureCollection fc);
}