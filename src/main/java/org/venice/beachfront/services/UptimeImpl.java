package org.venice.beachfront.services;

import org.springframework.stereotype.Service;

@Service
public class UptimeImpl implements Uptime {
	private long startTimeMillis = System.currentTimeMillis();
	
	@Override
	public double getUptimeSeconds() {
		long uptimeMillis = System.currentTimeMillis() - this.startTimeMillis;
		return ((double)uptimeMillis) / 1000;
	}

}
