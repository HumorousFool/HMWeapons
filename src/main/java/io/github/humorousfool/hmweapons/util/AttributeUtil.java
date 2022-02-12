package io.github.humorousfool.hmweapons.util;

import io.github.humorousfool.hmcombat.api.AttackSpeed;
import io.github.humorousfool.hmcombat.api.StatUtil;
import io.github.humorousfool.hmweapons.HMWeapons;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AttributeUtil
{
    public static final NamespacedKey damageReductionKey = new NamespacedKey(HMWeapons.getInstance(), "damageReduction");
    public static final NamespacedKey armourWeightKey = new NamespacedKey(HMWeapons.getInstance(), "armourWeight");
    public static final NamespacedKey durabilityKey = new NamespacedKey(HMWeapons.getInstance(), "durability");
    public static final NamespacedKey maxDurabilityKey = new NamespacedKey(HMWeapons.getInstance(), "maxDurability");
    public static final NamespacedKey maxEnchantabilityKey = new NamespacedKey(HMWeapons.getInstance(), "maxEnchantability");
    public static final NamespacedKey projectileDamageKey = new NamespacedKey(HMWeapons.getInstance(), "projectileDamage");
    public static final NamespacedKey projectileVelocityKey = new NamespacedKey(HMWeapons.getInstance(), "projectileVelocity");

    public static void setPower(ItemMeta meta, int amount)
    {
        meta.getPersistentDataContainer().set(StatUtil.powerKey, PersistentDataType.INTEGER, amount);
    }
    public static void setSpeed(ItemMeta meta, AttackSpeed speed)
    {
        meta.getPersistentDataContainer().set(StatUtil.speedKey, PersistentDataType.INTEGER, speed.id);
    }

    public static void setDamageReduction(ItemMeta meta, double amount)
    {
        meta.getPersistentDataContainer().set(damageReductionKey, PersistentDataType.DOUBLE, amount);
    }

    public static double getDamageReduction(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        return getDamageReduction(item.getItemMeta());
    }

    public static double getDamageReduction(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(damageReductionKey, PersistentDataType.DOUBLE)) return 0;
        return meta.getPersistentDataContainer().get(damageReductionKey, PersistentDataType.DOUBLE);
    }

    public static void setArmourWeight(ItemMeta meta, double amount)
    {
        meta.getPersistentDataContainer().set(armourWeightKey, PersistentDataType.DOUBLE, amount);
    }
    public static double getArmourWeight(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        return getArmourWeight(item.getItemMeta());
    }
    public static double getArmourWeight(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(armourWeightKey, PersistentDataType.DOUBLE)) return 0;
        return meta.getPersistentDataContainer().get(armourWeightKey, PersistentDataType.DOUBLE);
    }

    public static void setDurability(ItemMeta meta, int amount)
    {
        meta.getPersistentDataContainer().set(durabilityKey, PersistentDataType.INTEGER, amount);
    }
    public static int getDurability(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return -1;
        return getDurability(item.getItemMeta());
    }
    public static int getDurability(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(durabilityKey, PersistentDataType.INTEGER)) return -1;
        return meta.getPersistentDataContainer().get(durabilityKey, PersistentDataType.INTEGER);
    }

    public static void setMaxDurability(ItemMeta meta, int amount)
    {
        meta.getPersistentDataContainer().set(maxDurabilityKey, PersistentDataType.INTEGER, amount);
    }
    public static int getMaxDurability(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return -1;
        return getDurability(item.getItemMeta());
    }
    public static int getMaxDurability(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(maxDurabilityKey, PersistentDataType.INTEGER)) return -1;
        return meta.getPersistentDataContainer().get(maxDurabilityKey, PersistentDataType.INTEGER);
    }

    public static void setMaxEnchantability(ItemMeta meta, int amount)
    {
        meta.getPersistentDataContainer().set(maxEnchantabilityKey, PersistentDataType.INTEGER, amount);
    }
    public static int getMaxMaxEnchantability(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(maxEnchantabilityKey, PersistentDataType.INTEGER)) return -1;
        return meta.getPersistentDataContainer().get(maxEnchantabilityKey, PersistentDataType.INTEGER);
    }

    public static void setProjectileDamage(ItemMeta meta, double amount)
    {
        meta.getPersistentDataContainer().set(projectileDamageKey, PersistentDataType.DOUBLE, amount);
    }
    public static double getProjectileDamage(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(projectileDamageKey, PersistentDataType.DOUBLE)) return 0;
        return meta.getPersistentDataContainer().get(projectileDamageKey, PersistentDataType.DOUBLE);
    }
    public static double getProjectileDamage(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        return getProjectileDamage(item.getItemMeta());
    }

    public static void setProjectileVelocity(ItemMeta meta, double amount)
    {
        meta.getPersistentDataContainer().set(projectileVelocityKey, PersistentDataType.DOUBLE, amount);
    }
    public static double getProjectileVelocity(ItemMeta meta)
    {
        if(!meta.getPersistentDataContainer().has(projectileVelocityKey, PersistentDataType.DOUBLE)) return 0;
        return meta.getPersistentDataContainer().get(projectileVelocityKey, PersistentDataType.DOUBLE);
    }
    public static double getProjectileVelocity(ItemStack item)
    {
        if(item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        return getProjectileVelocity(item.getItemMeta());
    }
}
