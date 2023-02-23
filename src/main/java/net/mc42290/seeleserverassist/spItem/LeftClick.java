package net.mc42290.seeleserverassist.spItem;

import net.mc42290.seeleserverassist.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.mc42290.seeleserverassist.Util.getNBT;

public class LeftClick implements Listener {
    private static final Set<String> cooltime = new HashSet<>();

    private final JavaPlugin plugin;
    public LeftClick(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeftClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)))return;
        if(cooltime.contains("interact_"+p))return;
        cooltime.add("interact_"+p);
        Bukkit.getScheduler().runTaskLater(plugin,()->cooltime.remove("interact_"+p),1);  //2回は知らないようにするやつ

        Location loc = p.getLocation();
        int nbtM = Util.getNBT("adwe",e.getPlayer(), EquipmentSlot.HAND);

        switch (nbtM){
            case 12:
                double reach = getNBT("reach",p,EquipmentSlot.HAND);
                double damage = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * p.getAttackCooldown();
                p.getNearbyEntities(reach,reach,reach).stream()
                    .filter(entity -> (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.SKELETON)))
                    .filter(entity -> p.getLocation().distance(entity.getLocation()) < reach)
                    .filter(entity -> p.canSee(entity))
                    .filter(entity -> Util.getRelativeAngle(p,entity)< 2.0/180.0*Math.PI)
                    .map(entity -> (LivingEntity)entity)
                    .forEach(entity -> entity.damage(damage));

        }
    }

}
