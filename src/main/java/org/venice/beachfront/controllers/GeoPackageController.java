package org.venice.beachfront.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;

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
import org.springframework.web.context.request.async.DeferredResult;
import org.venice.beachfront.services.GeoPackageConverter;
import org.venice.beachfront.services.PiazzaApi;
import org.venice.beachfront.services.GeoPackageConverter.GeoPackageConversionError;

import rx.Observable;

@Controller
public class GeoPackageController {
    private PiazzaApi piazzaApi;
    private GeoPackageConverter geoPackageConverter;

    @Autowired
    public GeoPackageController(
        PiazzaApi piazzaApi,
        GeoPackageConverter geoPackageConverter
    ) {
        this.piazzaApi = piazzaApi;
        this.geoPackageConverter = geoPackageConverter;
    }

    @RequestMapping(
        path="/convert/{id}",
        method=RequestMethod.GET,
        produces={"application/x-sqlite3"})
    @ResponseBody
    public DeferredResult<byte[]> convertToGeoPackage (
        @PathVariable(name="id", required=true) String id,
        @RequestParam(name="pzKey", defaultValue="") String pzKey,
        final HttpServletResponse response
    ) {
        final DeferredResult<byte[]> result = new DeferredResult<>();
        if (pzKey.length() < 1) {
            result.setErrorResult(new MissingPiazzaKeyException());
            return result;
        }

        this.piazzaApi.getGeoJSON(id, pzKey)
            .map(json -> {
                return this.piazzaApi.geoJSONtoFeatureCollection(json);
            })
            .flatMap(fc -> {
                try {
                    byte[] gpkg = this.geoPackageConverter.geoJSONToGeoPackage(fc);
                    return Observable.just(gpkg);
                } catch (GeoPackageConversionError err) {
                    return Observable.error(err);
                }
            })
            .subscribe(geoPackageData -> {
                result.setResult(geoPackageData);
            }, err -> {
                result.setErrorResult(err);
            });

        return result;
    }

    @ExceptionHandler(MissingPiazzaKeyException.class)
    @ResponseBody
    private String missingPiazzaKeyHandler(HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "Missing `pzApi` query parameter.";
    }

    @ExceptionHandler(GeoPackageConverter.GeoPackageConversionError.class)
    @ResponseBody
    private String conversionFailedHandler(Exception ex, HttpServletResponse response) {
        StringWriter traceWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(traceWriter));
        String trace = traceWriter.getBuffer().toString();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return String.format("Failed to convert to GPKG:\n\n%s", trace);
    }

    @SuppressWarnings("serial")
    private class MissingPiazzaKeyException extends RuntimeException {}

}