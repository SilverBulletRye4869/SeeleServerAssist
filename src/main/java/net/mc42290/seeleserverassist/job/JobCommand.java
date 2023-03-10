package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.SeeleServerAssist;
import net.mc42290.seeleserverassist.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JobCommand implements CommandExecutor {
    private final JobMainSystem JOB_MAIN_SYSTEM;

    public JobCommand(JavaPlugin plugin,JobMainSystem jobMainSystem){
        PluginCommand command = plugin.getCommand("mc42290.job");
        this.JOB_MAIN_SYSTEM = jobMainSystem;
        if(command==null){
            Util.sendConsole("commandの登録に失敗しました", Util.MessageType.ERROR);
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        OfflinePlayer target = null;

        if(args.length < 1){
            //ヘルプ
            return true;
        }

        switch (args[0]){
            case "getticket":
                if(!sender.hasPermission("mc42290.admin.job"))return true;
                int availableNum = args.length >= 2 && args[1].matches("\\d+") ? Integer.parseInt(args[1]) : 1;
                ItemStack item = new NBTItem(new ItemStack(Material.PAPER)){{
                    setInteger("jobchange", availableNum);
                }}.getItem();

                p.getInventory().addItem(item);
                Util.sendPrefixMessage(p,"§d§l職業選択券("+availableNum+"回分)§a§lを取得しました");
                break;

            case "setjob":
                if(!sender.hasPermission("mc42290.admin.job"))return true;
                if(args.length<3){
                    Util.sendPrefixMessage(p,"§c対象と、jobを正しく入力してください。");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    Util.sendPrefixMessage(p,"§c対象のプレイヤーが見つかりませんでした");
                    return true;
                }
                JobMainSystem.JOB job;
                try {
                    job = JobMainSystem.JOB.valueOf(args[2]);
                }catch (IllegalArgumentException e){
                    Util.sendPrefixMessage(p,"§c§l職業を正しく入力してください");
                    return true;
                }
                JOB_MAIN_SYSTEM.setJob(target,job);
                Util.sendPrefixMessage(p,"§d§l"+target.getName()+"§aの職業を§d§l"+job+"§a§lに変更しました");

                break;

            case "removejob":
                if(!sender.hasPermission("mc42290.admin.job") || args.length < 2)return true;
                target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    Util.sendPrefixMessage(p,"§c対象のプレイヤーが見つかりませんでした");
                    return true;
                }
                SeeleServerAssist.getJobSystem().setJob(target,0);

            case "getjob":
                if(!sender.hasPermission("mc42290.admin.job"))return true;
                if(args.length<2 || (target = Bukkit.getOfflinePlayer(args[1])) == null)return true;
            case "check":
                if(target == null)target=p;
                Util.sendPrefixMessage(p,"§d§l"+target.getName()+"§a§lの職業は次の通りです");
                Util.sendPrefixMessage(p,JOB_MAIN_SYSTEM.getJob(target).toString());
                break;

        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            switch (args.length){
                case 1:
                    if(sender.hasPermission("mc42290.admin.job"))return List.of("getticket","setjob","getjob","check","removejob");
                    else return List.of("check");
                case 2:
                    switch (args[0]){
                        case "setjob":
                        case "getjob":
                        case "removejob":
                            if(sender.hasPermission("mc42290.admin.job"))
                            return Arrays.asList(Bukkit.getOfflinePlayers()).stream()
                                    .filter(g->g.getName().matches("^"+args[1]+".*$"))
                                    .map(e->e.getName())
                                    .collect(Collectors.toList());
                    }
                    break;
                case 3:
                    switch (args[0]){
                        case "setjob":
                            if(sender.hasPermission("mc42290.admin.job"))
                            return Arrays.asList(JobMainSystem.JOB.values()).stream()
                                    .filter(g -> g.name().matches("^" + args[2] + ".*$"))
                                    .map(e -> e.name())
                                    .collect(Collectors.toList());
                    }
                    break;

            }
            return List.of("");
        }
    }
}
