package org.venice.beachfront.services;

import java.io.StringWriter;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.GeoJSON;
import org.springframework.stereotype.Service;

@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
        public byte[] geoJSONToGeoPackage(DefaultFeatureCollection fc) throws GeoPackageConverter.GeoPackageConversionError {
            try {
                // TODO: Include actual conversion to GPKG here
                StringWriter writer = new StringWriter();
                GeoJSON.write(fc, writer);
                return writer.getBuffer().toString().getBytes();
            } catch (Exception e) {
                throw new GeoPackageConverter.GeoPackageConversionError(e);
            }
        }

}