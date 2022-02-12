package io.github.humorousfool.hmweapons.listener;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

public class ArmourListener implements Listener
{
    private final HashMap<Player, Double> damageReductions = new HashMap<>();
    private final HashMap<Player, DurabilityInfo> durabilities = new HashMap<>();
    private final UUID speedID = UUID.fromString("3204e147-4213-43e3-9404-4add41e2602f");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event)
    {
        readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
        updateAttributes(event.getPlayer(), EquipmentSlot.OFF_HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event)
    {
        writeDurability(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event)
    {
        readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
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

        writeDurability(event.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> {
            readDurability(event.getPlayer(), EquipmentSlot.OFF_HAND);
            updateAttributes(event.getPlayer(), EquipmentSlot.OFF_HAND);
            }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event)
    {
        if(event.isCancelled() || event.getInventory().getType() != InventoryType.CRAFTING) return;

        if(event.getSlotType() == InventoryType.SlotType.ARMOR ||  event.getSlot() == 40 || event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)
        {
            Player player = (Player) event.getWhoClicked();
            writeDurability(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> {
                readDurability(player, EquipmentSlot.OFF_HAND);
                updateAttributes(player, EquipmentSlot.OFF_HAND);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwap(PlayerSwapHandItemsEvent event)
    {
        if(event.isCancelled()) return;
        writeDurability(event.getPlayer());
        readDurability(event.getPlayer(), EquipmentSlot.HAND);
        updateAttributes(event.getPlayer(), EquipmentSlot.HAND);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event)
    {
        writeDurability(event.getPlayer());
        damageReductions.remove(event.getPlayer());
        durabilities.remove(event.getPlayer());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event)
    {
        if(event.isCancelled() || event.getDamage() <= 0D) return;

        if(event.getEntity() instanceof Player player)
        {
            if(damageReductions.containsKey(player))
                updateDurability(player, event.getDamage());

            if(damageReductions.containsKey(player))
                event.setDamage(event.getDamage() * damageReductions.get(player));
        }
    }

    private void updateAttributes(Player player, EquipmentSlot offhandSlot)
    {
        if(!durabilities.containsKey(player))
        {
            readDurability(player, offhandSlot);
            return;
        }
        DurabilityInfo values = durabilities.get(player);

        double helmetMul = values.headDurability >= 0 ? (100 - AttributeUtil.getDamageReduction(player.getInventory().getHelmet())) / 100 : 1;
        double chestplateMul = values.chestDurability >= 0 ? (100 - AttributeUtil.getDamageReduction(player.getInventory().getChestplate())) / 100: 1;
        double leggingsMul = values.leggingsDurability >= 0 ?  (100 - AttributeUtil.getDamageReduction(player.getInventory().getLeggings())) / 100 : 1;
        double bootsMul = values.bootsDurability >= 0 ? (100 - AttributeUtil.getDamageReduction(player.getInventory().getBoots())) / 100 : 1;

        double offhandMul = ItemUtil.getArmourSlot(player.getInventory().getItem(offhandSlot)) == null && values.offHandDurability >= 0 ? (100 - AttributeUtil.getDamageReduction(player.getInventory().getItem(offhandSlot))) / 100 : 1;

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

        double helmetWeight = values.headDurability >= 0 ? AttributeUtil.getArmourWeight(player.getInventory().getHelmet()) : 0;
        double chestplateWeight = values.chestDurability >= 0 ? AttributeUtil.getArmourWeight(player.getInventory().getChestplate()) : 0;
        double leggingsWeight = values.leggingsDurability >= 0 ? AttributeUtil.getArmourWeight(player.getInventory().getLeggings()) : 0;
        double bootsWeight = values.bootsDurability >= 0 ? AttributeUtil.getArmourWeight(player.getInventory().getBoots()) : 0;

        double totalWeight = -(helmetWeight + chestplateWeight + leggingsWeight + bootsWeight) / 100;

        AttributeModifier modifier = new AttributeModifier(speedID, "generic.movement_speed", totalWeight, AttributeModifier.Operation.ADD_SCALAR);

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(modifier);
        if(totalWeight != 0D)
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(modifier);
    }

    private void readDurability(Player player, EquipmentSlot offhandSlot)
    {

        DurabilityInfo values = new DurabilityInfo(AttributeUtil.getDurability(player.getInventory().getHelmet()),
                AttributeUtil.getDurability(player.getInventory().getChestplate()),
                AttributeUtil.getDurability(player.getInventory().getLeggings()),
                AttributeUtil.getDurability(player.getInventory().getBoots()),
                ItemUtil.getArmourSlot(player.getInventory().getItem(offhandSlot)) == null ? AttributeUtil.getDurability(player.getInventory().getItem(offhandSlot)) : -1,
                -1);

        if(durabilities.containsKey(player))
            durabilities.replace(player, values);
        else
            durabilities.put(player, values);
    }

    private void updateDurability(Player player, double attackDamage)
    {
        if(!durabilities.containsKey(player))
        {
            readDurability(player, EquipmentSlot.OFF_HAND);
            return;
        }

        DurabilityInfo values = durabilities.get(player);
        int damage = Math.max(1, (int) attackDamage / 4);

        values.update(damage);

        durabilities.replace(player, values);
        if(values.isDirty())
        {
            writeDurability(player);
            updateAttributes(player, EquipmentSlot.OFF_HAND);
        }
    }

    private void writeDurability(Player player)
    {
        applyDurability(player, EquipmentSlot.HEAD);
        applyDurability(player, EquipmentSlot.CHEST);
        applyDurability(player, EquipmentSlot.LEGS);
        applyDurability(player, EquipmentSlot.FEET);
        applyDurability(player, EquipmentSlot.OFF_HAND);
    }

    private void applyDurability(Player player, EquipmentSlot slot)
    {
        DurabilityInfo updated = durabilities.get(player);
        int newD = updated.getDurability(slot);
        if(newD < 0) return;
        if(newD == 0)
        {
            updated.setDurability(slot, -1);
            durabilities.replace(player, updated);
        }

        ItemStack item = player.getInventory().getItem(slot);
        // this can actually be null
        if(item == null) return;
        ItemMeta meta = item.getItemMeta();
        int current = AttributeUtil.getDurability(meta);
        if(current < 0) return;

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
        player.getInventory().setItem(slot, item);
    }

    private static class DurabilityInfo
    {
        int headDurability;
        int chestDurability;
        int leggingsDurability;
        int bootsDurability;
        int offHandDurability;

        DurabilityInfo(int head, int chest, int leggings, int boots, int offHand, int mainHand)
        {
            headDurability = head;
            chestDurability = chest;
            leggingsDurability = leggings;
            bootsDurability = boots;
            offHandDurability = offHand;
        }

        void update(int damage)
        {
            headDurability = calcDurability(headDurability, damage);
            chestDurability = calcDurability(chestDurability, damage);
            leggingsDurability = calcDurability(leggingsDurability, damage);
            bootsDurability = calcDurability(bootsDurability, damage);
            offHandDurability = calcDurability(offHandDurability, damage);
        }

        private int calcDurability(int i, int damage)
        {
            if(i < 0) return i;

            return Math.max(0, i - damage);
        }

        boolean isDirty()
        {
            return headDurability == 0 || chestDurability == 0 || leggingsDurability == 0 ||
                    bootsDurability == 0 || offHandDurability == 0;
        }

        int getDurability(EquipmentSlot slot)
        {
            return switch (slot)
            {
                case HEAD -> headDurability;
                case CHEST -> chestDurability;
                case LEGS -> leggingsDurability;
                case FEET -> bootsDurability;
                case OFF_HAND -> offHandDurability;
                case HAND -> -1;
            };
        }

        void setDurability(EquipmentSlot slot, int value)
        {
            switch (slot)
            {
                case HEAD -> headDurability = value;
                case CHEST -> chestDurability = value;
                case LEGS -> leggingsDurability = value;
                case FEET -> bootsDurability = value;
                case OFF_HAND -> offHandDurability = value;
            }
        }
    }
}
