package net.mc42290.seeleserverassist.job.skill;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Buff {

    private final JavaPlugin plugin = SeeleServerAssist.getInstance();
    private final JobMainSystem JOB_MAIN_SYSTEM;
    private final Table<String,Integer,Double> ATTACK_BUFF = HashBasedTable.create();
    private final Table<String,Integer,Double> RESISTANCE_BUFF = HashBasedTable.create();
    private final Table<String,Integer,Double> SPEED_BUFF = HashBasedTable.create();

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
            double attack=0,resistance=0,speed=0;
            for(int lv : levels){
                attack+=cs.getDouble(lv+".attack_buff",0);
                ATTACK_BUFF.put(job,lv,attack);
                resistance+=cs.getDouble(lv+".resistance_buff",0);
                RESISTANCE_BUFF.put(job,lv,resistance);
                speed+=cs.getDouble(lv+".speed_buff",0);
                SPEED_BUFF.put(job,lv,speed);
            }
        }
        return true;
    }

    public void buffDataClear(){
        ATTACK_BUFF.clear();
        RESISTANCE_BUFF.clear();
        SPEED_BUFF.clear();
    }

    public double getAttackBuff(String job,int targetLv){return getBuffAmount(ATTACK_BUFF,job,targetLv);}
    public double getResistanceBuff(String job, int targetLv){return getBuffAmount(RESISTANCE_BUFF,job,targetLv);}
    public double getSpeedBuff(String job,int targetLv){return getBuffAmount(SPEED_BUFF,job,targetLv);}

    private double getBuffAmount(Table<String,Integer,Double> table, String job, int targetLv){
        List<Integer> levels = new ArrayList<>(table.columnKeySet());
        Collections.reverse(levels);
        for(int lv : levels){
            if(lv < targetLv)return table.get(job,lv);
        }
        return 0.0;
    }
}
