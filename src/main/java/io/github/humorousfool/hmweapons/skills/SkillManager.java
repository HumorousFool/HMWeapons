package io.github.humorousfool.hmweapons.skills;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.config.Config;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SkillManager implements Listener
{
    private static final HashMap<Player, SkillSet> playerSkills = new HashMap<>();
    private static final String inventoryName = ChatColor.DARK_AQUA + "Skills";

    private static final UUID speedID = UUID.fromString("3203e142-4213-43f3-9404-4add41b2402f");
    private static final UUID healthID = UUID.fromString("5c36a964-9b53-4c78-ba65-8e1aac59bc99");

    private static File file;
    private static YamlConfiguration yaml;

    private static final Random random = new Random();

    public static void init()
    {
        file = new File(HMWeapons.getInstance().getDataFolder(), "skills.yml");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        yaml = YamlConfiguration.loadConfiguration(file);

        for(Player player : Bukkit.getServer().getOnlinePlayers())
        {
            loadPlayer(player);
        }
    }

    public static void loadPlayer(Player player)
    {
        for(String key : yaml.getValues(false).keySet())
        {
            if(player.getUniqueId().toString().equals(key))
            {
                List<Integer> list = yaml.getIntegerList(key);
                SkillSet skills = new SkillSet(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4), list.get(5));
                playerSkills.put(player, skills);
            }
        }

        if(!playerSkills.containsKey(player))
        {
            playerSkills.put(player, new SkillSet(0, 0, 0, 0, 0, 0));
            savePlayer(player);
        }
    }

    public static void savePlayer(Player player)
    {
        if(!playerSkills.containsKey(player)) return;
        SkillSet l = playerSkills.get(player);
        List<Integer> data = new ArrayList<>();
        data.add(l.strength);
        data.add(l.precision);
        data.add(l.constitution);
        data.add(l.agility);
        data.add(l.wisdom);
        data.add(l.sparePoints);
        yaml.set(player.getUniqueId().toString(), data);

        try {
            yaml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Failed to save skills!");
            e.printStackTrace();
        }
    }

    public static void saveAll()
    {
        for(Player player : playerSkills.keySet())
        {
            SkillSet l = playerSkills.get(player);
            List<Integer> data = new ArrayList<>();
            data.add(l.strength);
            data.add(l.precision);
            data.add(l.constitution);
            data.add(l.agility);
            data.add(l.wisdom);
            data.add(l.sparePoints);
            yaml.set(player.getUniqueId().toString(), data);
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Failed to save skills!");
            e.printStackTrace();
        }
    }

    public static void unloadPlayer(Player player)
    {
        playerSkills.remove(player);
    }

    public static SkillSet getSkills(Player player)
    {
        return playerSkills.getOrDefault(player, null);
    }

    public static void setSkills(Player player, SkillSet skills)
    {
        if(playerSkills.containsKey(player))
            playerSkills.replace(player, skills);
        else
            playerSkills.put(player, skills);
    }

    public static void updatePlayer(Player player)
    {
        SkillSet skills = getSkills(player);
        if(skills == null) return;

        // Constitution Each Level
        AttributeModifier health = new AttributeModifier(healthID, "generic.max_health", skills.constitution, AttributeModifier.Operation.ADD_NUMBER);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(health);
        if(skills.constitution > 0)
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(health);

        // Agility Mastery
        AttributeModifier speed = new AttributeModifier(speedID, "generic.movement_speed", 0.2D, AttributeModifier.Operation.ADD_SCALAR);
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(speed);
        if(skills.agility == Config.MaxSkillLevel)
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(speed);
    }

    public static void openInventory(Player player)
    {
        if(!playerSkills.containsKey(player))
        {
            loadPlayer(player);
        }

        SkillSet skills = getSkills(player);

        Inventory inventory = Bukkit.createInventory(null, 9, inventoryName);


        inventory.setItem(2, generateItem(Material.IRON_SWORD, skills.strength, ChatColor.RED + "Strength",
                "Increases the damage of melee critical by 0.2 " + ChatColor.RED + "\u2764",
                "Increases the damage of all melee attacks by 1 " + ChatColor.RED + "\u2764"));
        inventory.setItem(3, generateItem(Material.BOW, skills.precision, ChatColor.YELLOW + "Precision",
                "Increases the damage of arrows by 0.2 " + ChatColor.RED + "\u2764", "Critical arrows briefly slow down enemies"));
        inventory.setItem(4, generateItem(Material.IRON_HELMET, skills.constitution, ChatColor.GREEN + "Constitution",
                "Increases your health by 1 " + ChatColor.RED + "\u2764", "You gain resistance when you reach 5 " +
                        ChatColor.RED + "\u2764" + ChatColor.GRAY + " or below."));
        inventory.setItem(5, generateItem(Material.FEATHER, skills.agility, ChatColor.AQUA + "Agility",
                "Increases your chance of ignoring damage by 1%", "Your speed is increased by 20%"));
        inventory.setItem(6, generateItem(Material.BOOK, skills.wisdom, ChatColor.DARK_AQUA + "Wisdom",
                "Increases the power of enchantments", null));

        ItemStack reset = new ItemStack(Material.RED_TERRACOTTA, Math.max(1, skills.sparePoints));
        ItemMeta meta = reset.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Reset Skill Points");
        reset.setItemMeta(meta);
        inventory.setItem(8, reset);

        player.openInventory(inventory);
    }

    private static ItemStack generateItem(Material material, int count, String name, String eachLevel, String mastery)
    {
        ItemStack item = new ItemStack(material, Math.max(1, count));
        if(count == Config.MaxSkillLevel)
        {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
        }
        ItemMeta meta = item.getItemMeta();
        if(count == 0)
            meta.setDisplayName(ChatColor.DARK_GRAY + ChatColor.stripColor(name));
        else
            meta.setDisplayName(name);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        List<String> lore = new ArrayList<>();
        if(eachLevel != null)
        {
            lore.add(ChatColor.YELLOW + "Each Level:");

            lore.addAll(splitString(eachLevel));
        }
        if(mastery != null)
        {
            if(eachLevel != null)
                lore.add("");
            lore.add(ChatColor.RED + "Mastery: " + ChatColor.GRAY);
            lore.addAll(splitString(mastery));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static List<String> splitString(String text)
    {
        List<String> li = new ArrayList<>();
        boolean tryNewLine = false;
        int lastLine = 0;
        for(int i = 0; i < text.length(); i++)
        {
            if(text.charAt(i) == ' ' && tryNewLine)
            {
                if(lastLine == 0)
                    li.add(ChatColor.GRAY + text.substring(0, i));
                else
                    li.add(ChatColor.GRAY + text.substring(lastLine + 1, i));
                tryNewLine = false;
                lastLine = i;
            }
            else if(!tryNewLine && i - lastLine > 16)
            {
                tryNewLine = true;
            }
        }

        if(lastLine < text.length())
        {
            if(lastLine == 0)
                li.add(ChatColor.GRAY + text);
            else
                li.add(ChatColor.GRAY + text.substring(lastLine + 1));
        }

        return li;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event)
    {
        updatePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled() || event.getDamage() <= 0D) return;

        // Precision Mastery
        if(event.getEntity() instanceof LivingEntity living)
        {
            if(event.getEntity().getType() == EntityType.PLAYER)
            {
                Player player = (Player) event.getEntity();

                SkillSet skills = getSkills(player);
                if(skills != null)
                {
                    // Agility Each Level
                    if(skills.agility > 0)
                    {
                        if(random.nextInt(99) < skills.agility)
                        {
                            event.setCancelled(true);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, SoundCategory.PLAYERS, 1f, 2f);
                            return;
                        }
                    }

                    // Constitution Mastery
                    if(skills.constitution == Config.MaxSkillLevel && player.getHealth() > 5 && player.getHealth() - event.getDamage() < 5)
                    {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, false, true));
                    }
                }
            }

            if(event.getDamager().getType() == EntityType.ARROW && event.getDamager().getScoreboardTags().contains("mastery"))
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, true));
        }

        if(event.getDamager().getType() == EntityType.PLAYER)
        {
            Player player = (Player) event.getDamager();
            SkillSet skills = getSkills(player);
            if(skills != null && skills.strength > 0)
            {
                // Strength Each Level
                if(player.getVelocity().getY() < -0.0784000015258789)
                    event.setDamage(event.getDamage() + (0.2 * skills.strength));
                // Strength Mastery
                if(skills.strength == Config.MaxSkillLevel)
                    event.setDamage(event.getDamage() + 1D);
            }
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event)
    {
        if (event.isCancelled() || !(event.getEntity().getShooter() instanceof Player player)) return;

        if(!(event.getEntity() instanceof Arrow arrow)) return;

        SkillSet skills = SkillManager.getSkills(player);
        if(skills == null) return;
        if(skills.precision > 0)
        {
            // Precision Each Level
            arrow.setDamage(arrow.getDamage() + 0.2 * skills.precision);
            // Precision Mastery
            if(skills.precision == Config.MaxSkillLevel)
                arrow.addScoreboardTag("mastery");
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if(event.getInventory().getType() != InventoryType.CHEST || event.getInventory().getSize() != 9 || event.getSlot() < 0) return;
        if(event.getInventory().getHolder() != null || !event.getView().getTitle().contains(inventoryName)) return;

        event.setCancelled(true);

        if(event.getClickedInventory() != event.getView().getTopInventory()) return;

        Player player = (Player) event.getWhoClicked();
        if(!playerSkills.containsKey(player)) return;

        SkillSet skills = getSkills(player);

        if(event.getSlot() == 8)
        {
            SkillSet reset = new SkillSet(0, 0, 0, 0, 0,
                    skills.sparePoints + skills.strength + skills.precision + skills.constitution + skills.agility + skills.wisdom);
            playerSkills.replace(player, reset);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1f, 2f);
            updatePlayer(player);

            openInventory(player);
            return;
        }

        if(skills.sparePoints <= 0)
        {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 0.5f);
            return;
        }

        int amount;
        int slot;
        ChatColor colour;
        if(event.getSlot() == 2)
        {
            if(skills.strength >= Config.MaxSkillLevel) return;

            skills.strength += 1;
            slot = 2;
            amount = skills.strength;
            colour = ChatColor.RED;
        }
        else if(event.getSlot() == 3)
        {
            if(skills.precision >= Config.MaxSkillLevel) return;

            skills.precision += 1;
            slot = 3;
            amount = skills.precision;
            colour = ChatColor.YELLOW;
        }
        else if(event.getSlot() == 4)
        {
            if(skills.constitution >= Config.MaxSkillLevel) return;

            skills.constitution += 1;
            slot = 4;
            amount = skills.constitution;
            colour = ChatColor.GREEN;
            updatePlayer(player);
        }
        else if(event.getSlot() == 5)
        {
            if(skills.agility >= Config.MaxSkillLevel) return;

            skills.agility += 1;
            slot = 5;
            amount = skills.agility;
            colour = ChatColor.AQUA;
            updatePlayer(player);
        }
        else if(event.getSlot() == 6)
        {
            if(skills.wisdom >= Config.MaxSkillLevel) return;

            skills.wisdom += 1;
            slot = 6;
            amount = skills.wisdom;
            colour = ChatColor.DARK_AQUA;
        }
        else return;

        skills.sparePoints -= 1;
        playerSkills.replace(player, skills);

        ItemStack item = event.getInventory().getItem(slot);
        if(item == null) return;
        item.setAmount(amount);
        if(amount == Config.MaxSkillLevel)
        {
            item.addUnsafeEnchantment(Enchantment.LURE, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1f, 1f);
        }
        else
        {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1f, 2f);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colour + ChatColor.stripColor(meta.getDisplayName()));
        item.setItemMeta(meta);

        event.getInventory().getItem(8).setAmount(Math.max(1, skills.sparePoints));
    }
}
