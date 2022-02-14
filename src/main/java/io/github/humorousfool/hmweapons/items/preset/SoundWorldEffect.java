package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class SoundWorldEffect extends PresetEffect
{
    final Sound sound;
    final SoundCategory category;
    final float volume;
    final float pitch;

    public SoundWorldEffect(List<String> args)
    {
        super(args, 4);

        sound = Sound.valueOf(args.get(0));
        category = SoundCategory.valueOf(args.get(1));
        volume = Float.parseFloat(args.get(2));
        pitch = Float.parseFloat(args.get(3));
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), sound, category, volume, pitch);
        return false;
    }
}
