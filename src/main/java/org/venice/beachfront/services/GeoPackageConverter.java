package org.venice.beachfront.services;

import org.geotools.feature.DefaultFeatureCollection;

public interface GeoPackageConverter {
    public byte[] geoJSONToGeoPackage(DefaultFeatureCollection fc);
}