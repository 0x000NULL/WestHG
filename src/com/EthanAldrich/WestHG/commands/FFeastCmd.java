package WestHG.commands;

import WestHG.HG;
import WestHG.handlers.Feast;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FFeastCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("hg.ffeast")) {
			sender.sendMessage(ChatColor.RED + "No permission.");
			return false;
		}
        if (HG.gameTime == -1) {
			sender.sendMessage(ChatColor.RED + "The game has not begun yet.");
			return false;
		}
		Location loc = Feast.createFeast(true);
		Bukkit.getServer().broadcastMessage(ChatColor.RED + sender.getName() + " has forced a feast at (" + loc.getX()
				+ ", " + (loc.getY()) + ", " + loc.getZ() + ").");
		return false;
	}
}
