package io.github.humorousfool.hmweapons.items;

import io.github.humorousfool.hmcombat.api.AttackSpeed;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.items.preset.PresetEffect;
import io.github.humorousfool.hmweapons.items.preset.PresetEffects;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import io.github.humorousfool.hmweapons.util.MaterialStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomItem
{
    public final String id;

    public final String name;
    public final String type;

    public final Material material;
    public final int customModelData;

    public final boolean hasLives;
    public final boolean isStackable;
    public final boolean axePower;

    public final MaterialStats stats;
    public final AttackSpeed attackSpeed;

    public final List<String> notes;

    public final ArrayList<PresetEffect> effects = new ArrayList<>();

    public CustomItem(MemorySection data)
    {
        this.id = data.getName();

        this.name = data.getString("Name").replace("&", "§");
        this.type = data.getString("Type");

        this.material = Material.matchMaterial(data.getString("Material", "BARRIER"));
        this.customModelData = data.getInt("CustomModelData");

        this.hasLives = data.getBoolean("HasLives", false);
        this.isStackable = data.getBoolean("Stackable", false);
        this.axePower = data.getBoolean("AxePower", false);

        if(data.contains("Stats"))
            this.stats = new MaterialStats((MemorySection) data.get("Stats"));
        else
            this.stats = null;

        if(data.contains("AttackSpeed"))
            this.attackSpeed = AttackSpeed.fromInteger(data.getInt("AttackSpeed"));
        else
            this.attackSpeed = null;

        if(data.contains("Notes"))
        {
            this.notes = data.getStringList("Notes");
            for(int i = 0; i < notes.size(); i++)
            {
                notes.set(i, notes.get(i).replace("&", "§"));
            }
        }
        else
            this.notes = null;

        if(data.contains("PresetEffects"))
        {
            MemorySection effectSection = (MemorySection) data.get("PresetEffects");
            for(String key : effectSection.getKeys(false))
            {
                String sanitizedKey = key;
                try
                {
                    PresetEffect.ConditionalResponse response;
                    if(key.endsWith("_"))
                    {
                        response = PresetEffect.ConditionalResponse.IGNORE;
                        sanitizedKey = key.substring(0, key.length() - 1);
                    }
                    else if(key.endsWith("-"))
                    {
                        response = PresetEffect.ConditionalResponse.BREAK;
                        sanitizedKey = key.substring(0, key.length() - 1);
                    }
                    else
                        response = PresetEffect.ConditionalResponse.RETURN;

                    PresetEffect.ConditionalFlags flags = new PresetEffect.ConditionalFlags(key.contains("!"), response);
                    Class<? extends PresetEffect> effectClass = PresetEffects.valueOf(sanitizedKey.replace("+", "").replace("!", "").toUpperCase(Locale.ROOT)).clazz;
                    effects.add(effectClass.getDeclaredConstructor(List.class, PresetEffect.ConditionalFlags.class).newInstance(effectSection.getStringList(key), flags));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);
        ItemUtil.setItem(meta, id);

        if(item.getMaxStackSize() > 1 && !isStackable)
        {
            ItemUtil.makeNonStackable(meta);
        }

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();
        if(type != null)
            lore.add(ChatColor.GRAY + type);
        if(stats != null)
        {
            lore.add("");
            if(stats.ATTACK_DAMAGE != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.ATTACK_DAMAGE) + " Attack Damage");
                meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", stats.ATTACK_DAMAGE - 1D, AttributeModifier.Operation.ADD_NUMBER));
            }
            if(axePower)
            {
                lore.add(ChatColor.BLUE + "+5 Attack Power");
            }
            else if(stats.ATTACK_POWER != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.ATTACK_POWER) + " Attack Power");
                AttributeUtil.setPower(meta, (int) (stats.ATTACK_POWER * 20D));
            }
            if(stats.PROJECTILE_DAMAGE != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.PROJECTILE_DAMAGE) + " Projectile Damage");
                AttributeUtil.setProjectileDamage(meta, stats.PROJECTILE_DAMAGE);
            }
            if(stats.PROJECTILE_VELOCITY != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.PROJECTILE_VELOCITY) + "% Projectile Velocity");
                AttributeUtil.setProjectileVelocity(meta, stats.PROJECTILE_VELOCITY / 100);
            }
            if(stats.DAMAGE_REDUCTION != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.DAMAGE_REDUCTION) + "% Damage Reduction");
                AttributeUtil.setDamageReduction(meta, stats.DAMAGE_REDUCTION);
            }
            if(stats.ARMOUR_WEIGHT != 0)
            {
                lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.ARMOUR_WEIGHT) + " Armour Weight");
                AttributeUtil.setArmourWeight(meta, stats.ARMOUR_WEIGHT);
            }
        }

        if(attackSpeed != null)
        {
            lore.add(ChatColor.BLUE + attackSpeed.title + " Attack Speed");

            if(attackSpeed != AttackSpeed.FAST)
                AttributeUtil.setSpeed(meta, attackSpeed);
        }

        if(notes != null)
        {
            lore.add("");
            lore.addAll(notes);
        }


        if(stats != null && stats.ENCHANTABILITY != 0)
        {
            lore.add("");
            lore.add(ChatColor.GRAY + "(0/" + stats.ENCHANTABILITY + ") Enchantability");
            AttributeUtil.setMaxEnchantability(meta, stats.ENCHANTABILITY);
        }
        else if(hasLives)
            lore.add("");

        if(hasLives)
        {
            lore.add(ChatColor.GRAY + "(" + Config.MaxItemLives + "/" + Config.MaxItemLives + ") Lives");
            AttributeUtil.setLives(meta, Config.MaxItemLives);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
