package org.rscdaemon.server.util;

/**
 * Finds and sets all configurations for server.
 * 
 * @author Devin
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	static {
		loadEnv();
	}

	/**
	 * Called to load config settings from the given file
	 * 
	 * @param file
	 *            the xml file to load settings from
	 * @throws IOException
	 *             if an i/o error occurs
	 */
	public static void initConfig(String file) throws IOException {
		START_TIME = System.currentTimeMillis();

		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(file));

		SERVER_VERSION = Integer.parseInt(props.getProperty("version"));
		SERVER_NAME = props.getProperty("name");
		SERVER_IP = props.getProperty("ip");
		SERVER_PORT = Integer.parseInt(props.getProperty("port"));
		SERVER_LOCATION = props.getProperty("location");

		MAX_PLAYERS = Integer.parseInt(props.getProperty("maxplayers"));

		LS_IP = props.getProperty("lsip");
		LS_PORT = Integer.parseInt(props.getProperty("lsport"));
		LS_PASSWORD = props.getProperty("lspass");
		SERVER_NUM = Integer.parseInt(props.getProperty("servernum"));

		props.clear();
	}

	/**
	 * Called to load RSCD_HOME and CONF_DIR Used to be situated in
	 * PersistenceManager
	 */
	private static void loadEnv() {
		String home = System.getenv("RSCD_HOME");
		if (home == null) { // the env var hasnt been set, fall back to .
			home = ".";
		}
		CONF_DIR = home + File.separator + "conf" + File.separator + "server";
		RSCD_HOME = home;
	}

	public static String SERVER_IP, SERVER_NAME, RSCD_HOME, CONF_DIR,
			SERVER_LOCATION, LS_IP, LS_PASSWORD;
	public static int SERVER_PORT, SERVER_VERSION, MAX_PLAYERS, LS_PORT,
			SERVER_NUM;
	public static long START_TIME;
}
