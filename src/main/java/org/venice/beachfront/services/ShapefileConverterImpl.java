package org.venice.beachfront.services;

import java.io.File;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.type.AttributeDescriptorImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.feature.type.GeometryTypeImpl;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Implementation of the {@link GeoPackageConverter} interface.
 *
 * @version 1.0
 */
@Primary
@Service
public class ShapefileConverterImpl implements ShapefileConverter {
	
	private static Map<String, String> PROPERTIES = createProperties();
	private static Map<String, String> createProperties(){
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("algorithm_name", "algo_name");
		result.put("algorithm_id", "algo_id");
		result.put("algorithm_version", "algo_vers");
		result.put("cloud_cover", "cld_cover");
		result.put("resolution", "res");
		result.put("classification", "class");
		result.put("time_of_collect", "time");
		result.put("tide_min_24h", "tide_min");
		result.put("tide_max_24h", "tide_max");
		result.put("time_of_collect", "time");
		result.put("sensor_name", "sensor");
		return result;
	}
	
    /**
     * Perform the actual conversion from GeoJSON to Shapefile.
     *
     * @param geojson A byte array containing GeoJSON data
     * @return A byte array containing GPKG data
     */
    public byte[] apply(byte[] geojson) {
    	byte[] result = null;

        try {
    		String json = new String(geojson);
        	GeometryJSON gjson = new GeometryJSON();

        	Reader reader = new StringReader(json);
            FeatureJSON fjson = new FeatureJSON(gjson);

            FeatureCollection<?, ?> fc = fjson.readFeatureCollection(reader);
            FeatureType shorelineFeatureType = fc.getSchema();

            // DataStore
            FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
    		File shapefile = File.createTempFile("shorelines", ".shp");
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("url", shapefile.toURI().toURL());
            params.put("create spatial index", Boolean.TRUE);            
            DataStore dataStore = factory.createNewDataStore(params);

            // Feature Type
            SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
            sftb.setName(shorelineFeatureType.getName());
            sftb.setCRS(DefaultGeographicCRS.WGS84);
            List<AttributeDescriptor> ads = new ArrayList<AttributeDescriptor>();
            GeometryDescriptor gd = shorelineFeatureType.getGeometryDescriptor();
            GeometryType gt = gd.getType();
            Name geometryName = new NameImpl("the_geom");
            gt = new GeometryTypeImpl(geometryName, gt.getBinding(), 
            		DefaultGeographicCRS.WGS84, 
            		gt.isIdentified(), 
            		gt.isAbstract(), gt.getRestrictions(), 
            		gt.getSuper(), 
            		gt.getDescription());
            gd = new GeometryDescriptorImpl(gt, 
            		geometryName, 
            		gd.getMinOccurs(), 
            		gd.getMaxOccurs(), 
            		gd.isNillable(), 
            		gd.getDefaultValue());
            ads.add(gd);
            for (PropertyDescriptor pd : shorelineFeatureType.getDescriptors()){
            	AttributeDescriptor ad = (AttributeDescriptor)pd;
            	String propertyName = pd.getName().getLocalPart();
            	if (propertyName == "geometry") {
            		continue;
            	}else {
	    			if (PROPERTIES.containsKey(propertyName)) {
	        			propertyName = PROPERTIES.get(propertyName);
	    			}
	            	PropertyType pt = pd.getType();
	            	NameImpl pn = new NameImpl(propertyName);
	            	AttributeType at = new AttributeTypeImpl(pn, 
	            			pt.getBinding(), 
	            			false, 
	            			pt.isAbstract(), 
	            			null, 
	            			null, 
	            			pt.getDescription());
	
	            	ad = new AttributeDescriptorImpl(at, 
	            			pn, 
	            			pd.getMinOccurs(),
	            			pd.getMaxOccurs(), 
	            			pd.isNillable(), 
	            			null);
            	}
            	ads.add(ad);
            }
            sftb.addAll(ads);
            SimpleFeatureType featureType = sftb.buildFeatureType();
            dataStore.createSchema(featureType);

            // Feature Store
            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

            // Transaction
            Transaction transaction = new DefaultTransaction("create");
            featureStore.setTransaction(transaction);
            try {
            	featureStore.addFeatures(fcToSFC(fc, featureType));
                transaction.commit();
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
            
            // todo: ZIP

            // Return the results
            result = java.nio.file.Files.readAllBytes(shapefile.toPath());
        } catch (Exception e) {
            throw new GeoPackageConverter.GeoPackageConversionError(e);
        }
        return result;
    }
    
    private DefaultFeatureCollection fcToSFC(FeatureCollection<?, ?> input, SimpleFeatureType featureType){
    	DefaultFeatureCollection result = new DefaultFeatureCollection();
    	SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(featureType);
    	
    	FeatureIterator<?> fi = input.features();
    	Name destinationGPN = new NameImpl("the_geom");
    	while(fi.hasNext()){
    		Feature feature = fi.next();
    		sfb.reset();
    		
    		// Geometry
    		Name gpn = feature.getDefaultGeometryProperty().getName();
    		Geometry geometry = (Geometry)feature.getProperty(gpn).getValue();
    		geometry.setSRID(4326);
			sfb.set(destinationGPN, geometry);

			// Other properties
    		Collection<Property> properties = feature.getProperties();
    		for (Property property : properties) {
    			String propertyName = property.getName().getLocalPart();
    			if (propertyName == "geometry"){
    				continue;
    			}
    			Object value = property.getValue();
    			if (PROPERTIES.containsKey(propertyName)) {
        			sfb.set(PROPERTIES.get(propertyName), value);
    			} else {
        			sfb.set(propertyName, value);
    			}
    		}

    		SimpleFeature sf = sfb.buildFeature(feature.getIdentifier().getID());    	

    		result.add(sf);
    	}
    	return result;
    }
}
