package net.mc42290.seeleserverassist.spItem;

import net.mc42290.seeleserverassist.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class ReciveDamage implements Listener {
    private final JavaPlugin plugin;
    public ReciveDamage(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player))return;

        int nbtH = Util.getNBT("adwe", e.getEntity(), EquipmentSlot.HAND);
        int nbtOH = Util.getNBT("adwe", e.getEntity(), EquipmentSlot.OFF_HAND);
        if(nbtOH + nbtH ==0)return;
        EntityDamageEvent.DamageCause reason = e.getCause();
        switch (reason){
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                if(nbtOH==2)e.setCancelled(true);
                return;
            case POISON:
                if(nbtOH==3) e.setCancelled(true);
                return;
            case WITHER:
                if(nbtOH==4)e.setCancelled(true);
                return;
        }
    }
}
