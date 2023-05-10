package net.mc42290.seeleserverassist.job;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.mc42290.seeleserverassist.Util.UtilSet;
import net.mc42290.seeleserverassist.job.level.UserData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class JobChange {
    private static final ItemStack GUI_BG = UtilSet.createItem(Material.GRAY_STAINED_GLASS_PANE,"§r");
    private static final ItemStack CONFIRM_BG = UtilSet.createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l確定", List.of("§c§k§laaa §c§l職業の変更は有料です §c§k§laaa"));
    private static final ItemStack CANCEL_BG = UtilSet.createItem(Material.RED_STAINED_GLASS_PANE,"§v§lキャンセル");

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
        p.closeInventory();
        Inventory inv = Bukkit.createInventory(p,9, UtilSet.PREFIX+"§d§l職業選択画面");
        UtilSet.invFill(inv, UtilSet.GUI_BG);
        inv.setItem(1, UtilSet.createItem(Material.IRON_SWORD,"§b§lSWORD",null,11));
        inv.setItem(2, UtilSet.createItem(Material.IRON_AXE, "§5§lAXE",null,1));
        inv.setItem(3, UtilSet.createItem(Material.SHIELD,"§6§lSHIELD",null,6));
        inv.setItem(4, UtilSet.createItem(Material.BOW,"§c§lBOW"));
        inv.setItem(5, UtilSet.createItem(Material.IRON_SHOVEL ,"§d§lLANCE",null,1));
        inv.setItem(6, UtilSet.createItem(Material.IRON_HOE,"§a§lSCYTH"));
        inv.setItem(7, UtilSet.createItem(Material.KNOWLEDGE_BOOK,"§e§lWIZARD"));
        openingChoiceMenu.add(p);
        Bukkit.getScheduler().runTaskLater(plugin,()->p.openInventory(inv),1);
    }

    public void openConfirmMenu(Player p, JobMainSystem.JOB job){
        p.closeInventory();
        Inventory inv = Bukkit.createInventory(p,27,"§a§l本当に§d§l"+job+"§a§lで確定しますか？");
        UtilSet.invFill(inv);
        for(int i = 0;i<3;i++)inv.setItem(10+i,CANCEL_BG);
        for(int i = 0;i<3;i++)inv.setItem(14+i,CONFIRM_BG);
        confirmMenu.put(p,job);
        Bukkit.getScheduler().runTaskLater(plugin,()->p.openInventory(inv),1);
    }

    private class listener implements Listener {
        private final Set<Player> DOUBLE_RUN_CHECK_SET = new HashSet<>();

        @EventHandler
        public void onTicketUse(PlayerInteractEvent e){
            Player p = e.getPlayer();
            if(!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) || DOUBLE_RUN_CHECK_SET.contains(p))return;
            DOUBLE_RUN_CHECK_SET.add(p);
            Bukkit.getScheduler().runTaskLater(plugin,()->DOUBLE_RUN_CHECK_SET.remove(p),1);

            ItemStack item = p.getInventory().getItemInMainHand();
            if(item.getAmount()==0)return;
            NBTItem nbtItem = new NBTItem(item);
            if(!nbtItem.hasKey("jobchange"))return;
            int availableNum = nbtItem.getInteger("jobchange");
            if(availableNum < 1) {
                UtilSet.sendPrefixMessage(p,"§c§lこのチケットは無効です。");
                return;
            }
            if(item.getAmount() > 1){
                UtilSet.sendPrefixMessage(p,"§c§lスタックした状態での使用はできません");
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin,()->{
                InventoryType invType= p.getOpenInventory().getType();
                if( invType.equals(InventoryType.CRAFTING) || (p.getGameMode().equals(GameMode.CREATIVE) && invType.equals(InventoryType.CREATIVE)) )openChangeMenu(p);
            },1);
        }

        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            Player p = (Player) e.getWhoClicked();

            do {
                if(e.getCurrentItem() == null)return;
                int slot = e.getClickedInventory().getType().equals(InventoryType.CHEST) ? e.getSlot() : -1;  //開いているインベントリじゃないときはクリック処理は無効化
                if (openingChoiceMenu.contains(p)) {
                    if (slot < 1 || slot > 7) break;
                    JobMainSystem.JOB job = JobMainSystem.JOB.values()[slot - 1];
                    p.closeInventory();
                    openConfirmMenu(p, job);

                } else if (confirmMenu.containsKey(p)) {

                    if (slot > 9 && slot < 13) openChangeMenu(p);
                    else if (slot > 13 && slot < 17) {
                        ItemStack item = p.getInventory().getItemInMainHand();
                        if(item.getAmount() == 0)break;
                        NBTItem nbtItem = new NBTItem(item);
                        int available = nbtItem.getInteger("jobchange");

                        if (!nbtItem.hasKey("jobchange") || available < 1 || item.getAmount() > 1) {
                            UtilSet.sendPrefixMessage(p, "§c§lエラーが発生しました。");
                            UtilSet.sendPrefixMessage(p, "§c§l正しい職業変更券を1枚だけ持っているかを確認し、再度実行してください");
                            p.closeInventory();
                        } else {
                            JobMainSystem.JOB job = confirmMenu.get(p);
                            UserData userData = MAIN_SYSTEM.LEVEL_SYSTEM.getUserData(p);
                            userData.save(true);
                            MAIN_SYSTEM.setJob(p, job);
                            userData.reloadJob();
                            nbtItem.setInteger("jobchange", --available);
                            if (available > 0) p.getInventory().setItemInMainHand(nbtItem.getItem());
                            else p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                            UtilSet.sendPrefixMessage(p, "§a§l正常に職業を§d§l" + job + "§a§lに変更しました");
                            p.closeInventory();
                        }
                    }

                } else {
                    //無関係のGUIの時はスルー
                    return;
                }
            }while(false);

            e.setCancelled(true);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            Player p = (Player) e.getPlayer();
            openingChoiceMenu.remove(p);
            confirmMenu.remove(p);
        }
    }


}
