package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

public class BlockListener implements Listener
{
    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        if(event.getItemInHand().hasItemMeta() &&
                event.getItemInHand().getItemMeta().getPersistentDataContainer().has(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY))
            event.setCancelled(true);
    }
}
