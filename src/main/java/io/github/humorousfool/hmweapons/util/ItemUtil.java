package io.github.humorousfool.hmweapons.util;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.EnumSet;
import java.util.Set;
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
    public static void setItem(ItemMeta meta, String id)
    {
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, id);
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

    public static EnumSet<Material> getBlocksFromTag(String string)
    {
        String nameKey = string.substring(1);
        NamespacedKey key = keyFromString(nameKey);
        if(key == null)
        {
            throw new IllegalArgumentException("Entry " + string + " is not a valid tag!");
        }
        Set<Material> tags = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class).getValues();
        return tags.isEmpty() ? EnumSet.noneOf(Material.class) : EnumSet.copyOf(tags);
    }

    public static NamespacedKey keyFromString(String string)
    {
        try{
            if(string.contains(":"))
            {
                int index = string.indexOf(':');
                String namespace = string.substring(0,index);
                String key = string.substring(index+1);
                // While a string based constructor is not supposed to be used,
                // their does not exist any other method for doing this in < 1.16
                return new NamespacedKey(namespace, key);
            }
            else
                return NamespacedKey.minecraft(string);
        } catch(IllegalArgumentException e){
            return null;
        }
    }
}
