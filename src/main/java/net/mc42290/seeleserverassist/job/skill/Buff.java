package net.mc42290.seeleserverassist.job.skill;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import net.mc42290.seeleserverassist.job.level.UserData;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Buff {

    private final JavaPlugin plugin = SeeleServerAssist.getInstance();
    private final JobMainSystem JOB_MAIN_SYSTEM;
    private final Table<String,Integer,Double> ATTACK_BUFF = HashBasedTable.create();
    private final Table<String,Integer,Double> RESISTANCE_BUFF = HashBasedTable.create();
    private final Table<String,Integer,Double> SPEED_BUFF = HashBasedTable.create();
    private final Table<String,Integer,Double> HEALTH_BUFF = HashBasedTable.create();

    public final Table<Player,String,Double> BUFF_TABLE = HashBasedTable.create();

    public Buff(JobMainSystem mainSystem){
        JOB_MAIN_SYSTEM = mainSystem;
        setup();
    }

    public boolean setup(){
        YamlConfiguration yml = CustomConfig.getYmlByID("admin/job","buff.yml");
        if(yml==null)yml = CustomConfig.createYmlByID("admin/job","buff.yml");
        if(yml==null)return false;
        for(String job : JobMainSystem.JOB.toStrings()){
            ConfigurationSection cs = yml.getConfigurationSection(job);
            if(cs==null)continue;
            List<Integer> levels = cs.getKeys(false).stream().map(Integer::parseInt).collect(Collectors.toList());
            Collections.sort(levels);
            double attack=0,resistance=0,speed=0,health=0;
            for(int lv : levels){
                attack+=cs.getDouble(lv+".attack_buff",0);
                ATTACK_BUFF.put(job,lv,attack);
                resistance+=cs.getDouble(lv+".resistance_buff",0);
                RESISTANCE_BUFF.put(job,lv,resistance);
                speed+=cs.getDouble(lv+".speed_buff",0);
                SPEED_BUFF.put(job,lv,speed);
                health+=cs.getDouble(lv+".health_buff",0);
                HEALTH_BUFF.put(job,lv,health);
            }
        }
        return true;
    }

    public void buffDataClear(){
        ATTACK_BUFF.clear();
        RESISTANCE_BUFF.clear();
        SPEED_BUFF.clear();
        HEALTH_BUFF.clear();
    }

    public double getAttackBuff(String job,int targetLv){return getBuffAmount(ATTACK_BUFF,job,targetLv);}
    public double getResistanceBuff(String job, int targetLv){return getBuffAmount(RESISTANCE_BUFF,job,targetLv);}
    public double getSpeedBuff(String job,int targetLv){return getBuffAmount(SPEED_BUFF,job,targetLv);}
    public double getHealthBuff(String job,int targetLv){return getBuffAmount(HEALTH_BUFF,job,targetLv);}

    private double getBuffAmount(Table<String,Integer,Double> table, String job, int targetLv){
        List<Integer> levels = new ArrayList<>(table.columnKeySet());
        Collections.reverse(levels);
        for(int lv : levels){
            if(lv < targetLv)return table.get(job,lv);
        }
        return 0.0;
    }

    private void applyBuff(Player p){
        Set<JobMainSystem.JOB> jobs = JOB_MAIN_SYSTEM.getJob(p.getUniqueId());
        UserData data = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getUserData(p);

        double attack=0,resistance=0,speed=0,health=0;
        for(JobMainSystem.JOB job : jobs) {
            int lv = (int) data.getJobLv(job.getNum());
            attack += getAttackBuff(job.toString(), lv);
            resistance += getResistanceBuff(job.toString(), lv);
            speed += getSpeedBuff(job.toString(),lv);
            health += getHealthBuff(job.toString(),lv);
        }

        BUFF_TABLE.put(p,"attack",attack);
        BUFF_TABLE.put(p,"resistance",resistance);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() + speed);
        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() + health);
    }
}
