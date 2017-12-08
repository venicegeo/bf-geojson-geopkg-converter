package org.venice.beachfront.services;

import java.util.function.Function;


/**
 * Interface for a service to convert a JSON (GeoJSON) byte sequence 
 * to a zipped Shapefile byte sequence. Extends {@link Function} and
 * can be used as such.
 * 
 * @version 1.0
 */
@SuppressWarnings({"serial"})
public interface ShapefileConverter extends Function<byte[], byte[]> {
    /**
     * Special exception class for handling an error during the conversion process.
     */
    public class ShapefileConversionError extends RuntimeException {
        /**
         * Create a new ShapefileConversionError.
         * 
         * @param cause A {@link Throwable} that is the real cause of the failure
         */
        public ShapefileConversionError(Throwable cause) {
            super(cause);
        }
    }
}