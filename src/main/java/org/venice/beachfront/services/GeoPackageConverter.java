package org.venice.beachfront.services;

import java.util.function.Function;


@SuppressWarnings({"serial"})
public interface GeoPackageConverter extends Function<byte[], byte[]> {
    public class GeoPackageConversionError extends RuntimeException {
        public GeoPackageConversionError(Throwable cause) {
            super(cause);
        }
    }
}