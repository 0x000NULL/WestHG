package WestHG;

import com.EthanAldrich.WestHG.api.GameStartEvent;
import com.EthanAldrich.WestHG.api.SecondEvent;
import com.EthanAldrich.WestHG.api.WinEvent;
import com.EthanAldrich.WestHG.commands.FeastCmd;
import com.EthanAldrich.WestHG.commands.FFeastCmd;
import com.EthanAldrich.WestHG.commands.FTimeCmd;
import com.EthanAldrich.WestHG.commands.GM;
import com.EthanAldrich.WestHG.commands.HGCmd;
import com.EthanAldrich.WestHG.commands.InvCmd;
import com.EthanAldrich.WestHG.commands.KitCmd;
import com.EthanAldrich.WestHG.commands.Lag;
import com.EthanAldrich.WestHG.commands.SpawnCmd;
import com.EthanAldrich.WestHG.commands.SpectateCmd;
import com.EthanAldrich.WestHG.commands.StartCmd;
import com.EthanAldrich.WestHG.data.MySQL;
import com.EthanAldrich.WestHG.handlers.Cakes;
import com.EthanAldrich.WestHG.handlers.Feast;
import com.EthanAldrich.WestHG.handlers.GenerationHandler;
import com.EthanAldrich.WestHG.kits.Kit;
import com.EthanAldrich.WestHG.listeners.AllTimeListener;
import com.EthanAldrich.WestHG.listeners.GameListener;
import com.EthanAldrich.WestHG.listeners.PreGameListener;
import com.EthanAldrich.WestHG.utils.Undroppable;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Ethan Aldrich
 * 
 */
public class HG extends JavaPlugin {

	public static HG HG;
	public Metrics metrics;
	public String motd;
	public int preTime, gameTime = -1;
	public FileConfiguration config;

	private int minimumPlayers, resetTime, feastTime;

	@Override
	public void onLoad() {
		HG = this;
		configs();
		motd = config.getString("motd").replace("&", "�") + "\n";
		if (config.getBoolean("regenerate"))
			GenerationHandler.deleteWorld();
	}

	public static int check() {
		int players = com.EthanAldrich.WestHG.Gamer.getAliveGamers().size();
		if (players <= 1) {
			if (com.EthanAldrich.WestHG.Gamer.getAliveGamers().size() > 0)
				winner(com.EthanAldrich.WestHG.Gamer.getAliveGamers().get(0));
			else
				winner(null);
		}
		return players;
	}

	@Override
	public void onDisable() {
		MySQL.closeConnection();
	}

	@SuppressWarnings("unused")
	private int preGameTask, gameTask;

	@SuppressWarnings("deprecation")
	private static void winner(final com.EthanAldrich.WestHG.Gamer gamer) {
		if (gamer == null) {
			shutdown(ChatColor.RED + "Nobody won!");
			return;
		}
		Bukkit.getPluginManager().callEvent(new WinEvent(gamer));
		HG.registerPreEvents();
		Cakes.cakes(gamer.getPlayer());
		Bukkit.getScheduler().scheduleAsyncDelayedTask(HG, new Runnable() {
			public void run() {
				try {
					MySQL.incrementStat(gamer.getPlayer().getUniqueId(), "wins");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});
		Bukkit.getScheduler().scheduleSyncRepeatingTask(HG, new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().broadcastMessage(ChatColor.RED + gamer.getName() + " has won!!!");
			}
		}, 1, 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(HG, new Runnable() {
			@Override
			public void run() {
				shutdown(ChatColor.RED + gamer.getName() + " has won!!!\nServer restarting.");
			}
		}, 200);
	}

	private void startGameTimer() {
		gameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new SecondEvent());
				if (gameTime < 120) {
					int tim = 120 - gameTime;
					if (tim == 120 || tim == 60)
						Bukkit.getServer().broadcastMessage(
								ChatColor.RED + "Invincibillity wears off in " + ((tim) / 60) + " minutes.");
					else if (tim == 30 || tim == 15 || (tim <= 10 && tim > 0))
						Bukkit.getServer().broadcastMessage(
								ChatColor.RED + "Invincibillity wears off in " + (120 - gameTime) + " seconds.");
				} else if (gameTime == 120) {
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "The invincibillity has worn off!");
				} else if (gameTime == feastTime) {
					Feast.createFeast(false);
				} else if (gameTime > 3600) {
					shutdown(ChatColor.RED + "Game went on for too long!\nRestarting...");
				}
				gameTime++;
			}
		}, 0, 20);
	}

	@Override
	public void onEnable() {
		try {
			metrics = new Metrics;
			metrics.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (config.getBoolean("regenerate"))
			GenerationHandler.generateChunks();
		Kit.init();
		Bukkit.getPluginManager().registerEvents(new AllTimeListener(), this);
		startPregameTimer();
		registerPreEvents();
		registerCommands();
		registerEnchantments();
		registerRecipes();

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 20L, 1L);

		for (Player p : Bukkit.getOnlinePlayers())
			com.EthanAldrich.WestHG.Gamer.getGamer(p);

		if (config.getBoolean("mysql"))
			try {
				MySQL.initialize(config.getString("host"), config.getInt("port"), config.getString("database"),
						config.getString("table"), config.getString("user"), config.getString("password"));
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
	}

	private void startPregameTimer() {
		preGameTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (preTime == 180 || preTime == 120 || preTime == 60) {
					Bukkit.getServer()
							.broadcastMessage(ChatColor.RED + "Tournament starting in " + preTime / 60 + " minutes.");
				} else if (preTime == 45 || preTime == 30 || preTime == 15 || (preTime > 0 && preTime <= 10)) {
					Bukkit.getServer()
							.broadcastMessage(ChatColor.RED + "Tournament starting in " + preTime + " seconds.");
				} else if (preTime == 0) {
					if (com.EthanAldrich.WestHG.Gamer.getAliveGamers().size() < minimumPlayers) {
						Bukkit.getServer().broadcastMessage(ChatColor.RED + "Not enough players to start.");
						preTime = resetTime;
						return;
					}
					preTime = -1;
					start();
					return;
				}
				preTime--;
			}
		}, 0, 20);
	}

	private Listener preListener, gameListener;

	private void registerGameEvents() {
		gameListener = new GameListener();
		Bukkit.getPluginManager().registerEvents(gameListener, this);
		Bukkit.getPluginManager().registerEvents(new Feast(), this);
		Kit.registerKitEvents();
	}

	private void registerPreEvents() {
		preListener = new PreGameListener();
		Bukkit.getPluginManager().registerEvents(preListener, this);
	}

	private void unRegisterPreEvents() {
		HandlerList.unregisterAll(preListener);
	}

	private void registerCommands() {
		getCommand("kit").setExecutor(new KitCmd());
		getCommand("spectate").setExecutor(new SpectateCmd());
		getCommand("lag").setExecutor(new Lag());
		getCommand("gm").setExecutor(new GM());
		getCommand("start").setExecutor(new StartCmd());
		getCommand("forcetime").setExecutor(new FTimeCmd());
		getCommand("forcefeast").setExecutor(new FFeastCmd());
		getCommand("feast").setExecutor(new FeastCmd());
		getCommand("invsee").setExecutor(new InvCmd());
		getCommand("spawn").setExecutor(new SpawnCmd());
		getCommand("hg").setExecutor(new HGCmd());
	}

	private void registerEnchantments() {
		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Enchantment.registerEnchantment(Undroppable.ench);
			} catch (IllegalArgumentException e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configs() {
		config = getConfig();
		saveDefaultConfig();
		if (config.getBoolean("rewriteconfig")) {
			File file = new File(getDataFolder() + File.separator + "config.yml");
			file.delete();
			saveDefaultConfig();
		}
		preTime = config.getInt("pregamedelay");
		minimumPlayers = config.getInt("minimum");
		resetTime = config.getInt("retime");
		feastTime = config.getInt("feast");
	}

	@SuppressWarnings("deprecation")
	private void start() {
		Bukkit.getScheduler().cancelTask(preGameTask);
		unRegisterPreEvents();
		registerGameEvents();
		int parts = 0;
		for (com.EthanAldrich.WestHG.Gamer g : com.EthanAldrich.WestHG.Gamer.getGamers())
			if (g.isAlive())
				parts++;
		Bukkit.getServer().broadcastMessage(ChatColor.RED + "The tournament has started!\n" + "There are " + parts
				+ " players participating.\n" + "Everyone is invincible for 2 minutes.\n" + "Good Luck!");
		World world = Bukkit.getWorld("world");
		List<Player> participating = new ArrayList<Player>();
		for (com.EthanAldrich.WestHG.Gamer g : com.EthanAldrich.WestHG.Gamer.getGamers()) {
			if (g.getPlayer().getGameMode() != GameMode.SURVIVAL)
				continue;
			Player p = g.getPlayer();
			g.setAlive(true);
			p.closeInventory();
			clearPlayer(p);
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
			if (g.getKit().getKitName() == "Surprise") {
				g.setKit(Kit.kits.get(new Random().nextInt(Kit.kits.size())));
				p.sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "You got the " + g.getKit().getKitName()
						+ " kit!");
			}
			g.applyKit();
			p.updateInventory();
			p.teleport(new Location(world, getRandom(-50, 50), 80, getRandom(-50, 50)));
			participating.add(p);
		}
		startGameTimer();
		Bukkit.getPluginManager().callEvent(new GameStartEvent(participating));
	}


	private static void shutdown(String message) {
		for (Player p : Bukkit.getOnlinePlayers())
			p.kickPlayer(message);
		Bukkit.getServer().shutdown();
	}

	public static void clearPlayer(Player p) {
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setHealth(20);
		p.setFoodLevel(25);
		p.setFireTicks(0);
		p.setFallDistance(0.0F);
		p.setLevel(0);
		p.setExp(0);
		for (PotionEffect pe : p.getActivePotionEffects())
			p.removePotionEffect(pe.getType());
	}

	@SuppressWarnings("deprecation")
	private void registerRecipes() {
		ShapelessRecipe re = new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP));
		re.addIngredient(Material.BOWL);
		re.addIngredient(Material.CACTUS);
		ShapelessRecipe re2 = new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP));
		re2.addIngredient(Material.BOWL);
		re2.addIngredient(Material.INK_SACK, 3);
		Bukkit.getServer().addRecipe(re);
		Bukkit.getServer().addRecipe(re2);
	}

	public static int getRandom(int lower, int upper) {
		Random random = new Random();
		return random.nextInt((upper - lower) + 1) + lower;
	}
}
