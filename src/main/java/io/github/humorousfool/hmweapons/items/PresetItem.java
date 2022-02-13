package io.github.humorousfool.hmweapons.items;

import io.github.humorousfool.hmweapons.util.MaterialStats;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class PresetItem extends CustomItem
{

    public PresetItem(String id, String name, String type, Material material, int customModelData, boolean hasLives, boolean isStackable, boolean axePower, MaterialStats stats) {
        super(id, name, type, material, customModelData, hasLives, isStackable, axePower, stats);
    }

    public abstract boolean onUse(PlayerInteractEvent event, EquipmentSlot slot);
}
