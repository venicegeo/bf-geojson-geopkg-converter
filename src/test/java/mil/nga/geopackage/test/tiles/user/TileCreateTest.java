package mil.nga.geopackage.test.tiles.user;

import java.io.IOException;
import java.sql.SQLException;

import mil.nga.geopackage.test.CreateGeoPackageTestCase;

import org.junit.Test;

/**
 * Test Tiles from a created database
 * 
 * @author osbornb
 */
public class TileCreateTest extends CreateGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public TileCreateTest() {

	}

	/**
	 * Test reading
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testRead() throws SQLException {

		TileUtils.testRead(geoPackage);

	}

	/**
	 * Test updating
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	@Test
	public void testUpdate() throws SQLException, IOException {

		TileUtils.testUpdate(geoPackage);

	}

	/**
	 * Test creating
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreate() throws SQLException {

		TileUtils.testCreate(geoPackage);

	}

	/**
	 * Test deleting
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDelete() throws SQLException {

		TileUtils.testDelete(geoPackage);

	}

	/**
	 * Test getZoomLevel
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetZoomLevel() throws SQLException {

		TileUtils.testGetZoomLevel(geoPackage);

	}

	/**
	 * Test queryByRange
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testQueryByRange() throws SQLException {

		TileUtils.testQueryByRange(geoPackage);

	}

	/**
	 * Test tileMatrixBoundingBox
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testTileMatrixBoundingBox() throws SQLException {

		TileUtils.testTileMatrixBoundingBox(geoPackage);

	}

}
