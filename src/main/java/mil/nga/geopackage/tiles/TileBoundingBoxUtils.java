package mil.nga.geopackage.tiles;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.projection.Projection;
import mil.nga.geopackage.projection.ProjectionConstants;
import mil.nga.geopackage.projection.ProjectionFactory;
import mil.nga.geopackage.projection.ProjectionTransform;
import mil.nga.geopackage.tiles.matrix.TileMatrix;
import mil.nga.sf.Point;

/**
 * Tile Bounding Box utility methods
 *
 * @author osbornb
 */
public class TileBoundingBoxUtils {

	/**
	 * Web mercator projection
	 */
	private static Projection webMercator = ProjectionFactory
			.getProjection(ProjectionConstants.EPSG_WEB_MERCATOR);

	/**
	 * Get the overlapping bounding box between the two bounding boxes
	 *
	 * @param boundingBox
	 *            bounding box 1
	 * @param boundingBox2
	 *            bounding box 2
	 * @return bounding box
	 */
	public static BoundingBox overlap(BoundingBox boundingBox,
			BoundingBox boundingBox2) {

		double minLongitude = Math.max(boundingBox.getMinLongitude(),
				boundingBox2.getMinLongitude());
		double maxLongitude = Math.min(boundingBox.getMaxLongitude(),
				boundingBox2.getMaxLongitude());
		double minLatitude = Math.max(boundingBox.getMinLatitude(),
				boundingBox2.getMinLatitude());
		double maxLatitude = Math.min(boundingBox.getMaxLatitude(),
				boundingBox2.getMaxLatitude());

		BoundingBox overlap = null;

		if (minLongitude < maxLongitude && minLatitude < maxLatitude) {
			overlap = new BoundingBox(minLongitude, maxLongitude, minLatitude,
					maxLatitude);
		}

		return overlap;
	}

	/**
	 * Get the union bounding box combining the two bounding boxes
	 *
	 * @param boundingBox
	 *            bounding box 1
	 * @param boundingBox2
	 *            bounding box 2
	 * @return bounding box
	 */
	public static BoundingBox union(BoundingBox boundingBox,
			BoundingBox boundingBox2) {

		double minLongitude = Math.min(boundingBox.getMinLongitude(),
				boundingBox2.getMinLongitude());
		double maxLongitude = Math.max(boundingBox.getMaxLongitude(),
				boundingBox2.getMaxLongitude());
		double minLatitude = Math.min(boundingBox.getMinLatitude(),
				boundingBox2.getMinLatitude());
		double maxLatitude = Math.max(boundingBox.getMaxLatitude(),
				boundingBox2.getMaxLatitude());

		BoundingBox union = null;

		if (minLongitude < maxLongitude && minLatitude < maxLatitude) {
			union = new BoundingBox(minLongitude, maxLongitude, minLatitude,
					maxLatitude);
		}

		return union;
	}

	/**
	 * Get the X pixel for where the longitude fits into the bounding box
	 *
	 * @param width
	 *            width
	 * @param boundingBox
	 *            bounding box
	 * @param longitude
	 *            longitude
	 * @return x pixel
	 */
	public static float getXPixel(long width, BoundingBox boundingBox,
			double longitude) {

		double boxWidth = boundingBox.getMaxLongitude()
				- boundingBox.getMinLongitude();
		double offset = longitude - boundingBox.getMinLongitude();
		double percentage = offset / boxWidth;
		float pixel = (float) (percentage * width);

		return pixel;
	}

	/**
	 * Get the longitude from the pixel location, bounding box, and image width
	 * 
	 * @param width
	 *            width
	 * @param boundingBox
	 *            bounding box
	 * @param pixel
	 *            pixel
	 * @return longitude
	 */
	public static double getLongitudeFromPixel(long width,
			BoundingBox boundingBox, float pixel) {

		double boxWidth = boundingBox.getMaxLongitude()
				- boundingBox.getMinLongitude();
		double percentage = pixel / width;
		double offset = percentage * boxWidth;
		double longitude = offset + boundingBox.getMinLongitude();

		return longitude;
	}

	/**
	 * Get the Y pixel for where the latitude fits into the bounding box
	 *
	 * @param height
	 *            height
	 * @param boundingBox
	 *            bounding box
	 * @param latitude
	 *            latitude
	 * @return y pixel
	 */
	public static float getYPixel(long height, BoundingBox boundingBox,
			double latitude) {

		double boxHeight = boundingBox.getMaxLatitude()
				- boundingBox.getMinLatitude();
		double offset = boundingBox.getMaxLatitude() - latitude;
		double percentage = offset / boxHeight;
		float pixel = (float) (percentage * height);

		return pixel;
	}

	/**
	 * Get the latitude from the pixel location, bounding box, and image height
	 * 
	 * @param height
	 *            height
	 * @param boundingBox
	 *            bounding box
	 * @param pixel
	 *            pixel
	 * @return latitude
	 */
	public static double getLatitudeFromPixel(long height,
			BoundingBox boundingBox, float pixel) {

		double boxHeight = boundingBox.getMaxLatitude()
				- boundingBox.getMinLatitude();
		double percentage = pixel / height;
		double offset = percentage * boxHeight;
		double latitude = boundingBox.getMaxLatitude() - offset;

		return latitude;
	}

	/**
	 * Get the tile bounding box from the Google Maps API tile coordinates and
	 * zoom level
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getBoundingBox(int x, int y, int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileWidthDegrees = tileWidthDegrees(tilesPerSide);
		double tileHeightDegrees = tileHeightDegrees(tilesPerSide);

		double minLon = -180.0 + (x * tileWidthDegrees);
		double maxLon = minLon + tileWidthDegrees;

		double maxLat = 90.0 - (y * tileHeightDegrees);
		double minLat = maxLat - tileHeightDegrees;

		BoundingBox box = new BoundingBox(minLon, maxLon, minLat, maxLat);

		return box;
	}

	/**
	 * Get the Web Mercator tile bounding box from the Google Maps API tile
	 * coordinates and zoom level
	 *
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getWebMercatorBoundingBox(long x, long y, int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileSize = tileSize(tilesPerSide);

		double minLon = (-1 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ (x * tileSize);
		double maxLon = (-1 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ ((x + 1) * tileSize);
		double minLat = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- ((y + 1) * tileSize);
		double maxLat = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- (y * tileSize);

		BoundingBox box = new BoundingBox(minLon, maxLon, minLat, maxLat);

		return box;
	}

	/**
	 * Get the Web Mercator tile bounding box from the Google Maps API tile grid
	 * and zoom level
	 *
	 * @param tileGrid
	 *            tile grid
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getWebMercatorBoundingBox(TileGrid tileGrid,
			int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileSize = tileSize(tilesPerSide);

		double minLon = (-1 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ (tileGrid.getMinX() * tileSize);
		double maxLon = (-1 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				+ ((tileGrid.getMaxX() + 1) * tileSize);
		double minLat = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- ((tileGrid.getMaxY() + 1) * tileSize);
		double maxLat = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH
				- (tileGrid.getMinY() * tileSize);

		BoundingBox box = new BoundingBox(minLon, maxLon, minLat, maxLat);

		return box;
	}

	/**
	 * Get the Projected tile bounding box from the Google Maps API tile
	 * coordinates and zoom level
	 *
	 * @param projectionEpsg
	 *            projection epsg
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getProjectedBoundingBox(Long projectionEpsg,
			int x, int y, int zoom) {

		BoundingBox boundingBox = getWebMercatorBoundingBox(x, y, zoom);

		if (projectionEpsg != null) {
			ProjectionTransform transform = webMercator
					.getTransformation(projectionEpsg);
			boundingBox = transform.transform(boundingBox);
		}

		return boundingBox;
	}

	/**
	 * Get the Projected tile bounding box from the Google Maps API tile
	 * coordinates and zoom level
	 *
	 * @param projection
	 *            projection
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getProjectedBoundingBox(Projection projection,
			long x, long y, int zoom) {

		BoundingBox boundingBox = getWebMercatorBoundingBox(x, y, zoom);

		if (projection != null) {
			ProjectionTransform transform = webMercator
					.getTransformation(projection);
			boundingBox = transform.transform(boundingBox);
		}

		return boundingBox;
	}

	/**
	 * Get the Projected tile bounding box from the Google Maps API tile
	 * tileGrid and zoom level
	 *
	 * @param projectionEpsg
	 *            projection epsg
	 * @param tileGrid
	 *            tile grid
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getProjectedBoundingBox(Long projectionEpsg,
			TileGrid tileGrid, int zoom) {

		BoundingBox boundingBox = getWebMercatorBoundingBox(tileGrid, zoom);

		if (projectionEpsg != null) {
			ProjectionTransform transform = webMercator
					.getTransformation(projectionEpsg);
			boundingBox = transform.transform(boundingBox);
		}

		return boundingBox;
	}

	/**
	 * Get the Projected tile bounding box from the Google Maps API tile grid
	 * and zoom level
	 *
	 * @param projection
	 *            projection
	 * @param tileGrid
	 *            tile grid
	 * @param zoom
	 *            zoom level
	 * @return bounding box
	 */
	public static BoundingBox getProjectedBoundingBox(Projection projection,
			TileGrid tileGrid, int zoom) {

		BoundingBox boundingBox = getWebMercatorBoundingBox(tileGrid, zoom);

		if (projection != null) {
			ProjectionTransform transform = webMercator
					.getTransformation(projection);
			boundingBox = transform.transform(boundingBox);
		}

		return boundingBox;
	}

	/**
	 * Get the tile grid for the location specified as WGS84
	 * 
	 * @param point
	 *            point
	 * @param zoom
	 *            zoom level
	 * @return tile grid
	 * @since 1.1.0
	 */
	public static TileGrid getTileGridFromWGS84(Point point, int zoom) {
		Projection projection = ProjectionFactory
				.getProjection(ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM);
		return getTileGrid(point, zoom, projection);
	}

	/**
	 * Get the tile grid for the location specified as the projection
	 * 
	 * @param point
	 *            point
	 * @param zoom
	 *            zoom level
	 * @param projection
	 *            projection
	 * @return tile grid
	 * @since 1.1.0
	 */
	public static TileGrid getTileGrid(Point point, int zoom,
			Projection projection) {
		ProjectionTransform toWebMercator = projection
				.getTransformation(ProjectionConstants.EPSG_WEB_MERCATOR);
		Point webMercatorPoint = toWebMercator.transform(point);
		BoundingBox boundingBox = new BoundingBox(webMercatorPoint.getX(),
				webMercatorPoint.getX(), webMercatorPoint.getY(),
				webMercatorPoint.getY());
		return getTileGrid(boundingBox, zoom);
	}

	/**
	 * Get the tile grid that includes the entire tile bounding box
	 *
	 * @param webMercatorBoundingBox
	 *            web mercator bounding box
	 * @param zoom
	 *            zoom level
	 * @return tile grid
	 */
	public static TileGrid getTileGrid(BoundingBox webMercatorBoundingBox,
			int zoom) {

		int tilesPerSide = tilesPerSide(zoom);
		double tileSize = tileSize(tilesPerSide);

		int minX = (int) ((webMercatorBoundingBox.getMinLongitude() + ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH) / tileSize);
		double tempMaxX = (webMercatorBoundingBox.getMaxLongitude() + ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				/ tileSize;
		int maxX = (int) (tempMaxX - ProjectionConstants.WEB_MERCATOR_PRECISION);
		maxX = Math.min(maxX, tilesPerSide - 1);

		int minY = (int) (((webMercatorBoundingBox.getMaxLatitude() - ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH) * -1) / tileSize);
		double tempMaxY = ((webMercatorBoundingBox.getMinLatitude() - ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH) * -1)
				/ tileSize;
		int maxY = (int) (tempMaxY - ProjectionConstants.WEB_MERCATOR_PRECISION);
		maxY = Math.min(maxY, tilesPerSide - 1);

		TileGrid grid = new TileGrid(minX, maxX, minY, maxY);

		return grid;
	}

	/**
	 * Convert the bounding box coordinates to a new web mercator bounding box
	 *
	 * @param boundingBox
	 *            bounding box
	 * @return bounding box
	 */
	public static BoundingBox toWebMercator(BoundingBox boundingBox) {

		double minLatitude = Math.max(boundingBox.getMinLatitude(),
				ProjectionConstants.WEB_MERCATOR_MIN_LAT_RANGE);
		double maxLatitude = Math.min(boundingBox.getMaxLatitude(),
				ProjectionConstants.WEB_MERCATOR_MAX_LAT_RANGE);

		Point lowerLeftPoint = new Point(boundingBox.getMinLongitude(), minLatitude);
		Point upperRightPoint = new Point(boundingBox.getMaxLongitude(), maxLatitude);

		ProjectionTransform toWebMercator = ProjectionFactory.getProjection(
				ProjectionConstants.EPSG_WORLD_GEODETIC_SYSTEM)
				.getTransformation(ProjectionConstants.EPSG_WEB_MERCATOR);
		lowerLeftPoint = toWebMercator.transform(lowerLeftPoint);
		upperRightPoint = toWebMercator.transform(upperRightPoint);

		BoundingBox mercatorBox = new BoundingBox(lowerLeftPoint.getX(),
				upperRightPoint.getX(), lowerLeftPoint.getY(),
				upperRightPoint.getY());

		return mercatorBox;
	}

	/**
	 * Get the tile size in meters
	 *
	 * @param tilesPerSide
	 *            tiles per side
	 * @return tile size
	 */
	public static double tileSize(int tilesPerSide) {
		return (2 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				/ tilesPerSide;
	}

	/**
	 * Get the zoom level from the tile size in meters
	 * 
	 * @param tileSize
	 *            tile size in meters
	 * @return zoom level
	 * @since 1.2.0
	 */
	public static double zoomLevelOfTileSize(double tileSize) {
		double tilesPerSide = (2 * ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH)
				/ tileSize;
		double zoom = Math.log(tilesPerSide) / Math.log(2);
		return zoom;
	}

	/**
	 * Get the tile width in degrees
	 *
	 * @param tilesPerSide
	 *            tiles per side
	 * @return tile width degrees
	 */
	public static double tileWidthDegrees(int tilesPerSide) {
		return 360.0 / tilesPerSide;
	}

	/**
	 * Get the tile height in degrees
	 *
	 * @param tilesPerSide
	 *            tiles per side
	 * @return tile height degrees
	 */
	public static double tileHeightDegrees(int tilesPerSide) {
		return 180.0 / tilesPerSide;
	}

	/**
	 * Get the tiles per side, width and height, at the zoom level
	 *
	 * @param zoom
	 *            zoom level
	 * @return tiles per side
	 */
	public static int tilesPerSide(int zoom) {
		return (int) Math.pow(2, zoom);
	}

	/**
	 * Get the standard y tile location as TMS or a TMS y location as standard
	 * 
	 * @param zoom
	 *            zoom level
	 * @param y
	 *            y coordinate
	 * @return opposite tile format y
	 */
	public static int getYAsOppositeTileFormat(int zoom, int y) {
		int tilesPerSide = tilesPerSide(zoom);
		int oppositeY = tilesPerSide - y - 1;
		return oppositeY;
	}

	/**
	 * Get the zoom level from the tiles per side
	 *
	 * @param tilesPerSide
	 *            tiles per side
	 * @return zoom level
	 */
	public static int zoomFromTilesPerSide(int tilesPerSide) {
		return (int) (Math.log(tilesPerSide) / Math.log(2));
	}

	/**
	 * Get the tile grid
	 *
	 * @param totalBox
	 *            total bounding box
	 * @param matrixWidth
	 *            matrix width
	 * @param matrixHeight
	 *            matrix height
	 * @param boundingBox
	 *            bounding box
	 * @return tile grid
	 */
	public static TileGrid getTileGrid(BoundingBox totalBox, long matrixWidth,
			long matrixHeight, BoundingBox boundingBox) {

		long minColumn = getTileColumn(totalBox, matrixWidth,
				boundingBox.getMinLongitude());
		long maxColumn = getTileColumn(totalBox, matrixWidth,
				boundingBox.getMaxLongitude());

		if (minColumn < matrixWidth && maxColumn >= 0) {
			if (minColumn < 0) {
				minColumn = 0;
			}
			if (maxColumn >= matrixWidth) {
				maxColumn = matrixWidth - 1;
			}
		}

		long maxRow = getTileRow(totalBox, matrixHeight,
				boundingBox.getMinLatitude());
		long minRow = getTileRow(totalBox, matrixHeight,
				boundingBox.getMaxLatitude());

		if (minRow < matrixHeight && maxRow >= 0) {
			if (minRow < 0) {
				minRow = 0;
			}
			if (maxRow >= matrixHeight) {
				maxRow = matrixHeight - 1;
			}
		}

		TileGrid tileGrid = new TileGrid(minColumn, maxColumn, minRow, maxRow);

		return tileGrid;
	}

	/**
	 * Get the tile column of the longitude in constant units
	 *
	 * @param totalBox
	 *            total bounding box
	 * @param matrixWidth
	 *            matrix width
	 * @param longitude
	 *            in constant units
	 * @return tile column if in the range, -1 if before,
	 *         {@link TileMatrix#getMatrixWidth()} if after
	 */
	public static long getTileColumn(BoundingBox totalBox, long matrixWidth,
			double longitude) {

		double minX = totalBox.getMinLongitude();
		double maxX = totalBox.getMaxLongitude();

		long tileId;
		if (longitude < minX) {
			tileId = -1;
		} else if (longitude >= maxX) {
			tileId = matrixWidth;
		} else {
			double matrixWidthMeters = totalBox.getMaxLongitude()
					- totalBox.getMinLongitude();
			double tileWidth = matrixWidthMeters / matrixWidth;
			tileId = (long) ((longitude - minX) / tileWidth);
		}

		return tileId;
	}

	/**
	 * Get the tile row of the latitude in constant units
	 *
	 * @param totalBox
	 *            total bounding box
	 * @param matrixHeight
	 *            matrix height
	 * @param latitude
	 *            in constant units
	 * @return tile row if in the range, -1 if before,
	 *         {@link TileMatrix#getMatrixHeight()} if after
	 */
	public static long getTileRow(BoundingBox totalBox, long matrixHeight,
			double latitude) {

		double minY = totalBox.getMinLatitude();
		double maxY = totalBox.getMaxLatitude();

		long tileId;
		if (latitude <= minY) {
			tileId = matrixHeight;
		} else if (latitude > maxY) {
			tileId = -1;
		} else {
			double matrixHeightMeters = totalBox.getMaxLatitude()
					- totalBox.getMinLatitude();
			double tileHeight = matrixHeightMeters / matrixHeight;
			tileId = (long) ((maxY - latitude) / tileHeight);
		}

		return tileId;
	}

	/**
	 * Get the bounding box of the tile column and row in the tile matrix using
	 * the total bounding box with constant units
	 *
	 * @param totalBox
	 *            total bounding box
	 * @param tileMatrix
	 *            tile matrix
	 * @param tileColumn
	 *            tile column
	 * @param tileRow
	 *            tile row
	 * @return bounding box
	 * @since 1.2.0
	 */
	public static BoundingBox getBoundingBox(BoundingBox totalBox,
			TileMatrix tileMatrix, long tileColumn, long tileRow) {
		return getBoundingBox(totalBox, tileMatrix.getMatrixWidth(),
				tileMatrix.getMatrixHeight(), tileColumn, tileRow);
	}

	/**
	 * Get the bounding box of the tile column and row in the tile width and
	 * height bounds using the total bounding box with constant units
	 *
	 * @param totalBox
	 *            total bounding box
	 * @param tileMatrixWidth
	 *            matrix width
	 * @param tileMatrixHeight
	 *            matrix height
	 * @param tileColumn
	 *            tile column
	 * @param tileRow
	 *            tile row
	 * @return bounding box
	 * @since 1.2.0
	 */
	public static BoundingBox getBoundingBox(BoundingBox totalBox,
			long tileMatrixWidth, long tileMatrixHeight, long tileColumn,
			long tileRow) {
		TileGrid tileGrid = new TileGrid(tileColumn, tileColumn, tileRow,
				tileRow);
		return getBoundingBox(totalBox, tileMatrixWidth, tileMatrixHeight,
				tileGrid);
	}

	/**
	 * Get the bounding box of the tile grid in the tile matrix using the total
	 * bounding box with constant units
	 * 
	 * @param totalBox
	 *            total bounding box
	 * @param tileMatrix
	 *            tile matrix
	 * @param tileGrid
	 *            tile grid
	 * @return bounding box
	 * @since 1.2.0
	 */
	public static BoundingBox getBoundingBox(BoundingBox totalBox,
			TileMatrix tileMatrix, TileGrid tileGrid) {
		return getBoundingBox(totalBox, tileMatrix.getMatrixWidth(),
				tileMatrix.getMatrixHeight(), tileGrid);
	}

	/**
	 * Get the bounding box of the tile grid in the tile width and height bounds
	 * using the total bounding box with constant units
	 * 
	 * @param totalBox
	 *            total bounding box
	 * @param tileMatrixWidth
	 *            matrix width
	 * @param tileMatrixHeight
	 *            matrix height
	 * @param tileGrid
	 *            tile grid
	 * @return bounding box
	 * @since 1.2.0
	 */
	public static BoundingBox getBoundingBox(BoundingBox totalBox,
			long tileMatrixWidth, long tileMatrixHeight, TileGrid tileGrid) {

		// Get the tile width
		double matrixMinX = totalBox.getMinLongitude();
		double matrixMaxX = totalBox.getMaxLongitude();
		double matrixWidth = matrixMaxX - matrixMinX;
		double tileWidth = matrixWidth / tileMatrixWidth;

		// Find the longitude range
		double minLon = matrixMinX + (tileWidth * tileGrid.getMinX());
		double maxLon = matrixMinX + (tileWidth * (tileGrid.getMaxX() + 1));

		// Get the tile height
		double matrixMinY = totalBox.getMinLatitude();
		double matrixMaxY = totalBox.getMaxLatitude();
		double matrixHeight = matrixMaxY - matrixMinY;
		double tileHeight = matrixHeight / tileMatrixHeight;

		// Find the latitude range
		double maxLat = matrixMaxY - (tileHeight * tileGrid.getMinY());
		double minLat = matrixMaxY - (tileHeight * (tileGrid.getMaxY() + 1));

		BoundingBox boundingBox = new BoundingBox(minLon, maxLon, minLat,
				maxLat);

		return boundingBox;
	}

	/**
	 * Get the zoom level of where the web mercator bounding box fits into the
	 * complete world
	 *
	 * @param webMercatorBoundingBox
	 *            web mercator bounding box
	 * @return zoom level
	 */
	public static int getZoomLevel(BoundingBox webMercatorBoundingBox) {

		double worldLength = ProjectionConstants.WEB_MERCATOR_HALF_WORLD_WIDTH * 2;

		int widthTiles = (int) (worldLength / (webMercatorBoundingBox
				.getMaxLongitude() - webMercatorBoundingBox.getMinLongitude()));
		int heightTiles = (int) (worldLength / (webMercatorBoundingBox
				.getMaxLatitude() - webMercatorBoundingBox.getMinLatitude()));

		int tilesPerSide = Math.min(widthTiles, heightTiles);
		tilesPerSide = Math.max(tilesPerSide, 1);

		int zoom = zoomFromTilesPerSide(tilesPerSide);

		return zoom;
	}

	/**
	 * Get the pixel x size for the bounding box with matrix width and tile
	 * width
	 * 
	 * @param webMercatorBoundingBox
	 *            web mercator bounding box
	 * @param matrixWidth
	 *            matrix width
	 * @param tileWidth
	 *            tile width
	 * @return pixel x size
	 */
	public static double getPixelXSize(BoundingBox webMercatorBoundingBox,
			long matrixWidth, int tileWidth) {
		double pixelXSize = (webMercatorBoundingBox.getMaxLongitude() - webMercatorBoundingBox
				.getMinLongitude()) / matrixWidth / tileWidth;
		return pixelXSize;
	}

	/**
	 * Get the pixel y size for the bounding box with matrix height and tile
	 * height
	 * 
	 * @param webMercatorBoundingBox
	 *            web mercator bounding box
	 * @param matrixHeight
	 *            matrix height
	 * @param tileHeight
	 *            tile height
	 * @return pixel y size
	 */
	public static double getPixelYSize(BoundingBox webMercatorBoundingBox,
			long matrixHeight, int tileHeight) {
		double pixelYSize = (webMercatorBoundingBox.getMaxLatitude() - webMercatorBoundingBox
				.getMinLatitude()) / matrixHeight / tileHeight;
		return pixelYSize;
	}

	/**
	 * Bound the upper and lower bounds of the WGS84 bounding box with web
	 * mercator limits
	 * 
	 * @param boundingBox
	 *            wgs84 bounding box
	 * @return bounding box
	 * @since 1.1.6
	 */
	public static BoundingBox boundWgs84BoundingBoxWithWebMercatorLimits(
			BoundingBox boundingBox) {
		BoundingBox bounded = new BoundingBox(boundingBox);
		if (bounded.getMinLatitude() < ProjectionConstants.WEB_MERCATOR_MIN_LAT_RANGE) {
			bounded.setMinLatitude(ProjectionConstants.WEB_MERCATOR_MIN_LAT_RANGE);
		}
		if (bounded.getMaxLatitude() > ProjectionConstants.WEB_MERCATOR_MAX_LAT_RANGE) {
			bounded.setMaxLatitude(ProjectionConstants.WEB_MERCATOR_MAX_LAT_RANGE);
		}
		return bounded;
	}

	/**
	 * Get the tile grid that includes the entire tile bounding box
	 *
	 * @param boundingBox
	 *            wgs84 bounding box
	 * @param zoom
	 *            zoom level
	 *
	 * @return tile grid
	 * @since 1.2.0
	 */
	public static TileGrid getTileGridWGS84(BoundingBox boundingBox, int zoom) {

		int tilesPerLat = tilesPerWGS84LatSide(zoom);
		int tilesPerLon = tilesPerWGS84LonSide(zoom);

		double tileSizeLat = tileSizeLatPerWGS84Side(tilesPerLat);
		double tileSizeLon = tileSizeLonPerWGS84Side(tilesPerLon);

		int minX = (int) ((boundingBox.getMinLongitude() + ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH) / tileSizeLon);
		double tempMaxX = (boundingBox.getMaxLongitude() + ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH)
				/ tileSizeLon;
		int maxX = (int) tempMaxX;
		if (tempMaxX % 1 == 0) {
			maxX--;
		}
		maxX = Math.min(maxX, tilesPerLon - 1);

		int minY = (int) (((boundingBox.getMaxLatitude() - ProjectionConstants.WGS84_HALF_WORLD_LAT_HEIGHT) * -1) / tileSizeLat);
		double tempMaxY = ((boundingBox.getMinLatitude() - ProjectionConstants.WGS84_HALF_WORLD_LAT_HEIGHT) * -1)
				/ tileSizeLat;
		int maxY = (int) tempMaxY;
		if (tempMaxY % 1 == 0) {
			maxY--;
		}
		maxY = Math.min(maxY, tilesPerLat - 1);

		TileGrid grid = new TileGrid(minX, maxX, minY, maxY);

		return grid;
	}

	/**
	 * Get the WGS84 tile bounding box from the tile grid and zoom level
	 *
	 * @param tileGrid
	 *            tile grid
	 * @param zoom
	 *            zoom
	 *
	 * @return wgs84 bounding box
	 * @since 1.2.0
	 */
	public static BoundingBox getWGS84BoundingBox(TileGrid tileGrid, int zoom) {

		int tilesPerLat = tilesPerWGS84LatSide(zoom);
		int tilesPerLon = tilesPerWGS84LonSide(zoom);

		double tileSizeLat = tileSizeLatPerWGS84Side(tilesPerLat);
		double tileSizeLon = tileSizeLonPerWGS84Side(tilesPerLon);

		double minLon = (-1 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH)
				+ (tileGrid.getMinX() * tileSizeLon);
		double maxLon = (-1 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH)
				+ ((tileGrid.getMaxX() + 1) * tileSizeLon);
		double minLat = ProjectionConstants.WGS84_HALF_WORLD_LAT_HEIGHT
				- ((tileGrid.getMaxY() + 1) * tileSizeLat);
		double maxLat = ProjectionConstants.WGS84_HALF_WORLD_LAT_HEIGHT
				- (tileGrid.getMinY() * tileSizeLat);

		BoundingBox box = new BoundingBox(minLon, maxLon, minLat, maxLat);

		return box;
	}

	/**
	 * Get the tiles per latitude side at the zoom level
	 *
	 * @param zoom
	 *            zoom level
	 *
	 * @return tiles per latitude side
	 * @since 1.2.0
	 */
	public static int tilesPerWGS84LatSide(int zoom) {
		return tilesPerSide(zoom);
	}

	/**
	 * Get the tiles per longitude side at the zoom level
	 *
	 * @param zoom
	 *            zoom level
	 *
	 * @return tiles per longitude side
	 * @since 1.2.0
	 */
	public static int tilesPerWGS84LonSide(int zoom) {
		return 2 * tilesPerSide(zoom);
	}

	/**
	 * Get the tile height in degrees latitude
	 *
	 * @param tilesPerLat
	 *            tiles per latitude side
	 *
	 * @return degrees
	 * @since 1.2.0
	 */
	public static double tileSizeLatPerWGS84Side(int tilesPerLat) {
		return (2 * ProjectionConstants.WGS84_HALF_WORLD_LAT_HEIGHT)
				/ tilesPerLat;
	}

	/**
	 * Get the tile height in degrees longitude
	 *
	 * @param tilesPerLon
	 *            tiles per longitude side
	 *
	 * @return degrees
	 * @since 1.2.0
	 */
	public static double tileSizeLonPerWGS84Side(int tilesPerLon) {
		return (2 * ProjectionConstants.WGS84_HALF_WORLD_LON_WIDTH)
				/ tilesPerLon;
	}

}
