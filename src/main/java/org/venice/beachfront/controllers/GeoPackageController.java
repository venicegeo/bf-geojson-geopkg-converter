package org.venice.beachfront.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.venice.beachfront.services.GeoPackageConverter;
import org.venice.beachfront.services.PiazzaApi;

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
        produces={"text/plain", "application/x-sqlite3"})
    @ResponseBody
    public DeferredResult<byte[]> convertToGeoPackage(
        @PathVariable(name="id", required=true) String id,
        @RequestParam(name="pzKey", defaultValue="") String pzKey,
        final HttpServletResponse response
    ) {
        final DeferredResult<byte[]> result = new DeferredResult<>();

        if (pzKey.length() < 1) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            result.setResult("Must specify `pzKey` query parameter.".getBytes());  
            return result;
        }

        this.piazzaApi.getGeoJSON(id, pzKey)
            .map(json -> {
                return this.piazzaApi.geoJSONtoFeatureCollection(json);
            })
            .map(fc -> {
                return this.geoPackageConverter.geoJSONToGeoPackage(fc);
            })
            .subscribe(geoPackageData -> {
                result.setResult(geoPackageData);
            });

        return result;
    }
}