package nxtdelivery.quickF5.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import nxtdelivery.quickF5.Reference;
import nxtdelivery.quickF5.config.ConfigHandler.*;

public class ConfigCommand implements ICommand{

	private final List aliases;
	public boolean returnRelease;
	 public ConfigCommand() 
	    { 
	        aliases = new ArrayList(); 
	        aliases.add("qf5"); 
	        aliases.add("qF5"); 
	        aliases.add("qF"); 
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
			if(args[0].equals("mode")) {
				if(args[1].equals("toggle")) {
					System.out.println("[QuickF5] TOGGLE mode set by user command. writing new config...");
					ConfigHandler.writeConfig("mode", "toggle");
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Usage mode set to toggle."));
					if(ConfigHandler.modEnabled == false) {
						sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Note that mod is disabled. Use /quickF5 enabled to enable it again."));
					}
					
				}
				else if(args[1].equals("hold")) {
					System.out.println("[QuickF5] HOLD mode set by user command. writing new config...");
					ConfigHandler.writeConfig("mode", "hold");
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Usage mode set to hold."));
					if(ConfigHandler.modEnabled == false) {
						sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Note that mod is disabled. Use /quickF5 enabled to enable it again."));
					}
				}
				else {
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] invalid option for mode (" + args[1] + ")"));
					sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] command usage: /quickF5 mode [hold/toggle]"));
				}
			}
			if(args[0].equals("disabled")) {
				System.out.println("[QuickF5] mod state DISABLED set by user command. writing new config...");
				ConfigHandler.writeConfig("enabled", "false");
				sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Mod is now disabled. Use /quickF5 enabled to enable it again."));
			}
			if(args[0].equals("enabled")) {
				System.out.println("[QuickF5] mod state ENABLED set by user command. writing new config...");
				ConfigHandler.writeConfig("enabled", "true");
				sender.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.DARK_AQUA + "[QuickF5] Mod is now enabled."));
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
