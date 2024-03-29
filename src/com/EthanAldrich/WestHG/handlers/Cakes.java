package WestHG.handlers;

import WestHG.HG;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class Cakes {
	public static void cakes(final Player p) {
		Location loc = p.getLocation();
		loc.setY(114);
		loc.setX(loc.getX() + 1);
		loc.setZ(loc.getZ() + 1);

		Block glass = loc.getBlock();
		glass.setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH).setType(Material.GLASS);
		glass.getRelative(BlockFace.WEST).setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH_WEST).setType(Material.GLASS);
		glass.getRelative(BlockFace.EAST).setType(Material.GLASS);
		glass.getRelative(BlockFace.SOUTH_EAST).setType(Material.GLASS);
		glass.getRelative(BlockFace.SOUTH).setType(Material.GLASS);
		glass.getRelative(BlockFace.SOUTH_WEST).setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH_EAST).setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH)
				.setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_EAST)
				.setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_WEST)
				.setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH_WEST)
				.getRelative(BlockFace.NORTH_WEST).setType(Material.GLASS);
		glass.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.WEST)
				.setType(Material.GLASS);
		glass.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST)
				.setType(Material.GLASS);
		glass.getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH_WEST)
				.setType(Material.GLASS);

		loc.setY(115);
		Block cake = loc.getBlock();
		cake.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.WEST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH_WEST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.EAST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.SOUTH_EAST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.SOUTH).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.SOUTH_WEST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH_EAST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH)
				.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_EAST)
				.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH_WEST)
				.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH_WEST)
				.getRelative(BlockFace.NORTH_WEST).setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.WEST)
				.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST)
				.setType(Material.CAKE_BLOCK);
		cake.getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH_WEST)
				.setType(Material.CAKE_BLOCK);

		loc.setX(loc.getX() - 1);
		loc.setZ(loc.getZ() - 1);

		loc.setY(118);
		p.teleport(loc);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.HG, new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i <= 3; i++) {
					Location firework1 = p.getLocation();
					int random = new Random().nextInt(3);
					boolean flicker = false;
					if (random == 3) {
						flicker = true;
					}
					if (i == 0) {
						firework1.setX(firework1.getX() + 3);
						firework1.setZ(firework1.getZ() + 3);
					} else if (i == 1) {
						firework1.setX(firework1.getX() - 3);
						firework1.setZ(firework1.getZ() - 3);
					} else if (i == 2) {
						firework1.setX(firework1.getX() + 3);
						firework1.setZ(firework1.getZ() - 3);
					} else if (i == 3) {
						firework1.setX(firework1.getX() - 3);
						firework1.setZ(firework1.getZ() + 3);
					}
					Firework fw = (Firework) p.getWorld().spawnEntity(
							firework1, EntityType.FIREWORK);
					FireworkMeta fwm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder()
							.withColor(Color.AQUA).withColor(Color.RED)
							.withColor(Color.ORANGE).with(Type.BALL_LARGE)
							.flicker(flicker).build();
					fwm.addEffect(effect);
					fwm.setPower(1);

					fw.setFireworkMeta(fwm);
				}
			}
		}, 2, 30);
	}
}
