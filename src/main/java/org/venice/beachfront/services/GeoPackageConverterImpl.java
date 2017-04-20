package org.venice.beachfront.services;

import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
    private byte[] emptyGeoPackageData = new byte[0];

    public GeoPackageConverterImpl(@Value("classpath:empty.gpkg") Resource emptyGeoPackage) throws IOException {
        if (emptyGeoPackage != null) {
            this.emptyGeoPackageData = Files.readAllBytes(emptyGeoPackage.getFile().toPath());
        }
    }

    public byte[] apply(byte[] fc) {
        try {
            // TODO: Do actual conversion code here
            return this.emptyGeoPackageData;
        } catch (Exception e) {
            throw new GeoPackageConverter.GeoPackageConversionError(e);
        }
    }
}