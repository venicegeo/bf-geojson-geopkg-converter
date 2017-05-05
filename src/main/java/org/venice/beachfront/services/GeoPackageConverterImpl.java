package org.venice.beachfront.services;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.core.contents.ContentsDao;
import mil.nga.geopackage.core.contents.ContentsDataType;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.core.srs.SpatialReferenceSystemDao;
import mil.nga.geopackage.db.GeoPackageDataType;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.columns.GeometryColumnsDao;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.features.user.FeatureTable;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.manager.GeoPackageManager;
import mil.nga.sf.geojson.Feature;
import mil.nga.sf.geojson.FeatureCollection;
import mil.nga.sf.geojson.GeoJsonObject;

/**
 * Implementation of the {@link GeoPackageConverter} interface.
 *
 * @version 1.0
 */
@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
    /**
     * Perform the actual conversion from GeoJSON to GPKG.
     * 
     * @param geojson A byte array containing GeoJSON data
     * @return A byte array containing GPKG data 
     */
    public byte[] apply(byte[] geojson) {
    	byte[] result;
    	
        try {
    		String json = new String(geojson);
    		ObjectMapper mapper = new ObjectMapper();
    		FeatureCollection fc = (FeatureCollection)mapper.readValue(json, GeoJsonObject.class);
    		Map<String, String> propertiesMap = fc.getPropertiesMap();
    		
    		File newGeoPackage = File.createTempFile("Converted", ".gpkg");
    		newGeoPackage.delete();

    		// Create a new GeoPackage
    		GeoPackageManager.create(newGeoPackage);
    		
//    		// Open a GeoPackage
    		try (
				GeoPackage geoPackage = GeoPackageManager.open(newGeoPackage);
    		){	
    			SpatialReferenceSystemDao srsd = geoPackage.getSpatialReferenceSystemDao();
    			SpatialReferenceSystem srs4326 = srsd.getOrCreate(4326);
    			
    			geoPackage.createGeometryColumnsTable();
    			GeometryColumnsDao geometryColumnsDao = geoPackage.getGeometryColumnsDao();

    			List<FeatureColumn> columns = new LinkedList<FeatureColumn>();
    			int index = 0;
    			columns.add(FeatureColumn.createPrimaryKeyColumn(index++, "id"));
    			for (String propertyName : propertiesMap.keySet()){
    				columns.add(FeatureColumn.createColumn(index++, propertyName, GeoPackageDataType.fromName(propertiesMap.get(propertyName)), false, null));
    			}
    			columns.add(FeatureColumn.createGeometryColumn(index++, "geom", fc.getGeometryType(), false, null));
    			
    			FeatureTable table = new FeatureTable("geojson", columns);
    			geoPackage.createFeatureTable(table);
    			
    			ContentsDao contentsDao = geoPackage.getContentsDao();
    			Contents contents = new Contents();
    			contents.setDataType(ContentsDataType.FEATURES);
    			contents.setIdentifier("geojson");
    			contents.setDescription("imported GeoJSON file");
    			contents.setTableName("geojson");
    			contents.setSrs(srs4326);
    			contents.setLastChange(new Date());
    			
    			GeometryColumns geometryColumns = new GeometryColumns();
    			geometryColumns.setContents(contents);
    			geometryColumns.setColumnName("geom");
    			geometryColumns.setGeometryType(fc.getGeometryType());
    			geometryColumns.setSrs(srs4326);
    			
    			contentsDao.create(contents);
    			geometryColumnsDao.create(geometryColumns);
    			
    			FeatureDao featureDao = geoPackage.getFeatureDao(contents);
    			for (Feature feature : fc.getFeatures()) {
        			FeatureRow featureRow = featureDao.newRow();
        			for (int inx = 1; inx < columns.size() - 1; inx++) {
        				final String name = columns.get(inx).getName();
            			featureRow.setValue(name, feature.getProperties().get(name));
        			}
        			GeoPackageGeometryData geometryData = new GeoPackageGeometryData(srs4326.getId());
        			geometryData.setGeometry(feature.getGeometry().getGeometry());

        			featureRow.setGeometry(geometryData);
        			featureDao.create(featureRow);
    			}
    		}
            result = java.nio.file.Files.readAllBytes(newGeoPackage.toPath());
        } catch (Exception e) {
            throw new GeoPackageConverter.GeoPackageConversionError(e);
        }
        return result;
    }
}