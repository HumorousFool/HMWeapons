package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Locale;

public class SpawnRadiusEffect extends PresetEffect
{
    double minRadiusSquared;
    final double maxRadius;
    final EntityType entityType;

    public SpawnRadiusEffect(List<String> args, ConditionalFlags flags)
    {
        super( args, 3, flags);

        minRadiusSquared = Double.parseDouble(args.get(0));
        minRadiusSquared *= minRadiusSquared;
        maxRadius = Double.parseDouble(args.get(1));
        entityType = EntityType.valueOf(args.get(2).toUpperCase(Locale.ROOT));
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        boolean used = false;
        for(Entity e : player.getNearbyEntities(maxRadius, maxRadius, maxRadius))
        {
            if(!(e instanceof LivingEntity) || e.getLocation().distanceSquared(player.getLocation()) < minRadiusSquared) continue;

            e.getWorld().spawnEntity(e.getLocation(), entityType);
            used = true;
        }
        if(!used)
            player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1.5f);

        return used;
    }
}
