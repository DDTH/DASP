/**
 *Copyright [2010-2011] [dennis zhuang(killme2008@gmail.com)]
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License. 
 *You may obtain a copy of the License at 
 *             http://www.apache.org/licenses/LICENSE-2.0 
 *Unless required by applicable law or agreed to in writing, 
 *software distributed under the License is distributed on an "AS IS" BASIS, 
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 *either express or implied. See the License for the specific language governing permissions and limitations under the License
 */
package com.google.code.hs4j.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Auto reconnect request
 * 
 * @author dennis
 * 
 */
public final class ReconnectRequest implements Delayed {

	private final InetSocketAddress remoteAddr;
	private int tries;

	private static final long MIN_RECONNECT_INTERVAL = 1000;

	private static final long MAX_RECONNECT_INTERVAL = 60 * 1000;

	private volatile long nextReconnectTimestamp;

	public ReconnectRequest(InetSocketAddress remoteAddr, int tries,
			long reconnectInterval) {
		super();
		this.remoteAddr = remoteAddr;
		this.setTries(tries); // record reconnect times
		reconnectInterval = this.normalInterval(reconnectInterval);
		this.nextReconnectTimestamp = System.currentTimeMillis()
				+ reconnectInterval;
	}

	private long normalInterval(long reconnectInterval) {
		if (reconnectInterval < MIN_RECONNECT_INTERVAL) {
			reconnectInterval = MIN_RECONNECT_INTERVAL;
		}
		if (reconnectInterval > MAX_RECONNECT_INTERVAL) {
			reconnectInterval = MAX_RECONNECT_INTERVAL;
		}
		return reconnectInterval;
	}

	public long getDelay(TimeUnit unit) {
		return unit.convert(this.nextReconnectTimestamp
				- System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public int compareTo(Delayed o) {
		ReconnectRequest other = (ReconnectRequest) o;
		if (this.nextReconnectTimestamp > other.nextReconnectTimestamp) {
			return 1;
		} else {
			return -1;
		}
	}

	public final InetSocketAddress getRemoteAddr() {
		return this.remoteAddr;
	}

	public void updateNextReconnectTimeStamp(long interval) {
		interval = this.normalInterval(interval);
		this.nextReconnectTimestamp = System.currentTimeMillis() + interval;
	}

	public final void setTries(int tries) {
		this.tries = tries;
	}

	public final int getTries() {
		return this.tries;
	}

}
