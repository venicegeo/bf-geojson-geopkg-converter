package org.venice.beachfront.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;
import junit.framework.TestCase;
import mil.nga.sf.geojson.GeoJsonObject;

public class GeoPackageConverterImplTest extends TestCase {
    private GeoPackageConverter geoPackageConverter;
    private byte[] mockGeoPackageData = "test-data".getBytes();

    @Override
    public void setUp() throws IOException {
        this.geoPackageConverter = new GeoPackageConverterImpl();
//        ReflectionTestUtils.setField(this.geoPackageConverter, "emptyGeoPackageData", this.mockGeoPackageData);
    }

    public void testDummyEmptyData() throws UnsupportedEncodingException, IOException, URISyntaxException {
		java.net.URL url = ClassLoader.getSystemResource("fc-points.geojson");
		java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        byte[] result = this.geoPackageConverter.apply(java.nio.file.Files.readAllBytes(resPath));
        Assert.assertEquals(new String(this.mockGeoPackageData), new String(result));
    }
}