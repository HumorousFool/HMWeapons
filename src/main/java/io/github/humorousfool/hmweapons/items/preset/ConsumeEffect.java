package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ConsumeEffect extends PresetEffect
{
    public ConsumeEffect(List<String> args)
    {
        super( args, 0);
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        ItemStack item = event.getPlayer().getInventory().getItem(slot);
        item.setAmount(item.getAmount() - 1);
        event.getPlayer().getInventory().setItem(slot, item);
        return false;
    }
}
