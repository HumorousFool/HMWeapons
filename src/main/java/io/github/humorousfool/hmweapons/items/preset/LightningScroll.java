package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.items.CustomItem;
import io.github.humorousfool.hmweapons.items.PresetItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class LightningScroll extends PresetItem
{
    public LightningScroll(int cmd)
    {
        super("lightining_scroll", ChatColor.AQUA + "Scroll of Lightning", "Consumable", Material.NETHER_BRICK, cmd, false, false, false, null);
        notes.add("");
        notes.add(ChatColor.GRAY + "Right Click to Activate");
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        event.getPlayer().getInventory().setItem(slot, new ItemStack(Material.AIR));

        boolean used = false;
        for(Entity e : event.getPlayer().getNearbyEntities(10f, 10f, 10f))
        {
            if(!(e instanceof LivingEntity)) return false;

            e.getWorld().spawnEntity(e.getLocation(), EntityType.LIGHTNING);
            used = true;
        }
        if(!used)
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1.5f);

        return false;
    }
}
