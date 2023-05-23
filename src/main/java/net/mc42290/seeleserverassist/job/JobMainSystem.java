package net.mc42290.seeleserverassist.job;

import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.job.level.LevelMainSystem;
import net.mc42290.seeleserverassist.job.skill.Buff;
import net.mc42290.seeleserverassist.job.skill.BuffGui;
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
    public static final String YML_PREFIX = "record";

    private final JobChange JOB_CHANGE_SYSTEM;
    private final JavaPlugin plugin;

    public final LevelMainSystem LEVEL_SYSTEM;
    public final Predicate<Player> isNeet = p -> isJobMatch(p,JOB.NEET);
    public final Buff BUFF;
    public final BuffGui BUFF_GUI;


    public JobMainSystem(JavaPlugin plugin){
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        Bukkit.getOnlinePlayers().forEach(JobMainSystem.this::playerJobRegisterOnMemory);

        JOB_CHANGE_SYSTEM = new JobChange(plugin,this);
        new OffhandBowCancel(plugin);

        new CheckMatch(plugin,this);

        LEVEL_SYSTEM = new LevelMainSystem();
        Bukkit.getOnlinePlayers().forEach(LEVEL_SYSTEM::startRecord);

        BUFF = new Buff(this);
        Bukkit.getOnlinePlayers().forEach(BUFF::applyBuff);
        BUFF_GUI = new BuffGui(this);
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
        CustomConfig.getYmlByID("userdata",u.toString()).set("job",jobNum);
        CustomConfig.saveYmlByID("userdata",u.toString());
    }


    public Set<JOB> getJob(OfflinePlayer p){return getJob(p.getUniqueId());}
    public Set<JOB> getJob(UUID u){
        Set<JOB> jobs = new HashSet<>();
        char[] jobdata = getJob_c(u);
        for(int i = 1;i<=jobdata.length;i++){
            if(jobdata[jobdata.length - i] == '1')jobs.add(JOB.values()[i-1]);
        }
        return jobs;
    }

    public char[] getJob_c(UUID u){return Integer.toBinaryString(CustomConfig.getYmlByID("userdata",u.toString()).getInt("job",0)).toCharArray();}

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

        public static String getName(int index){
            try{
                return JOB.values()[index].toString();
            }catch (ArrayIndexOutOfBoundsException e){
                return null;
            }
        }

        private static String[] strings = null;
        public static String[] toStrings(){
            if(strings==null){
                JOB[] jobs = JOB.values();
                String[] strs = new String[jobs.length];
                for(int i = 0;i<strs.length;i++)strs[i] = jobs[i].toString();
                strings = strs;
            }
            return strings;
        }

        public static int size(){
            return JOB.values().length;
        }
    }

    public void playerJobRegisterOnMemory(Player p){
        UUID uuid =p.getUniqueId();
        String uuidStr = uuid.toString();
        int playerJob = 1;
        if(CustomConfig.existYml("userdata",uuidStr))playerJob = CustomConfig.getYmlByID("userdata",uuidStr).getInt("job");
        else{
            CustomConfig.createYmlByID("userdata",uuidStr);
            CustomConfig.getYmlByID("userdata",uuidStr).set("job",128);
            CustomConfig.saveYmlByID("userdata",uuidStr);
        }
        PLAYER_JOB.put(uuid,playerJob);
    }


    private class listener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent e){
            Player p = e.getPlayer();
            playerJobRegisterOnMemory(p);
            LEVEL_SYSTEM.startRecord(p);
            BUFF.applyBuff(p);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e){
            Player p = e.getPlayer();
            LEVEL_SYSTEM.save(p);
            PLAYER_JOB.remove(p.getUniqueId().toString());
        }

    }
}
