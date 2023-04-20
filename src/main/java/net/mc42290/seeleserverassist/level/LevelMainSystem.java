package net.mc42290.seeleserverassist.level;

import net.mc42290.seeleserverassist.SeeleServerAssist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class LevelMainSystem {
    private static final JavaPlugin plugin = SeeleServerAssist.getInstance();
    private final HashMap<Player,UserData> USER_DATA_MAP = new HashMap<>();

    public LevelMainSystem(){
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        new DataRecorder(this);
    }

    public UserData getUserData(Player p){
        return USER_DATA_MAP.get(p);
    }

    public void startRecord(Player p){
        USER_DATA_MAP.put(p,new UserData(p));
    }

    public boolean save(Player p){
        if(!USER_DATA_MAP.containsKey(p))return false;
        boolean res =  USER_DATA_MAP.get(p).save();
        USER_DATA_MAP.remove(p);
        return res;
    }


    private class listener implements Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent e){
            startRecord(e.getPlayer());
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e){
            //ymlに保存させる
            save(e.getPlayer());
        }
    }
}
