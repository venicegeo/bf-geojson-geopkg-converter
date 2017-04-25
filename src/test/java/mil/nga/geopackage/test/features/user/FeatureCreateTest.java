package mil.nga.geopackage.test.features.user;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;

/**
 * Test Features from a created database
 * 
 * @author osbornb
 */
public class FeatureCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public FeatureCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		FeatureUtils.testRead(geoPackage);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		FeatureUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		FeatureUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		FeatureUtils.testDelete(geoPackage);

	}

}
