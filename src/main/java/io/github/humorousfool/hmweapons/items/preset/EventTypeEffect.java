package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class EventTypeEffect extends PresetEffect
{
    public enum EventType
    {
        INTERACT_RIGHT,
        INTERACT_LEFT
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

    private boolean hasEventType(EventType type)
    {
        return eventTypes.contains(type);
    }
}
