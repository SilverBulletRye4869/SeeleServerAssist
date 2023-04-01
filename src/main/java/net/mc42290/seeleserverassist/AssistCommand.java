package net.mc42290.seeleserverassist;

import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import net.mc42290.seeleserverassist.Util.PlayerKill;
import net.mc42290.seeleserverassist.Util.UtilSet;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;


public class AssistCommand implements CommandExecutor {
    public AssistCommand(JavaPlugin plugin){
        PluginCommand command = plugin.getCommand("mc42290");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(args.length==0){
            //ヘルプ
            return true;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        switch (args[0]){
            case "killme":
                PlayerKill.kill(p,p.getName()+"は自ら命を絶った。");
                break;
            case "getnbt":
                if(!sender.hasPermission("mc42290.admin"))return true;
                if(item.getAmount() == 0)return true;

                if(args.length > 1){
                    NBTItem nbtItem = new NBTItem(item);
                    if(!nbtItem.hasKey(args[1])) UtilSet.sendPrefixMessage(p,"null");
                    UtilSet.sendPrefixMessage(p,"§d§l{ "+args[1]+" : "+nbtItem.getString(args[1])+" }");
                }else UtilSet.sendPrefixMessage(p," §d§l"+new NBTItem(item).toString());
                break;
            case "setnbt":
                if(!sender.hasPermission("mc42290.admin"))return true;
                if(item.getAmount() == 0 || args.length < 3)return true;
                p.getInventory().setItemInMainHand(new NBTItem(item){{
                    if(args[2].matches("\\d+.?\\d*"))setDouble(args[1],Double.parseDouble(args[2]));
                    else setString(args[1],args[2]);
                }}.getItem());
                UtilSet.sendPrefixMessage(p,"§a§l手に持ってるアイテムにnbt§d§l{ "+args[1]+" : "+args[2]+" }§a§lを適用しました");
                break;

            case "removenbt":
                if(!sender.hasPermission("mc42290.admin"))return true;
                if(item.getAmount() == 0 || args.length < 2 )return true;
                p.getInventory().setItemInMainHand(new NBTItem(item){{
                    removeKey(args[1]);
                }}.getItem());
                UtilSet.sendPrefixMessage(p,"§a§l手に持っているアイテムのnbt§d§l"+args[1]+"§a§lを除去しました");
                break;

            case "getmodel":
                ItemMeta meta = item.getItemMeta();
                if(!sender.hasPermission("mc42290.admin") || meta==null)return true;
                UtilSet.sendPrefixMessage(p,"§a§l現在持っているアイテムのカスモデ番号: §d§l"+ (meta.hasCustomModelData() ? meta.getCustomModelData(): "null"));
                break;

            case "setmodel":
                if(!sender.hasPermission("mc42290.admin") || item.getItemMeta()==null|| args.length < 2 || !args[1].matches("\\d+"))return true;
                item.getItemMeta().setCustomModelData(Integer.parseInt(args[1]));
                UtilSet.sendPrefixMessage(p,"§a§l現在持っているアイテムのカスモデ番号を§d§l"+args[1]+"§a§lに変更しました");

        }
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            if(!(sender instanceof Player))return null;
            Player p = (Player) sender;
            switch (args.length){
                case 1:
                    if(sender.hasPermission("mc42290.admin"))return List.of("killme","getnbt","setnbt","getmodel","setmodel","removenbt");
                    else return List.of("killme");

                case 2:
                    switch (args[0]){
                        case "getnbt":
                            if(sender.hasPermission("mc42290.admin")){
                                ItemStack item = p.getInventory().getItemInMainHand();
                                if(item.getAmount() ==0)return null;
                                NBTItem nbtItem = new NBTItem(item);
                                return nbtItem.getKeys().stream()
                                        .filter(g -> nbtItem.getType(g).equals(NBTType.NBTTagInt))
                                        .collect(Collectors.toList())
;                            }
                    }

            }
            return List.of("");
        }
    }
}
