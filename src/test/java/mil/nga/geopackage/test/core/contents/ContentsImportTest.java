package mil.nga.geopackage.test.core.contents;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test Contents from an imported database
 * 
 * @author osbornb
 */
public class ContentsImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public ContentsImportTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		ContentsUtils.testRead(geoPackage, null);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		ContentsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		ContentsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		ContentsUtils.testDelete(geoPackage);

	}

	/**
	 * Test cascade deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDeleteCascade() throws SQLException {

		ContentsUtils.testDeleteCascade(geoPackage);

	}

}
