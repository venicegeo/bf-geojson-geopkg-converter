package org.venice.beachfront.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.geotools.feature.DefaultFeatureCollection;
import org.mockito.Mockito;
import org.venice.beachfront.services.GeoPackageConverter;
import org.venice.beachfront.services.PiazzaApi;

import junit.framework.Assert;
import junit.framework.TestCase;


public class GeoPackageControllerTest extends TestCase {
    GeoPackageController controller;
    PiazzaApi mockPiazzaApi;
    GeoPackageConverter mockGeoPackageConverter;
    HttpServletResponse mockResponse;

    String mockItemId = "Piazza-Mock-Item-Id-123";
    String mockPzApiKey = "Piazza-Mock-API-Key-456";
    byte[] mockGeoJSON = "{}".getBytes();
    byte[] mockResultSqlite = "sample sqlite".getBytes();
    DefaultFeatureCollection mockFeatureCollection;

    public void setUp() {
        this.mockFeatureCollection = Mockito.mock(DefaultFeatureCollection.class);

        this.mockPiazzaApi = Mockito.mock(PiazzaApi.class);
        Mockito.when(this.mockPiazzaApi.getGeoJSON(this.mockItemId, this.mockPzApiKey))
            .thenReturn(CompletableFuture.completedFuture(mockGeoJSON));
        Mockito.when(this.mockPiazzaApi.geoJSONtoFeatureCollection(Mockito.any()))
            .thenReturn(this.mockFeatureCollection);

        this.mockGeoPackageConverter = Mockito.mock(GeoPackageConverter.class);
        this.mockResponse = Mockito.mock(HttpServletResponse.class);

        this.controller = new GeoPackageController(this.mockPiazzaApi, this.mockGeoPackageConverter);
    }

    public void testControllerRequiresPiazzaKey() throws ExecutionException, InterruptedException {
        try {
            byte[] result = this.controller.convertToGeoPackage(this.mockItemId, "", this.mockResponse).get();
            Assert.fail(String.format("Expected error for missing Piazza key, got %s", new String(result)));
        } catch (ExecutionException e) {
            Assert.assertEquals(
                GeoPackageController.MissingPiazzaKeyException.class, 
                e.getCause().getClass());
        }
    }

    public void testFailedConversionThrowsCorrectError() throws ExecutionException, InterruptedException {
        Mockito.when(
            this.mockGeoPackageConverter.apply(Mockito.any())
        ).thenThrow(GeoPackageConverter.GeoPackageConversionError.class);

        try {
            byte[] result = this.controller.convertToGeoPackage(this.mockItemId, this.mockPzApiKey, this.mockResponse).get();
            Assert.fail(String.format("Expected error due to failed conversion, got %s", new String(result)));
        } catch (ExecutionException e) {
            Assert.assertEquals(
                GeoPackageConverter.GeoPackageConversionError.class, 
                e.getCause().getClass());
        }
    }
}
