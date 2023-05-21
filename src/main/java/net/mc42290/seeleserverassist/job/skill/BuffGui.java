package net.mc42290.seeleserverassist.job.skill;

import net.mc42290.seeleserverassist.CustomConfig;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffGui {
    private static final Map<String,String> BuffNameMap = Map.of(
            "attack_buff","攻撃力上昇",
            "resistance_buff","被ダメージ減少",
            "speed_buff","移動速度上昇",
            "health_buff","最大体力上昇"
    );

    private final JobMainSystem JOB_MAIN_SYSTEM;
    private final JavaPlugin plugin = SeeleServerAssist.getInstance();
    public BuffGui(JobMainSystem jobMainSystem){
        JOB_MAIN_SYSTEM = jobMainSystem;
    }

    public void open(Player p, JobMainSystem.JOB job){
        YamlConfiguration yml = CustomConfig.getYmlByID("admin/job","buff");
        ConfigurationSection cs =  yml.getConfigurationSection(job.toString());
        if(cs==null)return;
        int row = (cs.getKeys(false).size() + 8)/9;
        Inventory inv = Bukkit.createInventory(p,row, UtilSet.PREFIX+"§c§lバフリスト");
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            int slot = 0;
            long lv = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getJobLv(p,job);
            for(String lv_s : cs.getKeys(false)){
                Map<String,Double> dataMap = new HashMap<>();
                cs.getConfigurationSection(lv_s).getKeys(false).forEach(type-> dataMap.put(type,cs.getDouble(lv_s+"."+type)));
                List<String> detail = new ArrayList<>();
                dataMap.forEach((type,amount)-> detail.add("§c"+BuffNameMap.get(type)+" "+amount+"%"));
                inv.setItem(slot++,UtilSet.createItem(
                        Integer.parseInt(lv_s) < lv ? Material.PAPER : Material.MAP,
                        "§f§lLv"+lv_s,
                        detail
                ));
            };
        });

    }
}
