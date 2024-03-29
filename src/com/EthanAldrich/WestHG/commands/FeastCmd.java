package WestHG.commands;

import WestHG.handlers.Feast;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeastCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player.");
			return false;
		}
		Location feast = Feast.getFeastLoc();
		if (feast == null) {
			sender.sendMessage(ChatColor.RED + "The feast has not been spawned yet.");
		} else {
			((Player) sender).setCompassTarget(feast);
			sender.sendMessage(ChatColor.GREEN + "Your compass is now pointing towards the feast!");
		}
		return false;
	}
}
