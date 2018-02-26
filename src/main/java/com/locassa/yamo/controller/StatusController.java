package com.locassa.yamo.controller;

import com.locassa.yamo.model.dto.StringDTO;
import com.locassa.yamo.util.YamoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
@Api(description = "Endpoints to manage the server health.")
public class StatusController {

    private static final Logger logger = Logger.getLogger(StatusController.class);

    @ApiOperation(value = "Sign in", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It gives details about the server status.")
    @RequestMapping(method = RequestMethod.GET)
    public void getHealth(HttpServletResponse response) throws IOException {
        logger.debug("getHealth");
        response.sendRedirect("health");
    }

    @ApiOperation(value = "Sign in", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It returns the current server version.")
    @RequestMapping(value = "version", method = RequestMethod.GET)
    public StringDTO getVersion() throws IOException {
        logger.debug("getVersion");
        return new StringDTO("1");
    }

    @ApiOperation(value = "Sign in", notes = YamoUtils.INFO_ABOUT_NO_AUTH_KEY + "It returns the current server date and time in milliseconds (UNIX).")
    @RequestMapping(value = "timestamp", method = RequestMethod.GET)
    public StringDTO getTimestamp() throws IOException {
        logger.debug("getTimestamp");
        return new StringDTO(String.valueOf(YamoUtils.now().getTime()));
    }

}
