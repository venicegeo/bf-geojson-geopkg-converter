/**
 * Copyright 2018, RadiantBlue Technologies, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
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