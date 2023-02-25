package net.mc42290.seeleserverassist.job;

import net.mc42290.seeleserverassist.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class JobMainSystem {
    private final JobChange JOB_CHANGE_SYSTEM;
    private final JavaPlugin plugin;

    public final Predicate<Player> isNeet = p -> isJobMatch(p,JOB.NEET);

    public JobMainSystem(JavaPlugin plugin){
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        Bukkit.getOnlinePlayers().forEach(JobMainSystem.this::playerJobRegisterOnMemory);

        JOB_CHANGE_SYSTEM = new JobChange(plugin,this);

        new CheckMatch(plugin,this);
    }


    private final HashMap<UUID, Integer> PLAYER_JOB = new HashMap<>();


    public boolean isJobMatch(Player p,JOB job){return isJobMatch(p,job.getNum());}
    public boolean isJobMatch(Player p, int jobNum){
        String jobNumBi = Integer.toBinaryString(PLAYER_JOB.get(p.getUniqueId()));
        if(jobNumBi.length() < jobNum)return false;
        return jobNumBi.charAt(jobNumBi.length() - jobNum) == '1';
    }


    public void setJob(OfflinePlayer p,JOB job){setJob(p,new JOB[]{job});}
    public void setJob(OfflinePlayer p,JOB ...jobs ){
        int jobNum = 0;
        for(JOB job :jobs){jobNum+=(int)Math.pow(2,job.getNum()-1);}
        setJob(p,jobNum);
    }
    @Deprecated public void setJob(OfflinePlayer p, int jobNum){
        UUID u = p.getUniqueId();
        PLAYER_JOB.put(u,jobNum);
        CustomConfig.getYmlByID(u.toString()).set("job",jobNum);
        CustomConfig.saveYmlByID(u.toString());
    }

    public Set<JOB> getJob(OfflinePlayer p){return getJob(p.getUniqueId());}
    public Set<JOB> getJob(UUID u){
        Set<JOB> jobs = new HashSet<>();
        char[] jobdata = Integer.toBinaryString(CustomConfig.getYmlByID(u.toString()).getInt("job",0)).toCharArray();
        for(int i = 1;i<=jobdata.length;i++){
            if(jobdata[jobdata.length - i] == '1')jobs.add(JOB.values()[i-1]);
        }
        return jobs;
    }

    public JobChange getJobChangeSystem(){return JOB_CHANGE_SYSTEM;}

    public enum JOB{
        SWORD(1),
        AXE(2),
        SHIELD(3),
        BOW(4),
        LANCE(5),
        SCYTH(6),
        WIZARD(7),
        NEET(8);

        private final int jobNum;
        JOB(int num){
            jobNum = num;
        }
        public int getNum(){return jobNum;}
    }

    public void playerJobRegisterOnMemory(Player p){
        UUID uuid =p.getUniqueId();
        String uuidStr = uuid.toString();
        int playerJob = 1;
        if(CustomConfig.existYml(uuidStr))playerJob = CustomConfig.getYmlByID(uuidStr).getInt("job");
        else{
            CustomConfig.createYmlByID(uuidStr);
            CustomConfig.getYmlByID(uuidStr).set("job",128);
            CustomConfig.saveYmlByID(uuidStr);
        }
        PLAYER_JOB.put(uuid,playerJob);
    }


    private class listener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e){playerJobRegisterOnMemory(e.getPlayer());}

        @EventHandler
        public void onQuit(PlayerQuitEvent e){
            PLAYER_JOB.remove(e.getPlayer().getUniqueId().toString());
        }

    }
}
