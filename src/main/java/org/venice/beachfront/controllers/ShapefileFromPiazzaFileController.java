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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.venice.beachfront.services.PiazzaApi;
import org.venice.beachfront.services.ShapefileConverter;

/**
 * Main controller class for the conversion endpoint.
 * 
 * @version 1.0
 */
@Controller
public class ShapefileFromPiazzaFileController {
	/**
	 * Injected dependency on the Piazza API service.
	 */
	private PiazzaApi piazzaApi;

	/**
	 * Injected dependency on the Shapefile converter service.
	 */
	private ShapefileConverter shapefileConverter;

	/**
	 * Construct the ShapefileFromPiazzaFileController.
	 * 
	 * @param piazzaApi
	 *            The Piazza API service instance to use
	 * @param shapefileConverter
	 *            The ShapefileConverter service instance to use
	 */
	@Autowired
	public ShapefileFromPiazzaFileController(PiazzaApi piazzaApi, ShapefileConverter shapefileConverter) {
		this.piazzaApi = piazzaApi;
		this.shapefileConverter = shapefileConverter;
	}

	/**
	 * Core handler for converting from a Piazza ID to a Shapefile.
	 * 
	 * This controller serves to coordinate the process of querying Piazza for
	 * GeoJSON data, calling the conversion step, then formatting the outcome as
	 * a proper HTTP response.
	 * 
	 * It is composed in an asynchronous way using Java 8
	 * {@link CompletableFuture} chaining. This enables non-blocking operation
	 * regardless of the webserver's execution mode (even if all requests are
	 * processed on the same thread).
	 * 
	 * {@link RequestMapping} decorated using the path {@code /convertshp/<id>},
	 * request method GET, and as producing "application/zip". It should
	 * result in a side-effect of setting the "Content-disposition" header on
	 * the given response as appropriate.
	 * 
	 * @param id
	 *            ID of the Piazza object being queried; filled in from the URL
	 * @param pzKey
	 *            Piazza API key to use; filled in from the "pzKey" query
	 *            parameter
	 * @param response
	 *            {@link HttpServletResponse} object for the response
	 * 
	 * @return A {@link Future} that will contain the resulting SQLite data once
	 *         the request is complete
	 */
	@RequestMapping(path = "/convertshp/{id}", method = RequestMethod.GET, produces = { "application/zip" })
	@ResponseBody
	public Future<byte[]> convertToShapefile(
			@PathVariable(name = "id", required = true) String id,
			@RequestParam(name = "pzKey", defaultValue = "") String pzKey, final HttpServletResponse response) {
		return CompletableFuture.supplyAsync(() -> {
			if (pzKey.length() < 1) {
				throw new MissingPiazzaKeyException();
			}
			return null;
		}).thenCompose(ok -> this.piazzaApi.getGeoJSON(id, pzKey)).thenApply(this.shapefileConverter)
				.thenApply((data) -> {
					String filename = String.format("%s.gpkg", id);
					response.setHeader("Content-disposition", String.format("attachment; filename=%s", filename));
					response.setHeader("Content-type", "application/zip");
					return data;
				});
	}

	/**
	 * Handler for missing the "pzApi" URL parameter.
	 * 
	 * As a side effect, sets the response status code to 400 Bad Request.
	 *
	 * @return String error message to return to the client
	 */
	@ExceptionHandler(MissingPiazzaKeyException.class)
	private ResponseEntity<String> missingPiazzaKeyHandler() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);

		return new ResponseEntity<String>("Missing `pzKey` query parameter.\n", headers, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handler for the call to Piazza for an object fails due to an arbitrary HTTP error
	 * @param ex exception containing the failed response entity, which gets passed through
	 * @return
	 */
	@ExceptionHandler(PiazzaApi.HttpRequestFailedException.class)
	private ResponseEntity<byte[]> fetchingPiazzaObjectFailed(PiazzaApi.HttpRequestFailedException ex) {
		return ex.getResponseEntity();
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
		String responseText = String.format("Failed to convert to Shapefile:\n\n%s\n", trace);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		return new ResponseEntity<String>(responseText, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Custom exception for handling a missing Piazza key.
	 * 
	 * Simple subclass of {@link RuntimeException}
	 */
	@SuppressWarnings("serial")
	public class MissingPiazzaKeyException extends RuntimeException {
	}

}