package io.github.humorousfool.hmweapons.crafting.infusion.materials;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class MaterialManager
{
    public static final HashMap<Integer, CraftingMaterial> materials = new HashMap<>();

    public static void loadMaterials(YamlConfiguration file)
    {
        materials.clear();

        for(String name : file.getValues(false).keySet())
        {
            Object section = file.get(name);
            if(!(section instanceof MemorySection)) continue;

            CraftingMaterial material = new CraftingMaterial((MemorySection) section);
            materials.put(material.id, material);
        }
    }
}
