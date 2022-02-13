package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.items.PresetItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class FireballScroll extends PresetItem
{
    public FireballScroll(int cmd)
    {
        super("fireball_scroll", ChatColor.AQUA + "Scroll of Fireball", "Consumable", Material.NETHER_BRICK, cmd, false, false, false, null);
        notes.add("");
        notes.add(ChatColor.GRAY + "Right Click to Activate");
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        event.getPlayer().getInventory().setItem(slot, new ItemStack(Material.AIR));

        Fireball fireball = event.getPlayer().launchProjectile(Fireball.class);
        fireball.setIsIncendiary(false);
        return false;
    }
}
