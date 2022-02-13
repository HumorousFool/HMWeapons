package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.items.CustomItem;
import io.github.humorousfool.hmweapons.items.ItemRegistry;
import io.github.humorousfool.hmweapons.items.PresetItem;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ItemListener implements Listener
{
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        String id = ItemUtil.getItem(event.getPlayer().getInventory().getItemInMainHand());
        EquipmentSlot slot;
        if(id != null)
            slot = EquipmentSlot.HAND;
        else
        {
            id = ItemUtil.getItem(event.getPlayer().getInventory().getItemInOffHand());
            if(id != null)
                slot = EquipmentSlot.OFF_HAND;
            else return;
        }

        for(PresetItem item : ItemRegistry.presetItems.values())
        {
            if(item.id.equals(id))
            {
                event.setCancelled(item.onUse(event, slot));
                return;
            }
        }
    }
}
