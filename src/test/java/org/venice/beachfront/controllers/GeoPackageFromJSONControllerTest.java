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
package org.venice.beachfront.controllers;

import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.venice.beachfront.services.GeoPackageConverter;

import junit.framework.TestCase;

public class GeoPackageFromJSONControllerTest extends TestCase {
    GeoPackageFromJSONController controller;
    GeoPackageConverter mockGeoPackageConverter;

    byte[] mockGeoJSON = "{}".getBytes();
    byte[] mockResultSqlite = "sample sqlite".getBytes();

    public void setUp() {
        this.mockGeoPackageConverter = Mockito.mock(GeoPackageConverter.class);
        Mockito.when(this.mockGeoPackageConverter.apply(this.mockGeoJSON))
            .thenReturn(this.mockResultSqlite);

        this.controller = new GeoPackageFromJSONController(this.mockGeoPackageConverter);
    }
    
    public void testCorrectMockedOutput() {
        ResponseEntity<byte[]> response = this.controller.convertToGeoPackage(this.mockGeoJSON);
        TestCase.assertEquals(new String(this.mockResultSqlite), new String(response.getBody()));
        TestCase.assertEquals(new MediaType("application", "x-sqlite3"), response.getHeaders().getContentType());
    }
}
