package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
        Player p;
        if(e.getDamager() instanceof Player)p = (Player) e.getDamager();
        else if(e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player)p = (Player)((Projectile)e.getDamager()).getShooter();
        else{return;}

        boolean jobMatch = true;
        double lvBonus = 1.0;
        do {
            ItemStack item = p.getInventory().getItemInMainHand();
            if(item.getAmount() == 0)break;
            NBTItem nbtItem = new NBTItem(item);
            if(!nbtItem.hasKey("job"))break;
            int jobNum = nbtItem.getInteger("job");
            jobMatch = MAIN_SYSTEM.isJobMatch(p,jobNum);
            lvBonus += MAIN_SYSTEM.LEVEL_SYSTEM.getUserData(p).getJobLv(jobNum) / 100.0;
        }while (false);

        if(SeeleServerAssist.getJobSystem().BUFF.BUFF_TABLE.contains(p,"attack")){
            double attackBuffAmount = SeeleServerAssist.getJobSystem().BUFF.BUFF_TABLE.get(p,"attack");
            e.setDamage(e.getDamage() * (1.0 + attackBuffAmount / 100.0));
        }


        if (jobMatch)e.setDamage(e.getDamage()*lvBonus);
        else e.setDamage(Math.max(0.1*p.getAttackCooldown(),e.getDamage()*0.01));
        if(MAIN_SYSTEM.isNeet.test(p))e.setDamage(e.getDamage()/(Math.random()*8.0+2));
    }
}
