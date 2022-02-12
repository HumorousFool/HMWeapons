package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener
{
    private final HashMap<Player, Double> damageReductions = new HashMap<>();
    private final UUID speedID = UUID.fromString("3204e147-4213-43e3-9404-4add41e2602f");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event)
    {
        updateAttributes(event.getPlayer(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        updateAttributes(event.getPlayer(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK || event.useItemInHand() == Event.Result.DENY) return;

        EquipmentSlot slot = ItemUtil.getArmourSlot(event.getPlayer().getInventory().getItem(event.getHand()));
        if(slot == null) return;

        ItemStack item = event.getPlayer().getInventory().getItem(slot);

        //This can ABSOLUTELY be null
        if(item != null && item.getType() != Material.AIR) return;

        Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> updateAttributes(event.getPlayer(), EquipmentSlot.OFF_HAND), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event)
    {
        if(event.isCancelled() || event.getInventory().getType() != InventoryType.CRAFTING) return;

        if(event.getSlotType() == InventoryType.SlotType.ARMOR ||  event.getSlot() == 40 || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
        {
            Player player = (Player) event.getWhoClicked();
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> updateAttributes(player, EquipmentSlot.OFF_HAND), 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(PlayerSwapHandItemsEvent event)
    {
        if(event.isCancelled()) return;
        updateAttributes(event.getPlayer(), EquipmentSlot.HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event)
    {
        damageReductions.remove(event.getPlayer());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled() || event.getDamage() <= 0D) return;

        if(event.getEntity() instanceof Player player && damageReductions.containsKey(player))
        {
            event.setDamage(event.getDamage() * damageReductions.get(player));
        }

        if(event.getDamager() instanceof Player player && event.getDamage() > 0.9 * player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue())
        {
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getType() == Material.AIR || !item.hasItemMeta()) return;
            ItemMeta meta = item.getItemMeta();

            int[] special = AttributeUtil.getSpecialEffects(meta);
            boolean area = false;
            for(int i : special)
            {
                if(i == 1 && player.getInventory().getItemInOffHand().getType() == Material.SHIELD)
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Item - Double Handed"));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.f, 0.5f);
                    return;
                }
                else if(i == 0)
                    area = true;
            }

            if(area)
            {
                for(Entity e : event.getEntity().getNearbyEntities(1.f, 1.f, 1.f))
                {
                    if(!(e instanceof LivingEntity living)) return;
                    living.damage(event.getDamage() / 2, player);
                }
            }
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent event)
    {
        if(event.isCancelled() || !(event.getEntity().getShooter() instanceof Player player)) return;

        EquipmentSlot slot;
        if(isRanged(player.getInventory().getItemInMainHand()))
            slot = EquipmentSlot.HAND;
        else if(isRanged(player.getInventory().getItemInOffHand()))
            slot = EquipmentSlot.OFF_HAND;
        else return;

        if(!(event.getEntity() instanceof Arrow arrow)) return;
        ItemStack item = player.getInventory().getItem(slot);
        if(item.getType() == Material.AIR || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if(slot != EquipmentSlot.HAND || player.getInventory().getItemInOffHand().getType() == Material.SHIELD)
        {
            int[] special = AttributeUtil.getSpecialEffects(meta);
            for(int i : special)
            {
                if(i == 1)
                {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + I18nSupport.getInternationalisedString("Item - Double Handed"));
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.f, 0.5f);
                    return;
                }

                if(i == 2)
                {
                    player.setCooldown(Material.BOW, 20);
                }
            }
        }

        double damage = AttributeUtil.getProjectileDamage(meta);
        double velocity = AttributeUtil.getProjectileVelocity(meta);

        if(damage != 0)
        {
            arrow.setDamage(arrow.getDamage() + damage);
        }

        if(velocity != 0)
        {
            arrow.setVelocity(arrow.getVelocity().multiply(1D + velocity));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event)
    {
        for(int i = 0; i < event.getEntity().getInventory().getSize(); i++)
        {
            ItemStack item = event.getEntity().getInventory().getItem(i);
            if(item == null || item.getType() == Material.AIR) continue;
            int lives = AttributeUtil.getLives(item);
            if(lives <= 0 && Config.OverrideKeepInventory)
            {
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                event.getEntity().getInventory().setItem(i, new ItemStack(Material.AIR));
            }
            else
            {
                // lives is greater than -1 therefore it must have meta
                ItemMeta meta = item.getItemMeta();
                lives -= 1;
                AttributeUtil.setLives(meta, lives);

                if(meta.hasLore())
                {
                    List<String> lore = meta.getLore();
                    if(lore.size() > 0)
                    {
                        if(lives > 0)
                            lore.set(lore.size() - 1, ChatColor.GRAY + "(" + lives + "/" + Config.MaxItemLives + ") Lives");
                        else
                            lore.set(lore.size() - 1, ChatColor.RED + "(" + lives + "/" + Config.MaxItemLives + ") Lives");
                        meta.setLore(lore);
                    }
                }

                item.setItemMeta(meta);
                event.getEntity().getInventory().setItem(i, item);
            }
        }
    }

    private boolean isRanged(ItemStack item)
    {
        return AttributeUtil.getProjectileDamage(item) != 0 || AttributeUtil.getProjectileVelocity(item) != 0;
    }

    private void updateAttributes(Player player, EquipmentSlot offhandSlot)
    {

        double helmetMul = (100 - AttributeUtil.getDamageReduction(player.getInventory().getHelmet())) / 100;
        double chestplateMul = (100 - AttributeUtil.getDamageReduction(player.getInventory().getChestplate())) / 100;
        double leggingsMul = (100 - AttributeUtil.getDamageReduction(player.getInventory().getLeggings())) / 100;
        double bootsMul = (100 - AttributeUtil.getDamageReduction(player.getInventory().getBoots())) / 100;

        double offhandMul = ItemUtil.getArmourSlot(player.getInventory().getItem(offhandSlot)) == null ? (100 - AttributeUtil.getDamageReduction(player.getInventory().getItem(offhandSlot))) / 100 : 1;

        double totalReduction = helmetMul * chestplateMul * leggingsMul * bootsMul * offhandMul;

        if(totalReduction == 1D)
        {
            damageReductions.remove(player);
        }
        else
        {
            if(damageReductions.containsKey(player))
                damageReductions.replace(player, totalReduction);
            else
                damageReductions.put(player, totalReduction);
        }

        double helmetWeight = AttributeUtil.getArmourWeight(player.getInventory().getHelmet());
        double chestplateWeight = AttributeUtil.getArmourWeight(player.getInventory().getChestplate());
        double leggingsWeight = AttributeUtil.getArmourWeight(player.getInventory().getLeggings());
        double bootsWeight = AttributeUtil.getArmourWeight(player.getInventory().getBoots());

        double totalWeight = -(helmetWeight + chestplateWeight + leggingsWeight + bootsWeight) / 100;

        AttributeModifier modifier = new AttributeModifier(speedID, "generic.movement_speed", totalWeight, AttributeModifier.Operation.ADD_SCALAR);

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(modifier);
        if(totalWeight != 0D)
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(modifier);
    }
}
