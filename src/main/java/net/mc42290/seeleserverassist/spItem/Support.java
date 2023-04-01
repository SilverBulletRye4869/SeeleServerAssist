package net.mc42290.seeleserverassist.spItem;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

import static net.mc42290.seeleserverassist.Util.UtilSet.*;

public class Support implements Listener {
    private static final String NBT_KEY = "sp_sp";
    private static final Set<String> cooltime = new HashSet<>();

    private final JavaPlugin plugin;

    public Support(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent e){
        if (!e.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player p = (Player) e.getDamager();
        Location loc;

        Entity victim = e.getEntity();
        for(EquipmentSlot es: EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(es);
            if(item == null || item.getType().equals(Material.AIR))continue;
            if(!getNBT_s("slot",p,es,"off_hand").equals(es.toString().toLowerCase()))continue;
            if (new NBTItem(item).hasKey(NBT_KEY + "_type") && ChanceOf(getNBT_lf(NBT_KEY + "_chance", p, es, 100))) {
                String type = getNBT_s(NBT_KEY + "_type", p,es, "");
                switch (type) {
                    case "showHp_per":
                    case "showHp_rea":
                        if (e.isCancelled()) return;
                        double hp = Math.max(((LivingEntity) victim).getHealth() - e.getFinalDamage(), 0);
                        double hp_max = ((LivingEntity) victim).getMaxHealth();

                        String msg = "§7§lHP: §a§l" + (type.equals("showHp_per") ? ((int) ((hp / hp_max) * 100) + "%") : String.format("%.2f§7§l/%.2f", hp, hp_max));
                        UtilSet.sendActionBar(p, msg);
                }

            }
        }
    }
}
