package net.mc42290.seeleserverassist.job;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        String item = p.getInventory().getItemInMainHand().getType().toString().toLowerCase();
        boolean jobMatch = true;

        if(item.endsWith("_sword") && jobMatch)jobMatch =MAIN_SYSTEM.isJobMatch(p, JobMainSystem.JOB.SWORD);
        else if(item.endsWith("_axe")&& jobMatch) jobMatch= MAIN_SYSTEM.isJobMatch(p, JobMainSystem.JOB.AXE);
        //else if(item.endsWith("_pickaxe")&& jobMatch) jobMatch = MAIN_SYSTEM.isJobMatch(p, JobMainSystem.JOB.SHOVEL);
        else if(item.endsWith("bow")&& jobMatch) jobMatch = MAIN_SYSTEM.isJobMatch(p, JobMainSystem.JOB.BOW);

        if (!jobMatch)e.setDamage(Math.max(1,e.getDamage()*0.01));

    }
}
