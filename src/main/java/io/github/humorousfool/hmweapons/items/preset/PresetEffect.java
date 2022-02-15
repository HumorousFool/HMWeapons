package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;

public abstract class PresetEffect
{
    public final ConditionalFlags flags;

    public PresetEffect(List<String> args, int argSize, ConditionalFlags flags)
    {
        if(args.size() != argSize)
        {
            throw new IllegalArgumentException(this.getClass().getName() + " expected" + argSize + "arguments but got " + args.size() + " instead!");
        }

        this.flags = flags;
    }

    public abstract boolean onInteract(PlayerInteractEvent event, EventContext context);

    public enum ConditionalResponse
    {
        IGNORE,
        BREAK,
        RETURN
    }

    public static class EventContext
    {
        public EquipmentSlot slot;
        public List<PresetEffect> effects;
        private int effectIndex;

        public EventContext(EquipmentSlot slot, List<PresetEffect> effects, int effectIndex)
        {
            this.slot = slot;
            this.effects = effects;
            this.effectIndex = effectIndex;
        }

        public void setEffectIndex(int index)
        {
            this.effectIndex = index;
        }
        public int getEffectIndex()
        {
            return effectIndex;
        }
    }

    public record ConditionalFlags(boolean inverse, ConditionalResponse response)
    {
        public boolean shouldStop(boolean value)
        {
            if(response == ConditionalResponse.IGNORE) return false;
            if(inverse) return value;

            return !value;
        }
    }
}
