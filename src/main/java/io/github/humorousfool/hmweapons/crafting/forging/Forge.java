package io.github.humorousfool.hmweapons.crafting.forging;

import io.github.humorousfool.hmcombat.api.AttackSpeed;
import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.crafting.ColourManager;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.CraftingMaterial;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import io.github.humorousfool.hmweapons.util.AttributeUtil;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import io.github.humorousfool.hmweapons.util.MaterialStats;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.Directional;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Forge implements Listener
{
    public static ItemStack getBlock()
    {
        ItemStack item = new ItemStack(Material.DROPPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Forge");
        meta.getPersistentDataContainer().set(ItemUtil.blockKey, PersistentDataType.STRING, "forge");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);
        return item;
    }

    private final String inventoryName = "Forge";
    private final int[] slots = {2, 4, 6};

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled() || event.getBlock().getType() != Material.DROPPER) return;
        if(!event.getItemInHand().hasItemMeta()) return;

        ItemMeta meta = event.getItemInHand().getItemMeta();
        if(!ItemUtil.getBlock(meta).equals("forge")) return;

        Directional directional = (Directional) event.getBlock().getBlockData();
        directional.setFacing(BlockFace.UP);
        event.getBlock().setBlockData(directional);

        Dropper dropper = (Dropper) event.getBlock().getState();
        dropper.getPersistentDataContainer().set(ItemUtil.blockKey, PersistentDataType.STRING, "forge");
        dropper.update();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event)
    {
        if(event.getBlock().getType() != Material.DROPPER) return;

        org.bukkit.block.Dropper dropper = (org.bukkit.block.Dropper) event.getBlock().getState();

        if(!ItemUtil.getBlock(dropper).equals("forge")) return;
        event.setDropItems(false);

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.useInteractedBlock() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.DROPPER) return;

        Dropper dropper = (Dropper) event.getClickedBlock().getState();

        if(!ItemUtil.getBlock(dropper).equals("forge")) return;
        event.setCancelled(true);

        ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = empty.getItemMeta();
        meta.setDisplayName("");
        empty.setItemMeta(meta);

        Inventory inv = Bukkit.createInventory(dropper, 9, inventoryName);
        for(int i = 0; i < 9; i++)
        {
            boolean available = true;
            for(int s : slots)
            {
                if (i == s)
                {
                    available = false;
                    break;
                }
            }
            if(!available) continue;

            inv.setItem(i, empty);
        }

        event.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if(event.getInventory().getType() != InventoryType.CHEST || event.getSlot() < 0) return;
        if(!(event.getInventory().getHolder() instanceof org.bukkit.block.Dropper) || !event.getView().getTitle().contains(inventoryName)) return;

        if(event.getClickedInventory() == event.getView().getTopInventory() || event.isShiftClick())
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMWeapons.getInstance(), () -> event.getView().getTopInventory().setItem(slots[2],
                    getResult(event.getView().getTopInventory().getItem(slots[0]),
                    event.getView().getTopInventory().getItem(slots[1]))), 1L);

        if(event.getClickedInventory() == event.getView().getTopInventory())
        {
            if(event.getSlot() == slots[2] && event.getCursor() != null && event.getCursor().getType() != Material.AIR)
            {
                event.setCancelled(true);
                return;
            }
            // take items
            else if(event.getView().getTopInventory().getItem(slots[2]) != null && event.getView().getTopInventory().getItem(slots[2]).getType() != Material.AIR)
            {
                ItemStack shape = event.getView().getTopInventory().getItem(slots[1]);

                if(event.getSlot() == slots[2] && shape != null && shape.getType() != Material.AIR && shape.hasItemMeta() &&
                        shape.getItemMeta().getPersistentDataContainer().has(ItemUtil.shapeKey, PersistentDataType.STRING))
                {
                    String shapeName = shape.getItemMeta().getPersistentDataContainer().get(ItemUtil.shapeKey, PersistentDataType.STRING);

                    ItemStack mat = event.getView().getTopInventory().getItem(slots[0]);

                    if(ShapeManager.armourShapes.containsKey(shapeName))
                    {
                        mat.setAmount(Math.max(0, mat.getAmount() - (ShapeManager.armourShapes.get(shapeName).materialsRequired)));
                    }
                    else if(ShapeManager.shapes.containsKey(shapeName))
                    {
                        mat.setAmount(Math.max(0, mat.getAmount() - (ShapeManager.shapes.get(shapeName).materialsRequired)));
                    }

                    int uses = AttributeUtil.getDurability(shape);
                    if(uses == 1 || uses == 0)
                    {
                        event.getView().getTopInventory().setItem(slots[1], new ItemStack(Material.AIR));
                    }
                    else if(uses > 1)
                    {
                        uses -= 1;
                        ItemMeta meta = shape.getItemMeta();
                        AttributeUtil.setDurability(meta, uses);
                        List<String> lore = meta.getLore();
                        lore.set(lore.size() - 1, ChatColor.GRAY + "(" + uses + "/" + Config.MaxShapeUses + ") Uses");
                        meta.setLore(lore);
                        shape.setItemMeta(meta);
                        event.getView().getTopInventory().setItem(slots[1], shape);
                    }

                    Player player = (Player) event.getWhoClicked();
                    player.playSound(((org.bukkit.block.Dropper) event.getInventory().getHolder()).getLocation(),
                            Sound.ITEM_BUCKET_FILL_LAVA, SoundCategory.BLOCKS, 1f, 1f);
                    player.playSound(((org.bukkit.block.Dropper) event.getInventory().getHolder()).getLocation(),
                            Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.8f, 1f);
                    player.playSound(((org.bukkit.block.Dropper) event.getInventory().getHolder()).getLocation(),
                            Sound.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1f, 1f);
                }
            }

            boolean valid = false;
            for(int i : slots)
            {
                if(i == event.getSlot())
                    valid = true;
            }
            if(!valid)
            {
                event.setCancelled(true);
                return;
            }
        }

        //Shift click into the correct slot
        if(event.getClickedInventory() == event.getView().getBottomInventory())
        {
            //Is shift click
            if (event.getClick() != ClickType.SHIFT_LEFT && event.getClick() != ClickType.SHIFT_RIGHT) return;
            //Slot is empty
            if(event.getView().getTopInventory().firstEmpty() == slots[2])
            {
                event.setCancelled(true);
                return;
            }

            ItemStack current = event.getCurrentItem();
            if(current == null || !current.hasItemMeta()) return;

            if(current.getItemMeta().getPersistentDataContainer().has(ItemUtil.shapeKey, PersistentDataType.STRING))
            {
                event.setCancelled(true);
                event.getView().getTopInventory().setItem(slots[1], event.getCurrentItem());
                event.setCurrentItem(new ItemStack(Material.AIR));
            }
        }
    }

    private ItemStack getResult(ItemStack first, ItemStack second)
    {
        if(first == null || first.getType() == Material.AIR) return new ItemStack(Material.AIR);
        if(second == null || second.getType() == Material.AIR) return new ItemStack(Material.AIR);

        if(!second.hasItemMeta() || !second.getItemMeta().getPersistentDataContainer().has(ItemUtil.shapeKey, PersistentDataType.STRING)) return new ItemStack(Material.AIR);

        String shapeName = second.getItemMeta().getPersistentDataContainer().get(ItemUtil.shapeKey, PersistentDataType.STRING);

        if(!first.hasItemMeta() || !first.getItemMeta().getPersistentDataContainer().has(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY)) return new ItemStack(Material.AIR);

        int[] materialIds = first.getItemMeta().getPersistentDataContainer().get(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY);
        if(materialIds.length < 1 || materialIds.length > 3) return new ItemStack(Material.AIR);

        ArrayList<CraftingMaterial> materials = new ArrayList<>();
        for(int id : materialIds)
        {
            if(!MaterialManager.materials.containsKey(id)) return new ItemStack(Material.AIR);

            materials.add(MaterialManager.materials.get(id));
        }

        if(ShapeManager.armourShapes.containsKey(shapeName))
        {
            return getArmourResult(materials, first.getAmount(), ShapeManager.armourShapes.get(shapeName));
        }
        if(ShapeManager.shapes.containsKey(shapeName))
        {
            return getItemResult(materials, first.getAmount(), ShapeManager.shapes.get(shapeName));
        }
        return new ItemStack(Material.AIR);
    }

    private ItemStack getArmourResult(ArrayList<CraftingMaterial> materials, int count, ArmourShape shape)
    {
        if(count < shape.materialsRequired) return new ItemStack(Material.AIR);

        Color colour = null;
        double damageReduction = 0D;
        double armourWeight = 0D;
        int enchantability = 0;
        float durability = 0;

        Material mat = null;
        boolean NoColour = false;
        if(materials.size() == 1)
        {
            mat = Material.matchMaterial(materials.get(0).armourMaterial + "_" + shape.type);

            colour = materials.get(0).colour;
            NoColour = materials.get(0).noArmourColour;
        }

        ArrayList<Color> colours = new ArrayList<>();
        boolean netherite = true;
        for(CraftingMaterial m : materials)
        {
            if(!m.isNetherite)
            {
                netherite = false;
            }

            colours.add(m.colour);
            damageReduction += m.stats.DAMAGE_REDUCTION;
            armourWeight += m.stats.ARMOUR_WEIGHT;
            durability += m.stats.DURABILITY;
            enchantability += m.stats.ENCHANTABILITY;
        }
        damageReduction /= materials.size();
        armourWeight /= materials.size();
        durability = (durability /materials.size()) * shape.durabilityMul;
        enchantability /= materials.size();

        if(mat == null)
        {
            colour = ColourManager.averageColours(colours);
            if(netherite)
                mat = Material.matchMaterial("NETHERITE_" + shape.type);
            else
                mat = Material.matchMaterial("LEATHER_" + shape.type);
        }

        if(mat == null) return new ItemStack(Material.AIR);

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if(meta instanceof LeatherArmorMeta && !NoColour)
            ((LeatherArmorMeta) meta).setColor(colour);

        if(materials.size() == 1)
            meta.setDisplayName(materials.get(0).chatColour + materials.get(0).prefix + " " + shape.name);
        else
            meta.setDisplayName(ChatColor.YELLOW + "Infused " + shape.name);

        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier("generic.armor", 0D, AttributeModifier.Operation.ADD_NUMBER));
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(damageReduction) + "% Damage Reduction");
        if(armourWeight >= 0)
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(armourWeight) + " Armour Weight");
        else
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimal(armourWeight) + " Armour Weight");
        lore.add("");
        lore.add(ChatColor.GRAY + "(0/" + enchantability + ") Enchantability");
        lore.add(ChatColor.GRAY + "(" + (int) durability + "/" + (int) durability + ") Durability");

        AttributeUtil.setDamageReduction(meta, damageReduction);
        AttributeUtil.setArmourWeight(meta, armourWeight);
        AttributeUtil.setDurability(meta, (int) durability);
        AttributeUtil.setMaxDurability(meta, (int) durability);
        AttributeUtil.setMaxEnchantability(meta, enchantability);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
    private ItemStack getItemResult(ArrayList<CraftingMaterial> materials, int count, ForgingShape shape)
    {
        if(count < shape.materialsRequired) return new ItemStack(Material.AIR);

        Color colour = materials.get(0).colour;
        MaterialStats stats = materials.get(0).stats;
        boolean isNetherite = materials.get(0).isNetherite;

        if(materials.size() > 1)
        {
            ArrayList<Color> colours = new ArrayList<>();
            for(int i = 1; i < materials.size(); i++)
            {
                CraftingMaterial testMat = materials.get(i);
                colours.add(testMat.colour);
                stats = stats.add(testMat.stats);

                if(!testMat.isNetherite)
                    isNetherite = false;
            }

            colour = ColourManager.averageColours(colours);
            stats = stats.divide(materials.size());
        }

        ColourManager.TextureInfo texture;
        if(isNetherite)
            texture = ColourManager.getClosestColourInGroup(shape.netheriteGroup, colour);
        else
            texture = ColourManager.getClosestColourInGroup(shape.colourGroup, colour);

        if(texture == null) return new ItemStack(Material.AIR);

        int durability = (int) ((float) stats.DURABILITY * shape.durabilityMul);

        ItemStack item = new ItemStack(texture.material);
        ItemMeta meta = item.getItemMeta();

        meta.setCustomModelData(texture.customModelData);

        if(materials.size() == 1)
            meta.setDisplayName(materials.get(0).chatColour + materials.get(0).prefix + " " + shape.name);
        else
            meta.setDisplayName(ChatColor.YELLOW + "Infused " + shape.name);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        List<String> lore = new ArrayList<>();

        lore.add("");
        if(shape.attackDamageMul != 0)
        {
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.ATTACK_DAMAGE * shape.attackDamageMul) + " Attack Damage");
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier("generic.attack_damage", (stats.ATTACK_DAMAGE * shape.attackDamageMul) - 1D, AttributeModifier.Operation.ADD_NUMBER));
        }
        if(shape.attackPowerMul != 0)
        {
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.ATTACK_POWER * shape.attackPowerMul) + " Attack Power");
            AttributeUtil.setPower(meta, (int) ((stats.ATTACK_POWER * shape.attackPowerMul) * 20D));
        }
        else if(shape.axePower)
        {
            lore.add(ChatColor.BLUE + "+5 Attack Power");
        }
        if(shape.projectileDamageMul != 0)
        {
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.PROJECTILE_DAMAGE * shape.projectileDamageMul) + "% Projectile Damage");
            AttributeUtil.setProjectileDamage(meta, (stats.PROJECTILE_DAMAGE * shape.projectileDamageMul) / 100);
        }
        if(shape.ranged)
        {
            lore.add(ChatColor.BLUE + ItemUtil.formatDecimalPlus(stats.PROJECTILE_VELOCITY) + "% Projectile Velocity");
            AttributeUtil.setProjectileVelocity(meta, stats.PROJECTILE_VELOCITY / 100);
        }

        if(!shape.ranged && shape.attackDamageMul != 0)
        {
            lore.add(ChatColor.BLUE + shape.attackSpeed.title + " Attack Speed");

            if(shape.attackSpeed != AttackSpeed.FAST)
                AttributeUtil.setSpeed(meta, shape.attackSpeed);
        }

        lore.add("");
        lore.add(ChatColor.GRAY + "(0/" + stats.ENCHANTABILITY + ") Enchantability");
        lore.add(ChatColor.GRAY + "(" + durability + "/" + durability + ") Durability");

        AttributeUtil.setDurability(meta, durability);
        AttributeUtil.setMaxDurability(meta, durability);
        AttributeUtil.setMaxEnchantability(meta, stats.ENCHANTABILITY);

        if(texture.material.getMaxStackSize() > 1)
        {
            ItemUtil.makeNonStackable(meta);
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if(event.getInventory().getType() != InventoryType.CHEST) return;
        if(!(event.getInventory().getHolder() instanceof org.bukkit.block.Dropper) || !event.getView().getTitle().contains(inventoryName)) return;

        for(int i : slots)
        {
            if(i == 6) continue;
            ItemStack item = event.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR)
            {
                int firstEmpty = event.getPlayer().getInventory().firstEmpty();
                if (firstEmpty >= 0)
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
}
