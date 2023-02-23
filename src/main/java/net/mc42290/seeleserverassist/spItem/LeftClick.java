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

import java.util.HashSet;
import java.util.Set;
import static net.mc42290.seeleserverassist.Util.*;

public class LeftClick implements Listener {
    private static final Set<String> cooltime = new HashSet<>();

    private final JavaPlugin plugin;
    public LeftClick(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)))return;
        if(cooltime.contains("interact_"+p))return;
        cooltime.add("interact_"+p);
        Bukkit.getScheduler().runTaskLater(plugin,()->cooltime.remove("interact_"+p),1);  //2回は知らないようにするやつ

        int nbtM = Util.getNBT("adwe",e.getPlayer(), EquipmentSlot.HAND);

        switch (nbtM){
            case 12:
                double reach = getNBT_lf("reach",p,EquipmentSlot.HAND,6);
                double theta = getNBT_lf("theta",p,EquipmentSlot.HAND, Math.PI / 90);
                double damage = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * p.getAttackCooldown();
                p.getNearbyEntities(reach,reach,reach).stream()
                    .filter(entity -> (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.SKELETON)))
                    .filter(entity -> p.getLocation().distance(entity.getLocation()) < reach)
                    .filter(p::hasLineOfSight)
                    .filter(entity -> Util.getRelativeAngle(p,entity)< theta)
                    .map(entity -> (LivingEntity)entity)
                    .forEach(entity -> entity.damage(damage,p));

        }
    }

}
