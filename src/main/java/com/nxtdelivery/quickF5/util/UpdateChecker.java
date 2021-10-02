package com.nxtdelivery.quickF5.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import com.nxtdelivery.quickF5.QuickF5;

public class UpdateChecker {
	public static String latestVersion;
	public static boolean updateNeeded(String currentVersion) {
		try {
			Properties prop = new Properties();
			InputStream input = new URL("https://raw.githubusercontent.com/nxtdaydelivery/quickF5/master/update.properties").openStream();
			prop.load(input);
			latestVersion = prop.getProperty("versionLatest");
			if(latestVersion.equals("0")) {
				QuickF5.LOGGER.warn("version checker is 0. This is a feature added to prevent errors. Version checker disabled.");
				return false;
			}
			if(!currentVersion.equals(latestVersion)) {
				QuickF5.LOGGER.warn("a newer version " + latestVersion + " is availible! Please consider updating! (" + currentVersion + ")");
				return true;
			} else {
				QuickF5.LOGGER.info("already using the newest version (" + latestVersion + ")");
				return false;
			}
		} catch(Exception e) {
			QuickF5.LOGGER.error(e);
			QuickF5.LOGGER.error("failed to check version. assuming latest version.");
			return false;
		}
	}
}
