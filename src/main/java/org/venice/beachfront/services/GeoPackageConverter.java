package org.venice.beachfront.services;

import org.geotools.feature.DefaultFeatureCollection;

@SuppressWarnings({"serial"})
public interface GeoPackageConverter {
    public byte[] geoJSONToGeoPackage(DefaultFeatureCollection fc) throws GeoPackageConversionError;

    public class GeoPackageConversionError extends RuntimeException {
        public GeoPackageConversionError(Throwable cause) {
            super(cause);
        }
    }
}