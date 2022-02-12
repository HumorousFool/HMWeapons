package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.crafting.forging.Forge;
import io.github.humorousfool.hmweapons.crafting.infusion.Infuser;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class CraftersCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.crafters"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Must Be Player"));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_RED + "Crafters");

        inv.addItem(Infuser.getBlock());
        inv.addItem(Forge.getBlock());

        ((Player) sender).openInventory(inv);

        return true;
    }
}
