/* Changelog 1.3
 * - changed prints to use proper logging system
 * - fixed entity shader loading to stop ghost chunks
 * - fixed name of mod to perform properly with java traditions
 * - general code cleanup
 */
package com.nxtdelivery.quickF5;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.nxtdelivery.quickF5.config.ConfigCommand;
import com.nxtdelivery.quickF5.config.ConfigHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
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
	private KeyBinding changePerspective = new KeyBinding("Change Perspective", ConfigHandler.key, "QuickF5");
	private KeyBinding changePerspective2 = new KeyBinding("Change Perspective (2nd person)", ConfigHandler.key2, "QuickF5");
	private static Minecraft mc = Minecraft.getMinecraft();
	public static final Logger LOGGER = LogManager.getLogger();
	private static boolean active = false;
	private int perspectiveMode = 0;
	
	public boolean registerBus(EventBus bus, LoadController controller) {				// register mod to the bus
		bus.register(this);
		return true;
	}
	
	
	@EventHandler()
	public void init(FMLInitializationEvent event) {
		LOGGER.info("[QuickF5] attempting to read config file...");
		ConfigHandler.ConfigLoad();											// load config things
		if(ConfigHandler.mode.equals("hold")) { ConfigHandler.returnRelease = true; }		// set return on release to true when hold is enabled
		LOGGER.info("[QuickF5] registering settings...");
		ClientRegistry.registerKeyBinding(changePerspective);			// register keys and buses (vroom vroom)
		ClientRegistry.registerKeyBinding(changePerspective2);
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new ConfigCommand());
		LOGGER.info("[QuickF5] Complete! QuickF5 loaded successfully.");
	}
	


	@SubscribeEvent
	public void onKeyPress(InputEvent.KeyInputEvent event) {
	    if(Keyboard.getEventKey() == changePerspective.getKeyCode() && ConfigHandler.modEnabled == true) {			// if key pressed is the right one, and mod is enabled it will register keypress.
	    	if(ConfigHandler.key != changePerspective.getKeyCode() ) {				// will write new key code if the player changed it in settings
	    		LOGGER.warn("[QuickF5] Key code from config (" + ConfigHandler.key + ") differs to key code just used! (" + changePerspective.getKeyCode() + ") writing new to config file...");
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
	    		LOGGER.warn("[QuickF5] Second key code from config (" + ConfigHandler.key + ") differs to key code just used! (" + changePerspective2.getKeyCode() + ") writing new to config file...");
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
}
