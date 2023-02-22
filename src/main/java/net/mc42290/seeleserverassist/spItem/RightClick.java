package net.mc42290.seeleserverassist.spItem;

import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class RightClick implements Listener {
    private static final Set<String> cooltime = new HashSet<>();

    private final JavaPlugin plugin;
    public RightClick(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAction(PlayerInteractEvent e){
        Player p = e.getPlayer();
        Location loc;
        switch (e.getAction()){
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                int nbtH = Util.getNBT("adwe",e.getPlayer(), EquipmentSlot.HAND);

                switch (nbtH){
                    case 10:
                    case 11:
                        if(cooltime.contains("bom_magic_"+p)) Util.sendPrefixMessage(p,"§cクールダウン中！");
                        else{
                            if ((loc = (nbtH == 10 ? p.getLocation().add(0, 1, 0) : p.getTargetBlockExact(100).getLocation())) == null)break;

                            loc.getWorld().getNearbyEntities(loc, 4, 4, 4).stream().filter(g -> g instanceof Monster).forEach(entity -> ((Monster) entity).damage(20));

                            p.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 10, 1, 0, 1);
                            p.playSound(p.getLocation(), "minecraft:entity.generic.explode", 1, 1);

                            cooltime.add("bom_magic_" + p);
                            Bukkit.getScheduler().runTaskLater(plugin, () -> cooltime.remove("bom_magic_" + p), 100);
                            break;
                        }

                }

                break;


            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:

        }
    }
}
