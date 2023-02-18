package net.mc42290.seeleserverassist.job;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class CheckMatch implements Listener {

    private final MainSystem MAIN_SYSTEM;
    public CheckMatch(JavaPlugin plugin, MainSystem system){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        MAIN_SYSTEM = system;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAttack(EntityDamageByEntityEvent e){
        if(!(e.getDamager() instanceof Player))return;
        Player p = (Player) e.getDamager();
        String item = p.getInventory().getItemInMainHand().getType().toString().toLowerCase();
        boolean jobMatch = true;

        if(item.endsWith("_sword") && jobMatch)jobMatch =MAIN_SYSTEM.isJobMatch(p, MainSystem.JOB.SWORD);
        else if(item.endsWith("_axe")&& jobMatch) jobMatch= MAIN_SYSTEM.isJobMatch(p, MainSystem.JOB.AXE);
        //else if(item.endsWith("_pickaxe")&& jobMatch) jobMatch = MAIN_SYSTEM.isJobMatch(p, MainSystem.JOB.SHOVEL);
        else if(item.endsWith("bow")&& jobMatch) jobMatch = MAIN_SYSTEM.isJobMatch(p, MainSystem.JOB.BOW);

        if (!jobMatch)e.setDamage(Math.max(1,e.getDamage()*0.01));

    }
}
