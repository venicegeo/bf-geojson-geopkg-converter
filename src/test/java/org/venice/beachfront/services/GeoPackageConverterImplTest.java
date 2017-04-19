package org.venice.beachfront.services;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GeoPackageConverterImplTest extends TestCase {
    private GeoPackageConverter geoPackageConverter;

    @Override
    public void setUp() {
        this.geoPackageConverter = new GeoPackageConverterImpl();
    }

    public void testNotImplemented() {
        byte[] result = this.geoPackageConverter.apply(null);
        Assert.assertEquals("Not implemented", new String(result));
    }
}