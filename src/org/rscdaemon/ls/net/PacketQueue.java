package org.rscdaemon.ls.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronized packet queue
 * 
 * @author Devin
 */
public class PacketQueue<T extends Packet> {
	/**
	 * The list of packets in the queue
	 */
	private ArrayList<T> packets = new ArrayList<T>();

	/**
	 * Adds a packet to the queue
	 */
	public void add(T p) {
		synchronized (packets) {
			packets.add(p);
		}
	}

	/**
	 * Returns if there is packets to process
	 */
	public boolean hasPackets() {
		return !packets.isEmpty();
	}

	/**
	 * Returns the packets currently in the list and removes them from the
	 * backing store
	 */
	public List<T> getPackets() {
		List<T> tmpList;
		synchronized (packets) {
			tmpList = (List<T>) packets.clone();
			packets.clear();
		}
		return tmpList;
	}
}
