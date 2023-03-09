package net.mc42290.seeleserverassist.job;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class OffhandBowCancel implements Listener {
    private final Set<Material> CANCEL_TYPE = Set.of(Material.BOW, Material.CROSSBOW,Material.TRIDENT);

    private final JavaPlugin plugin;
    public OffhandBowCancel(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }


    @EventHandler
    public void onUse(EntityShootBowEvent e){
        if(CANCEL_TYPE.contains(e.getEntity().getEquipment().getItemInOffHand().getType()))e.setCancelled(true);
    }

    @EventHandler//有識者、トライデントのオフハンドで投げるキャンセル方法ﾓﾄﾑ！
    public void onUse(PlayerInteractEvent e){
        if((e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) &&
                e.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.TRIDENT))e.setCancelled(true);
    }
}
