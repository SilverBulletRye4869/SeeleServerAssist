package net.mc42290.seeleserverassist;

import net.mc42290.seeleserverassist.Util.UtilSet;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomConfig {
    private static Map<String, YamlConfiguration> config = new HashMap<>();
    private static JavaPlugin plugin = SeeleServerAssist.getInstance();
    private static HashSet<String> existSet = new HashSet<>();

    public static YamlConfiguration getYmlByID(String parent,String id) {
        if(!config.containsKey(parent+"_"+id)){
            if(!reloadYmlByID(parent,id))return null;
        }
        return config.get(parent+"_"+id);
    }

    public static boolean existYml(String parent,String id){
        if(existSet.contains(parent+"_"+id))return true;
        if(new File(plugin.getDataFolder(),"data/"+parent+"/"+id+".yml").exists()){existSet.add(parent+"_"+id);return true;}
        return false;
    }

    public static YamlConfiguration createYmlByID(String parent,String id){
        File file = new File(plugin.getDataFolder(),"data/"+parent+"/"+id+".yml");
        try {
            file.createNewFile();
        }catch (IOException e){
            UtilSet.sendConsole("yml: 『"+parent+"/"+id+"』の作成に失敗しました。");
            e.printStackTrace();
            return null;
        }
        return getYmlByID(parent,id);
    }

    static boolean deleteYmlByID(String parent,String id){
        File file = new File(plugin.getDataFolder(),"data/"+parent+"/"+id+".yml");
        boolean result = file.delete();
        if(result){
            config.remove(parent+"_"+id);
            existSet.remove(parent+"_"+id);
        }
        return result;
    }

    public static boolean reloadYmlByID(String parent,String id){
        File file = new File(plugin.getDataFolder(),"data/"+parent+"/"+id+".yml");
        if(!file.exists())return false;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        config.put(parent+"_"+id,y);
        return true;
    }

    public static void saveYmlByID(String parent,String id){
        try{
            config.get(parent+"_"+id).save(new File(plugin.getDataFolder(),"data/"+parent+"/" + id + ".yml"));
        }catch (IOException e){
            UtilSet.sendConsole("yml: 『"+parent+"/"+id+"』の保存に失敗しました。");
        }
    }



}
