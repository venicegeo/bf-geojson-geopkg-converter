package org.venice.beachfront.services;


import org.geotools.feature.DefaultFeatureCollection;
import org.springframework.stereotype.Service;

@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
        public byte[] apply(DefaultFeatureCollection fc) {
            System.err.println("asdfasfd");
            try {
                // TODO: Do actual conversion code here
                return "Not implemented".getBytes();
            } catch (Exception e) {
                throw new GeoPackageConverter.GeoPackageConversionError(e);
            }
        }

}