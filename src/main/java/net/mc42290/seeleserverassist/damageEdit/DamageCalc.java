package net.mc42290.seeleserverassist.damageEdit;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DamageCalc implements Listener {
    //ノックバック処理無効のダメージ種
    private final Set<EntityDamageEvent.DamageCause> noNockBackCause = Set.of(
            EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.LAVA
    );

    private final Set<Player> coolDown = new HashSet<>();

    private final JavaPlugin plugin;

    public DamageCalc(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent e){
        //プレイヤーの時だけ実行
        Entity victim = e.getEntity();
        if(victim.getType()!= EntityType.PLAYER)return;
        e.setCancelled(true);
        Player p = (Player)victim;
        if(coolDown.contains(p))return;
        double[] armor = armorCalc(p);
        double damage = e.getDamage();

        //乱数による上下の振れ幅
        double rand =  Math.random() * 0.5 + 0.8;
        //ダメージ軽減料（防具,防具強度で軽減できる）
        double ShieldDamage = ( 4.2 * 0.0000001 * Math.pow(armor[0], 3.0) - 0.0006 * Math.pow(armor[0], 2.0) + 0.37*armor[0] ) / 100;

        damage-=armor[1];  //防具強度はそのままダメージ減衰
        damage *= rand * (1 -  ShieldDamage) * enchantCalc(p,e.getCause());  //防具値とエンチャントで軽減
        //ノックバック量（ノックバック耐性, 防具強度で軽減できる）
        double knockback = -0.7 * rand * (1-armor[2]/60*0.9);

        if(p.getHealth() - damage> 0){
            p.setHealth(p.getHealth() - damage);
            p.playSound(p.getLocation(),"minecraft:entity.player.death", 1, 1);
            coolDown.add(p);
            Bukkit.getScheduler().runTaskLater(plugin, () -> coolDown.remove(p),(long) (12+ 8 * (armor[1]/600)));

            if(!noNockBackCause.contains(e.getCause()))p.setVelocity(p.getLocation().getDirection().multiply(knockback));
        }
        else p.setHealth(0);
    }

    public double enchantCalc(Player p, EntityDamageEvent.DamageCause damageCause){
        double shield = 0.0;
        Inventory inv = p.getInventory();
        Enchantment specialEnch =null;
        double epf = 0;
        switch (damageCause){
            case LAVA:
            case FIRE:
            case FIRE_TICK:
                specialEnch =Enchantment.PROTECTION_FIRE;
                epf = 0.025;
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                epf = 0.03;
                specialEnch = Enchantment.PROTECTION_EXPLOSIONS;
                break;
            case FALL:
            case FLY_INTO_WALL:
                specialEnch = Enchantment.PROTECTION_FALL;
                epf = 0.12;
                break;
            case PROJECTILE:
                specialEnch = Enchantment.PROTECTION_PROJECTILE;
                epf = 0.03;
        }
        for(int i = 36;i<40;i++){
            ItemStack item = inv.getItem(i);
            if(item == null)continue;
            if(specialEnch!=null) shield += item.getEnchantmentLevel(specialEnch) * epf;
            shield += item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.015;
        }
        return Math.max((1.0 - shield),0.2);
    }

    private final Map<Integer, EquipmentSlot> CONST_SLOTMAP = Map.of(
            36,EquipmentSlot.LEGS,
            37,EquipmentSlot.FEET,
            38,EquipmentSlot.CHEST,
            39,EquipmentSlot.HEAD,
            40,EquipmentSlot.OFF_HAND
    );
    private final Attribute[] ATTRIBUTES = {Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS, Attribute.GENERIC_KNOCKBACK_RESISTANCE};
    public double[] armorCalc(Player p){
        double[] armor = {0,0,0}; //{防具, 防具強度, ノックバック耐性}

        Map<Integer,EquipmentSlot> slotMap = new HashMap<>(CONST_SLOTMAP){{put(p.getInventory().getHeldItemSlot(),EquipmentSlot.HAND);}};
        Inventory inv = p.getInventory();
        ItemStack itemS;
        ItemMeta itemM;
        for(int slot : slotMap.keySet()) {
            if((itemS=inv.getItem(slot)) == null || (itemM = itemS.getItemMeta()) == null || !itemM.hasAttributeModifiers())continue;

            for(int j = 0;j<ATTRIBUTES.length;j++){
                Collection<AttributeModifier> att = itemM.getAttributeModifiers(ATTRIBUTES[j]);
                if(att!=null) {
                    int place = j;
                    att.stream().filter(g -> g.getSlot().equals(slotMap.get(slot))).forEach(g -> armor[place] += g.getAmount());
                }
            }
        }
        return armor;
    }
}
