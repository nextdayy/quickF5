package com.nxtdelivery.quickStats.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.nxtdelivery.quickStats.QuickStats;
import com.nxtdelivery.quickStats.api.ApiRequest;
import com.nxtdelivery.quickStats.util.TickDelay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class GUIStats extends Gui { // TODO fix lag, possibly use extends Thread?
	private static Minecraft mc = Minecraft.getMinecraft();
	private final FontRenderer fr = mc.fontRendererObj;
	long systemTime = Minecraft.getSystemTime();
	ScaledResolution resolution = new ScaledResolution(mc);
	Integer frametime, height, width, guiScale, top, bottom, middle, halfWidth, seed, pad;
	Boolean beginTimer, retract;
	long frames, framesLeft, fifth, upperThreshold, lowerThreshold;
	Float fontScale, percentComplete;
	String username, title;
	ApiRequest api;

	public GUIStats(String user) {
		height = resolution.getScaledHeight();
		width = resolution.getScaledWidth();
		guiScale = mc.gameSettings.guiScale;
		frames = 5 * 60;
		framesLeft = 5 * 60;
		fifth = frames / 5;
		upperThreshold = frames - fifth;
		lowerThreshold = fifth;
		percentComplete = 0.0f;
		username = user;
		this.run();
	}

	@EventHandler()
	public void delete() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@EventHandler()
	public void run() {
		MinecraftForge.EVENT_BUS.register(this);
		api = new ApiRequest(username);
		api.start();
		mc.thePlayer.playSound("minecraft:random.successful_hit", 1.0F, 1.0F);
	}

	private static float clamp(float number) {
		return number < (float) 0.0 ? (float) 0.0 : Math.min(number, (float) 1.0);
	}

	private static float easeOut(float current, float goal) {
		if (Math.floor(Math.abs(goal - current) / (float) 0.01) > 0) {
			return current + (goal - current) / (float) 15.0;
		} else {
			return goal;
		}
	}

	@SubscribeEvent
	/**
	 * Some of this code was taken from PopupEvents by Sk1er club under GNU License.
	 * This math logic is used to render the window smoothly. All thanks to them,
	 * this window can render nice and smoothly!
	 */
	public void renderEvent(TickEvent.RenderTickEvent event) {
		if (framesLeft <= 0) {
			return;
		}

		while (systemTime < Minecraft.getSystemTime() + (1000 / 60)) {
			framesLeft--;
			systemTime += (1000 / 60);
		}
		percentComplete = clamp(easeOut(percentComplete,
				framesLeft < lowerThreshold ? 0.0f : framesLeft > upperThreshold ? 1.0f : framesLeft));

		switch (guiScale) { // TODO at the moment, these scales are hard coded. In later versions this will
							// be customizable.
		case 0: // AUTO scale
			middle = width - 60;
			top = 40;
			bottom = 95;
			halfWidth = 52;
			seed = 105;
			fontScale = 0.8f;
			pad = middle + 350; // text padding
			mc.thePlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.DARK_GRAY
					+ "[QuickStats] Note that the GUI is cursed on AUTO Scale. Use small, normal, or large."));
			break;
		case 1: // SMALL
			middle = width - 130;
			top = 50;
			bottom = 145;
			halfWidth = 112;
			seed = 220;
			fontScale = 0.8f;
			pad = middle + seed + 90; // text padding
			break;
		case 2: // NORMAL
			middle = width - 90; // position of window, smaller number = closer to edge
			top = 50; // top of window
			bottom = 115; // bottom of window
			halfWidth = 82; // width of window, larger number is larger window
			seed = 160; // controls the progress bar, larger number longer it goes
			fontScale = 0.8f; // font size
			pad = middle + halfWidth + 37; // text padding
			break;
		case 3: // LARGE
			middle = width - 90;
			top = 50;
			bottom = 115;
			halfWidth = 85;
			seed = 145;
			fontScale = 0.8f;
			pad = middle + 35; // text padding
			break;
		}

		int currentWidth = (int) (halfWidth * percentComplete);
		Gui.drawRect(middle - currentWidth, top, middle + currentWidth, bottom, new Color(27, 27, 27, 200).getRGB());

		if (percentComplete == 1.0F) {
			long length = upperThreshold - lowerThreshold;
			long current = framesLeft - lowerThreshold;
			float progress = 1F - clamp((float) current / (float) length);
			Gui.drawRect(middle - currentWidth, bottom - 2, (int) (middle - currentWidth + (seed * progress)), bottom,
					new Color(32, 50, 117, 200).getRGB()); // 128, 226, 126
			if (guiScale == 0) {
				GL11.glPushMatrix();
				GL11.glScalef(fontScale, fontScale, fontScale); // shrink font
				fontScale = 0.6f;
			}
			title = "Stats for " + api.formattedName;
			if (api.formattedName == null) {
				title = "Loading...";
			}
			if (api.noUser) {
				title = "User not found!";
			}
			if (api.generalError) {
				title = "An error occoured!";
			}
			fr.drawString(title, middle - fr.getStringWidth(title) / 2, 58, -1);
			if (guiScale != 0) {
				GL11.glPushMatrix();
				GL11.glScalef(fontScale, fontScale, fontScale); // shrink font
			}
			if (guiScale == 0) {
				GL11.glScalef(fontScale, fontScale, fontScale);
			}
			String resultMsg;
			try {
				for (int i = 0; i < api.result.size(); i++) {
					QuickStats.LOGGER.debug(api.result.get(i));
					resultMsg = api.result.get(i).toString();
					fr.drawString(resultMsg, pad, (10 * i) + 90, -1);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			GL11.glPopMatrix();
		}

	}

}
