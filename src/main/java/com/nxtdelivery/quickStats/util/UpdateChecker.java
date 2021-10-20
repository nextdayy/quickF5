package com.nxtdelivery.quickStats.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import com.nxtdelivery.quickStats.*;

public class UpdateChecker {
	public static String latestVersion;

	public static boolean updateNeeded(String currentVersion) {
		try {
			Properties prop = new Properties();
			InputStream input = new URL(
					"https://raw.githubusercontent.com/nxtdaydelivery/quickStats/master/update.properties")
							.openStream();
			prop.load(input);
			latestVersion = prop.getProperty("versionLatest");
			if (latestVersion.equals("0")) {
				QuickStats.LOGGER.warn(
						"version checker is 0. This is a feature added to prevent errors. Version checker disabled.");
				return false;
			}
			if (currentVersion.contains("beta")) {
				QuickStats.LOGGER.warn("beta build detected. This build might be unstable, use at your own risk!");
				QuickStats.betaFlag = true;
			}
			if (!currentVersion.equals(latestVersion)) {
				QuickStats.LOGGER.warn("a newer version " + latestVersion + " is availible! Please consider updating! ("
						+ currentVersion + ")");
				return true;
			} else {
				QuickStats.LOGGER.info("already using the newest version (" + latestVersion + ")");
				return false;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			QuickStats.LOGGER.error(e);
			QuickStats.LOGGER.error("failed to check version. assuming latest version.");
			return false;
		}
	}
}
