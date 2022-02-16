package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CraftListener implements Listener
{
    @EventHandler
    public void onCraft(PrepareItemCraftEvent event)
    {
        for(ItemStack item : event.getInventory())
        {
            if(item == null) continue;
            if(ItemUtil.getItem(item) != null || (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY)))
            {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event)
    {
        ItemStack item =event.getContents().getItem(3);
        if(item != null && ItemUtil.getItem(item) != null)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event)
    {
        if(event.getResult() == null) return;

        if(event.getInventory().getItem(1) != null || event.getInventory().getItem(1).getType() != Material.AIR) return;

        if(Config.NoAnvilRepairCost)
        {
            event.getInventory().setRepairCost(0);
        }

        if(event.getView().getPlayer().hasPermission("hmweapons.format"))
        {
            ItemStack item = event.getResult();
            if(!item.hasItemMeta())
            {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replace("&", "§"));
                item.setItemMeta(meta);
            }
        }
    }
}
