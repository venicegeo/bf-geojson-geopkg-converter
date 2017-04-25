package mil.nga.geopackage.test.schema.columns;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;
import mil.nga.geopackage.test.TestSetupTeardown;

/**
 * Test Data Columns from a created database
 * 
 * @author osbornb
 */
public class DataColumnsCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public DataColumnsCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		DataColumnsUtils.testRead(geoPackage,
				TestSetupTeardown.CREATE_DATA_COLUMNS_COUNT);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		DataColumnsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		DataColumnsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		DataColumnsUtils.testDelete(geoPackage);

	}

}
