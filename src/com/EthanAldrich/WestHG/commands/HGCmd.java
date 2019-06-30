package WestHG.commands;

import WestHG.HG;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


/**
 * @author Ethan Aldrich
 *
 */
public class HGCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "This server is running WestHG version "
                + HG.HG.getDescription().getVersion() + " made by Ethan Aldrich. You can download it here: https://github.com/ethanaldrich9986/WestHG");
		return false;
	}
}
