package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmcombat.api.StatUtil;
import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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

public class WeaponListener implements Listener
{
    private final HashMap<Player, WeaponDurabilityInfo> durabilities = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event)
    {
        readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event)
    {
        writeDurability(event.getEntity(), EquipmentSlot.HAND);
        writeDurability(event.getEntity(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event)
    {
        if(event.isCancelled() || event.getInventory().getType() != InventoryType.CRAFTING) return;

        Player player = (Player) event.getWhoClicked();
        if(event.getSlot() == player.getInventory().getHeldItemSlot())
        {
            ItemStack item = writeDurabilityItem(player, EquipmentSlot.HAND);
            if(item != null)
                event.setCurrentItem(item);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> readDurability(player, EquipmentSlot.OFF_HAND), 1L);
        }
        else if(event.getSlot() == 40)
        {
            ItemStack item = writeDurabilityItem(player, EquipmentSlot.OFF_HAND);
            if(item != null)
                event.setCurrentItem(item);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> readDurability(player, EquipmentSlot.OFF_HAND), 1L);
        }
        else if(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> readDurability(player, EquipmentSlot.OFF_HAND), 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(PlayerSwapHandItemsEvent event)
    {
        if(event.isCancelled()) return;
        ItemStack oldMain = writeDurabilityItem(event.getPlayer(), EquipmentSlot.HAND);
        ItemStack oldOff = writeDurabilityItem(event.getPlayer(), EquipmentSlot.OFF_HAND);

        if(oldMain != null)
            event.setOffHandItem(oldMain);
        if(oldOff != null)
            event.setMainHandItem(oldOff);
        readDurability(event.getPlayer(), EquipmentSlot.HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwitch(PlayerItemHeldEvent event)
    {
        if(event.isCancelled()) return;
        writeDurability(event.getPlayer(), EquipmentSlot.HAND);
        Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event)
    {
        writeDurability(event.getPlayer(), EquipmentSlot.HAND);
        writeDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
        durabilities.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event)
    {
        if(event.isCancelled() || event.getBlock().getType().getHardness() == 0) return;

        if(!durabilities.containsKey(event.getPlayer()) && isMelee(event.getPlayer().getInventory().getItemInMainHand()))
        {
            readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
        }

        if(durabilities.get(event.getPlayer()).mainHandDurability < 0)
        {
            event.setCancelled(true);
            return;
        }

        updateDurability(event.getPlayer(), EquipmentSlot.HAND);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled() || event.getDamage() <= 0D || !(event.getDamager() instanceof Player player)) return;

        if(!durabilities.containsKey(player) && isMelee(player.getInventory().getItemInMainHand()))
        {
            readDurability(player, EquipmentSlot.OFF_HAND);
        }
        if(durabilities.containsKey(player) && isMelee(player.getInventory().getItemInMainHand()))
        {
            WeaponDurabilityInfo values = durabilities.get(player);
            if(values.mainHandDurability < 0)
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + I18nSupport.getInternationalisedString("Item Is Broken"));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 0.5f);
                return;
            }

            updateDurability(player, EquipmentSlot.HAND);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            if(!durabilities.containsKey(event.getPlayer()) || !isMelee(event.getPlayer().getInventory().getItemInMainHand())) return;

            WeaponDurabilityInfo values = durabilities.get(event.getPlayer());
            if(values.mainHandDurability < 0)
            {
                event.getPlayer().sendMessage(ChatColor.DARK_RED + I18nSupport.getInternationalisedString("Item Is Broken"));
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 0.5f);
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

        if(!durabilities.containsKey(player))
        {
            readDurability(player, EquipmentSlot.OFF_HAND);
        }
        if(durabilities.containsKey(player))
        {
            WeaponDurabilityInfo values = durabilities.get(player);
            if(values.getDurability(slot) < 0)
            {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + I18nSupport.getInternationalisedString("Item Is Broken"));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1f, 0.5f);
                return;
            }

            updateDurability(player, slot);

            if(!(event.getEntity() instanceof Arrow arrow)) return;
            ItemStack item = player.getInventory().getItem(slot);
            if(item.getType() == Material.AIR || !item.hasItemMeta()) return;

            ItemMeta meta = item.getItemMeta();
            double damage = AttributeUtil.getProjectileDamage(meta);
            double velocity = AttributeUtil.getProjectileVelocity(meta);

            if(damage != 0)
            {
                arrow.setDamage(arrow.getDamage() * (1D + damage));
            }

            if(velocity != 0)
            {
                arrow.setVelocity(arrow.getVelocity().multiply(1D + velocity));
            }
        }
    }

    // Reads both
    private void readDurability(Player player, EquipmentSlot offHand)
    {
        player.sendMessage("Reading");
        EquipmentSlot mainHand;
        if(offHand == EquipmentSlot.HAND)
            mainHand = EquipmentSlot.OFF_HAND;
        else
            mainHand = EquipmentSlot.HAND;

        WeaponDurabilityInfo values = new WeaponDurabilityInfo(
                isValidWeapon(player.getInventory().getItem(mainHand)) ? AttributeUtil.getDurability(player.getInventory().getItem(mainHand)) : -1,
                isValidWeapon(player.getInventory().getItem(offHand)) ? AttributeUtil.getDurability(player.getInventory().getItem(offHand)) : -1);

        if(durabilities.containsKey(player))
            durabilities.replace(player, values);
        else
            durabilities.put(player, values);

        player.sendMessage(values.mainHandDurability + " | " + values.offHandDurability);
    }

    // Updates one
    private void updateDurability(Player player, EquipmentSlot slot)
    {
        player.sendMessage("Updating " + slot.name());
        if(!durabilities.containsKey(player)) return;

        WeaponDurabilityInfo values = durabilities.get(player);
        int durability = values.getDurability(slot);

        if(durability < 0) return;

        values.setDurability(Math.max(0, durability - 1), slot);

        durabilities.replace(player, values);

        if(values.isDirty())
        {
            writeDurability(player, slot);
        }

        player.sendMessage(values.mainHandDurability + " | " + values.offHandDurability);
    }

    private void writeDurability(Player player, EquipmentSlot slot)
    {
        ItemStack item = writeDurabilityItem(player, slot);
        if(item == null) return;
        player.getInventory().setItem(slot, item);
    }

    // Updates one
    private ItemStack writeDurabilityItem(Player player, EquipmentSlot slot)
    {
        player.sendMessage("Writing " + slot.name());
        WeaponDurabilityInfo values = durabilities.get(player);
        int newD = values.getDurability(slot);

        if(newD < 0) return null;

        if(newD == 0)
        {
            values.setDurability(-1, slot);
            durabilities.replace(player, values);
        }

        ItemStack item = player.getInventory().getItem(slot);
        // this can also be null
        if(item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        int current = AttributeUtil.getDurability(meta);
        if(current < 0) return null;

        List<String> lore = meta.getLore();
        if(newD == 0)
        {
            AttributeUtil.setDurability(meta, -1);
            lore.set(lore.size() - 1, ChatColor.RED + "(" + newD + "/" + AttributeUtil.getMaxDurability(meta) + ") Durability");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f);
        }
        else
        {
            AttributeUtil.setDurability(meta, newD);
            lore.set(lore.size() - 1, ChatColor.GRAY + "(" + newD + "/" + AttributeUtil.getMaxDurability(meta) + ") Durability");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private boolean isValidWeapon(ItemStack item)
    {
        return isMelee(item) || isRanged(item);
    }

    private boolean isMelee(ItemStack item)
    {
        return StatUtil.getPower(item) != 0 || StatUtil.getSpeed(item) != null;
    }

    private boolean isRanged(ItemStack item)
    {
        return AttributeUtil.getProjectileDamage(item) != 0 || AttributeUtil.getProjectileVelocity(item) != 0;
    }

    private static class WeaponDurabilityInfo
    {
        int mainHandDurability;
        int offHandDurability;

        WeaponDurabilityInfo(int mainHand, int offHand)
        {
            mainHandDurability = mainHand;
            offHandDurability = offHand;
        }

        void setDurability(int value, EquipmentSlot slot)
        {
            switch (slot)
            {
                case HAND -> mainHandDurability = value;
                case OFF_HAND -> offHandDurability = value;
            }
        }

        int getDurability(EquipmentSlot slot)
        {
            return switch (slot) {
                case HAND -> mainHandDurability;
                case OFF_HAND -> offHandDurability;
                default -> -1;
            };
        }

        boolean isDirty()
        {
            return mainHandDurability == 0 || offHandDurability == 0;
        }
    }
}
