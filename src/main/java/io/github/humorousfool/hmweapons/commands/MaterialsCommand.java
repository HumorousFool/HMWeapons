package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.CraftingMaterial;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class MaterialsCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.materials"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Must Be Player"));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Materials");

        for(int i = 0; i < Math.min(MaterialManager.materials.size(), 54); i++)
        {
            CraftingMaterial material = MaterialManager.materials.values().stream().toList().get(i);
            inv.addItem(material.getItem());
        }

        ((Player) sender).openInventory(inv);

        return true;
    }
}
