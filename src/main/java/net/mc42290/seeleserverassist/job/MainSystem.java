package net.mc42290.seeleserverassist.job;

import net.mc42290.seeleserverassist.CustomConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MainSystem {
    public MainSystem(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }


    private final HashMap<String, Integer> PLAYER_JOB = new HashMap<>();


    public boolean isJobMatch(Player p, JOB job){
        String jobNumBi = Integer.toBinaryString(PLAYER_JOB.get(p.getUniqueId().toString()));
        int num = job.getNum();
        if(jobNumBi.length() < num)return false;
        return jobNumBi.charAt(jobNumBi.length() - num) == '1';
    }
    public enum JOB{
        SWORD(1),
        AXE(2),
        SHOVEL(3),
        BOW(4),
        LANCE(5),
        SCYTH(6),
        WIZARD(7);

        private final int jobNum;
        JOB(int num){
            jobNum = num;
        }
        public int getNum(){return jobNum;}
    }


    private class listener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e){
            String uuidStr = e.getPlayer().getUniqueId().toString();
            int playerJob = 1;
            if(CustomConfig.existYml(uuidStr))playerJob = CustomConfig.getYmlByID(uuidStr).getInt("job");
            else{
                CustomConfig.createYmlByID(uuidStr);
                CustomConfig.getYmlByID(uuidStr).set("job",1);
                CustomConfig.saveYmlByID(uuidStr);
            }
            PLAYER_JOB.put(uuidStr,playerJob);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e){
            PLAYER_JOB.remove(e.getPlayer().getUniqueId().toString());
        }
    }
}
