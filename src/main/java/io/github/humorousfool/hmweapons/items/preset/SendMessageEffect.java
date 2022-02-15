package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SendMessageEffect extends PresetEffect
{
    String message;

    public SendMessageEffect(List<String> args, ConditionalFlags flags)
    {
        super(args, 1, flags);

        message = args.get(0).replace("&", "§");
    }

    @Override
    public boolean onInteract(PlayerInteractEvent event, EventContext context)
    {
        event.getPlayer().sendMessage(message);
        return true;
    }
}
