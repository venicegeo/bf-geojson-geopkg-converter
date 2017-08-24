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
