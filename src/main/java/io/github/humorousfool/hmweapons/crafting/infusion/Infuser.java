package io.github.humorousfool.hmweapons.crafting.infusion;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.crafting.ColourManager;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.CraftingMaterial;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import io.github.humorousfool.hmweapons.crafting.infusion.recipes.InfusionRecipe;
import io.github.humorousfool.hmweapons.crafting.infusion.recipes.RecipeManager;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import io.github.humorousfool.hmweapons.util.MaterialStats;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Infuser implements Listener
{
    public static ItemStack getBlock()
    {
        ItemStack item = new ItemStack(Material.DISPENSER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Infuser");
        meta.getPersistentDataContainer().set(ItemUtil.blockKey, PersistentDataType.STRING, "infuser");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }

    private final String inventoryName = "Infuser";

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled() || event.getBlock().getType() != Material.DISPENSER) return;
        if(!event.getItemInHand().hasItemMeta()) return;

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if(!ItemUtil.getBlock(meta).equals("infuser")) return;

        Dispenser dispenserData = (Dispenser) event.getBlock().getBlockData();
        dispenserData.setFacing(BlockFace.UP);
        event.getBlock().setBlockData(dispenserData);

        org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) event.getBlock().getState();
        dispenser.getPersistentDataContainer().set(ItemUtil.blockKey, PersistentDataType.STRING, "infuser");
        dispenser.update();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event)
    {
        if(event.getBlock().getType() != Material.DISPENSER) return;

        org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) event.getBlock().getState();

        if(!ItemUtil.getBlock(dispenser).equals("infuser")) return;
        event.setDropItems(false);

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getBlock());

        ItemStack item = dispenser.getInventory().getItem(0);
        if(item != null || item.getType() != Material.AIR)
        {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.useInteractedBlock() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.DISPENSER) return;

        org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) event.getClickedBlock().getState();

        if(!ItemUtil.getBlock(dispenser).equals("infuser")) return;
        event.setCancelled(true);

        Inventory inv = Bukkit.createInventory(dispenser, InventoryType.BREWING, inventoryName);
        inv.setItem(4, dispenser.getInventory().getItem(0));

        event.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        //Check for correct inventory
        if(event.getInventory().getType() != InventoryType.BREWING || event.getSlot() < 0) return;
        if(!(event.getInventory().getHolder() instanceof org.bukkit.block.Dispenser) || !event.getView().getTitle().contains(inventoryName)) return;

        if(event.getClickedInventory() == event.getView().getTopInventory())
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> update(event.getView().getTopInventory()), 1L);
        else if(event.isShiftClick())
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> update(event.getView().getTopInventory()), 1L);

        //Shift Clicking
        if(event.getClickedInventory() == event.getView().getBottomInventory())
        {
            if(event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT) return;

            ItemStack current = event.getCurrentItem();
            if(current == null) return;

            if(current.getType() == Material.BLAZE_POWDER || current.getType() == Material.GLASS_BOTTLE)
            {
                event.setCancelled(true);
                return;
            }

            //Coal
            if(current.getType() == Material.COAL || current.getType() == Material.CHARCOAL)
            {
                ItemStack result = moveItems(current.clone(), event.getView().getTopInventory(), 4, 64);
                if(result.getAmount() != current.getAmount())
                {
                    event.setCancelled(true);
                    event.getView().getBottomInventory().setItem(event.getSlot(), result);
                }
            }
            else
            {
                Inventory topInventory = event.getView().getTopInventory();
                int slot = 0;
                if(topInventory.getItem(0) != null && topInventory.getItem(0).getType() != Material.AIR)
                {
                    if(topInventory.getItem(1) == null || topInventory.getItem(1).getType() == Material.AIR)
                        slot = 1;
                    else if(topInventory.getItem(2) == null || topInventory.getItem(2).getType() == Material.AIR)
                        slot = 2;
                    else
                        return;
                }
                ItemStack result = moveItems(current.clone(), topInventory, slot, 64);
                event.setCancelled(true);
                event.getView().getBottomInventory().setItem(event.getSlot(), result);
            }
        }

        else if(event.getSlot() == 3)
        {
            ItemStack result = event.getInventory().getItem(3);
            if(result == null || result.getType() == Material.AIR)
                return;
            if(event.getCursor() != null && event.getCursor().getType() != Material.AIR && !event.isShiftClick())
            {
                if(event.getCursor().getType() != result.getType()) return;
                if(event.getCursor().hasItemMeta() && !result.hasItemMeta()) return;
                if(!event.getCursor().hasItemMeta() && result.hasItemMeta()) return;
            }

            ItemStack coal = event.getInventory().getItem(4);
            if(coal == null) return;
            coal.setAmount(coal.getAmount() - 1);
            event.getInventory().setItem(4, coal);

            for(int i = 0; i < 3; i++)
            {
                ItemStack item = event.getInventory().getItem(i);
                if(item == null || item.getType() == Material.AIR) continue;

                item.setAmount(item.getAmount() - 1);
                event.getInventory().setItem(i, item);
            }

            Player player = (Player) event.getWhoClicked();
            player.playSound(((org.bukkit.block.Dispenser) event.getInventory().getHolder()).getLocation(),
                    Sound.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1f, 1f);
            player.playSound(((org.bukkit.block.Dispenser) event.getInventory().getHolder()).getLocation(),
                    Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8f, 1f);

            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(),
                () -> {
                    for (HumanEntity he : event.getInventory().getViewers()) {
                        Player p = (Player) he;
                        p.updateInventory();
                    }
                }, 1L);
        }

        //Directly clicking
        else if(event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT)
        {
            ItemStack cursor = event.getCursor();
            if(cursor == null) return;

            if(cursor.getType() == Material.BLAZE_POWDER || cursor.getType() == Material.GLASS_BOTTLE)
            {
                event.setCancelled(true);
                return;
            }

            int amount = 64;
            if(event.getClick() == ClickType.RIGHT)
                amount = 1;

            //Coal
            if(cursor.getType() == Material.COAL || cursor.getType() == Material.CHARCOAL && event.getSlot() == 4)
            {
                ItemStack result = moveItems(cursor.clone(), event.getView().getTopInventory(), event.getSlot(), amount);
                if(result.getAmount() != cursor.getAmount())
                {
                    event.setCancelled(true);
                    event.getView().setCursor(result);
                }
            }
            else if(cursor.getType() != Material.AIR)
            {
                if(event.getSlot() > 2) return;

                event.setCancelled(true);
                Inventory topInventory = event.getView().getTopInventory();

                ItemStack inSlot = topInventory.getItem(event.getSlot());
                if(inSlot != null && inSlot.getType() != Material.AIR)
                {
                    if(cursor.getType() != inSlot.getType())
                    {
                        topInventory.setItem(event.getSlot(), cursor);
                        event.getView().setCursor(inSlot);
                        return;
                    }
                }

                ItemStack result = moveItems(cursor.clone(), topInventory, event.getSlot(), amount);
                event.getView().setCursor(result);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if(event.getInventory().getType() != InventoryType.BREWING) return;
        if(!(event.getInventory().getHolder() instanceof org.bukkit.block.Dispenser) || !event.getView().getTitle().contains(inventoryName)) return;
        event.getInventory().getHolder().getInventory().setItem(0, event.getView().getTopInventory().getItem(4));

        for(int i = 0; i < 3; i++)
        {
            ItemStack item = event.getInventory().getItem(i);
            if(item != null && item.getType() != Material.AIR)
            {
                int firstEmpty = event.getPlayer().getInventory().firstEmpty();
                if(firstEmpty >= 0)
                    event.getPlayer().getInventory().addItem(item);
                else
                {
                    Location loc = event.getPlayer().getEyeLocation();
                    loc.setY(loc.getY() - 0.2D);
                    Item dropped = event.getPlayer().getWorld().dropItem(event.getPlayer().getEyeLocation(), item);
                    dropped.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.25f));
                }
            }
        }
    }

    private ItemStack moveItems(ItemStack from, Inventory toInventory, int toSlot, int maxAmount)
    {
        int amount = Math.min(from.getAmount(), maxAmount);

        ItemStack item = toInventory.getItem(toSlot);
        if(item == null || item.getType() == Material.AIR)
        {
            item = from.clone();
            item.setAmount(amount);
            toInventory.setItem(toSlot, item);
            from.setAmount(from.getAmount() - amount);
        }

        else if(item.getType() == from.getType() && item.getAmount() < item.getMaxStackSize())
        {
            if(item.getAmount() + amount > item.getMaxStackSize())
            {
                from.setAmount(item.getAmount());
                item.setAmount(item.getMaxStackSize());
                toInventory.setItem(toSlot, item);
            }
            else
            {
                item.setAmount(item.getAmount() + amount);
                toInventory.setItem(toSlot, item);
                from.setAmount(from.getAmount() - amount);
            }
        }

        return from;
    }

    private void update(Inventory inventory)
    {
        ItemStack coal = inventory.getItem(4);
        if(coal != null && (coal.getType() == Material.COAL || coal.getType() == Material.CHARCOAL))
        {
            for(InfusionRecipe recipe : RecipeManager.recipes)
            {
                if(!recipe.matches(inventory.getItem(0), inventory.getItem(1), inventory.getItem(2))) continue;

                if(!MaterialManager.materials.containsKey(recipe.result)) continue;
                CraftingMaterial material = MaterialManager.materials.get(recipe.result);
                inventory.setItem(3, material.getItem());
                return;
            }
        }
        else
        {
            inventory.setItem(3, null);
            return;
        }

        //Combining materials
        ArrayList<Integer> materials = new ArrayList<>();
        ArrayList<Color> colours = new ArrayList<>();
        ArrayList<MaterialStats> materialStats = new ArrayList<>();
        boolean isNetherite = true;
        for(int i = 0; i < 3; i++)
        {
            ItemStack item = inventory.getItem(i);
            //If it is empty ignore it
            if(item == null || item.getType() == Material.AIR) continue;

            //Must have persistent data
            if(!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY))
            {
                inventory.setItem(3, null);
                return;
            }

            int[] materialTypes = item.getItemMeta().getPersistentDataContainer().get(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY);
            //Already combined materials cannot be combined
            if(materialTypes == null || materialTypes.length != 1)
            {
                inventory.setItem(3, null);
                return;
            }

            int id = materialTypes[0];

            //Cannot combine if a material no longer exists
            if(!MaterialManager.materials.containsKey(id))
            {
                inventory.setItem(3, null);
                return;
            }

            CraftingMaterial material = MaterialManager.materials.get(id);
            colours.add(material.colour);
            materialStats.add(material.stats);

            if(!material.isNetherite)
                isNetherite = false;

            materials.add(id);
        }

        //We need at least 2 materials
        if(materials.size() < 2)
        {
            inventory.setItem(3, null);
            return;
        }

        //if all 3 materials are the same they cannot be combined
        if(materials.size() == 3 && materials.get(1).equals(materials.get(0)) && materials.get(2).equals(materials.get(0)))
        {
            inventory.setItem(3, null);
            return;
        }
        else if(materials.size() == 2 && materials.get(1).equals(materials.get(0)))
        {
            inventory.setItem(3, null);
            return;
        }

        Color colour = ColourManager.averageColours(colours);
        ColourManager.TextureInfo texture;

        // If all materials are netherite get an ingot that will float in lava
        if(isNetherite)
            texture = ColourManager.getClosestNetheriteIngot(colour);
        else
            texture = ColourManager.getClosestIngot(colour);

        if(texture == null)
        {
            inventory.setItem(3, null);
            return;
        }

        ItemStack combined = new ItemStack(texture.material);
        ItemMeta meta = combined.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Infused Material");
        if(texture.customModelData > 0)
            meta.setCustomModelData(texture.customModelData);
        meta.getPersistentDataContainer().set(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY, materials.stream().mapToInt(i->i).toArray());
        meta.setLore(MaterialStats.average(materialStats).getLore());
        combined.setItemMeta(meta);

        inventory.setItem(3, combined);
    }
}
