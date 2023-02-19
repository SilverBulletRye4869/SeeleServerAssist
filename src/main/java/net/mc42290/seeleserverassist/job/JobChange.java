package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class JobChange {
    private static final ItemStack GUI_BG = Util.createItem(Material.GRAY_STAINED_GLASS_PANE,"§r");
    private static final ItemStack CONFIRM_BG = Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l確定", List.of("&c&k&laaa §c§l職業の変更は有料です &c&k&laaa"));
    private static final ItemStack CANCEL_BG = Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§v§lキャンセル");

    private final JavaPlugin plugin;
    private final Set<Player> openingChoiceMenu = new HashSet<>();
    private final Map<Player, JobMainSystem.JOB> confirmMenu = new HashMap<>();
    private final JobMainSystem MAIN_SYSTEM;


    public JobChange(JavaPlugin plugin, JobMainSystem mainSystem){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
        this.MAIN_SYSTEM = mainSystem;
    }

    public void openChangeMenu(Player p){
        Inventory inv = Bukkit.createInventory(p,9,Util.PREFIX+"§d§l職業選択画面");
        Util.invFill(inv,Util.GUI_BG);
        inv.setItem(1,Util.createItem(Material.DIAMOND_SWORD,"§b§lSWORD"));
        inv.setItem(2,Util.createItem(Material.NETHERITE_AXE, "§5§lAXE"));
        inv.setItem(3,Util.createItem(Material.GOLDEN_SHOVEL,"§6§lSHOVEL"));
        inv.setItem(4,Util.createItem(Material.BOW,"§c§lBOW"));
        inv.setItem(5,Util.createItem(Material.STICK ,"§d§lLANCE",null,Map.of(Enchantment.DAMAGE_ALL,1)));
        inv.setItem(6,Util.createItem(Material.SLIME_BALL,"§a§lSCYTH"));
        inv.setItem(7,Util.createItem(Material.KNOWLEDGE_BOOK,"§e§lWIZARD"));

        p.openInventory(inv);
    }

    public void openConfirmMenu(Player p, JobMainSystem.JOB job){
        Inventory inv = Bukkit.createInventory(p,27,"§a§l本当に§d§l"+job+"§a§lで確定しますか？");
        Util.invFill(inv);
        for(int i = 0;i<3;i++)inv.setItem(10+i,CONFIRM_BG);
        for(int i = 0;i<3;i++)inv.setItem(14+i,CANCEL_BG);
        p.openInventory(inv);
    }

    private class listener implements Listener {


        @EventHandler
        public void onTicketUse(PlayerInteractEvent e){
            if(!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR)))return;
            Player p = e.getPlayer();
            ItemStack item = p.getInventory().getItemInMainHand();
            NBTItem nbtItem = new NBTItem(item);
            if(!nbtItem.hasKey("jobchange"))return;
            int availableNum = nbtItem.getInteger("jobchange");
            if(availableNum < 1) {
                Util.sendPrefixMessage(p,"§c§lこのチケットは無効です。");
                return;
            }
            if(item.getAmount() > 1){
                Util.sendPrefixMessage(p,"§c§lスタックした状態での使用はできません");
                return;
            }
            openChangeMenu(p);
        }

        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            Player p = (Player) e.getWhoClicked();

            if(e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            if(openingChoiceMenu.contains(p)){
                int slot = e.getSlot();
                if(slot<1 || slot>7)return;
                JobMainSystem.JOB job = JobMainSystem.JOB.values()[slot-1];
                confirmMenu.put(p,job);
                openConfirmMenu(p,job);

            }else if(confirmMenu.keySet().contains(p)){
                int slot = e.getSlot();

                if(slot>9 && slot <13)openChangeMenu(p);
                else if(slot >13 && slot<17){
                    ItemStack item = p.getInventory().getItemInMainHand();
                    NBTItem nbtItem = new NBTItem(item);
                    int available = nbtItem.getInteger("jobchange");

                    if(!nbtItem.hasKey("jobchange") || available < 1 || item.getAmount() > 1){
                        Util.sendPrefixMessage(p,"§c§lエラーが発生しました。");
                        Util.sendPrefixMessage(p,"§c§l正しい職業変更券を1枚だけ持っているかを確認し、再度実行してください");
                        p.closeInventory();
                    }else{
                        JobMainSystem.JOB job = confirmMenu.get(p);
                        MAIN_SYSTEM.setJob(p,job);
                        nbtItem.setInteger("jobchange",--available);
                        if(available > 0) p.getInventory().setItemInMainHand(nbtItem.getItem());
                        else p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        Util.sendPrefixMessage(p,"§a§l正常に職業を§d§l"+job+"§a§lに変更しました");
                    }
                }

            }else{
                return;
            }
            e.setCancelled(true);
        }
    }
}
