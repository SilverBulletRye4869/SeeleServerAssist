package net.mc42290.seeleserverassist.Util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerKill implements Listener {
    private static final Map<Player,String> KILL_MESSAGE_MEMO = new HashMap<>();
    private static final String PREFIX = "§7§l[§4§lDEATH§7§l]§c";

    public PlayerKill(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public static void kill(Player p,String reason){
        KILL_MESSAGE_MEMO.put(p,reason);
        p.setHealth(0);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player p = e.getEntity();
        if(KILL_MESSAGE_MEMO.containsKey(p)){
            e.setDeathMessage(PREFIX +KILL_MESSAGE_MEMO.get(p));
            KILL_MESSAGE_MEMO.remove(p);
        }
        else e.setDeathMessage(PREFIX+e.getDeathMessage());
    }



}