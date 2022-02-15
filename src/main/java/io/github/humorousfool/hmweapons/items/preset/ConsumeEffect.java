package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConsumeEffect extends PresetEffect
{
    public ConsumeEffect(List<String> args, ConditionalFlags flags)
    {
        super( args, 0, flags);
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        if(context.slot == null) return false;
        ItemStack item = player.getInventory().getItem(context.slot);
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItem(context.slot, item);
        return true;
    }
}
