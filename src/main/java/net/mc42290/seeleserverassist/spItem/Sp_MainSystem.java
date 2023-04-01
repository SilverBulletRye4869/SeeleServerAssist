package net.mc42290.seeleserverassist.spItem;

import net.mc42290.seeleserverassist.spItem.attack.Physical;
import net.mc42290.seeleserverassist.spItem.receiveDamage.Environment;
import net.mc42290.seeleserverassist.spItem.receiveDamage.Magic;
import org.bukkit.plugin.java.JavaPlugin;

public class Sp_MainSystem {
    private final JavaPlugin plugin;

    public Sp_MainSystem(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void setup(){
        new Physical(plugin);

        new Magic(plugin);
        new Environment(plugin);

        new Support(plugin);
    }
}
