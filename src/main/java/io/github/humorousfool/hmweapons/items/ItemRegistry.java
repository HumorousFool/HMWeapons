package io.github.humorousfool.hmweapons.items;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class ItemRegistry
{
    public static final HashMap<String, CustomItem> items = new HashMap<>();

    public static void loadItems(YamlConfiguration file)
    {
        items.clear();

        for(String name : file.getValues(false).keySet())
        {
            Object section = file.get(name);
            if(!(section instanceof MemorySection)) continue;

            CustomItem item = new CustomItem((MemorySection) section);
            items.put(item.id, item);
        }
    }
}
