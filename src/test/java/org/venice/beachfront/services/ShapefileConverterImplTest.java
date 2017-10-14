package org.venice.beachfront.services;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;

import junit.framework.TestCase;

public class ShapefileConverterImplTest extends TestCase {
    private ShapefileConverter shapefileConverter;

    @Override
    public void setUp() throws IOException {
        this.shapefileConverter = new ShapefileConverterImpl();
    }

    public void testData() throws URISyntaxException, IOException {
//    	processFile("taz10.geojson");
//    	processFile("fc-points.geojson");
    	processFile("shorelines-fc.geojson");
    	processFile("LC80020522017095LGN00.geojson");
    }
    private void processFile(String inFileName) throws URISyntaxException, IOException{
		java.net.URL url = ClassLoader.getSystemResource(inFileName);
		java.nio.file.Path resPath = java.nio.file.Paths.get(url.toURI());
        byte[] result = this.shapefileConverter.apply(java.nio.file.Files.readAllBytes(resPath));
        Assert.assertNotNull(result);
    }
}