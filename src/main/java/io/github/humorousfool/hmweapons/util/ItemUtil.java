package io.github.humorousfool.hmweapons.util;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemUtil
{
    public static final NamespacedKey materialKey = new NamespacedKey(HMWeapons.getInstance(), "material");
    public static final NamespacedKey shapeKey = new NamespacedKey(HMWeapons.getInstance(), "shape");
    public static final NamespacedKey blockKey = new NamespacedKey(HMWeapons.getInstance(), "block");
    public static final NamespacedKey itemKey = new NamespacedKey(HMWeapons.getInstance(), "item");
    private static final NamespacedKey stackabilityKey = new NamespacedKey(HMWeapons.getInstance(), "unstackable");

    public static String getBlock(PersistentDataHolder holder)
    {
        if(!holder.getPersistentDataContainer().has(blockKey, PersistentDataType.STRING))
            return null;
        return holder.getPersistentDataContainer().get(blockKey, PersistentDataType.STRING);
    }

    public static String getItem(ItemStack item)
    {
        if(!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(itemKey, PersistentDataType.STRING))
            return null;
        return item.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
    }

    public static ItemStack makeNonStackable(ItemStack item)
    {
        ItemMeta meta = item.getItemMeta();
        makeNonStackable(meta);
        item.setItemMeta(meta);
        return item;
    }

    public static void makeNonStackable(ItemMeta meta)
    {
        meta.getPersistentDataContainer().set(stackabilityKey, PersistentDataType.STRING, UUID.randomUUID().toString());
    }

    public static String formatDecimalPlus(double value)
    {
        String s = formatDecimal(value);
        if(value >= 0)
            return "+" + s;
        return s;
    }

    public static String formatDecimal(double value)
    {
        String s = Double.toString(value);
        if(s.endsWith(".0"))
            return s.replace(".0", "");
        else if(s.contains(".") && s.endsWith("0"))
            return String.format("%.1f", value);
        else
            return String.format("%.2f", value);
    }

    public static EquipmentSlot getArmourSlot(ItemStack item)
    {
        if (item == null) return null;
        final String typeNameString = item.getType().name();
        if(typeNameString.endsWith("_HELMET")) return EquipmentSlot.HEAD;
        if(typeNameString.endsWith("_CHESTPLATE")) return EquipmentSlot.CHEST;
        if(typeNameString.endsWith("_LEGGINGS")) return EquipmentSlot.LEGS;
        if(typeNameString.endsWith("_BOOTS")) return EquipmentSlot.FEET;

        return null;
    }
}
