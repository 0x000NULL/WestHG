package WestHG.kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Ethan extends Kit {

    @Override
    public String getKitName() {
        return "Ethan";
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (hasAbillity(event.getPlayer()) && event.getBlock().getType() == Material.GRAVEL) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0, 0.5),
                    new ItemStack(Material.FLINT));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack drop = event.getItemDrop().getItemStack();
        if (drop.getType() == Material.BOW && hasAbillity(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        for (ItemStack is : event.getDrops()) {
            if ((((is.getType() == Material.BOW && is.getEnchantments().containsKey(Enchantment.ARROW_KNOCKBACK)))
                    || is.getType() == Material.ARROW) && hasAbillity(event.getEntity()))
                is.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player))
            return;
        if (hasAbillity(event.getEntity().getKiller()))
            if (event.getEntity() instanceof Skeleton || event.getEntity() instanceof Chicken)
                event.getDrops().addAll(event.getDrops());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity))
            return;
        if (((LivingEntity) event.getEntity()).getNoDamageTicks() >= 10 || event.isCancelled())
            return;
        if (event.getDamager() instanceof Arrow) {
            final Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                Player p = (Player) arrow.getShooter();
                if (hasAbillity(p)) {
                    p.getInventory().addItem(new ItemStack(Material.ARROW));
                    arrow.remove();
                }
            }
        }
    }

    public ItemStack[] getItems() {
        ItemStack bow = createItem(Material.BOW, getKitName(), false);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
        bow.addEnchantment(Enchantment.KNOCKBACK, 2);

        ItemStack[] items = new ItemStack[]{bow, new ItemStack(Material.ARROW, 60)};

        return items;
    }

    @Override
    protected ItemStack getIcon() {
        return createItem(Material.END_ROD, getKitName(), false);
    }

    @Override
    protected List<String> getDescription() {
        List<String> list = new ArrayList<String>();
        list.add("When you hit your target, you get a new arrow");
        list.add("Your bow has Punch I");
        return list;
    }

    @Override
    protected List<String> getStartingItems() {
        List<String> list = getNewStringList();
        list.add("1 Bow");
        return list;
    }
}
