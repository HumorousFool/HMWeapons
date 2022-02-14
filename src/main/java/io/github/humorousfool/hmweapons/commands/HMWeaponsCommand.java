package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMWeaponsCommand implements CommandExecutor, TabCompleter
{
    private final List<String> reloadTypes = Arrays.asList("materials", "recipes", "shapes", "items");

    Logger logger = HMWeapons.getInstance().getLogger();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.hmweapons"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(args.length > 0 && args[0].equalsIgnoreCase("reload"))
        {
            if(!sender.hasPermission("hmweapons.command.hmweapons.reload"))
            {
                sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
                return true;
            }

            if(args.length > 1)
            {
                String type = args[1];
                if(type.equalsIgnoreCase("materials"))
                {
                    log(sender, I18nSupport.getInternationalisedString("Reload Materials Colours"));
                    HMWeapons.getInstance().reloadMaterialsAndColours();
                }
                else if(type.equalsIgnoreCase("recipes"))
                {
                    log(sender, I18nSupport.getInternationalisedString("Reload Recipes"));
                    HMWeapons.getInstance().reloadRecipes();
                }
                else if(type.equalsIgnoreCase("shapes"))
                {
                    log(sender, I18nSupport.getInternationalisedString("Reload Shapes"));
                    HMWeapons.getInstance().reloadShapes();
                }
                else if(type.equalsIgnoreCase("items"))
                {
                    log(sender, I18nSupport.getInternationalisedString("Reload Items"));
                    HMWeapons.getInstance().reloadItems();
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Reload Invalid") + ": materials, recipes, shapes, items");
                    return true;
                }
            }
            else
            {
                log(sender, I18nSupport.getInternationalisedString("Reload Materials Colours"));
                HMWeapons.getInstance().reloadMaterialsAndColours();
                log(sender, I18nSupport.getInternationalisedString("Reload Recipes"));
                HMWeapons.getInstance().reloadRecipes();
                log(sender, I18nSupport.getInternationalisedString("Reload Shapes"));
                HMWeapons.getInstance().reloadShapes();
                log(sender, I18nSupport.getInternationalisedString("Reload Items"));
                HMWeapons.getInstance().reloadItems();
            }

            log(sender, I18nSupport.getInternationalisedString("Reload Complete"));
        }
        else
        {
            Plugin plugin = HMWeapons.getInstance();
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.getName() + " " + plugin.getDescription().getVersion());
            sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getDescription());

            StringBuilder authors = new StringBuilder(plugin.getDescription().getAuthors().get(0));
            for(int i = 1; i < plugin.getDescription().getAuthors().size(); i++)
            {
                authors.append(", ").append(plugin.getDescription().getAuthors().get(i));
            }
            sender.sendMessage(ChatColor.AQUA + "by " + authors);
        }

        return true;
    }
    
    private void log(CommandSender sender, String message)
    {
        if(sender instanceof Player) 
            sender.sendMessage(ChatColor.AQUA + message);
        logger.log(Level.INFO, message);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
    {
        if(args.length == 1)
        {
            List<String> r = new ArrayList<>();
            r.add("reload");
            return  r;
        }
        else if(args.length == 2)
            return reloadTypes;
        return null;
    }
}
