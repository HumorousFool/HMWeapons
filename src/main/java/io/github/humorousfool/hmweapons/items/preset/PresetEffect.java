package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public abstract class PresetEffect
{
    public PresetEffect(List<String> args, int argSize)
    {

        if(args.size() != argSize)
        {
            throw new IllegalArgumentException(this.getClass().getName() + " expected" + argSize + "arguments but got " + args.size() + " instead!");
        }
    }

    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot) { return false; }
}
