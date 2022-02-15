package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SoundWorldEffect extends PresetEffect
{
    final Sound sound;
    final SoundCategory category;
    final float volume;
    final float pitch;
    boolean atTarget;

    public SoundWorldEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 5, flags);

        sound = Sound.valueOf(args.get(0));
        category = SoundCategory.valueOf(args.get(1));
        volume = Float.parseFloat(args.get(2));
        pitch = Float.parseFloat(args.get(3));
        atTarget = Boolean.parseBoolean(args.get(4));
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        if(!atTarget)
            playSound(event.getPlayer(), event.getPlayer().getLocation());
        else if(event.getClickedBlock() != null)
            playSound(event.getPlayer(), event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5));
        return true;
    }

    private void playSound(Player player, Location location)
    {
       player.getWorld().playSound(location, sound, category, volume, pitch);
    }
}
