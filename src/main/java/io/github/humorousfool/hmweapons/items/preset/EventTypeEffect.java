package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.EntityType;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class EventTypeEffect extends PresetEffect
{
    public enum EventType
    {
        INTERACT_RIGHT,
        INTERACT_LEFT,
        LAUNCH_ARROW,
        LAUNCH_OTHER,
        HIT_ARROW,
        HIT_ARROW_ENTITY,
        HIT_OTHER,
        HIT_OTHER_ENTITY
    }

    public final ArrayList<EventType> eventTypes = new ArrayList<>();

    public EventTypeEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, args.size(), flags);

        for(String s : args)
        {
            eventTypes.add(EventType.valueOf(s));
        }
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
            return hasEventType(EventType.INTERACT_RIGHT);
        else if(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR)
            return hasEventType(EventType.INTERACT_LEFT);

        return false;
    }

    @Override
    public boolean onProjectileLaunch(ProjectileLaunchEvent event, EventContext context)
    {
        if(event.getEntity().getType() == EntityType.ARROW && hasEventType(EventType.LAUNCH_ARROW))
            return true;
        return hasEventType(EventType.LAUNCH_OTHER);
    }

    @Override
    public boolean onProjectileHit(ProjectileHitEvent event, EventContext context)
    {
        if(event.getHitEntity() != null)
        {
            if(event.getEntity().getType() == EntityType.ARROW && hasEventType(EventType.HIT_ARROW_ENTITY))
                return true;
            return hasEventType(EventType.HIT_OTHER_ENTITY);
        }

        if(event.getEntity().getType() == EntityType.ARROW && hasEventType(EventType.HIT_ARROW))
            return true;
        return hasEventType(EventType.HIT_OTHER);
    }

    private boolean hasEventType(EventType type)
    {
        return eventTypes.contains(type);
    }
}
