package io.github.humorousfool.hmweapons.crafting.forging;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class ShapeManager
{
    public static final HashMap<String, ForgingShape> shapes = new HashMap<>();
    public static final HashMap<String, ArmourShape> armourShapes = new HashMap<>();

    public static void loadShapes(YamlConfiguration file)
    {
        shapes.clear();

        for(String name : file.getValues(false).keySet())
        {
            Object section = file.get(name);
            if(!(section instanceof MemorySection)) continue;

            ForgingShape shape = new ForgingShape((MemorySection) section);
            shapes.put(shape.id, shape);
        }

        armourShapes.put("helmet", new ArmourShape("Helmet", "HELMET", Material.GOLD_NUGGET, 100, 6));
        armourShapes.put("chestplate", new ArmourShape("Chestplate", "Chestplate", Material.GOLD_NUGGET, 101, 10));
        armourShapes.put("leggings", new ArmourShape("Leggings", "Leggings", Material.GOLD_NUGGET, 102, 8));
        armourShapes.put("boots", new ArmourShape("Boots", "Boots", Material.GOLD_NUGGET, 103, 6));
    }
}
