package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
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

    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        return run(event.getPlayer(), context);
    }

    public boolean onProjectileLaunch(ProjectileLaunchEvent event, EventContext context)
    {
        return run((Player) event.getEntity().getShooter(), context);
    }

    public boolean onProjectileHit(ProjectileHitEvent event, EventContext context)
    {
        return run((Player) event.getEntity().getShooter(), context);
    }

    protected boolean run(Player player, EventContext context) { return false; }

    public enum ConditionalResponse
    {
        IGNORE,
        BREAK,
        RETURN
    }

    public static class EventContext
    {
        public String id;
        public EquipmentSlot slot;
        public List<PresetEffect> effects;
        private int effectIndex;

        public EventContext(String id, EquipmentSlot slot, List<PresetEffect> effects, int effectIndex)
        {
            this.id = id;
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
