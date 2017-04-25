package mil.nga.geopackage.test.attributes;

import java.sql.SQLException;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;

import org.junit.Test;

/**
 * Test Attributes from a created database
 * 
 * @author osbornb
 */
public class AttributesCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public AttributesCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		AttributesUtils.testRead(geoPackage);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		AttributesUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		AttributesUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		AttributesUtils.testDelete(geoPackage);

	}

}
