package org.venice.beachfront.services;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;

import junit.framework.TestCase;

public class GeoPackageConverterImplTest extends TestCase {
    private GeoPackageConverter geoPackageConverter;

    @Override
    public void setUp() throws IOException {
        this.geoPackageConverter = new GeoPackageConverterImpl();
    }

    public void testData() throws URISyntaxException, IOException {
    	processFile("LC80020522017095LGN00.geojson");
    	processFile("taz10.geojson");
    	processFile("fc-points.geojson");
    }
    private void processFile(String inFileName) throws URISyntaxException, IOException{
		java.net.URL url = ClassLoader.getSystemResource(inFileName);
		java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        byte[] result = this.geoPackageConverter.apply(java.nio.file.Files.readAllBytes(resPath));
        Assert.assertNotNull(result);
    }
}