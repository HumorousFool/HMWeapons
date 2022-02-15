package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PotionLocalEffect extends PresetEffect
{
    PotionEffectType effectType;
    int duration;
    int amplifier;
    boolean particles;

    public PotionLocalEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 4, flags);

        effectType = PotionEffectType.getByName(args.get(0));
        duration = Integer.parseInt(args.get(1));
        amplifier = Integer.parseInt(args.get(2));
        particles = Boolean.parseBoolean(args.get(3));
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        return event.getPlayer().addPotionEffect(new PotionEffect(effectType, duration, amplifier, false, particles));
    }
}
