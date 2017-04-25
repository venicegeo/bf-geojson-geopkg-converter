package mil.nga.geopackage.test.features.columns;

import java.sql.SQLException;

import org.junit.Test;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;
import mil.nga.geopackage.test.TestSetupTeardown;

/**
 * Test Geometry Columns from a created database
 * 
 * @author osbornb
 */
public class GeometryColumnsCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GeometryColumnsCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		GeometryColumnsUtils.testRead(geoPackage,
				TestSetupTeardown.CREATE_GEOMETRY_COLUMNS_COUNT);

	}

//	/**
//	 * Test reading using the SQL/MM view
//	 * 
//	 * @throws SQLException
//	 */
//	@Test
//	public void testSqlMmRead() throws SQLException {
//
//		GeometryColumnsUtils.testSqlMmRead(geoPackage,
//				TestSetupTeardown.CREATE_GEOMETRY_COLUMNS_COUNT);
//
//	}

//	/**
//	 * Test reading using the SF/SQL view
//	 * 
//	 * @throws SQLException
//	 */
//	@Test
//	public void testSfSqlRead() throws SQLException {
//
//		GeometryColumnsUtils.testSfSqlRead(geoPackage,
//				TestSetupTeardown.CREATE_GEOMETRY_COLUMNS_COUNT);
//
//	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testUpdate() throws SQLException {

		GeometryColumnsUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		GeometryColumnsUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		GeometryColumnsUtils.testDelete(geoPackage);

	}

}
