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

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Sample implementation of the {@link GeoPackageConverter} interface.
 *
 * TODO: This currently contains a stub implementation as the actual conversion is yet to be implemented.
 * 
 * @version 1.0
 */
@Service
public class MockGeoPackageConverterImpl implements GeoPackageConverter {
    private byte[] emptyGeoPackageData = new byte[0];

    /**
     * Construct a new instance of GeoPackageConverterImpl.
     * 
     * @param emptyGeoPackage Default value that the stub converter should return. If {@code null}, 
     *                        it will use an empty string.
     */
    public MockGeoPackageConverterImpl(@Value("classpath:empty.gpkg") Resource emptyGeoPackage) throws IOException {
        if (emptyGeoPackage != null) {
            this.emptyGeoPackageData = Files.readAllBytes(emptyGeoPackage.getFile().toPath());
        }
    }

    /**
     * Perform the actual conversion from GeoJSON to GPKG.
     * 
     * TODO: implement non-stub functionality
     * 
     * @param geojson A byte array containing GeoJSON data
     * @return A byte array containing GPKG data 
     */
    public byte[] apply(byte[] geojson) {
        try {
            // TODO: Do actual conversion code here
            return this.emptyGeoPackageData;
        } catch (Exception e) {
            throw new GeoPackageConverter.GeoPackageConversionError(e);
        }
    }
}