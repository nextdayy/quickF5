/* Changelog 1.5.4
 * - fixed version checker crash
 * - added reload command
 * - full fix will be very soon.
 * - more code cleanup
 */
package com.nxtdelivery.quickF5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import com.nxtdelivery.quickF5.config.ConfigCommand;
import com.nxtdelivery.quickF5.config.ConfigHandler;
import com.nxtdelivery.quickF5.util.TickDelay;
import com.nxtdelivery.quickF5.util.UpdateChecker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;



@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class QuickF5 {

	@Mod.Instance("qF5")			// variables and things
	public static QuickF5 instance;
	private KeyBinding changePerspective;
	private KeyBinding changePerspective2;
	private static Minecraft mc = Minecraft.getMinecraft();
	public static final Logger LOGGER = LogManager.getLogger("QuickF5");
	private static boolean active = false;
	private int perspectiveMode = 0;
	public static boolean updateCheck;
	
	public boolean registerBus(EventBus bus, LoadController controller) {				// register mod to the bus
		bus.register(this);
		return true;
	}
	
	
	@EventHandler()
	public void init(FMLInitializationEvent event) {
		LOGGER.info("attempting to check update status...");
		updateCheck = UpdateChecker.updateNeeded(Reference.VERSION);
		LOGGER.info("attempting to read config file...");
		ConfigHandler.ConfigLoad();											// load config things
		if(ConfigHandler.mode.equals("hold")) { ConfigHandler.returnRelease = true; }		// set return on release to true when hold is enabled
		LOGGER.info("registering settings...");
		changePerspective = new KeyBinding("Change Perspective", ConfigHandler.key, "QuickF5");
		changePerspective2 = new KeyBinding("Change Perspective (2nd person)", ConfigHandler.key2, "QuickF5");
		ClientRegistry.registerKeyBinding(changePerspective);			// register keys and buses (vroom vroom)
		ClientRegistry.registerKeyBinding(changePerspective2);
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new ConfigCommand());
		LOGGER.info("Complete! QuickF5 loaded successfully.");
	}
	


	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
	    if(Keyboard.getEventKey() == changePerspective.getKeyCode() && ConfigHandler.modEnabled == true) {			// if key pressed is the right one, and mod is enabled it will register keypress.
	    	if(ConfigHandler.key != changePerspective.getKeyCode() ) {				// will write new key code if the player changed it in settings
	    		LOGGER.warn("Key code from config (" + ConfigHandler.key + ") differs to key code just used! (" + changePerspective.getKeyCode() + ") writing new to config file...");
	    		ConfigHandler.writeConfig("key", Integer.toString(changePerspective.getKeyCode()));
	    	}
	    	if (Keyboard.getEventKeyState()) {						// actual stuff that happens when key is pressed
				active = !active;
				if (active) {
					perspectiveMode = mc.gameSettings.thirdPersonView;
					mc.gameSettings.thirdPersonView = 1;
					mc.entityRenderer.loadEntityShader(mc.thePlayer);
				} else {
					mc.gameSettings.thirdPersonView = perspectiveMode;
				}
			}
			else if (ConfigHandler.returnRelease) {
				active = false;
				mc.gameSettings.thirdPersonView = perspectiveMode;
			}
		}
	    if(Keyboard.getEventKey() == changePerspective2.getKeyCode() && ConfigHandler.modEnabled == true) {			// if key pressed is the right one, and mod is enabled it will register keypress.
	    	if(ConfigHandler.key2 != changePerspective2.getKeyCode() ) {				// will write new key code if the player changed it in settings
	    		LOGGER.warn("Second key code from config (" + ConfigHandler.key + ") differs to key code just used! (" + changePerspective2.getKeyCode() + ") writing new to config file...");
	    		ConfigHandler.writeConfig("key2", Integer.toString(changePerspective2.getKeyCode()));
	    	}
	    	if (Keyboard.getEventKeyState()) {						// actual stuff that happens when key is pressed
				active = !active;
				if (active) {
					perspectiveMode = mc.gameSettings.thirdPersonView;
					mc.gameSettings.thirdPersonView = 2;
					mc.entityRenderer.loadEntityShader(mc.thePlayer);
				} else {
					mc.gameSettings.thirdPersonView = perspectiveMode;
				}
			}
			else if (ConfigHandler.returnRelease) {
				active = false;
				mc.gameSettings.thirdPersonView = perspectiveMode;
			}
		}
		if (Keyboard.getEventKey() == mc.gameSettings.keyBindTogglePerspective.getKeyCode()) {		// mod wont do anything if already in F5 mode
			active = false;
		}
	}
	
	
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {				// singleplayer mode
		if(!event.world.isRemote) {   
			if(ConfigHandler.firstStart == true) {
				updateCheck = false;
				ConfigHandler.fileCorrupt = false;			// just in case something wierd happens
				QuickF5.LOGGER.info("Thank you for installing quickF5! I hope that you enjoy it!");
				new TickDelay(() ->  sendMessages("","[QuickF5] Thank you for installing quickF5! I hope that you enjoy it!","[QuickF5] If you want to configure the mod, type /quickF5 for more."),20); 
				ConfigHandler.firstStart = false;
				return;
			}
			if(updateCheck == true) {
				new TickDelay(() -> sendUpdateMessage(),20);
				updateCheck = false;
				return;
			}
			if(ConfigHandler.fileCorrupt == true) {
				try {
					new TickDelay(() ->  sendMessages("","[QuickF5] An error occured while trying to read config file.","[QuickF5] If you just updated, ignore this message. You will have to rebind your keys."),20); 
					ConfigHandler.fileCorrupt = false;
					return;
				} catch(NullPointerException e) {
					LOGGER.fatal(e);
					LOGGER.error("skipping corruption message, bad world return!");
				}
			}
		}
	}
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {				// server mode 
        if (event.entity != null && event.entity instanceof EntityPlayer) {
        	if(ConfigHandler.firstStart == true) {
				updateCheck = false;
				ConfigHandler.fileCorrupt = false;			// just in case something wierd happens
				QuickF5.LOGGER.info("Thank you for installing quickF5! I hope that you enjoy it!");
				new TickDelay(() ->  sendMessages("","[QuickF5] Thank you for installing quickF5! I hope that you enjoy it!","[QuickF5] If you want to configure the mod, type /quickF5 for more."),20); 
				ConfigHandler.firstStart = false;
				return;
			}
			if(updateCheck == true) {
				new TickDelay(() -> sendUpdateMessage(),20);
				updateCheck = false;
				return;
			}
			if(ConfigHandler.fileCorrupt == true) {
				try {
					new TickDelay(() ->  sendMessages("","[QuickF5] An error occured while trying to read config file.","[QuickF5] If you just updated, ignore this message. You will have to rebind your keys."),20); 
					ConfigHandler.fileCorrupt = false;
					return;
				} catch(NullPointerException e) {
					LOGGER.fatal(e);
					LOGGER.error("skipping corruption message, bad world return!");
				}
			}
        }
	}
	
	
	
	
	private Runnable sendMessages(String message1, String message2, String message3) {
		try {
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + message1));
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + message2));
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + message3));
		} catch(NullPointerException e) {
			LOGGER.fatal(e);
			LOGGER.error("skipping new message, bad world return!");
		}
		return null;
	}
	private Runnable sendUpdateMessage() {
		try {
			IChatComponent comp = new ChatComponentText("Click here to update it!");
			ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL,"https://github.com/nxtdaydelivery/quickF5/releases"));
			style.setColor(EnumChatFormatting.DARK_AQUA);
			style.setUnderlined(true);
			comp.setChatStyle(style);
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "--------------------------------------"));
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + ("A newer version of QuickF5 is available! (" + UpdateChecker.latestVersion + ")")));
			mc.thePlayer.addChatMessage(comp);
			mc.thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "--------------------------------------"));
		} catch(NullPointerException e) {
			LOGGER.fatal(e);
			LOGGER.error("skipping update message, bad world return!");
		}
		return null;
	}
}

