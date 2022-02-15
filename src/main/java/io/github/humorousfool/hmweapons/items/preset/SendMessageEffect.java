package io.github.humorousfool.hmweapons.items.preset;

import org.bukkit.entity.Player;

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
    protected boolean run(Player player, EventContext context)
    {
        player.sendMessage(message);
        return true;
    }
}
