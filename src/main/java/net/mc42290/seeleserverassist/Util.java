package net.mc42290.seeleserverassist;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Util {
    public static final String PREFIX = ChatColor.of("#171b4a") +"§l[Seele_42290]";
    public static final ItemStack GUI_BG = createItem(Material.BLUE_STAINED_GLASS_PANE,"§r");
    public static final ItemStack NULL_BG = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE,"§r");
    private static final Logger log = SeeleServerAssist.getLog();
    private static final JavaPlugin plugin = SeeleServerAssist.getInstance();

    public static ItemStack createItem(Material m,String name){return createItem(m,name,null,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore){return createItem(m,name,lore,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, Map<Enchantment,Integer> ench){return createItem(m,name,lore,0,ench);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){return createItem(m,name,lore,model,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model, Map<Enchantment,Integer> ench){
        ItemStack item = new ItemStack(m);
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta!=null){
            itemMeta.setDisplayName(name);
            if(lore!=null)itemMeta.setLore(lore);
            itemMeta.setCustomModelData(model);
            item.setItemMeta(itemMeta);
        }
        if(ench!=null)item.addUnsafeEnchantments(ench);
        return item;
    }

    public static void invFill(Inventory inv){invFill(inv,GUI_BG,false);}
    public static void invFill(Inventory inv,ItemStack item){invFill(inv,item,false);}
    public static void invFill(Inventory inv,ItemStack item,boolean isAppend){
        int size = inv.getSize();
        for(int i = 0;i<size;i++){
            if(isAppend && inv.getItem(i).getType() != Material.AIR)continue;
            inv.setItem(i,item);
        }
    }

    public static int[] getRectSlotPlaces(int start,int w,int h){
        int[] slotPlaces = new int[w*h];
        for(int i = 0;i<slotPlaces.length;i++)slotPlaces[i] = start + i % w + 9 * (i/w);
        return slotPlaces;
    }

    public static void sendPrefixMessage(Player p, String msg) {
        p.sendMessage(PREFIX + "§r" + msg);
    }

    public enum MessageType{INFO,WARNING,ERROR}
    public static void sendConsole(String msg){sendConsole(msg,MessageType.INFO);}
    public static void sendConsole(String msg, MessageType type){
        switch (type) {
            case INFO:
                log.info(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case WARNING:
                log.warning(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case ERROR:
                log.severe(String.format("[%s] " + msg, plugin.getDescription().getName()));
        }
    }

    //サジェストメッセージ送信
    public static void sendSuggestMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command));
        p.spigot().sendMessage(msg);
    }

    //ﾗﾝコマンドメッセージを送信
    public static void sendRunCommandMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(msg);
    }

    //アクションバーに表示
    public static void sendActionBar(Player p,String text){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public static void broadcast(String s){
        plugin.getServer().broadcastMessage(s);
    }

    public static int getNBT(String key, Entity e, EquipmentSlot type){
        if(e.getType() != EntityType.PLAYER)return 0;
        Player p = (Player) e;
        ItemStack item = p.getInventory().getItem(type);
        if(item == null || item.getType()== Material.AIR) return 0;
        return new NBTItem(item).getInteger(key);
    }

    public static boolean ChanceOf(double chance){
        double r = Math.random() * 100;
        if(r < chance)return true;
        else return false;
    }

    public static Location LocationCPY(Location loc){
        return new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch());
    }
}
