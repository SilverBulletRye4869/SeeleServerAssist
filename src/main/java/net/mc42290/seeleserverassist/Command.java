package net.mc42290.seeleserverassist;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.job.JobMainSystem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Command implements CommandExecutor {
    public Command(JavaPlugin plugin){
        PluginCommand command = plugin.getCommand("mc42290");
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player) || !sender.hasPermission("mc42290.admin"))return true;
        Player p = (Player) sender;
        OfflinePlayer target = null;

        if(args.length < 1){
            //ヘルプ
            return true;
        }

        switch (args[0]){
            case "getticket":
                ItemStack item = new NBTItem(new ItemStack(Material.PAPER)){{
                    setInteger("jobchange", args.length >= 2 && args[1].matches("\\d+") ? Integer.parseInt(args[1]) : 1);
                }}.getItem();
                p.getInventory().addItem(item);
                Util.sendPrefixMessage(p,"§d§l職業選択券("+item.getAmount()+"回分)§a§lを取得しました");
                break;

            case "setjob":
                if(args.length<3){
                    Util.sendPrefixMessage(p,"§c対象と、jobを正しく入力してください。");
                    return true;
                }
                target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    Util.sendPrefixMessage(p,"§c対象のプレイヤーが見つかりませんでした");
                    return true;
                }
                JobMainSystem.JOB job = JobMainSystem.JOB.valueOf(args[2]);
                if(job.getNum()<1){
                    Util.sendPrefixMessage(p,"§c§l職業を正しく入力してください");
                    return true;
                }
                SeeleServerAssist.getJobSystem().setJob(target,job);
                Util.sendPrefixMessage(p,"§d§l"+target.getName()+"§aの職業を§d§l"+job+"§a§lに変更しました");

                break;

            case "getjob":
                if(args.length<2 || (target = Bukkit.getOfflinePlayer(args[1])) == null)return true;
                Util.sendPrefixMessage(p,"§d§l"+target.getName()+"§a§lの職業は次の通りです");
                Util.sendPrefixMessage(p,SeeleServerAssist.getJobSystem().getJob(target).toString());
                break;

        }
        return true;
    }
}
