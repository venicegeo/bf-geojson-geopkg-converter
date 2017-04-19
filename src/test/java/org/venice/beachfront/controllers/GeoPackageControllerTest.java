package org.venice.beachfront.controllers;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

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

    public void setUp() {
        this.mockPiazzaApi = Mockito.mock(PiazzaApi.class);
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
}
