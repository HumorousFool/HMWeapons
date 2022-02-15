package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConsumeEffect extends PresetEffect
{
    public ConsumeEffect(List<String> args, ConditionalFlags flags)
    {
        super( args, 0, flags);
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        ItemStack item = event.getPlayer().getInventory().getItem(context.slot);
        item.setAmount(item.getAmount() - 1);
        event.getPlayer().getInventory().setItem(context.slot, item);
        return true;
    }
}
