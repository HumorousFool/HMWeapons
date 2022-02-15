package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public class HeldSlotEffect extends PresetEffect
{
    EquipmentSlot slot;

    public HeldSlotEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 1, flags);

        slot = EquipmentSlot.valueOf(args.get(0));
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        return context.slot == slot;
    }
}
