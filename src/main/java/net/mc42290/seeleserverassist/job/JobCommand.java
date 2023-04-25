package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.job.level.Calcer;
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

            case "getlevel"-> {
                if(args.length<2) {
                    UtilSet.sendPrefixMessage(p, "§1§l------- [JobLevel] -------");
                    for (String job_s : JobMainSystem.JOB.toStrings())
                        if (!job_s.equals("NEET"))
                            UtilSet.sendPrefixMessage(p, "§a§l" + job_s + "§f§l -> §d§lLv" + JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getJobLv(p, job_s));
                    UtilSet.sendPrefixMessage(p, "§6§lPlayerLv §f§l-> §d§l§nLv" + JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getPlayerLv(p));
                }else{
                    if(args[1].equals("NEET"))return true;
                    if(!Arrays.asList(JobMainSystem.JOB.toStrings()).contains(args[1]))return true;
                    long exp = JOB_MAIN_SYSTEM.LEVEL_SYSTEM.getExp(p,args[1]);
                    UtilSet.sendPrefixMessage(p,"§1§l------- ["+args[1]+"] -------");
                    UtilSet.sendPrefixMessage(p,"§a§l現在の経験値: §d§l"+exp);
                    UtilSet.sendPrefixMessage(p,"§a§l現在のレベル: §d§l"+ Calcer.calcJobLv(exp));
                    UtilSet.sendPrefixMessage(p,"§a§l次のレベルまでに必要な経験値: §d§l"+Calcer.calcNeedExp(exp));
                }
            }

            case "check"-> {
                UtilSet.sendPrefixMessage(p, "§d§l" + p.getName() + "§a§lの職業は次の通りです");
                UtilSet.sendPrefixMessage(p, JOB_MAIN_SYSTEM.getJob(p).toString());
            }

        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
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
                        case "getlevel" ->{
                            List<String> list = Arrays.asList(JobMainSystem.JOB.toStrings());
                            list.remove("NEET");
                            return list;
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
                    }
                }

            }
            return List.of("");
        }
    }
}
