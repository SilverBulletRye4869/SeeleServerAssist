package net.mc42290.seeleserverassist.level;

import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class UserData {
    private static final JavaPlugin plugin = SeeleServerAssist.getInstance();

    private final UUID UUID;
    private final Player P;
    private final long LOGIN_TIME;
    private double attackDamageAmount = 0;
    private double receiveDamageAmount = 0;
    private double healAmount = 0;

    public UserData(Player p){
        UUID = p.getUniqueId();
        P = p;
        LOGIN_TIME = System.currentTimeMillis();
    }

    public void addAD(double amount){
        attackDamageAmount+=amount;
    }

    public void addRD(double amount){
        receiveDamageAmount+=amount;
    }

    public void addH(double amount){
        healAmount+=amount;
    }

    public double getAD(){return attackDamageAmount;}
    public double getRD(){return receiveDamageAmount;}
    public double getH(){return healAmount;}
    public long getPlayTime(){return System.currentTimeMillis()-LOGIN_TIME;}

    public boolean save(){
        YamlConfiguration yml = CustomConfig.getYmlByID("userdata",UUID.toString());
        JobMainSystem jobSystem =  SeeleServerAssist.getJobSystem();
        char[] jobData = jobSystem.getJob_c(UUID);
        if(jobData==null)return false;
        Long exp = Calcer.calcExp(getPlayTime(),attackDamageAmount,receiveDamageAmount);
        for(int i = 1;i<=jobData.length;i++){
            if(i==8||jobData[jobData.length - i] == '0')continue;
            yml.set("data."+(i-1)+".attackDamageAmount",yml.getDouble("data."+(i-1)+".attackDamageAmount",0)+attackDamageAmount);
            yml.set("data."+(i-1)+".receiveDamageAmount",yml.getDouble("data."+(i-1)+".receiveDamageAmount",0)+receiveDamageAmount);
            yml.set("data."+(i-1)+".loginTime",yml.getLong("data."+(i-1)+".loginTime")+getPlayTime());
            yml.set("data."+(i-1)+".exp",yml.getLong("data."+(i-1)+".exp")+exp);
        }
        yml.set("data.all.loginTime",yml.getLong("data.all.loginTime",0)+getPlayTime());
        return CustomConfig.saveYmlByID("userdata",UUID.toString());
    }

}
