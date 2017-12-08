package org.venice.beachfront.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.venice.beachfront.services.ShapefileConverter;

@Controller
public class ShapefileFromJSONController {
	/**
	 * Injected dependency on the Shapefile converter service.
	 */
	private ShapefileConverter shapefileConverter;
	
	/**
	 * Construct the ShapefileFromJSONFileController.
	 * 
	 * @param shapefileConverter
	 *            The ShapefileConverter service instance to use
	 */
	@Autowired
	public ShapefileFromJSONController(ShapefileConverter shapefileConverter) {
		this.shapefileConverter = shapefileConverter;
	}

	/**
	 * Core handler for converting from a Piazza ID to a GeoJSON file.
	 * 
	 * This controller provides a simple interface for immediate conversion of 
	 * GeoJSON data to a Shapefile (.zip containing .shp, etc.).
	 * 
	 * {@link RequestMapping} decorated using the path {@code /convertshp},
	 * request method POST, and as producing "application/zip". 
	 * 
	 * @param geojson a byte array containing the raw GeoJSON received via POST
	 * request body
	 * 
	 * @return A {@link ResponseEntity} that contains the converted Shapefile data
	 */
	@RequestMapping(path = "/convertshp", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/zip"} )
	@ResponseBody
	public ResponseEntity<byte[]> convertToShapefile(@RequestBody byte[] geojson) {
		byte[] gpkg = this.shapefileConverter.apply(geojson);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "zip"));
		return new ResponseEntity<byte[]>(gpkg, headers, HttpStatus.OK);
	}

	/**
	 * Handler for errors during the GeoJSON to Shapefile conversion.
	 * 
	 * General handler for a server error during the conversion. As a side
	 * effect, sets the response status code to 500 Server Error.

	 * @return String error message to return to the client
	 */
	@ExceptionHandler(ShapefileConverter.ShapefileConversionError.class)
	private ResponseEntity<String> conversionFailedHandler(Exception ex) {
		StringWriter traceWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(traceWriter));
		String trace = traceWriter.getBuffer().toString();
		String responseText = String.format("Failed to convert to GPKG:\n\n%s\n", trace);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<String>(responseText, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}