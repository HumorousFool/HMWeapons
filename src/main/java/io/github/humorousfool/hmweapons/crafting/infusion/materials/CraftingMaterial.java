package io.github.humorousfool.hmweapons.crafting.infusion.materials;

import io.github.humorousfool.hmweapons.crafting.ColourManager;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import io.github.humorousfool.hmweapons.util.MaterialStats;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CraftingMaterial
{
    public final String name;
    public final int id;
    public final String prefix;
    public final ChatColor chatColour;

    public final MaterialStats stats;

    public final Material material;
    public final boolean customModelData;
    public final Color colour;
    public final boolean noArmourColour;

    public final String armourMaterial;

    public final boolean isNetherite;

    public CraftingMaterial(MemorySection data)
    {
        this.name = data.getName();
        this.id = data.getInt("Id");
        this.prefix = data.getString("Prefix", ChatColor.RED + "ERROR");
        this.chatColour = ChatColor.valueOf(data.getString("ChatColour", "WHITE"));

        this.stats = new MaterialStats((MemorySection) data.get("Stats"));

        this.material = Material.matchMaterial(data.getString("Material", "BARRIER"));
        this.customModelData = data.getBoolean("CustomModelData");
        List<Integer> rgb = data.getIntegerList("RGB");
        this.colour = Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2));
        this.noArmourColour = data.getBoolean("NoArmourColour", false);
        this.armourMaterial = data.getString("ArmourMaterial");

        this.isNetherite = data.getBoolean("IsNetherite", false);

        if(data.getBoolean("IsIngot", false))
        {
            if(isNetherite)
            {
                if(customModelData)
                    ColourManager.netheriteColours.put(colour, new ColourManager.TextureInfo(material, id));
                else
                    ColourManager.netheriteColours.put(colour, new ColourManager.TextureInfo(material, 0));
            }
            else
            {
                if(customModelData)
                    ColourManager.ingotColours.put(colour, new ColourManager.TextureInfo(material, id));
                else
                    ColourManager.ingotColours.put(colour, new ColourManager.TextureInfo(material, 0));
            }
        }
    }

    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chatColour + name);
        if(customModelData)
            meta.setCustomModelData(id);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(stats.getLore());
        meta.getPersistentDataContainer().set(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY, new int[] {id});

        item.setItemMeta(meta);
        return item;
    }
}
