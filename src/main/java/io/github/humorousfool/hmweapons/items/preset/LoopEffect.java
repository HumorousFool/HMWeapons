package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class LoopEffect extends PresetEffect
{
    int range;
    int next;

    public LoopEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 2, flags);

        range = Integer.parseInt(args.get(0));
        next = Integer.parseInt(args.get(1));
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        EventContext ctx = new EventContext(context.slot, context.effects, context.getEffectIndex());
        boolean broken = false;
        for(int i = 0; i < range; i++)
        {
            if(broken) break;

            for(int e = context.getEffectIndex() + 1; e <= Math.min(context.effects.size(), context.getEffectIndex() + next); e++)
            {
                ctx.setEffectIndex(e);
                PresetEffect effect = context.effects.get(e);
                if(effect.flags.shouldStop(effect.onInteract(event, ctx)))
                {
                    if(effect.flags.response() == ConditionalResponse.RETURN)
                    {
                        context.setEffectIndex(context.getEffectIndex() + next);
                        return false;
                    }
                    else if(effect.flags.response() == ConditionalResponse.BREAK)
                    {
                        broken = true;
                        break;
                    }
                }
            }
        }

        context.setEffectIndex(context.getEffectIndex() + next);
        return true;
    }
}
