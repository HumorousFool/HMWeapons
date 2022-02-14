package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.items.CustomItem;
import io.github.humorousfool.hmweapons.items.ItemRegistry;
import io.github.humorousfool.hmweapons.items.preset.PresetEffect;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

        for(CustomItem item : ItemRegistry.items.values())
        {
            if(item.id.equals(id))
            {
                for(PresetEffect effect : item.effects)
                {
                    if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                        event.setCancelled(effect.onUse(event, slot));
                }

                return;
            }
        }
    }
}
