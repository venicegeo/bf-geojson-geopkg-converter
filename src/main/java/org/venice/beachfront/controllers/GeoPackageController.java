package org.venice.beachfront.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.venice.beachfront.services.GeoPackageConverter;
import org.venice.beachfront.services.PiazzaApi;

/**
 * Main controller class for the conversion endpoint.
 * 
 * @version 1.0
 */
@Controller
public class GeoPackageController {
    /**
     * Injected dependency on the Piazza API service.
     */
    private PiazzaApi piazzaApi;

    /**
     * Injected dependency on the GeoPackage converter service.
     */
    private GeoPackageConverter geoPackageConverter;

    /**
     * Construct the GeoPackageController.
     * 
     * @param piazzaApi The Piazza API service instance to use
     * @param geoPackageConverter The GeoPackageConverter service instance to use
     */
    @Autowired
    public GeoPackageController(
        PiazzaApi piazzaApi,
        GeoPackageConverter geoPackageConverter
    ) {
        this.piazzaApi = piazzaApi;
        this.geoPackageConverter = geoPackageConverter;
    }

    /**
     * Core handler for converting from a Piazza ID to a GeoJSON file.
     * 
     * This controller serves to coordinate the process of querying Piazza
     * for GeoJSON data, calling the conversion step, then formatting the
     * outcome as a proper HTTP response.
     * 
     * It is composed in an asynchronous way using Java 8 {@link CompletableFuture}
     * chaining. This enables non-blocking operation regardless of the
     * webserver's execution mode (even if all requests are processed on the
     * same thread).
     * 
     * {@link RequestMapping} decorated using the path {@code /convert/<id>},
     * request method GET, and as producing "application/x-sqlite3". It should
     * result in a side-effect of setting the "Content-disposition" header on
     * the given response as appropriate.
     * 
     * @param id ID of the Piazza object being queried; filled in from the URL
     * @param pzKey Piazza API key to use; filled in from the "pzKey" query parameter
     * @param response {@link HttpServletResponse} object for the response
     * 
     * @return A {@link Future} that will contain the resulting SQLite data once
     *         the request is complete
     */
    @RequestMapping(
        path="/convert/{id}",
        method=RequestMethod.GET,
        produces={"application/x-sqlite3"})
    @ResponseBody
    public Future<byte[]> convertToGeoPackage (
        @PathVariable(name="id", required=true) String id,
        @RequestParam(name="pzKey", defaultValue="") String pzKey,
        final HttpServletResponse response
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (pzKey.length() < 1) {
                throw new MissingPiazzaKeyException();
            }
            return null;
        })
            .thenCompose(ok -> this.piazzaApi.getGeoJSON(id, pzKey))
            .thenApply(this.geoPackageConverter)
            .thenApply((data) -> {
                String filename = String.format("%s.gpkg", id);
                response.setHeader("Content-disposition", String.format("attachment; filename=%s", filename));
                return data;
            });
    }

    /**
     * Handler for missing the "pzApi" URL parameter.
     * 
     * As a side effect, sets the response status code to 400 Bad Request.
     * 
     * @param response {@link HttpServletResponse} response object
     * @return String error message to return to the client
     */
    @ExceptionHandler(MissingPiazzaKeyException.class)
    @ResponseBody
    private String missingPiazzaKeyHandler(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "Missing `pzApi` query parameter.";
    }

    /**
     * Handler for errors during the GeoJSON to GPKG conversion.
     * 
     * General handler for a server error during the conversion.
     * As a side efect, sets the response status code to 500 Server Error.
     * 
     * @param response {@link HttpServletResponse} response object
     * @return String error message to return to the client
     */
    @ExceptionHandler(GeoPackageConverter.GeoPackageConversionError.class)
    @ResponseBody
    private String conversionFailedHandler(Exception ex, HttpServletResponse response) {
        StringWriter traceWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(traceWriter));
        String trace = traceWriter.getBuffer().toString();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return String.format("Failed to convert to GPKG:\n\n%s", trace);
    }

    /**
     * Custom exception for handling a missing Piazza key.
     * 
     * Simple subclass of {@link RuntimeException}
     */
    @SuppressWarnings("serial")
    public class MissingPiazzaKeyException extends RuntimeException {}

}