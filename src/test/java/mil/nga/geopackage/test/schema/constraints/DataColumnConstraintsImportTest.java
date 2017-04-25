package mil.nga.geopackage.test.schema.constraints;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test Data Column Constraints from an imported database
 * 
 * @author osbornb
 */
public class DataColumnConstraintsImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public DataColumnConstraintsImportTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		DataColumnConstraintsUtils.testRead(geoPackage, null);

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
