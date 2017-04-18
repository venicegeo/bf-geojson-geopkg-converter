package org.venice.beachfront.services;

import java.io.StringWriter;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geojson.GeoJSON;
import org.springframework.stereotype.Service;

@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
        public byte[] geoJSONToGeoPackage(DefaultFeatureCollection fc) {
            String output;
            try {
                // TODO: Include actual conversion to GPKG here
                StringWriter writer = new StringWriter();
                GeoJSON.write(fc, writer);
                output = writer.getBuffer().toString();
            } catch (Exception e) {
                e.printStackTrace();
                output = "";
            }
            return output.getBytes();
        }

}