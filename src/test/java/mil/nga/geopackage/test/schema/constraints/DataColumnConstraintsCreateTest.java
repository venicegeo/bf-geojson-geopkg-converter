package mil.nga.geopackage.test.schema.constraints;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;
import mil.nga.geopackage.test.TestSetupTeardown;

/**
 * Test Data Column Constraints from a created database
 * 
 * @author osbornb
 */
public class DataColumnConstraintsCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public DataColumnConstraintsCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		DataColumnConstraintsUtils.testRead(geoPackage,
				TestSetupTeardown.CREATE_DATA_COLUMN_CONSTRAINTS_COUNT);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		DataColumnConstraintsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		DataColumnConstraintsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		DataColumnConstraintsUtils.testDelete(geoPackage);

	}

}
