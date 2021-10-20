package com.nxtdelivery.quickStats.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nxtdelivery.quickStats.QuickStats;
import com.nxtdelivery.quickStats.util.ConfigHandler;
import com.nxtdelivery.quickStats.util.LocrawUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ApiRequest extends Thread {
	private static Minecraft mc = Minecraft.getMinecraft();
	String username, uuid, rank, rankColor, playerName;
	public JsonObject rootStats, achievementStats;
	public String formattedName;
	public ArrayList result;
	public boolean noUser = false;
	public boolean generalError = false;

	public ApiRequest(String uname) {
		username = uname;
	}

	public void run() {
		/* get UUID from Mojang */
		try {
			InputStream input = new URL("https://api.mojang.com/users/profiles/minecraft/" + username).openStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);
			JsonObject jsonObject = new JsonParser().parse(responseStrBuilder.toString()).getAsJsonObject();
			uuid = jsonObject.get("id").getAsString();
			// System.out.println(uuid);
		} catch (IllegalStateException e) {
			mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(
					EnumChatFormatting.DARK_GRAY + "[QuickStats] Player not found: " + username));
			noUser = true;
			return;
		} catch (Exception e) {
			QuickStats.LOGGER.error(e.getStackTrace().toString());
			mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
					+ "[QuickStats] an unexpected error occoured. Check logs for more info."));
		}

		/* process request from Hypixel */
		try {
			String url = "https://api.hypixel.net/player?key=" + ConfigHandler.apiKey + "&uuid=" + uuid;
			// System.out.println(url);
			InputStream input = new URL(url).openStream();
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			StringBuilder responseStrBuilder = new StringBuilder();
			String inputStr;
			while ((inputStr = streamReader.readLine()) != null)
				responseStrBuilder.append(inputStr);
			// System.out.println(responseStrBuilder.toString);
			JsonObject js1 = new JsonParser().parse(responseStrBuilder.toString()).getAsJsonObject();
			/*
			 * for(String key: flattenJson.keySet()){ // DEBUG: print all keys
			 * System.out.println(key); }
			 */

			Boolean success = (Boolean) js1.get("success").getAsBoolean();
			if (success) {
				QuickStats.LOGGER.info("successfully proccessed from Hypxiel");
				JsonObject js2 = js1.get("player").getAsJsonObject();
				try { // get rank and name
					playerName = js2.get("displayname").getAsString();
					rank = js2.get("newPackageRank").getAsString();
					if (rank.equals("MVP_PLUS")) {
						try {
							rankColor = js2.get("rankPlusColor").getAsString(); // get plus color
							if (js2.get("monthlyPackageRank").getAsString().equals("SUPERSTAR")) { // test for mvp++
								rank = "SUPERSTAR";
							}
						} catch (Exception e) {
							rank = "MVP_PLUS";
							rankColor = "PINK";
							// e.printStackTrace();
						}
					}
				} catch (NullPointerException e) {
					rank = "non";
				}
				formattedName = getFormattedName(playerName, rank, rankColor);
				// mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText("Stats for
				// " + formattedName));
				rootStats = js2.get("stats").getAsJsonObject();
				achievementStats = js2.get("achievements").getAsJsonObject();
				result = Stats.getStats(rootStats, achievementStats, LocrawUtil.gameType);
				// JsonObject js4 = js3.get("SkyWars").getAsJsonObject(); // DEBUG: test
				// System.out.println(js4.get("kills_solo_insane").getAsString());
				for (int i = 0; i < result.size(); i++) {
					// QuickStats.LOGGER.debug(result.get(i));
					// mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(
					// EnumChatFormatting.DARK_GRAY + result.get(i).toString()));
				}
			} else {
				mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
						+ "[QuickStats] The Hypixel API didn't process the request properly. Try again."));
				QuickStats.LOGGER.error("error occoured when building after API request, closing");
				js1 = null;
			}

		} catch (IOException e) {
			if (ConfigHandler.apiKey.equals("none")) {
				mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
						+ "[QuickStats] You haven't set an API key yet! Type /api new to get one, and the mod should grab it."));
			} else {
				mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
						+ "[QuickStats] failed to contact Hypixel API. This is usually due to an invalid API key."));
				mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
						+ "[QuickStats] On Hypixel, type /api new to get a new key and the mod should automatically grab it."));
			}
			return;
		} catch (Exception e) {
			// QuickStats.LOGGER.error(e.getStackTrace().toString());
			e.printStackTrace();
			mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
					+ "[QuickStats] an unexpected error occoured. Check logs for more info."));
			generalError = true;
			return;
		}
	}

	private String getFormattedName(String name, String rank, String color) { // TODO add more colors.
		QuickStats.LOGGER.debug(color);
		String formattedName;
		boolean getColor = false;
		Integer plusA = 0;
		switch (rank) {
		case "VIP":
			formattedName = "\u00A7a[VIP] " + name; // � = \u00A7
			break;
		case "VIP_PLUS":
			formattedName = "\u00A7a[VIP\u00A76+\u00A7a] " + name;
			break;
		case "MVP":
			formattedName = "\u00A7b[MVP] " + name;
			break;
		case "MVP_PLUS":
			getColor = true;
			plusA = 1;
			formattedName = "\u00A7b[MVP";
			break;
		case "SUPERSTAR":
			getColor = true;
			plusA = 2;
			formattedName = "\u00A76[MVP";
			break;
		default:
			formattedName = "\u00A77" + name;
			break;
		}
		if (getColor) {
			switch (color) {
			case "DARK_RED":
				formattedName += "\u00A74+";
				break;
			case "DARK_GREEN":
				formattedName += "\u00A72+";
				break;
			case "BLACK":
				formattedName += "\u00A70+";
				break;
			case "PINK":
				formattedName += "\u00A7d+";
				break;
			case "BLUE":
				formattedName += "\u00A79+";
				break;
			default:
				formattedName += "+";
			}
			if (plusA == 2) {
				formattedName += "+\u00A76] " + name;
			} else {
				formattedName += "\u00A7b] " + name;
			}
		}

		return formattedName;
	}

}