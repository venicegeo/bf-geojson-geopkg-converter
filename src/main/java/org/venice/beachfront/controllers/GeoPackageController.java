package org.venice.beachfront.controllers;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jni.Time;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

@Controller
public class GeoPackageController {
    @RequestMapping(
        path="/convert",
        method=RequestMethod.GET,
        produces={"text/plain", "application/x-sqlite3"})
    @ResponseBody
    public DeferredResult<byte[]> convertToGeoPackage(
        HttpServletResponse response
    ) {
        final DeferredResult<byte[]> result = new DeferredResult<>();
        
        response.setHeader("Content-Disposition", "attachment; filename=foo.txt");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            result.setResult("hello world".getBytes());
        }).start();

        return result;
    }
}