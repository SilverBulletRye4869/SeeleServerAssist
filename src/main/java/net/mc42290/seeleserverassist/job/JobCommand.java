package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.job.level.Calcer;
import net.mc42290.seeleserverassist.job.level.UserData;
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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JobCommand implements CommandExecutor {
    private final JobMainSystem JOB_MAIN_SYSTEM;

    public JobCommand(JavaPlugin plugin,JobMainSystem jobMainSystem){
        PluginCommand command = plugin.getCommand("mc42290.job");
        this.JOB_MAIN_SYSTEM = jobMainSystem;
        if(command==null){
            UtilSet.sendConsole("commandの登録に失敗しました", UtilSet.MessageType.ERROR);
            return;
        }
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;

        if(args.length < 1){
            //ヘルプ
            return true;
        }

        switch (args[0]){
            case "getticket"-> {
                if (!sender.hasPermission("mc42290.admin.job")) return true;
                int availableNum = args.length >= 2 && args[1].matches("\\d+") ? Integer.parseInt(args[1]) : 1;
                ItemStack item = new NBTItem(new ItemStack(Material.PAPER)) {{
                    setInteger("jobchange", availableNum);
                }}.getItem();

                p.getInventory().addItem(item);
                UtilSet.sendPrefixMessage(p, "§d§l職業選択券(" + availableNum + "回分)§a§lを取得しました");
            }

            case "setjob"-> {
                if (!sender.hasPermission("mc42290.admin.job")) return true;
                if (args.length < 3) {
                    UtilSet.sendPrefixMessage(p, "§c対象と、jobを正しく入力してください。");
                    return true;
                }
                OfflinePlayer target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    UtilSet.sendPrefixMessage(p, "§c対象のプレイヤーが見つかりませんでした");
                    return true;
                }
                JobMainSystem.JOB job;
                try {
                    job = JobMainSystem.JOB.valueOf(args[2]);
                } catch (IllegalArgumentException e) {
                    UtilSet.sendPrefixMessage(p, "§c§l職業を正しく入力してください");
                    return true;
                }
                JOB_MAIN_SYSTEM.setJob(target, job);
                UtilSet.sendPrefixMessage(p, "§d§l" + target.getName() + "§aの職業を§d§l" + job + "§a§lに変更しました");

            }

            case "removejob"-> {
                if (!sender.hasPermission("mc42290.admin.job") || args.length < 2) return true;
                OfflinePlayer target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    UtilSet.sendPrefixMessage(p, "§c対象のプレイヤーが見つかりませんでした");
                    return true;
                }
                JOB_MAIN_SYSTEM.setJob(target, 0);
            }

            case "getjob"-> {
                if (!sender.hasPermission("mc42290.admin.job")) return true;
                OfflinePlayer target;
                if (args.length < 2 || (target = Bukkit.getOfflinePlayer(args[1])) == null) return true;
                UtilSet.sendPrefixMessage(p, "§d§l" + target.getName() + "§a§lの職業は次の通りです");
                UtilSet.sendPrefixMessage(p, JOB_MAIN_SYSTEM.getJob(target).toString());
            }

            case "addexp","removeexp" ->{
                if(args.length<3|| !args[2].matches("\\d+"))return true;
                int exp = Integer.parseInt(args[2]);
                Player target = Bukkit.getPlayer(args[1]);
                if(target==null)return true;
                UserData ud = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getUserData(target);
                ud.addBonusExp(args[0].equals("addexp") ? exp : -exp);
                ud.apply();
            }


            case "getlevel"-> {
                OfflinePlayer target;
                if(!p.hasPermission("mc42290.admin.job") || args.length<3)target=p;
                else target=Bukkit.getOfflinePlayer(args[2]);
                if(args.length<2) {
                    UtilSet.sendPrefixMessage(p, "§6§l------- [JobLevel] -------");
                    for (String job_s : JobMainSystem.JOB.toStrings())
                        if (!job_s.equals("NEET")) {
                            long lv = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getJobLv(target, job_s);
                            UtilSet.sendPrefixMessage(p, "§7§l" + job_s + " -> "+(lv>0 ? "§a§lLv" + lv : "§3§lLv0"));
                        }
                    UtilSet.sendPrefixMessage(p, "§7§lPlayerLv -> §e§l§nLv" + JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getPlayerLv(p));
                }else{
                    if(args[1].equals("NEET"))return true;
                    if(!Arrays.asList(JobMainSystem.JOB.toStrings()).contains(args[1]))return true;
                    long exp = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getExp(p,args[1]);
                    UtilSet.sendPrefixMessage(p,"§6§l------- ["+args[1]+"] -------");
                    UtilSet.sendPrefixMessage(p,"§7§l現在のレベル: §e§l"+ Calcer.calcJobLv(exp));
                    UtilSet.sendPrefixMessage(p,"§7§l現在の経験値: §a§l"+exp+" §7§l/ "+Calcer.calcNextLvExp(exp));
                }
            }

            case "check"-> {
                UtilSet.sendPrefixMessage(p, "§d§l" + p.getName() + "§a§lの職業は次の通りです");
                UtilSet.sendPrefixMessage(p, JOB_MAIN_SYSTEM.getJob(p).toString());
            }

            case "skillcheck" ->{
                if(args.length<2){
                    if(!Arrays.stream(JobMainSystem.JOB.toStrings()).toList().contains(args[1]))return true;
                    JobMainSystem.JOB job = JobMainSystem.JOB.valueOf(args[1]);
                    JOB_MAIN_SYSTEM.BUFF_GUI.open(p,job);
                }
            }

        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            if(!(sender instanceof Player))return null;
            Player p = (Player) sender;
            switch (args.length){
                case 1 -> {
                    return sender.hasPermission("mc42290.admin.job") ? List.of("getticket", "setjob", "getjob", "check", "removejob", "getlevel") : List.of("check","getlevel");
                }

                case 2-> {
                    switch (args[0]) {
                        case "setjob", "getjob", "removejob"-> {
                            if (sender.hasPermission("mc42290.admin.job"))
                                return Arrays.asList(Bukkit.getOfflinePlayers()).stream()
                                        .filter(g -> g.getName().matches("^" + args[1] + ".*$"))
                                        .map(e -> e.getName())
                                        .collect(Collectors.toList());
                        }
                        case "getlevel" -> {
                            return Arrays.stream(JobMainSystem.JOB.toStrings())
                                    .filter(g -> g.matches("^" + args[1].toUpperCase() + ".*$"))
                                    .filter(g -> !g.matches("NEET"))
                                    .collect(Collectors.toList());
                        }
                        case "skillcheck" ->{
                            return Arrays.stream(JobMainSystem.JOB.values())
                                    .filter(job -> JOB_MAIN_SYSTEM.isJobMatch(p,job.getNum()))
                                    .map(job -> job.toString())
                                    .collect(Collectors.toList());
                        }

                    }
                }

                case 3-> {
                    switch (args[0]) {
                        case "setjob"-> {
                            if (sender.hasPermission("mc42290.admin.job"))
                                return Arrays.asList(JobMainSystem.JOB.values()).stream()
                                        .filter(g -> g.name().matches("^" + args[2] + ".*$"))
                                        .map(e -> e.name())
                                        .collect(Collectors.toList());

                        }

                        case "getlevel" ->{
                            if(sender.hasPermission("mc42290.admin.job"))
                                return Bukkit.getOnlinePlayers().stream()
                                        .filter(g->g.getName().matches("^" + args[2] + ".*$"))
                                        .map(e->e.getName()).collect(Collectors.toList());
                        }
                    }
                }

            }
            return List.of("");
        }
    }
}
