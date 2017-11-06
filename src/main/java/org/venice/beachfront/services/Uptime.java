package org.venice.beachfront.services;

/**
 * Interface for a simple service reporting server uptime
 * 
 * @version 1.0
 */

public interface Uptime {
	/**
	 * @return uptime seconds at call time
	 */
	public double getUptimeSeconds();
}
