package io.github.humorousfool.hmweapons.crafting.infusion.recipes;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.logging.Level;

public class InfusionRecipe
{
    public final ArrayList<Material> vanillaMaterials = new ArrayList<>();
    public final ArrayList<Integer> craftingMaterials = new ArrayList<>();

    public final String name;
    public final int result;

    public InfusionRecipe(MemorySection data)
    {
        this.name = data.getName();
        this.result = data.getInt("Result");

        for(String id : data.getStringList("Materials"))
        {
            if(id.matches("\\d+"))
                craftingMaterials.add(Integer.parseInt(id));
            else
            {
                Material vanilla = Material.matchMaterial(id);
                if(vanilla == null)
                {
                    HMWeapons.getInstance().getLogger().log(Level.SEVERE, "Could not load infusion recipe \"" +
                            data.getName() + "\": The material \"" + id + "\" is invalid.");
                    return;
                }
                vanillaMaterials.add(vanilla);
            }
        }
    }

    public boolean matches(ItemStack... materials)
    {
        ArrayList<Material> requiredVanilla = new ArrayList<>(vanillaMaterials);
        ArrayList<Integer> requiredMaterials = new ArrayList<>(craftingMaterials);

        for(ItemStack item : materials)
        {
            if(item == null || item.getType() == Material.AIR) continue;

            if(item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY))
            {
                int[] materialTypes = item.getItemMeta().getPersistentDataContainer().get(ItemUtil.materialKey, PersistentDataType.INTEGER_ARRAY);
                if(materialTypes != null && materialTypes.length == 1)
                {
                    if(requiredMaterials.contains(materialTypes[0]))
                    {
                        requiredMaterials.remove((Integer) materialTypes[0]);
                        continue;
                    }
                }
            }
            else if(requiredVanilla.contains(item.getType()))
            {
                requiredVanilla.remove(item.getType());
                continue;
            }

            return false;
        }

        return requiredVanilla.isEmpty() && requiredMaterials.isEmpty();
    }
}
