package net.mc42290.seeleserverassist.level;

import net.mc42290.seeleserverassist.SeeleServerAssist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class DataRecorder implements Listener {
    private static final JavaPlugin plugin = SeeleServerAssist.getInstance();

    private final LevelMainSystem MAIN_SYSTEM;

    public DataRecorder(LevelMainSystem mainSystem){
        this.MAIN_SYSTEM = mainSystem;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onAttackAndDamage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player)MAIN_SYSTEM.getUserData((Player)e.getDamager()).addAD(e.getDamage());
        if(e.getEntity() instanceof Player)MAIN_SYSTEM.getUserData((Player)e.getEntity()).addRD(e.getDamage());

    }

}
