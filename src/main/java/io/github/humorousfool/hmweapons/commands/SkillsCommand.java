package io.github.humorousfool.hmweapons.commands;

import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import io.github.humorousfool.hmweapons.skills.SkillManager;
import io.github.humorousfool.hmweapons.skills.SkillSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillsCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(!sender.hasPermission("hmweapons.command.skills"))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Insufficient Permissions"));
            return true;
        }

        if(args.length > 0 && sender.hasPermission("hmweapons.command.skills.other"))
        {
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null || !target.isOnline())
            {
                sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Invalid Player"));
                return true;
            }

            SkillSet skillSet = SkillManager.getSkills(target);
            if(skillSet == null)
            {
                sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("No Skills Loaded"));
                SkillManager.loadPlayer(target);
                return true;
            }
            if(args.length >= 2 && args[1].equalsIgnoreCase("add"))
            {
                if(args.length == 2)
                {
                    return false;
                }

                int amount = Integer.parseInt(args[2]);
                skillSet.sparePoints += amount;
                if(amount > 1)
                    target.sendMessage(ChatColor.GREEN + "You have gained " + ChatColor.GOLD + ChatColor.BOLD + args[2] + ChatColor.GREEN + " skill points!");
                else
                    target.sendMessage(ChatColor.GREEN + "You have gained " + ChatColor.GOLD + ChatColor.BOLD + args[2] + ChatColor.GREEN + " skill point!");
                SkillManager.setSkills(target, skillSet);
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Strength: " + skillSet.strength);
            sender.sendMessage(ChatColor.YELLOW + "Precision: " + skillSet.precision);
            sender.sendMessage(ChatColor.GREEN + "Constitution: " + skillSet.constitution);
            sender.sendMessage(ChatColor.AQUA + "Agility: " + skillSet.agility);
            sender.sendMessage(ChatColor.DARK_AQUA + "Wisdom: " + skillSet.wisdom);
            sender.sendMessage("Spare Points: " + skillSet.sparePoints);
            return true;
        }

        if(!(sender instanceof Player player))
        {
            sender.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Must Be Player"));
            return true;
        }

        SkillManager.openInventory(player);

        return true;
    }
}
