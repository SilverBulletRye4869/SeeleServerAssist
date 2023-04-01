package net.mc42290.seeleserverassist;

import net.mc42290.seeleserverassist.Util.PlayerKill;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.damageEdit.DamageCalc;
import net.mc42290.seeleserverassist.deathpenalty.setHealthAndSatisfaction;
import net.mc42290.seeleserverassist.job.JobCommand;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import net.mc42290.seeleserverassist.spItem.Sp_MainSystem;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class SeeleServerAssist extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static JobMainSystem JOB_SYSTEM = null;
    //private static JDA jda = null;
    //private static Map<String, TextChannel> textChannelMap = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(!folderSetup()){
            UtilSet.sendConsole("プラグインフォルダの作成に失敗しました", UtilSet.MessageType.ERROR);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();
        // Plugin startup logic
        plugin = this;
        log = getLogger();

        new DamageCalc(this);
        new setHealthAndSatisfaction(this);
        JOB_SYSTEM = new JobMainSystem(this);
        new JobCommand(this,JOB_SYSTEM);
        new AssistCommand(this);

        new Sp_MainSystem(this).setup();

        //Util
        new PlayerKill(this);

        //botStart();
    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}
    public static JobMainSystem getJobSystem(){return JOB_SYSTEM;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        /*
        if(jda!=null){
            jda.shutdownNow();
        }
        */
    }

    private boolean folderSetup(){
        try{
            Files.createDirectories(Paths.get(this.getDataFolder()+"/data/userdata"));
            Files.createDirectories(Paths.get(this.getDataFolder()+"/log"));
        }catch (IOException e){
            UtilSet.sendConsole("dataフォルダ又はlogフォルダの作成に失敗しました", UtilSet.MessageType.ERROR);
            e.printStackTrace();
            return false;
        }
        File file = new File(this.getDataFolder()+"/README.txt");

        try {
            file.createNewFile();
        }catch (IOException e){
            if(Files.exists(Paths.get(this.getDataFolder()+"README.txt")))return true;
            else{
                UtilSet.sendConsole("README.txtファイルの作成に失敗しました", UtilSet.MessageType.ERROR);
                e.printStackTrace();
                return false;
            }
        }
        if(!file.isFile()){
            UtilSet.sendConsole("README.txtはファイルではありません", UtilSet.MessageType.ERROR);
            return false;
        }
        if(!file.canWrite()){
            UtilSet.sendConsole("README.txtは書き込み不可のファイルです", UtilSet.MessageType.ERROR);
            return false;
        }

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(
                    "[注意] config.yml以外のファイルを手動で編集した場合の動作は保証しません\n" +
                            "[Note] We do not guarantee the operation if files other than \"config.yml\" are edited manually."
            );
            fw.close();
        }catch (IOException e){
            UtilSet.sendConsole("README.txtファイルの書き込みに失敗しました", UtilSet.MessageType.ERROR);
            return false;
        }

        return true;
    }

    /*
    private boolean botStart(){
        final String BOT_TOKEN = getConfig().getString("discord.bot_token");
        if(BOT_TOKEN == null || BOT_TOKEN.equals(""))return false;
        try{
            jda = JDABuilder.createDefault(BOT_TOKEN, GatewayIntent.GUILD_MESSAGES)
                    .setRawEventsEnabled(true)
                    .setActivity(Activity.playing("Seele鯖"))
                    .build();
        }catch (LoginException e){
            UtilSet.sendConsole("BOTの起動に失敗しました。tokenが会っているかを再度ご確認ください。");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static TextChannel getTextChannel(String type){
        if(jda == null)return null;
        if(!textChannelMap.containsKey(type)){
            if(plugin.getConfig().getString("discord.channel."+type)==null)return null;
            textChannelMap.put(type,jda.getTextChannelById(plugin.getConfig().getString("discord.channel."+type)));
        }
        return textChannelMap.get(type);
    }

    */
}
