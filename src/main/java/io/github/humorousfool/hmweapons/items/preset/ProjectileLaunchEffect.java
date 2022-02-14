package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ProjectileLaunchEffect extends PresetEffect
{
    final double velocity;
    final Class<? extends Projectile> entityType;
    final float explosiveYield;
    final int count;
    final double spread;

    public ProjectileLaunchEffect(List<String> args)
    {
        super(args, 5);

        velocity = Double.parseDouble(args.get(0));
        EntityType t = EntityType.valueOf(args.get(1).toUpperCase(Locale.ROOT));
        entityType = (Class<? extends Projectile>) t.getEntityClass();

        explosiveYield = Explosive.class.isAssignableFrom(entityType) ? Float.parseFloat(args.get(2)) : -1;

        count = Integer.parseInt(args.get(3));
        spread = Double.parseDouble(args.get(4));
    }

    @Override
    public boolean onUse(PlayerInteractEvent event, EquipmentSlot slot)
    {
        Random random = new Random();
        for(int i = 0; i < count; i++)
        {
            double x = (random.nextDouble() - 0.5) * spread;
            double y = (random.nextDouble() - 0.5) * spread;
            double z = (random.nextDouble() - 0.5) * spread;

            Projectile projectile = event.getPlayer().launchProjectile(entityType);
            projectile.setVelocity(projectile.getVelocity().multiply(velocity).add(new Vector(x, y, z)));
            if(projectile.getType() == EntityType.ARROW)
                ((Arrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
            if(explosiveYield >= 0)
                ((Fireball) projectile).setYield(explosiveYield);
        }
        return false;
    }
}
