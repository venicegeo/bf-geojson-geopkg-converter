package org.venice.beachfront.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

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
import mil.nga.sf.geojson.GeoJsonFile;
import mil.nga.sf.geojson.GeoJsonObject;
import mil.nga.sf.GeometryType;
import mil.nga.sf.Point;

/**
 * Sample implementation of the {@link GeoPackageConverter} interface.
 *
 * TODO: This currently contains a stub implementation as the actual conversion is yet to be implemented.
 * 
 * @version 1.0
 */
@Service
public class GeoPackageConverterImpl implements GeoPackageConverter {
    /**
     * Perform the actual conversion from GeoJSON to GPKG.
     * 
     * TODO: implement non-stub functionality
     * 
     * @param geojson A byte array containing GeoJSON data
     * @return A byte array containing GPKG data 
     */
    public byte[] apply(byte[] geojson) {
        try {
    		String json = new String(geojson);
    		ObjectMapper mapper = new ObjectMapper();
    		FeatureCollection fc = (FeatureCollection)mapper.readValue(json, GeoJsonObject.class);
    		Collection<String> properties = getProperties(fc);
    		
    		File newGeoPackage = File.createTempFile("GeoPackageTests", ".gpkg");
    		newGeoPackage.delete();

    		// Create a new GeoPackage
    		boolean created = GeoPackageManager.create(newGeoPackage);
    		assertTrue(created);
    		
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
    			for (String property : properties){
    				columns.add(FeatureColumn.createColumn(index++, property, GeoPackageDataType.TEXT, false, null));
    			}
    			columns.add(FeatureColumn.createGeometryColumn(index++, "geom", GeometryType.POINT, false, null));
    			
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
    			geometryColumns.setGeometryType(GeometryType.POINT);
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
        } catch (Exception e) {
            throw new GeoPackageConverter.GeoPackageConversionError(e);
        }
        return null;
    }
    private Collection<String> getProperties(FeatureCollection input){
    	Collection<String> result = new HashSet<String>();
    	for (final Feature feature : input.getFeatures()){
    		for (final String property : feature.getProperties().keySet()){
    			if (!result.contains(property)) {
    				result.add(property);
    			}
    		}
    	}
    	return result;
    }
}