package org.rscdaemon.ls.net;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles internal server ip bans.
 * 
 * @author Devin
 */

public class Ipban {

	public static ArrayList<String> ipbans = new ArrayList<String>();

	static void saveIPBans() {
		try {
			PrintWriter banIP = new PrintWriter(new FileWriter("ipbans.txt"));
			for (int i = 0; i < ipbans.size(); i++) {
				banIP.println(ipbans.get(i).toString());
			}
			banIP.close();
		} catch (Exception e) {
			System.out.println("Could not save ip ban list");
		}
	}

	public static void loadIPBans() {
		try {
			ipbans.clear();
			Scanner bannedIP = new Scanner(new File("ipbans.txt"));
			while (bannedIP.hasNextLine()) {
				String s = bannedIP.nextLine();
				ipbans.add(s);
			}
			bannedIP.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void removeIPBan(String ip) {
		ipbans.remove(ip);
		saveIPBans();
	}

	public static void addIPBan(String ip) {
		ipbans.add(ip);
		saveIPBans();
	}

}