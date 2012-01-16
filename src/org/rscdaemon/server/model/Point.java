package org.rscdaemon.server.model;

import org.rscdaemon.server.util.Formulae;

public class Point {
	protected int x;
	protected int y;

	public static Point location(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException(
					"Point may not contain non negative values x:" + x + " y:"
							+ y);
		}
		return new Point(x, y);
	}

	protected Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int wildernessLevel() {
		int wild = 2203 - (y + (1776 - (944 * Formulae.getHeight(this))));
		if (x + 2304 >= 2640) {
			wild = -50;
		}
		if (wild > 0) {
			return 1 + wild / 6;
		}
		return 0;
	}

	public boolean inWilderness() {
		return wildernessLevel() > 0;
	}

	public boolean inModRoom() {
		return inBounds(64, 1639, 80, 1643);
	}

	public boolean inJail() {
		return inBounds(359, 3428, 362, 3430);
	}

	public boolean inWaitingRoom() {
		return inBounds(580, 661, 598, 668);
	}

	public boolean inDuelArena() {
		return inBounds(598, 699, 616, 712);
	}

	public final int getY() {
		return y;
	}

	public final int getX() {
		return x;
	}

	public final boolean equals(Object o) {
		if (o instanceof Point) {
			return this.x == ((Point) o).x && this.y == ((Point) o).y;
		}
		return false;
	}

	public int hashCode() {
		return x << 16 | y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public String getDescription() {
		if (inModRoom()) {
			return "Mod Room";
		}
		int wild = wildernessLevel();
		if (wild > 0) {
			return "Wilderness lvl-" + wild;
		}
		return "Unknown";
	}

	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

}
