package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class EffectLocalEffect extends PresetEffect
{
    PotionEffectType effectType;
    int duration;
    int amplifier;
    boolean particles;

    public EffectLocalEffect(List<String> args)
    {
        super(args, 4);

        effectType = PotionEffectType.getByName(args.get(0));
        duration = Integer.parseInt(args.get(1));
        amplifier = Integer.parseInt(args.get(2));
        particles = Boolean.parseBoolean(args.get(3));
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        event.getPlayer().addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, particles));
        return false;
    }
}
