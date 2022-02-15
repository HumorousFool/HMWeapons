package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ProjectileLaunchEffect extends PresetEffect
{
    final double velocity;
    final Class<? extends Projectile> entityType;
    final float explosiveYield;
    final double spread;

    Random random = new Random();

    public ProjectileLaunchEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 4, flags);

        velocity = Double.parseDouble(args.get(0));
        EntityType t = EntityType.valueOf(args.get(1).toUpperCase(Locale.ROOT));
        entityType = (Class<? extends Projectile>) t.getEntityClass();

        explosiveYield = Explosive.class.isAssignableFrom(entityType) ? Float.parseFloat(args.get(2)) : -1;

        spread = Double.parseDouble(args.get(3));
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        double x = (random.nextDouble() - 0.5) * spread;
        double y = (random.nextDouble() - 0.5) * spread;
        double z = (random.nextDouble() - 0.5) * spread;

        Projectile projectile = player.launchProjectile(entityType);
        projectile.setVelocity(projectile.getVelocity().multiply(velocity).add(new Vector(x, y, z)));
        if(projectile.getType() == EntityType.ARROW)
            ((Arrow) projectile).setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        if(explosiveYield >= 0)
            ((Fireball) projectile).setYield(explosiveYield);
        return true;
    }
}
