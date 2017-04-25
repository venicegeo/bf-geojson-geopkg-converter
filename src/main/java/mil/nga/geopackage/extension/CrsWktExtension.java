package mil.nga.geopackage.extension;

import mil.nga.geopackage.GeoPackageConstants;
import mil.nga.geopackage.GeoPackageCore;
import mil.nga.geopackage.core.srs.SpatialReferenceSystem;
import mil.nga.geopackage.db.GeoPackageCoreConnection;
import mil.nga.geopackage.property.GeoPackageProperties;
import mil.nga.geopackage.property.PropertyConstants;

/**
 * OGC Well known text representation of Coordinate Reference Systems extension
 * 
 * @author osbornb
 * @since 1.1.8
 */
public class CrsWktExtension extends BaseExtension {

	/**
	 * Name
	 */
	public static final String NAME = "crs_wkt";

	/**
	 * Extension name
	 */
	public static final String EXTENSION_NAME = GeoPackageConstants.GEO_PACKAGE_EXTENSION_AUTHOR
			+ Extensions.EXTENSION_NAME_DIVIDER + NAME;

	/**
	 * Extension definition URL
	 */
	public static final String DEFINITION = GeoPackageProperties.getProperty(
			PropertyConstants.EXTENSIONS, NAME);

	/**
	 * Extension new column name
	 */
	public static final String COLUMN_NAME = GeoPackageProperties.getProperty(
			PropertyConstants.EXTENSIONS + PropertyConstants.PROPERTY_DIVIDER
					+ NAME, "column_name");

	/**
	 * Extension new column definition
	 */
	public static final String COLUMN_DEF = GeoPackageProperties.getProperty(
			PropertyConstants.EXTENSIONS + PropertyConstants.PROPERTY_DIVIDER
					+ NAME, "column_def");

	/**
	 * Connection
	 */
	private GeoPackageCoreConnection connection = null;

	/**
	 * Constructor
	 * 
	 * @param geoPackage
	 *            GeoPackage
	 * 
	 */
	public CrsWktExtension(GeoPackageCore geoPackage) {
		super(geoPackage);
		connection = geoPackage.getDatabase();
	}

	/**
	 * Get or create the extension
	 * 
	 * @return extension
	 */
	public Extensions getOrCreate() {

		Extensions extension = getOrCreate(EXTENSION_NAME, null, null,
				DEFINITION, ExtensionScopeType.READ_WRITE);

		if (!hasColumn()) {
			createColumn();
		}

		return extension;
	}

	/**
	 * Determine if the GeoPackage has the extension
	 * 
	 * @return true if has extension
	 */
	public boolean has() {

		boolean exists = has(EXTENSION_NAME, null, null);

		if (exists) {
			exists = hasColumn();
		}

		return exists;
	}

	/**
	 * Update the extension definition
	 * 
	 * @param srsId
	 * @param definition
	 */
	public void updateDefinition(long srsId, String definition) {
		connection.execSQL("UPDATE " + SpatialReferenceSystem.TABLE_NAME
				+ " SET " + COLUMN_NAME + " = '" + definition + "' WHERE "
				+ SpatialReferenceSystem.COLUMN_SRS_ID + " = " + srsId);
	}

	/**
	 * Get the extension definition
	 * 
	 * @return definition
	 */
	public String getDefinition(long srsId) {
		String definition = connection.querySingleStringResult("SELECT "
				+ COLUMN_NAME + " FROM " + SpatialReferenceSystem.TABLE_NAME
				+ " WHERE " + SpatialReferenceSystem.COLUMN_SRS_ID + " = ?",
				new String[] { String.valueOf(srsId) });
		return definition;
	}

	/**
	 * Create the extension column
	 */
	private void createColumn() {
		connection.addColumn(SpatialReferenceSystem.TABLE_NAME, COLUMN_NAME,
				COLUMN_DEF);

		// Update the existing known SRS values
		updateDefinition(GeoPackageProperties.getIntegerProperty(
				PropertyConstants.WGS_84, PropertyConstants.SRS_ID),
				GeoPackageProperties.getProperty(PropertyConstants.WGS_84,
						PropertyConstants.DEFINITION_12_063));
		updateDefinition(
				GeoPackageProperties.getIntegerProperty(
						PropertyConstants.UNDEFINED_CARTESIAN,
						PropertyConstants.SRS_ID),
				GeoPackageProperties.getProperty(
						PropertyConstants.UNDEFINED_CARTESIAN,
						PropertyConstants.DEFINITION_12_063));
		updateDefinition(GeoPackageProperties.getIntegerProperty(
				PropertyConstants.UNDEFINED_GEOGRAPHIC,
				PropertyConstants.SRS_ID), GeoPackageProperties.getProperty(
				PropertyConstants.UNDEFINED_GEOGRAPHIC,
				PropertyConstants.DEFINITION_12_063));
		updateDefinition(GeoPackageProperties.getIntegerProperty(
				PropertyConstants.WEB_MERCATOR, PropertyConstants.SRS_ID),
				GeoPackageProperties.getProperty(
						PropertyConstants.WEB_MERCATOR,
						PropertyConstants.DEFINITION_12_063));
	}

	/**
	 * Determine if the GeoPackage SRS table has the extension column
	 * 
	 * @return
	 */
	private boolean hasColumn() {
		boolean exists = connection.columnExists(
				SpatialReferenceSystem.TABLE_NAME, COLUMN_NAME);
		return exists;
	}

}
