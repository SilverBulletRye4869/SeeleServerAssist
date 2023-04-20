package net.mc42290.seeleserverassist.spItem.attack;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

import static net.mc42290.seeleserverassist.Util.UtilSet.*;
public class Physical implements Listener {
    private static final String NBT_KEY = "sp_atp";
    private static final Set<String> cooltime = new HashSet<>();

    private final JavaPlugin plugin;

    public Physical(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityAttack(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player p = (Player) e.getDamager();
        Location loc;

        Entity victim = e.getEntity();

        for(EquipmentSlot es : EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(es);
            if(item == null || item.getType().equals(Material.AIR))continue;
            if(!getNBT_s("slot",p,es,"hand").equals(es.toString().toLowerCase()))continue;
            if (new NBTItem(item).hasKey(NBT_KEY + "_type") && ChanceOf(getNBT_lf(NBT_KEY + "_chance", p, es, 100))) {
                switch (getNBT_s(NBT_KEY + "_type", e.getDamager(), es, "")) {
                    case "adwe":
                        new PotionEffect(PotionEffectType.WITHER, 200, 3).apply(((LivingEntity) victim));
                        p.playSound(p.getLocation(), "minecraft:entity.wither.hurt", 1, 1);

                    case "addm":
                        victim.getWorld().spawnParticle(Particle.SONIC_BOOM, victim.getLocation(), 1, 0, 0, 0);
                        p.playSound(p.getLocation(), "minecraft:entity.warden.sonic_boom", 1, 1);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> ((LivingEntity) victim).damage(getNBT_lf(NBT_KEY + "_amount", p, EquipmentSlot.HAND, 0)), 13/*0.65秒後*/);
                        break;
                    case "drain":
                        p.setHealth(Math.min(p.getMaxHealth(),e.getFinalDamage() * (getNBT_lf(NBT_KEY + "_amount", p, es, 0) / 100.0) + p.getHealth()));
                        break;
                    case "edReach":
                        if (p.getLocation().distance(victim.getLocation()) > UtilSet.getNBT_lf(NBT_KEY + "_amount", p, EquipmentSlot.HAND, 4.0))
                            e.setCancelled(true);
                        break;
                    case "throwTriArrow":
                        loc = p.getLocation();
                        for (int i = -1; i < 2; i++) {
                            Location loc2 = LocationCPY(loc);
                            int rota = 30 * i;
                            float yaw = loc.getYaw();
                            loc2.setYaw(yaw + rota);
                            loc2.setPitch(loc.getPitch());
                            loc2.add(Math.sin(yaw / 180 * Math.PI) * -0.3, 1.5, Math.cos(yaw / 180 * Math.PI) * 0.3);
                            Entity entity = p.getWorld().spawnEntity(loc2, EntityType.ARROW);
                            entity.teleport(loc2);
                            entity.setVelocity(loc2.getDirection().multiply(10));
                        }
                }
            }
        }



    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)))return;
        if(cooltime.contains("interact_"+p))return;
        cooltime.add("interact_"+p);
        Bukkit.getScheduler().runTaskLater(plugin,()->cooltime.remove("interact_"+p),1);  //2回は知らないようにするやつ


        if (new NBTItem(p.getInventory().getItemInMainHand()).hasKey(NBT_KEY + "_type") && ChanceOf(getNBT_lf(NBT_KEY + "_chance", p, EquipmentSlot.HAND, 100))) {
            switch (getNBT_s(NBT_KEY + "_type", e.getPlayer(), EquipmentSlot.HAND, "")) {
                case "edReach":
                    double reach = getNBT_lf(NBT_KEY+"_amount", p, EquipmentSlot.HAND, 6);
                    double theta = Math.PI/ 90.0;
                    double damage = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() * p.getAttackCooldown();
                    p.getNearbyEntities(reach, reach, reach).stream()
                            .filter(entity -> (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.SKELETON)))
                            .filter(entity -> p.getLocation().distance(entity.getLocation()) < reach)
                            .filter(p::hasLineOfSight)
                            .filter(entity -> UtilSet.getRelativeAngle(p, entity) < theta)
                            .map(entity -> (LivingEntity) entity)
                            .forEach(entity -> entity.damage(damage, p));

            }
        }
    }

}
