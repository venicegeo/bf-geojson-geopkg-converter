package mil.nga.geopackage.test.features.user;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.TestCase;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageException;
import mil.nga.geopackage.db.SQLUtils;
import mil.nga.geopackage.db.SQLiteQueryBuilder;
import mil.nga.geopackage.features.columns.GeometryColumns;
import mil.nga.geopackage.features.columns.GeometryColumnsDao;
import mil.nga.geopackage.features.user.FeatureColumn;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.features.user.FeatureRow;
import mil.nga.geopackage.features.user.FeatureTable;
import mil.nga.geopackage.geom.GeoPackageGeometryData;
import mil.nga.geopackage.test.TestUtils;
import mil.nga.geopackage.test.geom.GeoPackageGeometryDataUtils;
import mil.nga.geopackage.user.ColumnValue;
import mil.nga.geopackage.user.UserCoreResultUtils;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryCollection;
import mil.nga.sf.GeometryType;
import mil.nga.sf.LineString;
import mil.nga.sf.MultiLineString;
import mil.nga.sf.MultiPoint;
import mil.nga.sf.MultiPolygon;
import mil.nga.sf.Point;
import mil.nga.sf.Polygon;
import mil.nga.sf.wkb.util.ByteReader;
import mil.nga.sf.wkb.GeometryReader;

/**
 * Features Utility test methods
 * 
 * @author osbornb
 */
public class FeatureUtils {

	private static final double POINT_UPDATED_X = 45.11111;
	private static final double POINT_UPDATED_Y = 89.99999;
	private static final double POINT_UPDATED_Z = 10.55555;
	private static final double POINT_UPDATED_M = 2.87878;

	/**
	 * Test read
	 * 
	 * @param geoPackage
	 * @throws SQLException
	 */
	public static void testRead(GeoPackage geoPackage) throws SQLException {

		GeometryColumnsDao geometryColumnsDao = geoPackage
				.getGeometryColumnsDao();

		if (geometryColumnsDao.isTableExists()) {
			List<GeometryColumns> results = geometryColumnsDao.queryForAll();

			for (GeometryColumns geometryColumns : results) {

				// Test the get feature DAO methods
				FeatureDao dao = geoPackage.getFeatureDao(geometryColumns);
				TestCase.assertNotNull(dao);
				dao = geoPackage.getFeatureDao(geometryColumns.getContents());
				TestCase.assertNotNull(dao);
				dao = geoPackage.getFeatureDao(geometryColumns.getTableName());
				TestCase.assertNotNull(dao);

				TestCase.assertNotNull(dao.getDb());
				TestCase.assertEquals(geometryColumns.getId(), dao
						.getGeometryColumns().getId());
				TestCase.assertEquals(geometryColumns.getTableName(),
						dao.getTableName());
				TestCase.assertEquals(geometryColumns.getColumnName(),
						dao.getGeometryColumnName());

				FeatureTable featureTable = dao.getTable();
				String[] columns = featureTable.getColumnNames();
				int geomIndex = featureTable.getGeometryColumnIndex();
				TestCase.assertTrue(geomIndex >= 0
						&& geomIndex < columns.length);
				TestCase.assertEquals(geometryColumns.getColumnName(),
						columns[geomIndex]);

				// Query for all
				FeatureResultSet cursor = dao.queryForAll();
				int count = cursor.getCount();
				int manualCount = 0;
				while (cursor.moveToNext()) {
					GeoPackageGeometryData geoPackageGeometryData = cursor
							.getGeometry();
					if (cursor.getBlob(featureTable.getGeometryColumnIndex()) != null) {
						TestCase.assertNotNull(geoPackageGeometryData);
						Geometry geometry = geoPackageGeometryData
								.getGeometry();
						GeometryType geometryType = geometryColumns
								.getGeometryType();
						validateGeometry(geometryType, geometry);

						byte[] wkbBytes = geoPackageGeometryData.getWkbBytes();
						int byteLenth = wkbBytes.length;
						TestCase.assertTrue(byteLenth > 0);
						ByteReader wkbReader = new ByteReader(wkbBytes);
						wkbReader.setByteOrder(geoPackageGeometryData
								.getByteOrder());
						Geometry geometryFromBytes = GeometryReader
								.readGeometry(wkbReader);
						TestCase.assertNotNull(geometryFromBytes);
						TestCase.assertEquals(geometry.getGeometryType(),
								geometryFromBytes.getGeometryType());
						validateGeometry(geometryType, geometryFromBytes);

						ByteBuffer wkbByteBuffer = geoPackageGeometryData
								.getWkbByteBuffer();
						TestCase.assertEquals(byteLenth,
								wkbByteBuffer.remaining());
						byte[] wkbBytes2 = new byte[wkbByteBuffer.remaining()];
						wkbByteBuffer.get(wkbBytes2);
						ByteReader wkbReader2 = new ByteReader(wkbBytes2);
						wkbReader2.setByteOrder(geoPackageGeometryData
								.getByteOrder());
						Geometry geometryFromBytes2 = GeometryReader
								.readGeometry(wkbReader2);
						TestCase.assertNotNull(geometryFromBytes2);
						TestCase.assertEquals(geometry.getGeometryType(),
								geometryFromBytes2.getGeometryType());
						validateGeometry(geometryType, geometryFromBytes2);
					}

					FeatureRow featureRow = cursor.getRow();
					validateFeatureRow(columns, featureRow);

					manualCount++;
				}
				TestCase.assertEquals(count, manualCount);
				cursor.close();

				// Manually query for all and compare
				Connection connection = dao.getConnection();
				String sql = SQLiteQueryBuilder.buildQueryString(false,
						dao.getTableName(), null, null, null, null, null, null);
				ResultSet resultSet = SQLUtils.query(connection, sql, null);
				int resultSetCount = SQLUtils.count(connection, sql, null);
				cursor = new FeatureResultSet(featureTable, resultSet,
						resultSetCount);
				count = cursor.getCount();
				manualCount = 0;
				while (cursor.moveToNext()) {
					GeoPackageGeometryData geometry = cursor.getGeometry();
					if (cursor.getBlob(featureTable.getGeometryColumnIndex()) != null) {
						TestCase.assertNotNull(geometry);
					}
					manualCount++;
				}
				TestCase.assertEquals(count, manualCount);

				TestCase.assertTrue("No features to test", count > 0);

				cursor.close();

				resultSet = SQLUtils.query(connection, sql, null);
				resultSetCount = SQLUtils.count(connection, sql, null);
				cursor = new FeatureResultSet(featureTable, resultSet,
						resultSetCount);

				// Choose random feature
				int random = (int) (Math.random() * count);
				cursor.moveToPosition(random);
				FeatureRow featureRow = cursor.getRow();

				cursor.close();

				// Query by id
				FeatureRow queryFeatureRow = dao.queryForIdRow(featureRow
						.getId());
				TestCase.assertNotNull(queryFeatureRow);
				TestCase.assertEquals(featureRow.getId(),
						queryFeatureRow.getId());

				// Find two non id non geom columns
				FeatureColumn column1 = null;
				FeatureColumn column2 = null;
				for (FeatureColumn column : featureRow.getTable().getColumns()) {
					if (!column.isPrimaryKey() && !column.isGeometry()) {
						if (column1 == null) {
							column1 = column;
						} else {
							column2 = column;
							break;
						}
					}
				}

				// Query for equal
				if (column1 != null) {

					Object column1Value = featureRow
							.getValue(column1.getName());
					Class<?> column1ClassType = column1.getDataType()
							.getClassType();
					boolean column1Decimal = column1ClassType == Double.class
							|| column1ClassType == Float.class;
					ColumnValue column1FeatureValue;
					if (column1Decimal) {
						column1FeatureValue = new ColumnValue(column1Value,
								.000001);
					} else {
						column1FeatureValue = new ColumnValue(column1Value);
					}
					cursor = dao.queryForEq(column1.getName(),
							column1FeatureValue);
					TestCase.assertTrue(cursor.getCount() > 0);
					boolean found = false;
					while (cursor.moveToNext()) {
						queryFeatureRow = cursor.getRow();
						TestCase.assertEquals(column1Value,
								queryFeatureRow.getValue(column1.getName()));
						if (!found) {
							found = featureRow.getId() == queryFeatureRow
									.getId();
						}
					}
					TestCase.assertTrue(found);
					cursor.close();

					// Query for field values
					Map<String, ColumnValue> fieldValues = new HashMap<String, ColumnValue>();
					fieldValues.put(column1.getName(), column1FeatureValue);
					Object column2Value = null;
					ColumnValue column2FeatureValue;
					if (column2 != null) {
						column2Value = featureRow.getValue(column2.getName());
						Class<?> column2ClassType = column2.getDataType()
								.getClassType();
						boolean column2Decimal = column2ClassType == Double.class
								|| column2ClassType == Float.class;
						if (column2Decimal) {
							column2FeatureValue = new ColumnValue(column2Value,
									.000001);
						} else {
							column2FeatureValue = new ColumnValue(column2Value);
						}
						fieldValues.put(column2.getName(), column2FeatureValue);
					}
					cursor = dao.queryForValueFieldValues(fieldValues);
					TestCase.assertTrue(cursor.getCount() > 0);
					found = false;
					while (cursor.moveToNext()) {
						queryFeatureRow = cursor.getRow();
						TestCase.assertEquals(column1Value,
								queryFeatureRow.getValue(column1.getName()));
						if (column2 != null) {
							TestCase.assertEquals(column2Value,
									queryFeatureRow.getValue(column2.getName()));
						}
						if (!found) {
							found = featureRow.getId() == queryFeatureRow
									.getId();
						}
					}
					TestCase.assertTrue(found);
					cursor.close();
				}
			}
		}

	}

	/**
	 * Validate a feature row
	 * 
	 * @param columns
	 * @param featureRow
	 */
	private static void validateFeatureRow(String[] columns,
			FeatureRow featureRow) {
		TestCase.assertEquals(columns.length, featureRow.columnCount());

		for (int i = 0; i < featureRow.columnCount(); i++) {
			FeatureColumn column = featureRow.getTable().getColumns().get(i);
			TestCase.assertEquals(i, column.getIndex());
			TestCase.assertEquals(columns[i], featureRow.getColumnName(i));
			TestCase.assertEquals(i, featureRow.getColumnIndex(columns[i]));
			int rowType = featureRow.getRowColumnType(i);
			Object value = featureRow.getValue(i);

			switch (rowType) {

			case UserCoreResultUtils.FIELD_TYPE_INTEGER:
				TestUtils.validateIntegerValue(value, column.getDataType());
				break;

			case UserCoreResultUtils.FIELD_TYPE_FLOAT:
				TestUtils.validateFloatValue(value, column.getDataType());
				break;

			case UserCoreResultUtils.FIELD_TYPE_STRING:
				TestCase.assertTrue(value instanceof String);
				break;

			case UserCoreResultUtils.FIELD_TYPE_BLOB:
				if (featureRow.getGeometryColumnIndex() == i) {
					TestCase.assertTrue(value instanceof GeoPackageGeometryData);
				} else {
					TestCase.assertTrue(value instanceof byte[]);
				}
				break;

			case UserCoreResultUtils.FIELD_TYPE_NULL:
				TestCase.assertNull(value);
				break;

			}
		}

		TestCase.assertTrue(featureRow.getId() >= 0);
	}

	/**
	 * Validate the geometry
	 * 
	 * @param geometryType
	 * @param geometry
	 */
	private static void validateGeometry(GeometryType geometryType,
			Geometry geometry) {

		switch (geometryType) {
		case POINT:
			TestCase.assertTrue(geometry instanceof Point);
			Point point = (Point) geometry;
			validatePoint(point, point);
			break;
		case LINESTRING:
			TestCase.assertTrue(geometry instanceof LineString);
			LineString lineString = (LineString) geometry;
			validateLineString(lineString, lineString);
			break;
		case POLYGON:
			TestCase.assertTrue(geometry instanceof Polygon);
			Polygon polygon = (Polygon) geometry;
			validatePolygon(polygon, polygon);
			break;
		case MULTIPOINT:
			TestCase.assertTrue(geometry instanceof MultiPoint);
			MultiPoint multiPoint = (MultiPoint) geometry;
			validateMultiPoint(multiPoint, multiPoint);
			break;
		case MULTILINESTRING:
			TestCase.assertTrue(geometry instanceof MultiLineString);
			MultiLineString multiLineString = (MultiLineString) geometry;
			validateMultiLineString(multiLineString, multiLineString);
			break;
		case MULTIPOLYGON:
			TestCase.assertTrue(geometry instanceof MultiPolygon);
			MultiPolygon multiPolygon = (MultiPolygon) geometry;
			validateMultiPolygon(multiPolygon, multiPolygon);
			break;
		case GEOMETRYCOLLECTION:
			TestCase.assertTrue(geometry instanceof GeometryCollection);
			GeometryCollection<?> geometryCollection = (GeometryCollection<?>) geometry;
			validateGeometryCollection(geometryCollection, geometryCollection);
			break;
		default:

		}
	}

	/**
	 * Validate Z and M values
	 * 
	 * @param topGeometry
	 * @param geometry
	 */
	private static void validateZAndM(Geometry topGeometry, Geometry geometry) {
		TestCase.assertEquals(topGeometry.hasZ(), geometry.hasZ());
		TestCase.assertEquals(topGeometry.hasM(), geometry.hasM());
	}

	/**
	 * Validate Point
	 * 
	 * @param topGeometry
	 * @param point
	 */
	private static void validatePoint(Geometry topGeometry, Point point) {

		TestCase.assertEquals(GeometryType.POINT, point.getGeometryType());

		validateZAndM(topGeometry, point);

		if (topGeometry.hasZ()) {
			TestCase.assertNotNull(point.getZ());
		} else {
			TestCase.assertNull(point.getZ());
		}

		if (topGeometry.hasM()) {
			TestCase.assertNotNull(point.getM());
		} else {
			TestCase.assertNull(point.getM());
		}
	}

	/**
	 * Validate Line String
	 * 
	 * @param topGeometry
	 * @param lineString
	 */
	private static void validateLineString(Geometry topGeometry,
			LineString lineString) {

		TestCase.assertEquals(GeometryType.LINESTRING,
				lineString.getGeometryType());

		validateZAndM(topGeometry, lineString);

		for (Point point : lineString.getPoints()) {
			validatePoint(topGeometry, point);
		}

	}

	/**
	 * Validate Polygon
	 * 
	 * @param topGeometry
	 * @param polygon
	 */
	private static void validatePolygon(Geometry topGeometry, Polygon polygon) {

		TestCase.assertEquals(GeometryType.POLYGON, polygon.getGeometryType());

		validateZAndM(topGeometry, polygon);

		for (LineString ring : polygon.getRings()) {
			validateLineString(topGeometry, ring);
		}

	}

	/**
	 * Validate Multi Point
	 * 
	 * @param topGeometry
	 * @param multiPoint
	 */
	private static void validateMultiPoint(Geometry topGeometry,
			MultiPoint multiPoint) {

		TestCase.assertEquals(GeometryType.MULTIPOINT,
				multiPoint.getGeometryType());

		validateZAndM(topGeometry, multiPoint);

		for (Point point : multiPoint.getPoints()) {
			validatePoint(topGeometry, point);
		}

	}

	/**
	 * Validate Multi Line String
	 * 
	 * @param topGeometry
	 * @param multiLineString
	 */
	private static void validateMultiLineString(Geometry topGeometry,
			MultiLineString multiLineString) {

		TestCase.assertEquals(GeometryType.MULTILINESTRING,
				multiLineString.getGeometryType());

		validateZAndM(topGeometry, multiLineString);

		for (LineString lineString : multiLineString.getLineStrings()) {
			validateLineString(topGeometry, lineString);
		}

	}

	/**
	 * Validate Multi Polygon
	 * 
	 * @param topGeometry
	 * @param multiPolygon
	 */
	private static void validateMultiPolygon(Geometry topGeometry,
			MultiPolygon multiPolygon) {

		TestCase.assertEquals(GeometryType.MULTIPOLYGON,
				multiPolygon.getGeometryType());

		validateZAndM(topGeometry, multiPolygon);

		for (Polygon polygon : multiPolygon.getPolygons()) {
			validatePolygon(topGeometry, polygon);
		}

	}

	/**
	 * Validate Geometry Collection
	 * 
	 * @param topGeometry
	 * @param geometryCollection
	 */
	private static void validateGeometryCollection(Geometry topGeometry,
			GeometryCollection<?> geometryCollection) {

		validateZAndM(topGeometry, geometryCollection);

		for (Geometry geometry : geometryCollection.getGeometries()) {
			validateGeometry(geometry.getGeometryType(), geometry);
		}

	}

	/**
	 * Test update
	 * 
	 * @param geoPackage
	 * @throws SQLException
	 */
	public static void testUpdate(GeoPackage geoPackage) throws SQLException {

		GeometryColumnsDao geometryColumnsDao = geoPackage
				.getGeometryColumnsDao();

		if (geometryColumnsDao.isTableExists()) {
			List<GeometryColumns> results = geometryColumnsDao.queryForAll();

			for (GeometryColumns geometryColumns : results) {

				FeatureDao dao = geoPackage.getFeatureDao(geometryColumns);
				TestCase.assertNotNull(dao);

				FeatureResultSet cursor = dao.queryForAll();
				int count = cursor.getCount();
				if (count > 0) {

					// // Choose random feature
					// int random = (int) (Math.random() * count);
					// cursor.moveToPosition(random);
					cursor.moveToFirst();

					GeoPackageGeometryData geometryData = cursor.getGeometry();
					while (geometryData == null && cursor.moveToNext()) {
						geometryData = cursor.getGeometry();
					}
					if (geometryData != null) {
						String updatedString = null;
						String updatedLimitedString = null;
						Boolean updatedBoolean = null;
						Byte updatedByte = null;
						Short updatedShort = null;
						Integer updatedInteger = null;
						Long updatedLong = null;
						Float updatedFloat = null;
						Double updatedDouble = null;
						byte[] updatedBytes = null;
						byte[] updatedLimitedBytes = null;

						Geometry geometry = geometryData.getGeometry();
						FeatureRow originalRow = cursor.getRow();
						FeatureRow featureRow = cursor.getRow();

						try {
							featureRow.setValue(featureRow.getPkColumnIndex(),
									9);
							TestCase.fail("Updated the primary key value");
						} catch (GeoPackageException e) {
							// expected
						}

						for (FeatureColumn featureColumn : dao.getTable()
								.getColumns()) {
							if (!featureColumn.isPrimaryKey()) {

								if (featureColumn.isGeometry()) {

									boolean updateGeometry = true;

									switch (geometry.getGeometryType()) {

									case POINT:
										Point point = (Point) geometry;
										point = updatePoint(point);
										break;
									case MULTIPOINT:
										MultiPoint multiPoint = (MultiPoint) geometry;
										List<Point> newPoints = new LinkedList<Point>();
										if (multiPoint.numPoints() > 1) {
											multiPoint.getPoints().remove(0);
										}
										for (Point multiPointPoint : multiPoint
												.getPoints()) {
											point = updatePoint(multiPointPoint);
										}
										MultiPoint newMultiPoint = new MultiPoint(multiPoint.hasZ(), multiPoint.hasM());
										newMultiPoint.setGeometries(newPoints);
										geometry = newMultiPoint;
										break;

									default:
										updateGeometry = false;
									}
									if (updateGeometry) {
										featureRow.setValue(
												featureColumn.getIndex(),
												geometryData);
									}

								} else {
									switch (featureRow
											.getRowColumnType(featureColumn
													.getIndex())) {

									case UserCoreResultUtils.FIELD_TYPE_STRING:
										if (updatedString == null) {
											updatedString = UUID.randomUUID()
													.toString();
										}
										if (featureColumn.getMax() != null) {
											if (updatedLimitedString != null) {
												if (updatedString.length() > featureColumn
														.getMax()) {
													updatedLimitedString = updatedString
															.substring(
																	0,
																	featureColumn
																			.getMax()
																			.intValue());
												} else {
													updatedLimitedString = updatedString;
												}
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedLimitedString);
										} else {
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedString);
										}
										break;
									case UserCoreResultUtils.FIELD_TYPE_INTEGER:
										switch (featureColumn.getDataType()) {
										case BOOLEAN:
											if (updatedBoolean == null) {
												updatedBoolean = !((Boolean) featureRow
														.getValue(featureColumn
																.getIndex()));
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedBoolean);
											break;
										case TINYINT:
											if (updatedByte == null) {
												updatedByte = (byte) (((int) (Math
														.random() * (Byte.MAX_VALUE + 1))) * (Math
														.random() < .5 ? 1 : -1));
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedByte);
											break;
										case SMALLINT:
											if (updatedShort == null) {
												updatedShort = (short) (((int) (Math
														.random() * (Short.MAX_VALUE + 1))) * (Math
														.random() < .5 ? 1 : -1));
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedShort);
											break;
										case MEDIUMINT:
											if (updatedInteger == null) {
												updatedInteger = (int) (((int) (Math
														.random() * (Integer.MAX_VALUE + 1))) * (Math
														.random() < .5 ? 1 : -1));
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedInteger);
											break;
										case INT:
										case INTEGER:
											if (updatedLong == null) {
												updatedLong = (long) (((int) (Math
														.random() * (Long.MAX_VALUE + 1))) * (Math
														.random() < .5 ? 1 : -1));
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedLong);
											break;
										default:
											TestCase.fail("Unexpected integer type: "
													+ featureColumn
															.getDataType());
										}
										break;
									case UserCoreResultUtils.FIELD_TYPE_FLOAT:
										switch (featureColumn.getDataType()) {
										case FLOAT:
											if (updatedFloat == null) {
												updatedFloat = (float) Math
														.random()
														* Float.MAX_VALUE;
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedFloat);
											break;
										case DOUBLE:
										case REAL:
											if (updatedDouble == null) {
												updatedDouble = Math.random()
														* Double.MAX_VALUE;
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedDouble);
											break;
										default:
											TestCase.fail("Unexpected float type: "
													+ featureColumn
															.getDataType());
										}
										break;
									case UserCoreResultUtils.FIELD_TYPE_BLOB:
										if (updatedBytes == null) {
											updatedBytes = UUID.randomUUID()
													.toString().getBytes();
										}
										if (featureColumn.getMax() != null) {
											if (updatedLimitedBytes != null) {
												if (updatedBytes.length > featureColumn
														.getMax()) {
													updatedLimitedBytes = new byte[featureColumn
															.getMax()
															.intValue()];
													ByteBuffer
															.wrap(updatedBytes,
																	0,
																	featureColumn
																			.getMax()
																			.intValue())
															.get(updatedLimitedBytes);
												} else {
													updatedLimitedBytes = updatedBytes;
												}
											}
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedLimitedBytes);
										} else {
											featureRow.setValue(
													featureColumn.getIndex(),
													updatedBytes);
										}
										break;
									default:
									}
								}

							}
						}

						cursor.close();

						TestCase.assertEquals(1, dao.update(featureRow));

						long id = featureRow.getId();
						FeatureRow readRow = dao.queryForIdRow(id);
						TestCase.assertNotNull(readRow);
						TestCase.assertEquals(originalRow.getId(),
								readRow.getId());
						GeoPackageGeometryData readGeometryData = readRow
								.getGeometry();
						Geometry readGeometry = readGeometryData.getGeometry();

						for (String readColumnName : readRow.getColumnNames()) {

							FeatureColumn readFeatureColumn = readRow
									.getColumn(readColumnName);
							if (!readFeatureColumn.isPrimaryKey()
									&& !readFeatureColumn.isGeometry()) {
								switch (readRow
										.getRowColumnType(readColumnName)) {
								case UserCoreResultUtils.FIELD_TYPE_STRING:
									if (readFeatureColumn.getMax() != null) {
										TestCase.assertEquals(
												updatedLimitedString,
												readRow.getValue(readFeatureColumn
														.getIndex()));
									} else {
										TestCase.assertEquals(
												updatedString,
												readRow.getValue(readFeatureColumn
														.getIndex()));
									}
									break;
								case UserCoreResultUtils.FIELD_TYPE_INTEGER:
									switch (readFeatureColumn.getDataType()) {
									case BOOLEAN:
										TestCase.assertEquals(
												updatedBoolean,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									case TINYINT:
										TestCase.assertEquals(
												updatedByte,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									case SMALLINT:
										TestCase.assertEquals(
												updatedShort,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									case MEDIUMINT:
										TestCase.assertEquals(
												updatedInteger,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									case INT:
									case INTEGER:
										TestCase.assertEquals(
												updatedLong,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									default:
										TestCase.fail("Unexpected integer type: "
												+ readFeatureColumn
														.getDataType());
									}
									break;
								case UserCoreResultUtils.FIELD_TYPE_FLOAT:
									switch (readFeatureColumn.getDataType()) {
									case FLOAT:
										TestCase.assertEquals(
												updatedFloat,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									case DOUBLE:
									case REAL:
										TestCase.assertEquals(
												updatedDouble,
												readRow.getValue(readFeatureColumn
														.getIndex()));
										break;
									default:
										TestCase.fail("Unexpected integer type: "
												+ readFeatureColumn
														.getDataType());
									}
									break;
								case UserCoreResultUtils.FIELD_TYPE_BLOB:
									if (readFeatureColumn.getMax() != null) {
										GeoPackageGeometryDataUtils
												.compareByteArrays(
														updatedLimitedBytes,
														(byte[]) readRow
																.getValue(readFeatureColumn
																		.getIndex()));
									} else {
										GeoPackageGeometryDataUtils
												.compareByteArrays(
														updatedBytes,
														(byte[]) readRow
																.getValue(readFeatureColumn
																		.getIndex()));
									}
									break;
								default:
								}
							}

						}

						switch (geometry.getGeometryType()) {

						case POINT:
							Point point = (Point) readGeometry;
							validateUpdatedPoint(point);
							break;

						case MULTIPOINT:
							MultiPoint originalMultiPoint = (MultiPoint) geometry;
							MultiPoint multiPoint = (MultiPoint) readGeometry;
							TestCase.assertEquals(
									originalMultiPoint.numPoints(),
									multiPoint.numPoints());
							for (Point multiPointPoint : multiPoint.getPoints()) {
								validateUpdatedPoint(multiPointPoint);
							}
							break;

						default:
							geometry.getGeometryType();
						}

						// Compare the modified geometry with the updated
						// geometry
						// from the database
						GeoPackageGeometryDataUtils.compareGeometries(geometry,
								readGeometry);

						// Compare the geo package headers since nothing in the
						// header was changed
						GeoPackageGeometryDataUtils.compareByteArrays(
								geometryData.getHeaderBytes(),
								readGeometryData.getHeaderBytes());

					}

				}
				cursor.close();
			}
		}

	}

	/**
	 * Update a point
	 * 
	 * @param point
	 */
	private static Point updatePoint(Point point) {
		return new Point(POINT_UPDATED_X, POINT_UPDATED_Y, point.hasZ() ? POINT_UPDATED_Z : null, point.hasM() ? POINT_UPDATED_M : null);
	}

	/**
	 * Validate an updated point
	 * 
	 * @param point
	 */
	private static void validateUpdatedPoint(Point point) {
		TestCase.assertEquals(POINT_UPDATED_X, point.getX());
		TestCase.assertEquals(POINT_UPDATED_Y, point.getY());
		if (point.hasZ()) {
			TestCase.assertEquals(POINT_UPDATED_Z, point.getZ());
		}
		if (point.hasM()) {
			TestCase.assertEquals(POINT_UPDATED_M, point.getM());
		}
	}

	/**
	 * Test create
	 * 
	 * @param geoPackage
	 * @throws SQLException
	 */
	public static void testCreate(GeoPackage geoPackage) throws SQLException {

		GeometryColumnsDao geometryColumnsDao = geoPackage
				.getGeometryColumnsDao();

		if (geometryColumnsDao.isTableExists()) {
			List<GeometryColumns> results = geometryColumnsDao.queryForAll();

			for (GeometryColumns geometryColumns : results) {

				FeatureDao dao = geoPackage.getFeatureDao(geometryColumns);
				TestCase.assertNotNull(dao);

				FeatureResultSet cursor = dao.queryForAll();
				int count = cursor.getCount();
				if (count > 0) {

					// Choose random feature
					int random = (int) (Math.random() * count);
					cursor.moveToPosition(random);

					FeatureRow featureRow = cursor.getRow();
					cursor.close();

					// Create new row from existing
					long id = featureRow.getId();
					featureRow.resetId();
					long newRowId = dao.create(featureRow);

					TestCase.assertEquals(newRowId, featureRow.getId());

					// Verify original still exists and new was created
					featureRow = dao.queryForIdRow(id);
					TestCase.assertNotNull(featureRow);
					FeatureRow queryFeatureRow = dao.queryForIdRow(newRowId);
					TestCase.assertNotNull(queryFeatureRow);
					cursor = dao.queryForAll();
					TestCase.assertEquals(count + 1, cursor.getCount());
					cursor.close();

					// Create new row with copied values from another
					FeatureRow newRow = dao.newRow();
					for (FeatureColumn column : dao.getTable().getColumns()) {

						if (column.isPrimaryKey()) {
							try {
								newRow.setValue(column.getName(), 10);
								TestCase.fail("Set primary key on new row");
							} catch (GeoPackageException e) {
								// Expected
							}
						} else {
							newRow.setValue(column.getName(),
									featureRow.getValue(column.getName()));
						}
					}

					long newRowId2 = dao.create(newRow);

					TestCase.assertEquals(newRowId2, newRow.getId());

					// Verify new was created
					FeatureRow queryFeatureRow2 = dao.queryForIdRow(newRowId2);
					TestCase.assertNotNull(queryFeatureRow2);
					cursor = dao.queryForAll();
					TestCase.assertEquals(count + 2, cursor.getCount());
					cursor.close();
				}
				cursor.close();
			}
		}

	}

	/**
	 * Test delete
	 * 
	 * @param geoPackage
	 * @throws SQLException
	 */
	public static void testDelete(GeoPackage geoPackage) throws SQLException {

		GeometryColumnsDao geometryColumnsDao = geoPackage
				.getGeometryColumnsDao();

		if (geometryColumnsDao.isTableExists()) {
			List<GeometryColumns> results = geometryColumnsDao.queryForAll();

			for (GeometryColumns geometryColumns : results) {

				FeatureDao dao = geoPackage.getFeatureDao(geometryColumns);
				TestCase.assertNotNull(dao);

				FeatureResultSet cursor = dao.queryForAll();
				int count = cursor.getCount();
				if (count > 0) {

					// Choose random feature
					int random = (int) (Math.random() * count);
					cursor.moveToPosition(random);

					FeatureRow featureRow = cursor.getRow();
					cursor.close();

					// Delete row
					TestCase.assertEquals(1, dao.delete(featureRow));

					// Verify deleted
					FeatureRow queryFeatureRow = dao.queryForIdRow(featureRow
							.getId());
					TestCase.assertNull(queryFeatureRow);
					cursor = dao.queryForAll();
					TestCase.assertEquals(count - 1, cursor.getCount());
					cursor.close();
				}
				cursor.close();
			}
		}
	}

}
