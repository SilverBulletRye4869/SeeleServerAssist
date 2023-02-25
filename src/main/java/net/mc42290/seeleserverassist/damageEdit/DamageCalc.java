package net.mc42290.seeleserverassist.damageEdit;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DamageCalc implements Listener {
    //ノックバック処理無効のダメージ種
    private final Set<EntityDamageEvent.DamageCause> NO_KNOCKBACK_CASES = Set.of(
            EntityDamageEvent.DamageCause.DROWNING,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.POISON,
            EntityDamageEvent.DamageCause.WITHER,
            EntityDamageEvent.DamageCause.HOT_FLOOR,
            EntityDamageEvent.DamageCause.LAVA
    );

    private final Set<LivingEntity> COOLDOWN = new HashSet<>();

    private final JavaPlugin plugin;


    public DamageCalc(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent e){
        //プレイヤーの時だけ実行
        LivingEntity victim = (LivingEntity)e.getEntity();
        if(victim.getType()!= EntityType.PLAYER)return;
        e.setCancelled(true);

        if(COOLDOWN.contains(victim))return;
        double[] armor = armorCalc(victim);
        //victim.sendMessage(Arrays.toString(armor));
        double damage = e.getDamage();
        //victim.sendMessage("-----------------\ndef: "+e.getDamage());

        //乱数による上下の振れ幅
        double rand =  Math.random() * 0.5 + 0.8;
        //ダメージ軽減料（防具,防具強度で軽減できる）
        double shieldDamage = ( 4.2 * 0.0000001 * Math.pow(armor[0], 3.0) - 0.0006 * Math.pow(armor[0], 2.0) + 0.37*armor[0] ) / 100;

        damage-=armor[1];  //防具強度はそのままダメージ減衰
        damage *= rand * (1 -  shieldDamage) * enchantCalc(victim,e.getCause());  //防具値とエンチャントで軽減
        //ノックバック量（ノックバック耐性, 防具強度で軽減できる）
        double knockback = -0.7 * rand * (1-armor[2]/60*0.9);
        //victim.sendMessage("shield: "+shieldDamage);

        damage = Math.max(damage,0.1);
        if(victim.getHealth() - damage> 0){
            victim.setHealth(victim.getHealth() - damage);
            if(victim instanceof Player){
                COOLDOWN.add(victim);
                Bukkit.getScheduler().runTaskLater(plugin, () -> COOLDOWN.remove(victim),(long) (12+ 8 * (armor[1]/600)));
                ((Player)victim).playSound(victim.getLocation(),"minecraft:entity.player.death", 1, 1);
            }

            if(!NO_KNOCKBACK_CASES.contains(e.getCause()))victim.setVelocity(victim.getLocation().getDirection().multiply(knockback));
        }
        else{
            victim.setHealth(0);
        }
    }

    private final Set<EquipmentSlot> SLOT_SET = Set.of(
            EquipmentSlot.FEET,
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST,
            EquipmentSlot.HEAD,
            EquipmentSlot.OFF_HAND,
            EquipmentSlot.HAND
    );

    public double enchantCalc(LivingEntity entity, EntityDamageEvent.DamageCause damageCause){
        double shield = 0.0;
        EntityEquipment inv = entity.getEquipment();
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
        for (EquipmentSlot equipmentSlot : SLOT_SET) {
            ItemStack item = inv.getItem(equipmentSlot);
            if(item == null)continue;
            if(specialEnch!=null) shield += item.getEnchantmentLevel(specialEnch) * epf;
            shield += item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 0.015;
        }
        return Math.max((1.0 - shield),0.2);
    }


    private final Attribute[] ATTRIBUTES = {Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS, Attribute.GENERIC_KNOCKBACK_RESISTANCE};
    public double[] armorCalc(LivingEntity entity){
        double[] armor = {0,0,0}; //{防具, 防具強度, ノックバック耐性}
        EntityEquipment inv = entity.getEquipment();

        SLOT_SET.forEach(slot->
        {
            ItemStack itemS;
            ItemMeta itemM;
            if((itemS=inv.getItem(slot)) == null || (itemM = itemS.getItemMeta()) == null || !itemM.hasAttributeModifiers())return;

            for(int j = 0;j<ATTRIBUTES.length;j++){
                Collection<AttributeModifier> att = itemM.getAttributeModifiers(ATTRIBUTES[j]);
                if(att!=null) {
                    int place = j;
                    att.stream()
                            .filter(g-> g.getSlot()!=null)
                            .filter(g -> g.getSlot().equals(slot))
                            .forEach(g -> armor[place] += g.getAmount());
                }
            }
        });
        return armor;
    }
}
