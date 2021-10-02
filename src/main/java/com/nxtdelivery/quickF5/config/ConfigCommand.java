package com.nxtdelivery.quickF5.config;

import java.util.ArrayList;
import java.util.List;

import com.nxtdelivery.quickF5.QuickF5;
import com.nxtdelivery.quickF5.Reference;
import com.nxtdelivery.quickF5.config.ConfigHandler.*;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ConfigCommand implements ICommand{

	private final List aliases;
	public boolean returnRelease;
	 public ConfigCommand() 
	    { 
	        aliases = new ArrayList(); 
	        aliases.add("qf5"); 
	        aliases.add("qF5"); 
	        aliases.add("quickf5"); 
	    } 
	 
	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "quickF5";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "quickF5 <>";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		try {
			switch(args[0]) {
				case "mode": 
					if(args[1].equals("toggle")) {
						QuickF5.LOGGER.info("TOGGLE mode set by user command. writing new config...");
						ConfigHandler.writeConfig("mode", "toggle");
						sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Usage mode set to toggle."));
						if(ConfigHandler.modEnabled == false) {
							sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Note that mod is disabled. Use /quickF5 enabled to enable it again."));
						}
						
					}
					else if(args[1].equals("hold")) {
						QuickF5.LOGGER.info("HOLD mode set by user command. writing new config...");
						ConfigHandler.writeConfig("mode", "hold");
						sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Usage mode set to hold."));
						if(ConfigHandler.modEnabled == false) {
							sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Note that mod is disabled. Use /quickF5 enabled to enable it again."));
						}
					}
					break;
				case "disabled":
					QuickF5.LOGGER.info("mod state DISABLED set by user command. writing new config...");
					ConfigHandler.writeConfig("enabled", "false");
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Mod is now disabled. Use /quickF5 enabled to enable it again."));
					break;
				case "enabled":
					QuickF5.LOGGER.info("mod state ENABLED set by user command. writing new config...");
					ConfigHandler.writeConfig("enabled", "true");
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Mod is now enabled."));
					break;
				default:
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] invalid option (" + args[0] + ")"));
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Comamnd usage: /quickF5 mode [hold/toggle], /quickF5 [enabled/disabled]"));
					break;
			}
		} catch(Exception e) {
			sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Command menu (mod version " + Reference.VERSION + ")"));
			sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Comamnd usage: /quickF5 mode [hold/toggle], /quickF5 [enabled/disabled]"));
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}
