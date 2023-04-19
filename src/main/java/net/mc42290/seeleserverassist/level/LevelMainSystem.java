package net.mc42290.seeleserverassist.level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class LevelMainSystem {
    private final JavaPlugin plugin;
    private final HashMap<Player,UserData> USER_DATA_MAP = new HashMap<>();

    public LevelMainSystem(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        new DataRecorder(this);
    }

    public UserData getUserData(Player p){
        return USER_DATA_MAP.get(p);
    }

    public boolean save(Player p){
        if(!USER_DATA_MAP.containsKey(p))return false;
        return USER_DATA_MAP.get(p).save();
    }


    private class listener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent e){
            Player p = e.getPlayer();
            USER_DATA_MAP.put(p,new UserData(p));
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e){
            //ymlに保存させる
            USER_DATA_MAP.get(e.getPlayer()).save();
        }
    }
}
