package io.github.humorousfool.hmweapons.items.preset;

import io.github.humorousfool.hmweapons.items.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveItemEffect extends PresetEffect
{
    String itemName;
    int count;
    boolean custom;

    public GiveItemEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 3, flags);

        itemName = args.get(0);
        count = Integer.parseInt(args.get(1));
        custom = Boolean.parseBoolean(args.get(2));
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        ItemStack item;
        if(custom)
        {
            item = ItemRegistry.items.get(itemName).getItem();
            item.setAmount(count);
        }
        else
            item = new ItemStack(Material.matchMaterial(itemName), count);

        player.getInventory().addItem(item);

        return true;
    }
}
