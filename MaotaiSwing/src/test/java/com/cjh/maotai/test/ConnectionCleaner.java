package com.cjh.maotai.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ConnectionCleaner implements Runnable {

	final PoolingHttpClientConnectionManager pm;
	
	AtomicBoolean taskFinishFlag;

	public ConnectionCleaner(PoolingHttpClientConnectionManager pm, AtomicBoolean taskFinishFlag) {
		this.pm = pm;
		this.taskFinishFlag = taskFinishFlag;
	}

	@Override
	public void run() {
		try {
			while (!taskFinishFlag.get()) {
				synchronized (this) {
					wait(5000);
					// Close expired connections
					pm.closeExpiredConnections();
					// Optionally, close connections
					// that have been idle longer than 10 sec
					pm.closeIdleConnections(10, TimeUnit.SECONDS);
				}
			}
		} catch (InterruptedException ex) {
			// terminate
		}
	}

}
