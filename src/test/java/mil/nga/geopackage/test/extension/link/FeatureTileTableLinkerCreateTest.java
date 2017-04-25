package mil.nga.geopackage.test.extension.link;

import java.sql.SQLException;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;

import org.junit.Test;

/**
 * Test Feature Tile Table Linker from a created database
 * 
 * @author osbornb
 */
public class FeatureTileTableLinkerCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeatureTileTableLinkerCreateTest() {

	}

	/**
	 * Test link
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testLink() throws SQLException {

		FeatureTileTableLinkerUtils.testLink(geoPackage);

	}

}
