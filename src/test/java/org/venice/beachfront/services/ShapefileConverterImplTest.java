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