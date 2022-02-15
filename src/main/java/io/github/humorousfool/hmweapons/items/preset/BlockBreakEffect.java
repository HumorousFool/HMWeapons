package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakEffect extends PresetEffect
{
    List<Material> materials = new ArrayList<>();

    public BlockBreakEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, args.size(), flags);

        for(String key : args)
        {
            if(key.startsWith("#"))
            {
                materials.addAll(ItemUtil.getBlocksFromTag(key));
            }
            else
                materials.add(Material.matchMaterial(key));
        }
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        if(event.getClickedBlock() != null && materials.contains(event.getClickedBlock().getType()))
        {
            event.getPlayer().breakBlock(event.getClickedBlock());
            return true;
        }

        return false;
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent event, EventContext context)
    {
        if(materials.contains(event.getEntity().getLocation().getBlock().getType()))
        {
            ((Player) event.getEntity().getShooter()).breakBlock(event.getEntity().getLocation().getBlock());
            return true;
        }

        return false;
    }

    @Override
    public boolean onProjectileHit(ProjectileHitEvent event, EventContext context)
    {
        if(event.getHitBlock() != null && materials.contains(event.getHitBlock().getType()))
        {
            ((Player) event.getEntity().getShooter()).breakBlock(event.getHitBlock());
            return true;
        }

        return false;
    }
}
