package org.venice.beachfront.services;

import java.util.function.Function;


/**
 * Interface for a service to convert a JSON (GeoJSON) byte sequence 
 * to a SQLite (GPKG) byte sequence. Extends {@link Function} and
 * can be used as such.
 * 
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public interface GeoPackageConverter extends Function<byte[], byte[]> {
    /**
     * Special exception class for handling an error during the conversion process.
     */
    public class GeoPackageConversionError extends RuntimeException {
        /**
         * Create a new GeoPackageConversionError.
         * 
         * @param cause A {@link Throwable} that is the real cause of the failure
         */
        public GeoPackageConversionError(Throwable cause) {
            super(cause);
        }
    }
}