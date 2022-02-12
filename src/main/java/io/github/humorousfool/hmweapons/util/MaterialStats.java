package io.github.humorousfool.hmweapons.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.List;

public class MaterialStats
{
    public final double ATTACK_DAMAGE;
    public final double ATTACK_POWER;

    public final double PROJECTILE_DAMAGE;
    public final double PROJECTILE_VELOCITY;

    public final double DAMAGE_REDUCTION;
    public final double ARMOUR_WEIGHT;

    public final int ENCHANTABILITY;

    public MaterialStats(MemorySection stats)
    {
        this.ATTACK_DAMAGE = stats.getDouble("ATTACK_DAMAGE");
        this.ATTACK_POWER = stats.getDouble("ATTACK_POWER");
        this.PROJECTILE_DAMAGE = stats.getDouble("PROJECTILE_DAMAGE");
        this.PROJECTILE_VELOCITY = stats.getDouble("PROJECTILE_VELOCITY");
        this.DAMAGE_REDUCTION = stats.getDouble("DAMAGE_REDUCTION");
        this.ARMOUR_WEIGHT = stats.getDouble("ARMOUR_WEIGHT");
        this.ENCHANTABILITY = stats.getInt("ENCHANTABILITY");
    }

    public MaterialStats(double attackDamage, double attackPower,
                         double projectileDamage, double projectileVelocity,
                         double damageReduction, double armourWeight, double enchantability)
    {
        this.ATTACK_DAMAGE = attackDamage;
        this.ATTACK_POWER = attackPower;
        this.PROJECTILE_DAMAGE = projectileDamage;
        this.PROJECTILE_VELOCITY = projectileVelocity;
        this.DAMAGE_REDUCTION = damageReduction;
        this.ARMOUR_WEIGHT = armourWeight;
        this.ENCHANTABILITY = (int) enchantability;
    }

    public List<String> getLore()
    {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "Attack Damage: " + ItemUtil.formatDecimal(ATTACK_DAMAGE));
        lore.add(ChatColor.GRAY + "Attack Power: " + ItemUtil.formatDecimal(ATTACK_POWER));
        lore.add(ChatColor.GRAY + "Projectile Damage: " + ItemUtil.formatDecimalPlus(PROJECTILE_DAMAGE));
        lore.add(ChatColor.GRAY + "Projectile Velocity: " + ItemUtil.formatDecimalPlus(PROJECTILE_VELOCITY) + "%");
        lore.add(ChatColor.GRAY + "Damage Reduction: " + ItemUtil.formatDecimal(DAMAGE_REDUCTION) + "%");
        lore.add(ChatColor.GRAY + "Armour Weight: " + ItemUtil.formatDecimal(ARMOUR_WEIGHT));
        lore.add(ChatColor.GRAY + "Enchantability: " + ENCHANTABILITY);

        return lore;
    }

    public MaterialStats add(MaterialStats other)
    {
        return new MaterialStats(ATTACK_DAMAGE + other.ATTACK_DAMAGE, ATTACK_POWER + other.ATTACK_POWER,
                PROJECTILE_DAMAGE + other.PROJECTILE_DAMAGE, PROJECTILE_VELOCITY + other.PROJECTILE_VELOCITY,
                DAMAGE_REDUCTION + other.DAMAGE_REDUCTION, ARMOUR_WEIGHT + other.ARMOUR_WEIGHT,
                ENCHANTABILITY + other.ENCHANTABILITY);
    }

    public MaterialStats divide(double divider)
    {
        return new MaterialStats(ATTACK_DAMAGE / divider, ATTACK_POWER / divider,
                PROJECTILE_DAMAGE / divider, PROJECTILE_VELOCITY / divider,
                DAMAGE_REDUCTION / divider, ARMOUR_WEIGHT / divider, ENCHANTABILITY / divider);
    }

    public static MaterialStats average(ArrayList<MaterialStats> stats)
    {
        MaterialStats result = stats.get(0);
        for(int i = 1; i < stats.size(); i++)
        {
            result = result.add(stats.get(i));
        }

        return result.divide(stats.size());
    }
}