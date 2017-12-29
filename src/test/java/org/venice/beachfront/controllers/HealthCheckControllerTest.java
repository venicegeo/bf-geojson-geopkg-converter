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
