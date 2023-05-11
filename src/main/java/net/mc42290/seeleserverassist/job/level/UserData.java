package net.mc42290.seeleserverassist.job.level;

import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.mc42290.seeleserverassist.job.JobMainSystem.YML_PREFIX;

public class UserData {
    private static final String[] jobNames = JobMainSystem.JOB.toStrings();



    private final UUID UUID;
    private final Player P;
    private long loginTime;
    private long lastApplyTime;
    private double attackDamageAmount = 0;
    private double attackDamageAmount_total = 0;
    private double receiveDamageAmount = 0;
    private double receiveDamageAmount_total = 0;
    private double healAmount = 0;
    private long bonusExp = 0;

    private long[] exp = new long[JobMainSystem.JOB.size()];
    private long[] lv = new long[JobMainSystem.JOB.size()];
    private char[] jobData;


    public UserData(Player p){
        UUID = p.getUniqueId();
        P = p;
        loginTime =lastApplyTime= System.currentTimeMillis();
        YamlConfiguration yml = CustomConfig.getYmlByID("userdata",UUID.toString());
        for(int i = 0;i<jobNames.length;i++){
            exp[i] = yml.getLong(YML_PREFIX+"."+jobNames[i]+".exp",0);
            lv[i] = yml.getLong(YML_PREFIX+"."+jobNames[i]+".lv",0);
        }
        jobData = SeeleServerAssist.getJobSystem().getJob_c(UUID);
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

    public void addBonusExp(long amount){bonusExp+=amount;}
    public void removeBonusExp(long amount){bonusExp = Math.max(0,bonusExp-amount);}
    public void resetBonusExp(long amount){bonusExp = 0;}

    public double getAD(){return attackDamageAmount;}
    public double getRD(){return receiveDamageAmount;}
    public double getH(){return healAmount;}
    public long getPlayTime(){return System.currentTimeMillis()- lastApplyTime;}
    public long getBonusExp(){return bonusExp;}

    public void reloadJob(){
        jobData = SeeleServerAssist.getJobSystem().getJob_c(UUID);
    }
    public long getJobLv(int jobNum){
        return (0 > jobNum ||jobNum>=lv.length) ? -1 : lv[jobNum];
    }

    public boolean apply(){
        if(jobData==null)return false;
        Long exp = Calcer.calcExp(getPlayTime(),attackDamageAmount,receiveDamageAmount,bonusExp);
        for(int i = 0;i<jobData.length;i++){
            String jobName = jobNames[i];
            if(jobName.equals("NEET")||jobData[jobData.length - i - 1] == '0')continue;
            this.exp[i]+=exp;
            this.lv[i] = Calcer.calcJobLv(this.exp[i]);
        }
        lastApplyTime = System.currentTimeMillis();
        this.reset();

        return true;
    }

    public boolean save(){return save(false);}
    public boolean save(boolean toApply){
        if(toApply)apply();
        YamlConfiguration yml = CustomConfig.getYmlByID("userdata",UUID.toString());
        JobMainSystem jobSystem =  SeeleServerAssist.getJobSystem();
        char[] jobData = jobSystem.getJob_c(UUID);
        if(jobData==null)return false;
        for(int i = 0;i<jobData.length;i++){
            String jobName = jobNames[i];
            if(jobName.equals("NEET")||jobData[jobData.length - i - 1] == '0')continue;
            yml.set(YML_PREFIX+"."+jobName+".attackDamageAmount",yml.getDouble(YML_PREFIX+"."+jobName+".attackDamageAmount",0)+attackDamageAmount_total);
            yml.set(YML_PREFIX+"."+jobName+".receiveDamageAmount",yml.getDouble(YML_PREFIX+"."+jobName+".receiveDamageAmount",0)+receiveDamageAmount_total);
            yml.set(YML_PREFIX+"."+jobName+".loginTime",yml.getLong(YML_PREFIX+"."+jobName+".loginTime",0)+getPlayTime());
            yml.set(YML_PREFIX+"."+jobName+".exp",this.exp[i]);
            yml.set(YML_PREFIX+"."+jobName+".lv",this.lv[i]);
        }
        yml.set(YML_PREFIX+".all.loginTime",yml.getLong(YML_PREFIX+".all.loginTime",0)+getPlayTime());
        attackDamageAmount_total = receiveDamageAmount_total = 0;
        reset();
        return CustomConfig.saveYmlByID("userdata",UUID.toString());
    }

    public void reset(){
        attackDamageAmount_total+=attackDamageAmount;
        receiveDamageAmount_total+=receiveDamageAmount;
        attackDamageAmount = receiveDamageAmount = healAmount = bonusExp = 0;
    }

}
