package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.items.CustomItem;
import io.github.humorousfool.hmweapons.items.ItemRegistry;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ItemsCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.items"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Must Be Player"));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Crafters");

        for(int i = 0; i < Math.min(ItemRegistry.items.size(), 54); i++)
        {
            CustomItem item = ItemRegistry.items.values().stream().toList().get(i);
            inv.addItem(item.getItem());
        }

        for(int i = 0; i < Math.min(ItemRegistry.presetItems.size(), 54 - ItemRegistry.items.size()); i++)
        {
            CustomItem item = ItemRegistry.presetItems.values().stream().toList().get(i);
            inv.addItem(item.getItem());
        }

        ((Player) sender).openInventory(inv);

        return true;
    }
}
