package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.crafting.forging.ArmourShape;
import io.github.humorousfool.hmweapons.crafting.forging.ForgingShape;
import io.github.humorousfool.hmweapons.crafting.forging.ShapeManager;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShapesCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.shapes"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Must Be Player"));
            return true;
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + "Shapes");

        for(int i = 0; i < Math.min(ShapeManager.shapes.size(), 54); i++)
        {
            ForgingShape material = ShapeManager.shapes.values().stream().toList().get(i);
            inv.addItem(material.getItem());
        }

        for(int i = 0; i < Math.min(ShapeManager.armourShapes.size(), 54 - ShapeManager.shapes.size()); i++)
        {
            ArmourShape material = ShapeManager.armourShapes.values().stream().toList().get(i);
            inv.addItem(material.getItem());
        }

        ((Player) sender).openInventory(inv);

        return true;
    }
}
