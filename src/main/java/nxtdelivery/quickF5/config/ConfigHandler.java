package nxtdelivery.quickF5.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.Loader;


public class ConfigHandler {
	static File configMain = new File(Loader.instance().getConfigDir(), "QuickF5.properties");
	public static int key;
	public static int key2;
	public static String mode;
	public static boolean returnRelease;
	public static boolean modEnabled;
	
	public static void ConfigLoad() {
		if(configMain.exists() == true) {
			FileReader reader;
			try {
				reader = new FileReader(configMain);
				Properties prop = new Properties();
			    prop.load(reader);
			    
			    key = Integer.parseInt(prop.getProperty("key"));
			    System.out.println("[QuickF5] Key read from config is " + key);
			    key2 = Integer.parseInt(prop.getProperty("key2"));
			    System.out.println("[QuickF5] Second key read from config is " + key2);
			    
			    mode = prop.getProperty("mode");
			    System.out.println("[QuickF5] Mode read from config is " + mode);
			    if(ConfigHandler.mode.equals("hold")) {
					returnRelease = true;
				} else { returnRelease = false; }
			    
			    modEnabled = Boolean.parseBoolean(prop.getProperty("enabled"));
			    if(modEnabled == true) {
			    	System.out.println("[QuickF5] State read from config is ENABLED");
			    } 
			    if(modEnabled == false) { 
			    	System.out.println("[QuickF5] State read from config is DISABLED. Keybinds wont work.");
			    }
			    
			    reader.close();
			    System.out.println("[QuickF5] Config read complete");
			} catch (FileNotFoundException e) {
				createConfig();
			} catch (IOException e) {
				System.out.println(e);
			} catch (Exception e) {
				createConfig();
				System.out.println(e);
			}
		    
		}
		else {
			createConfig();
		}
	}
	private static void createConfig() {
		try {
			try {
				configMain.createNewFile();
			}
			catch(Exception e) {
				System.out.println(e);
			}
			System.out.println("[QuickF5] Config file does exist. assuming first startup or corruption of file.");
			System.out.println("[QuickF5] Generating new config file...");
			FileWriter writer = new FileWriter(configMain);
			Properties prop = new Properties();
			prop.setProperty("mode", "hold");
			prop.setProperty("key", "33");				// key F
			prop.setProperty("key2", "47");				// key V
			prop.setProperty("enabled", "true");
			prop.store(writer, "QuickF5 configuration");
			writer.close();
			System.out.println("[QuickF5] Config file created");
			ConfigLoad();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	public static void writeConfig(String type, String data) {
		try {	
			FileWriter writer = new FileWriter(configMain);
			Properties prop = new Properties();
			System.out.println("[QuickF5] attempting to write new data to config file");
			if(type == "key") {
				prop.setProperty("mode", mode);
				prop.setProperty(type, data);
				prop.setProperty("key2", Integer.toString(key2));
				prop.setProperty("enabled", Boolean.toString(modEnabled));
			}
			if(type == "key2") {
				prop.setProperty("mode", mode);
				prop.setProperty("key", Integer.toString(key));
				prop.setProperty(type, data);
				prop.setProperty("enabled", Boolean.toString(modEnabled));
			}
			if(type == "mode") {
				prop.setProperty(type, data);
				prop.setProperty("key", Integer.toString(key));
				prop.setProperty("key2", Integer.toString(key2));
				prop.setProperty("enabled", Boolean.toString(modEnabled));
			}
			if(type == "enabled") {
				prop.setProperty("mode", mode);
				prop.setProperty("key", Integer.toString(key));
				prop.setProperty("key2", Integer.toString(key2));
				prop.setProperty(type, data);
			}
			prop.store(writer, "QuickF5 configuration");
			writer.close();
			System.out.println("[QuickF5] config written successfully. reloading config...");
			ConfigLoad();
		}
		catch(NullPointerException e) {
			System.out.println(e);
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}
