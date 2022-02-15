package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class RandomEffect extends PresetEffect
{
    Random random = new Random();

    double chance;

    public RandomEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 1, flags);

        chance = Double.parseDouble(args.get(0)) / 100;
    }

    @Override
    protected boolean run(Player player, EventContext context)
    {
        return random.nextDouble() < chance;
    }
}
