package org.rscdaemon.server.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.rscdaemon.server.Server;
import org.rscdaemon.server.model.World;

/**
 * Global logger
 * 
 * @author Devin
 */

public class Logger {
	/**
	 * World instance
	 */
	private static final World world = World.getWorld();
	private static Map<String, PrintStream> outStreams = new HashMap<String, PrintStream>();

	public static void print(Object o) {
		System.out.println(o.toString());
	}

	public static synchronized void log(String f, Object o) {
		String s = prefix() + o.toString();
		try {
			PrintStream tmpStream = outStreams.get(f);
			if (tmpStream == null) {
				tmpStream = newStream(f);
			}
			tmpStream.println(s);
		} catch (IOException ex) {
			Logger.error("Failed to log to " + f + ", msg='" + s + "'");
		}
	}

	private static String prefix() {
		return "[" + time() + "] [" + Thread.currentThread().getName() + "] ";
	}

	private static String time() {
		return Formulae.timeSince(Server.START_TIME);
	}

	private static PrintStream newStream(String file) throws IOException {
		File f = new File("log/" + file);
		if (f.exists())
			rotate(f);
		PrintStream p = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(new File("log/" + file)), 1024));
		outStreams.put(file, p);
		return p;
	}

	private static void rotate(File f) throws IOException {
		f.renameTo(new File("log/old/" + System.currentTimeMillis() + "-"
				+ f.getName()));
	}

	public static void connection(Object o) {
		System.out.println(o.toString());
	}

	public static void mod(Object o) {
		world.getServer().getLoginConnector().getActionSender()
				.logAction(o.toString(), 3);
	}

	public static void event(Object o) {
		world.getServer().getLoginConnector().getActionSender()
				.logAction(o.toString(), 1);
	}

	public static void error(Object o) {
		if (o instanceof Exception) {
			Exception e = (Exception) o;
			e.printStackTrace();
			if (world == null || !world.getServer().isInitialized()) {
				System.exit(1);
			} else {
				world.getServer().kill();
			}
			return;
		}
		world.getServer().getLoginConnector().getActionSender()
				.logAction(o.toString(), 2);
	}
}
