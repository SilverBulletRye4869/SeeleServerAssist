package net.mc42290.seeleserverassist;

import net.mc42290.seeleserverassist.damageEdit.DamageCalc;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import net.mc42290.seeleserverassist.spItem.Attack;
import net.mc42290.seeleserverassist.spItem.ReciveDamage;
import net.mc42290.seeleserverassist.spItem.RightClick;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class SeeleServerAssist extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static JobMainSystem JOB_SYSTEM = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        log = getLogger();

        new DamageCalc(this);
        JOB_SYSTEM = new JobMainSystem(this);
        new Attack(this);new ReciveDamage(this);new RightClick(this);
        new Command(this);
    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}
    public static JobMainSystem getJobSystem(){return JOB_SYSTEM;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static void folderSetup(){
    }
}
