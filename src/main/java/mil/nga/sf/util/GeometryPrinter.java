package mil.nga.sf.util;

import java.util.List;

import mil.nga.sf.CircularString;
import mil.nga.sf.CompoundCurve;
import mil.nga.sf.Geometry;
import mil.nga.sf.GeometryCollection;
import mil.nga.sf.GeometryType;
import mil.nga.sf.LineString;
import mil.nga.sf.MultiLineString;
import mil.nga.sf.MultiPoint;
import mil.nga.sf.MultiPolygon;
import mil.nga.sf.Point;
import mil.nga.sf.Polygon;
import mil.nga.sf.PolyhedralSurface;
import mil.nga.sf.TIN;
import mil.nga.sf.Triangle;

/**
 * String representation of a Geometry
 * 
 * @author osbornb
 */
public class GeometryPrinter {

	/**
	 * Get Geometry Information as a String
	 * 
	 * @param geometry
	 *            geometry
	 * @return geometry String
	 */
	public static String getGeometryString(Geometry geometry) {

		StringBuilder message = new StringBuilder();

		GeometryType geometryType = geometry.getGeometryType();
		switch (geometryType) {
		case POINT:
			addPointMessage(message, (Point) geometry);
			break;
		case LINESTRING:
			addLineStringMessage(message, (LineString) geometry);
			break;
		case POLYGON:
			addPolygonMessage(message, (Polygon) geometry);
			break;
		case MULTIPOINT:
			addMultiPointMessage(message, (MultiPoint) geometry);
			break;
		case MULTILINESTRING:
			addMultiLineStringMessage(message, (MultiLineString) geometry);
			break;
		case MULTIPOLYGON:
			addMultiPolygonMessage(message, (MultiPolygon) geometry);
			break;
		case CIRCULARSTRING:
			addLineStringMessage(message, (CircularString) geometry);
			break;
		case COMPOUNDCURVE:
			addCompoundCurveMessage(message, (CompoundCurve) geometry);
			break;
		case POLYHEDRALSURFACE:
			addPolyhedralSurfaceMessage(message, (PolyhedralSurface) geometry);
			break;
		case TIN:
			addPolyhedralSurfaceMessage(message, (TIN) geometry);
			break;
		case TRIANGLE:
			addPolygonMessage(message, (Triangle) geometry);
			break;
		case GEOMETRYCOLLECTION:
			@SuppressWarnings("unchecked")
			GeometryCollection<Geometry> geomCollection = (GeometryCollection<Geometry>) geometry;
			message.append("Geometries: " + geomCollection.numGeometries());
			List<Geometry> geometries = geomCollection.getGeometries();
			for (int i = 0; i < geometries.size(); i++) {
				Geometry subGeometry = geometries.get(i);
				message.append("\n\n");
				message.append(Geometry.class.getSimpleName() + " " + (i + 1));
				message.append("\n");
				message.append(subGeometry.getGeometryType().getName());
				message.append("\n");
				message.append(getGeometryString(subGeometry));
			}
			break;
		default:
		}

		return message.toString();
	}

	/**
	 * Add Point message
	 * 
	 * @param message
	 * @param point
	 */
	private static void addPointMessage(StringBuilder message, Point point) {
		message.append("Latitude: ").append(point.getY());
		message.append("\nLongitude: ").append(point.getX());
	}

	/**
	 * Add MultiPoint message
	 * 
	 * @param message
	 * @param multiPoint
	 */
	private static void addMultiPointMessage(StringBuilder message,
			MultiPoint multiPoint) {
		message.append(Point.class.getSimpleName() + "s: "
				+ multiPoint.numPoints());
		List<Point> points = multiPoint.getPoints();
		for (int i = 0; i < points.size(); i++) {
			Point point = points.get(i);
			message.append("\n\n");
			message.append(Point.class.getSimpleName() + " " + (i + 1));
			message.append("\n");
			addPointMessage(message, point);
		}
	}

	/**
	 * Add LineString message
	 * 
	 * @param message
	 * @param lineString
	 */
	private static void addLineStringMessage(StringBuilder message,
			LineString lineString) {
		message.append(Point.class.getSimpleName() + "s: "
				+ lineString.numPoints());
		for (Point point : lineString.getPoints()) {
			message.append("\n\n");
			addPointMessage(message, point);
		}
	}

	/**
	 * Add MultiLineString message
	 * 
	 * @param message
	 * @param multiLineString
	 */
	private static void addMultiLineStringMessage(StringBuilder message,
			MultiLineString multiLineString) {
		message.append(LineString.class.getSimpleName() + "s: "
				+ multiLineString.numLineStrings());
		List<LineString> lineStrings = multiLineString.getLineStrings();
		for (int i = 0; i < lineStrings.size(); i++) {
			LineString lineString = lineStrings.get(i);
			message.append("\n\n");
			message.append(LineString.class.getSimpleName() + " " + (i + 1));
			message.append("\n");
			addLineStringMessage(message, lineString);
		}
	}

	/**
	 * Add Polygon message
	 * 
	 * @param message
	 * @param polygon
	 */
	private static void addPolygonMessage(StringBuilder message, Polygon polygon) {
		message.append("Rings: " + polygon.numRings());
		List<LineString> rings = polygon.getRings();
		for (int i = 0; i < rings.size(); i++) {
			LineString ring = rings.get(i);
			message.append("\n\n");
			if (i > 0) {
				message.append("Hole " + i);
				message.append("\n");
			}
			addLineStringMessage(message, ring);
		}
	}

	/**
	 * Add MultiPolygon message
	 * 
	 * @param message
	 * @param multiPolygon
	 */
	private static void addMultiPolygonMessage(StringBuilder message,
			MultiPolygon multiPolygon) {
		message.append(Polygon.class.getSimpleName() + "s: "
				+ multiPolygon.numPolygons());
		List<Polygon> polygons = multiPolygon.getPolygons();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			message.append("\n\n");
			message.append(Polygon.class.getSimpleName() + " " + (i + 1));
			message.append("\n");
			addPolygonMessage(message, polygon);
		}
	}

	/**
	 * Add CompoundCurve message
	 * 
	 * @param message
	 * @param compoundCurve
	 */
	private static void addCompoundCurveMessage(StringBuilder message,
			CompoundCurve compoundCurve) {
		message.append(LineString.class.getSimpleName() + "s: "
				+ compoundCurve.numLineStrings());
		List<LineString> lineStrings = compoundCurve.getLineStrings();
		for (int i = 0; i < lineStrings.size(); i++) {
			LineString lineString = lineStrings.get(i);
			message.append("\n\n");
			message.append(LineString.class.getSimpleName() + " " + (i + 1));
			message.append("\n");
			addLineStringMessage(message, lineString);
		}
	}

	/**
	 * Add PolyhedralSurface message
	 * 
	 * @param message
	 * @param polyhedralSurface
	 */
	private static void addPolyhedralSurfaceMessage(StringBuilder message,
			PolyhedralSurface polyhedralSurface) {
		message.append(Polygon.class.getSimpleName() + "s: "
				+ polyhedralSurface.numPolygons());
		List<Polygon> polygons = polyhedralSurface.getPolygons();
		for (int i = 0; i < polygons.size(); i++) {
			Polygon polygon = polygons.get(i);
			message.append("\n\n");
			message.append(Polygon.class.getSimpleName() + " " + (i + 1));
			message.append("\n");
			addPolygonMessage(message, polygon);
		}
	}

}
