package net.mc42290.seeleserverassist.spItem.receiveDamage;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static net.mc42290.seeleserverassist.Util.UtilSet.*;

public class Environment implements Listener {
    private final JavaPlugin plugin;
    private static final String NBT_KEY = "sp_rde";

    public Environment(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player))return;
        Player p = (Player) e.getEntity();

        //ヘルメット
        for(EquipmentSlot es : EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(es);
            if(item == null || item.getType().equals(Material.AIR))continue;
            if(!getNBT_s("slot",p,es,"off_hand").equals(es.toString().toLowerCase()))continue;
            if (new NBTItem(item).hasKey(NBT_KEY + "_type") && ChanceOf(getNBT_lf(NBT_KEY + "_chance", p, es, 100))) {
                EntityDamageEvent.DamageCause reason = e.getCause();

                switch (getNBT_s(NBT_KEY + "_type", p, es, "")) {
                    case "invalidFire":
                        if (reason.equals(EntityDamageEvent.DamageCause.FIRE) || reason.equals(EntityDamageEvent.DamageCause.FIRE_TICK) || reason.equals(EntityDamageEvent.DamageCause.LAVA))
                            e.setCancelled(true);
                }
            }
        }
    }
}
