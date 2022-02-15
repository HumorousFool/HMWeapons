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
        if(event.getHand() == null) return;
        String id = ItemUtil.getItem(event.getPlayer().getInventory().getItem(event.getHand()));
        if(id == null) return;

        for(CustomItem item : ItemRegistry.items.values())
        {
            if(item.id.equals(id))
            {
                PresetEffect.EventContext ctx = new PresetEffect.EventContext(event.getHand(), item.effects, 0);
                for(int i = 0; i < item.effects.size(); i++)
                {
                    ctx.setEffectIndex(i);
                    PresetEffect effect = item.effects.get(i);
                    if(effect.flags.shouldStop(effect.onInteract(event, ctx))) return;
                    i = ctx.getEffectIndex();
                }

                return;
            }
        }
    }
}
