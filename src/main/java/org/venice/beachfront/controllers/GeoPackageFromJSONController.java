/**
 * Copyright 2018, RadiantBlue Technologies, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
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
import org.venice.beachfront.services.GeoPackageConverter;

@Controller
public class GeoPackageFromJSONController {
	/**
	 * Injected dependency on the GeoPackage converter service.
	 */
	private GeoPackageConverter geoPackageConverter;
	
	/**
	 * Construct the GeoPackageFromJSONFileController.
	 * 
	 * @param geoPackageConverter
	 *            The GeoPackageConverter service instance to use
	 */
	@Autowired
	public GeoPackageFromJSONController(GeoPackageConverter geoPackageConverter) {
		this.geoPackageConverter = geoPackageConverter;
	}

	/**
	 * Core handler for converting from a Piazza ID to a GeoJSON file.
	 * 
	 * This controller provides a simple interface for immediate conversion of 
	 * GeoJSON data to a GeoPackage file.
	 * 
	 * {@link RequestMapping} decorated using the path {@code /convert},
	 * request method POST, and as producing "application/x-sqlite3". 
	 * 
	 * @param geojson a byte array containing the raw GeoJSON received via POST
	 * request body
	 * 
	 * @return A {@link ResponseEntity} that contains the converted GeoPackage data
	 */
	@RequestMapping(path = "/convert", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/x-sqlite3"} )
	@ResponseBody
	public ResponseEntity<byte[]> convertToGeoPackage(@RequestBody byte[] geojson) {
		byte[] gpkg = this.geoPackageConverter.apply(geojson);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("application", "x-sqlite3"));
		return new ResponseEntity<byte[]>(gpkg, headers, HttpStatus.OK);
	}

	/**
	 * Handler for errors during the GeoJSON to GPKG conversion.
	 * 
	 * General handler for a server error during the conversion. As a side
	 * effect, sets the response status code to 500 Server Error.

	 * @return String error message to return to the client
	 */
	@ExceptionHandler(GeoPackageConverter.GeoPackageConversionError.class)
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