package net.mc42290.seeleserverassist.job.level;

import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

import static net.mc42290.seeleserverassist.job.JobMainSystem.YML_PREFIX;

public class LevelMainSystem {

    private static final JavaPlugin plugin = SeeleServerAssist.getInstance();
    private static final String[] jobNames = JobMainSystem.JOB.toStrings();
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
        boolean res =  USER_DATA_MAP.get(p).save(true);
        USER_DATA_MAP.remove(p);
        return res;
    }

    public long getPlayerLv(Player p){return getPlayerLv(p.getUniqueId());}
    public long getPlayerLv(UUID uuid){
        YamlConfiguration yml = CustomConfig.getYmlByID("userdata",uuid.toString());
        long[] jobLevels = new long[jobNames.length];
        for(int i = 1;i<jobNames.length;i++)jobLevels[i-1] =yml.getLong(YML_PREFIX+"."+jobNames[i]+".lv",0);
        return Calcer.calcPlayerLv(jobLevels);
    }

    public long getJobLv(OfflinePlayer p, JobMainSystem.JOB job){return getJobLv(p.getUniqueId(),job.toString());}
    public long getJobLv(UUID uuid,JobMainSystem.JOB job){return getJobLv(uuid,job.toString());}
    public long getJobLv(OfflinePlayer p,String job){return getJobLv(p.getUniqueId(),job);}
    public long getJobLv(UUID uuid,String job){
        YamlConfiguration yml =CustomConfig.getYmlByID("userdata",uuid.toString());
        long exp = yml.getLong(YML_PREFIX+"."+job+".exp",0);
        long jobLv = Calcer.calcJobLv(exp);
        return jobLv;
    }

    public long getExp(OfflinePlayer p, JobMainSystem.JOB job){return getExp(p.getUniqueId(),job.toString());}
    public long getExp(UUID uuid,JobMainSystem.JOB job){return getExp(uuid,job.toString());}
    public long getExp(OfflinePlayer p,String job){return getExp(p.getUniqueId(),job);}
    public long getExp(UUID uuid,String job){return CustomConfig.getYmlByID("userdata",uuid.toString()).getLong(YML_PREFIX+"."+job+".exp",0);}

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
