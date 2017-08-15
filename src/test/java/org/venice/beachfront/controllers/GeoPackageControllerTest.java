package org.venice.beachfront.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.venice.beachfront.services.GeoPackageConverter;
import org.venice.beachfront.services.PiazzaApi;

import junit.framework.TestCase;


public class GeoPackageControllerTest extends TestCase {
    GeoPackageController controller;
    PiazzaApi mockPiazzaApi;
    GeoPackageConverter mockGeoPackageConverter;
    HttpServletResponse mockResponse;

    String mockItemId = "Piazza-Mock-Item-Id-123";
    String mockPzApiKey = "Piazza-Mock-API-Key-456";
    String mockBadPzApiKey = "BOGUS_KEY";
    String mockUnauthorizedHttpBody = "401 Unauthorized";
    byte[] mockGeoJSON = "{}".getBytes();
    byte[] mockResultSqlite = "sample sqlite".getBytes();

    public void setUp() {
        this.mockPiazzaApi = Mockito.mock(PiazzaApi.class);
        Mockito.when(this.mockPiazzaApi.getGeoJSON(this.mockItemId, this.mockPzApiKey))
            .thenReturn(CompletableFuture.completedFuture(mockGeoJSON));
        Mockito.when(this.mockPiazzaApi.getGeoJSON(this.mockItemId, this.mockBadPzApiKey))
        	.thenThrow(new PiazzaApi.HttpRequestFailedException(new ResponseEntity<byte[]>(this.mockUnauthorizedHttpBody.getBytes(), HttpStatus.UNAUTHORIZED)));

        this.mockGeoPackageConverter = Mockito.mock(GeoPackageConverter.class);
        Mockito.when(this.mockGeoPackageConverter.apply(this.mockGeoJSON))
            .thenReturn(this.mockResultSqlite);

        this.mockResponse = Mockito.mock(HttpServletResponse.class);
        this.controller = new GeoPackageController(this.mockPiazzaApi, this.mockGeoPackageConverter);
    }

    public void testControllerRequiresPiazzaKey() throws ExecutionException, InterruptedException {
        try {
            byte[] result = this.controller.convertToGeoPackage(this.mockItemId, "", this.mockResponse).get();
            TestCase.fail(String.format("Expected error for missing Piazza key, got %s", new String(result)));
        } catch (ExecutionException e) {
            TestCase.assertEquals(
                GeoPackageController.MissingPiazzaKeyException.class, 
                e.getCause().getClass());
        }
    }

    @SuppressWarnings("unchecked")
	public void testFailedConversionThrowsCorrectError() throws ExecutionException, InterruptedException {
        Mockito.when(
            this.mockGeoPackageConverter.apply(Mockito.any())
        ).thenThrow(GeoPackageConverter.GeoPackageConversionError.class);

        try {
            byte[] result = this.controller.convertToGeoPackage(this.mockItemId, this.mockPzApiKey, this.mockResponse).get();
            TestCase.fail(String.format("Expected error due to failed conversion, got %s", new String(result)));
        } catch (ExecutionException e) {
        	TestCase.assertEquals(
                GeoPackageConverter.GeoPackageConversionError.class, 
                e.getCause().getClass());
        }
    }

    public void testCorrectMockedOutput() throws ExecutionException, InterruptedException {
        byte[] result = this.controller.convertToGeoPackage(this.mockItemId, this.mockPzApiKey, this.mockResponse).get();
        TestCase.assertEquals(new String(this.mockResultSqlite), new String(result));
    }

    public void testCorrectHeaderSideEffect() throws ExecutionException, InterruptedException {
        this.controller.convertToGeoPackage(this.mockItemId, this.mockPzApiKey, this.mockResponse).get();
        Mockito.verify(this.mockResponse).setHeader(
            Mockito.eq("Content-disposition"), 
            Mockito.eq(String.format("attachment; filename=%s.gpkg", this.mockItemId))
        );
    }
}
