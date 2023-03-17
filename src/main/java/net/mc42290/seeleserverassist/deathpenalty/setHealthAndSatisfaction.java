package net.mc42290.seeleserverassist.deathpenalty;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class setHealthAndSatisfaction implements Listener {
    private final JavaPlugin plugin;
    private final Set<OfflinePlayer> BYPASS_PLAYERS = new HashSet<>();

    public setHealthAndSatisfaction(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        final Player p = e.getPlayer();
        if(p.hasPermission("mc42290.admin") || p.hasPermission("mc42290.deathpenalty.bypass") || BYPASS_PLAYERS.contains(p))return;
        Bukkit.getScheduler().runTaskLater(plugin,()->{
            p.setHealth(2);
            p.setFoodLevel(6);
        },1);
    }

    public boolean addPlayer(OfflinePlayer p){
        return BYPASS_PLAYERS.add(p);
    }

    public boolean removePlayer(OfflinePlayer p){
        return BYPASS_PLAYERS.remove(p);
    }

    public void clearPlayer(){
        BYPASS_PLAYERS.clear();
    }
}
