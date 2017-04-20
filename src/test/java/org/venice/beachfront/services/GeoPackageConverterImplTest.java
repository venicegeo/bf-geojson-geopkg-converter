package org.venice.beachfront.services;

import java.io.IOException;

import org.springframework.test.util.ReflectionTestUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GeoPackageConverterImplTest extends TestCase {
    private GeoPackageConverter geoPackageConverter;
    private byte[] mockGeoPackageData = "test-data".getBytes();

    @Override
    public void setUp() throws IOException {
        this.geoPackageConverter = new GeoPackageConverterImpl(null);
        ReflectionTestUtils.setField(this.geoPackageConverter, "emptyGeoPackageData", this.mockGeoPackageData);
    }

    public void testDummyEmptyData() {
        byte[] result = this.geoPackageConverter.apply(null);
        Assert.assertEquals(new String(this.mockGeoPackageData), new String(result));
    }
}