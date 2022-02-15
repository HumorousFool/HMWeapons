package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.Material;
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
}
