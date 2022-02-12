package io.github.humorousfool.hmweapons.crafting.forging;

import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArmourShape
{
    public ArmourShape(String name, String type, float durabilityMul, Material material, int customModelData, int materialsRequired)
    {
        this.name = name;
        this.type = type;
        this.durabilityMul = durabilityMul;

        this.material = material;
        this.customModelData = customModelData;
        this.materialsRequired = materialsRequired;
    }

    public final String name;
    public final String type;
    public final float durabilityMul;

    public final Material material;
    public final int customModelData;
    public final int materialsRequired;

    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name + " Shape");
        if(customModelData != 0)
            meta.setCustomModelData(customModelData);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.getPersistentDataContainer().set(ItemUtil.shapeKey, PersistentDataType.STRING, name.toLowerCase(Locale.ROOT).replace(' ', '_'));
        AttributeUtil.setDurability(meta, Config.MaxShapeUses);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Materials Required: " + materialsRequired);
        lore.add(ChatColor.GRAY + "Durability: " + durabilityMul + "x");
        lore.add("");
        lore.add(ChatColor.GRAY + "(" + Config.MaxShapeUses + "/" + Config.MaxShapeUses + ") Uses");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return ItemUtil.makeNonStackable(item);

    }
}
