package net.mc42290.seeleserverassist.spItem;

import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static net.mc42290.seeleserverassist.Util.*;

public class Attack implements Listener {
    private final JavaPlugin plugin;
    public Attack(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageByEntityEvent e){
        if(!e.getDamager().getType().equals(EntityType.PLAYER))return;
        Player p = (Player) e.getDamager();
        Location loc;

        Entity victim = e.getEntity();

        int nbtM , nbtH;
        //メインハンド
        switch (nbtM = getNBT("adwe", e.getDamager(), EquipmentSlot.HAND)) {
            case 1:
            case 4:
                if (ChanceOf(85)) return;
                new PotionEffect(PotionEffectType.WITHER, 200, 3).apply(((LivingEntity)victim));
                p.playSound(p.getLocation(), "minecraft:entity.wither.hurt", 1, 1);

            case 5:
                if (ChanceOf(80)) return;
            case 9:
                victim.getWorld().spawnParticle(Particle.SONIC_BOOM, victim.getLocation(), 1, 0, 0, 0);
                p.playSound(p.getLocation(), "minecraft:entity.warden.sonic_boom", 1, 1);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (nbtM == 5) ((LivingEntity)victim).damage(14);
                    else ((LivingEntity)victim).damage(10);
                }, 13/*0.65秒後*/);
                break;
            case 8:
                p.setHealth(e.getDamage() * 0.3 + p.getHealth());
                break;
            case 12:
                double reach = getNBT("reach",e.getDamager(),EquipmentSlot.HAND);
                double distance = p.getLocation().distance(victim.getLocation());
                if(distance>reach)e.setCancelled(true);
                break;
            case 13:
                loc = p.getLocation();
                for(int i = -1;i<2;i++){
                    Location loc2 = LocationCPY(loc);
                    int rota = 30 * i;
                    float yaw = loc.getYaw();
                    loc2.setYaw(yaw + rota);
                    loc2.setPitch(loc.getPitch());
                    loc2.add(Math.sin(yaw/ 180 * Math.PI) * -0.3,1.5,Math.cos(yaw/ 180 * Math.PI) * 0.3);
                    Entity entity = p.getWorld().spawnEntity(loc2, EntityType.ARROW);
                    entity.teleport(loc2);
                    entity.setVelocity(loc2.getDirection().multiply(10));
                }


        }

        switch (nbtH = getNBT("adwe",e.getDamager(),EquipmentSlot.HEAD)){
            case 6:
            case 7:
                double hp = Math.max(((LivingEntity) victim).getHealth() - e.getFinalDamage(),0);
                double hp_max = ((LivingEntity) victim).getMaxHealth();
                String msg ="§7§lHP: §a§l"+( nbtH == 6 ? ( (int)((hp/hp_max)*100)+"%" ) : ( hp+"§7§l/"+hp_max ));
                Util.sendActionBar(p,msg);
        }

    }
}
