package com.nxtdelivery.quickF5.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import com.nxtdelivery.quickF5.QuickF5;

import net.minecraft.util.EnumChatFormatting;

public class UpdateChecker {
	public static String latestVersion;
	public static boolean updateNeeded(String currentVersion) {
		try {
			//URL url = new URL("https://raw.githubusercontent.com/nxtdaydelivery/quickF5/master/update.properties");
			//scn = new Scanner(url.openStream());
			//String text = new Scanner(url.openStream() ).useDelimiter("\\A").next();
			Properties prop = new Properties();
			//prop.load(text);
			InputStream input = new URL("https://raw.githubusercontent.com/nxtdaydelivery/quickF5/master/update.properties").openStream();
			prop.load(input);
			latestVersion = prop.getProperty("versionLatest");
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
