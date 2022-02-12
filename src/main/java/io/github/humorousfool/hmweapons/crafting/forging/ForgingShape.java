package io.github.humorousfool.hmweapons.crafting.forging;

import io.github.humorousfool.hmcombat.api.AttackSpeed;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForgingShape
{
    public final String name;
    public final String id;

    public final Material material;
    public final int customModelData;

    public final int materialsRequired;
    public final String colourGroup;
    public final String netheriteGroup;
    public final AttackSpeed attackSpeed;

    public final List<String> notes;

    public final boolean axePower;
    public final List<Integer> specialEffects;
    public final boolean ranged;

    public final float attackDamageMul;
    public final float attackPowerMul;

    public final float projectileDamageMul;

    public ForgingShape(MemorySection data)
    {
        this.name = data.getName();
        this.id = name.toLowerCase(Locale.ROOT).replace(' ', '_');

        this.material = Material.matchMaterial(data.getString("Material", "BARRIER"));
        this.customModelData = data.getInt("CustomModelData", 0);

        this.materialsRequired = data.getInt("MaterialsRequired");
        this.colourGroup = data.getString("ColourGroup");
        this.netheriteGroup = data.getString("NetheriteGroup");
        this.attackSpeed = AttackSpeed.fromInteger(data.getInt("AttackSpeed", 0));

        this.notes = data.getStringList("Notes");

        this.axePower = data.getBoolean("AxePower", false);

        this.specialEffects = data.getIntegerList("SpecialEffects");

        this.ranged = data.getBoolean("Ranged", false);

        this.attackDamageMul = (float) data.getDouble("Multipliers.ATTACK_DAMAGE", 0D);
        this.attackPowerMul = (float) data.getDouble("Multipliers.ATTACK_POWER", 0D);
        this.projectileDamageMul = (float) data.getDouble("Multipliers.PROJECTILE_DAMAGE", 0D);
    }

    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name + " Shape");
        if(customModelData != 0)
            meta.setCustomModelData(customModelData);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        meta.getPersistentDataContainer().set(ItemUtil.shapeKey, PersistentDataType.STRING, id);
        AttributeUtil.setUses(meta, Config.MaxShapeUses);

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Materials Required: " + materialsRequired);
        if(attackDamageMul != 0D)
        {
            if(!ranged)
                lore.add(ChatColor.GRAY + "Attack Speed: " + attackSpeed.title);
            lore.add(ChatColor.GRAY + "Attack Damage: " + ItemUtil.formatDecimal(attackDamageMul) + "x");
        }
        if(axePower)
            lore.add(ChatColor.GRAY + "Attack Power: 5");
        else if(attackPowerMul != 0D)
            lore.add(ChatColor.GRAY + "Attack Power: " + ItemUtil.formatDecimal(attackPowerMul) + "x");
        if(projectileDamageMul != 0D)
            lore.add(ChatColor.GRAY + "Projectile Damage: " + ItemUtil.formatDecimal(projectileDamageMul) + "x");
        for(String note : notes)
        {
            lore.add(ChatColor.GRAY + note);
        }
        lore.add("");
        lore.add(ChatColor.GRAY + "(" + Config.MaxShapeUses + "/" + Config.MaxShapeUses + ") Uses");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return ItemUtil.makeNonStackable(item);
    }
}
