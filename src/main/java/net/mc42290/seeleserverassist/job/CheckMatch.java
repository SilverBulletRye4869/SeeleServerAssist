package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class CheckMatch implements Listener {

    private final JobMainSystem MAIN_SYSTEM;
    public CheckMatch(JavaPlugin plugin, JobMainSystem system){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        MAIN_SYSTEM = system;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAttack(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player))return;
        Player p = (Player) e.getDamager();
        boolean jobMatch = true;
        do {
            ItemStack item = p.getInventory().getItemInMainHand();
            if(item.getAmount() == 0)break;
            NBTItem nbtItem = new NBTItem(item);
            if(!nbtItem.hasKey("job"))break;
            int jobNum = nbtItem.getInteger("job");
            jobMatch = MAIN_SYSTEM.isJobMatch(p,jobNum);

        }while (false);
        if (!jobMatch)e.setDamage(Math.max(0.1*p.getAttackCooldown(),e.getDamage()*0.01));
        if(MAIN_SYSTEM.isNeet.test(p))e.setDamage(e.getDamage()/(Math.random()*8.0+2));
    }
}
