package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.HMWeapons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;

public class EntityTrailEffect extends PresetEffect
{
    final double startX, startY, startZ;
    final double velocity;
    final int lifeTime;
    final int waitTime;
    final long rate;
    final EntityType entityType;

    public EntityTrailEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 8, flags);

        startX = Double.parseDouble(args.get(0));
        startY = Double.parseDouble(args.get(1));
        startZ = Double.parseDouble(args.get(2));
        velocity = Double.parseDouble(args.get(3));
        lifeTime = Integer.parseInt(args.get(4));
        waitTime = Integer.parseInt(args.get(5));
        rate = Long.parseLong(args.get(6));
        entityType = EntityType.valueOf(args.get(7).toUpperCase(Locale.ROOT));
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        new BukkitRunnable()
        {
            Location loc = player.getLocation().add(startX, startY, startZ);
            final Vector direction = player.getLocation().getDirection().multiply(velocity);
            int currentTime = 0;

            @Override
            public void run()
            {
                if(currentTime >= lifeTime)
                    Bukkit.getScheduler().cancelTask(getTaskId());
                loc = loc.add(direction);

                while(currentTime < waitTime)
                {
                    if(!loc.getBlock().isPassable())
                    {
                        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1.5f);
                        Bukkit.getScheduler().cancelTask(getTaskId());
                        return;
                    }
                    loc = loc.add(direction);
                    currentTime++;
                }

                if(!loc.getBlock().isPassable())
                {
                    Bukkit.getScheduler().cancelTask(getTaskId());
                    return;
                }

                player.getWorld().spawnEntity(loc, entityType);

                currentTime++;
            }
        }.runTaskTimer(HMWeapons.getInstance(), 0, rate);

        return true;
    }
}
