package mil.nga.geopackage.test.extension;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test Extensions from an imported database
 * 
 * @author osbornb
 */
public class ExtensionsImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public ExtensionsImportTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		ExtensionsUtils.testRead(geoPackage, null);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		ExtensionsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		ExtensionsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		ExtensionsUtils.testDelete(geoPackage);

	}

}
