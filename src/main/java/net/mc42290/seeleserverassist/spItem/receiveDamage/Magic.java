package net.mc42290.seeleserverassist.spItem.receiveDamage;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
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

public class Magic implements Listener {
    private final JavaPlugin plugin;
    private static final String NBT_KEY = "sp_rdm";

    public Magic(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player))return;
        Player p = (Player) e.getEntity();

        EntityDamageEvent.DamageCause reason = e.getCause();
        for(EquipmentSlot es:EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(es);
            if(item == null || item.getType().equals(Material.AIR))continue;
            if(!getNBT_s("slot",p,es,"off_hand").equals(es.toString().toLowerCase()))continue;
            if (new NBTItem(item).hasKey(NBT_KEY + "_type") && ChanceOf(getNBT_lf(NBT_KEY + "_chance", p, es, 100))) {
                switch (getNBT_s(NBT_KEY + "_type", p, es, "")) {
                    case "invalidPoison":
                        if (reason.equals(EntityDamageEvent.DamageCause.POISON)) e.setCancelled(true);
                        break;
                    case "invalidWither":
                        if (reason.equals(EntityDamageEvent.DamageCause.WITHER)) e.setCancelled(true);
                        break;
                }
            }
        }
    }
}