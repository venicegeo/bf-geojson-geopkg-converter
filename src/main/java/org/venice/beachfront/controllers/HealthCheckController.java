package org.venice.beachfront.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.venice.beachfront.services.Uptime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Controller class for a simple health check.
 * 
 * @version 1.0
 */
@Controller
public class HealthCheckController {
	private Uptime uptimeService;
	
	@Autowired
	public HealthCheckController(Uptime uptimeService) {
		this.uptimeService = uptimeService;
	}
	
	@RequestMapping(path = "/", method = RequestMethod.GET, produces = { "application/json" })
	@ResponseBody
	public HealthCheckData healthCheckUptime() {
		HealthCheckData data = new HealthCheckData();
		data.setUptime(this.uptimeService.getUptimeSeconds());
		return data;
	}
	
	public static class HealthCheckData {
		@JsonProperty("uptime") double uptime;

		/**
		 * @return the uptimeMillis
		 */
		public double getUptime() {
			return this.uptime;
		}

		/**
		 * @param uptimeMillis the uptimeMillis to set
		 */
		public void setUptime(double uptime) {
			this.uptime = uptime;
		}
		
	}

}
