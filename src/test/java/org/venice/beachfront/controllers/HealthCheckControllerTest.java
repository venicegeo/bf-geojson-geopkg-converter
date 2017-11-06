package org.venice.beachfront.controllers;

import org.mockito.Mockito;
import org.venice.beachfront.services.Uptime;

import junit.framework.TestCase;

public class HealthCheckControllerTest extends TestCase {
	Uptime mockUptimeService;
	HealthCheckController healthCheckController;
	
	double mockUptimeValue = 123.123;
	
	public void setUp() {
		this.mockUptimeService = Mockito.mock(Uptime.class);
		Mockito.when(this.mockUptimeService.getUptimeSeconds()).thenReturn(mockUptimeValue);
		
		this.healthCheckController = new HealthCheckController(this.mockUptimeService);
	}
	
	public void testReturnsCorrectUptime() {
		double uptime = this.healthCheckController.healthCheckUptime().getUptime();
		TestCase.assertEquals(this.mockUptimeValue, uptime, 0.0001);
	}
}
