package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.Player;
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
    protected boolean run(Player player, EventContext context)
    {
        return context.slot == slot;
    }
}
