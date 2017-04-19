package org.venice.beachfront.services;

import java.util.function.Function;

import org.geotools.feature.DefaultFeatureCollection;

@SuppressWarnings({"serial"})
public interface GeoPackageConverter extends Function<DefaultFeatureCollection, byte[]> {
    public class GeoPackageConversionError extends RuntimeException {
        public GeoPackageConversionError(Throwable cause) {
            super(cause);
        }
    }
}