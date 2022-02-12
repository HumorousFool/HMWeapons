package io.github.humorousfool.hmweapons.crafting;

import io.github.humorousfool.hmweapons.HMWeapons;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ColourManager
{
    public static final HashMap<Color, TextureInfo> ingotColours = new HashMap<>();
    public static final HashMap<Color, TextureInfo> netheriteColours = new HashMap<>();
    public static final HashMap<String, HashMap<Color, TextureInfo>> colourGroups = new HashMap<>();

    public static void loadColours(YamlConfiguration file)
    {
        colourGroups.clear();

        MemorySection ingots = (MemorySection) file.get("Ingots");
        if(ingots != null)
        {
            for(String name : ingots.getKeys(false))
            {
                List<Integer> rgb = ingots.getIntegerList(name + ".RGB");
                if(rgb.size() != 3) continue;

                Color colour = Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2));
                TextureInfo info = new TextureInfo(Material.matchMaterial(ingots.getString(name + ".Material", "BARRIER")),
                        ingots.getInt(name + ".CustomModelData"));
                ingotColours.put(colour, info);
            }
        }

        MemorySection netheriteIngots = (MemorySection) file.get("NetheriteIngots");
        if(netheriteIngots != null)
        {
            for(String name : netheriteIngots.getKeys(false))
            {
                List<Integer> rgb = netheriteIngots.getIntegerList(name + ".RGB");
                if(rgb.size() != 3) continue;

                Color colour = Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2));
                TextureInfo info = new TextureInfo(Material.matchMaterial(netheriteIngots.getString(name + ".Material", "BARRIER")),
                        netheriteIngots.getInt(name + ".CustomModelData"));
                netheriteColours.put(colour, info);
            }
        }

        for(String groupName : file.getValues(false).keySet())
        {
            if(groupName.equalsIgnoreCase("Ingots") || groupName.equalsIgnoreCase("NetheriteIngots")) continue;

            Object testGroup = file.get(groupName);
            if(!(testGroup instanceof MemorySection group))
            {
                HMWeapons.getInstance().getLogger().log(Level.SEVERE, "Could not load colour group \"" +
                        groupName + "\": The group is not a memory section.");
                continue;
            }

            HashMap<Color, TextureInfo> colours = new HashMap<>();

            for(String name : group.getKeys(false))
            {
                List<Integer> rgb = group.getIntegerList(name + ".RGB");
                if(rgb.size() != 3) continue;
                int cmd = Integer.parseInt(name);

                Color colour = Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2));
                TextureInfo info = new TextureInfo(Material.matchMaterial(group.getString(name + ".Material", "BARRIER")), cmd);
                colours.put(colour, info);
            }

            colourGroups.put(groupName, colours);
        }
    }

    public static TextureInfo getClosestIngot(Color colour)
    {
        double distSquared = Double.MAX_VALUE;
        Color result = null;

        for(Color c : ingotColours.keySet())
        {
            //Weighted RGB colour values for better accuracy
            double nds = Math.abs(Math.pow((c.getRed()-colour.getRed())*0.30, 2) +
                    Math.pow((c.getGreen()-colour.getGreen())*0.59, 2) +
                    Math.pow((c.getBlue()-colour.getBlue())*0.11, 2));

            if(nds < distSquared)
            {
                distSquared = nds;
                result = c;
            }
        }

        if(result == null) return null;
        return ingotColours.get(result);
    }

    public static TextureInfo getClosestNetheriteIngot(Color colour)
    {
        double distSquared = Double.MAX_VALUE;
        Color result = null;

        for(Color c : netheriteColours.keySet())
        {
            //Weighted RGB colour values for better accuracy
            double nds = Math.abs(Math.pow((c.getRed()-colour.getRed())*0.30, 2) +
                    Math.pow((c.getGreen()-colour.getGreen())*0.59, 2) +
                    Math.pow((c.getBlue()-colour.getBlue())*0.11, 2));

            if(nds < distSquared)
            {
                distSquared = nds;
                result = c;
            }
        }

        if(result == null) return null;
        return netheriteColours.get(result);
    }

    public static TextureInfo getClosestColourInGroup(String group, Color colour)
    {
        System.out.println(group);
        if(!colourGroups.containsKey(group)) return null;
        HashMap<Color, TextureInfo> map = colourGroups.get(group);

        double distSquared = Double.MAX_VALUE;
        Color result = null;

        for(Color c : map.keySet())
        {
            if(c == colour)
            {
                result = colour;
                break;
            }
            //Weighted RGB colour values for better accuracy
            double nds = Math.abs(Math.pow((c.getRed()-colour.getRed())*0.30, 2) +
                    Math.pow((c.getGreen()-colour.getGreen())*0.59, 2) +
                    Math.pow((c.getBlue()-colour.getBlue())*0.11, 2));

            if(nds < distSquared)
            {
                distSquared = nds;
                result = c;
            }
        }

        if(result == null) return null;
        return map.get(result);
    }

    public static Color averageColours(ArrayList<Color> colours)
    {
        double r = 0;
        double g = 0;
        double b = 0;
        for(Color colour : colours)
        {
            r += colour.getRed();
            g += colour.getGreen();
            b += colour.getBlue();
        }
        r /= colours.size();
        g /= colours.size();
        b /= colours.size();

        return Color.fromRGB((int) r, (int) g, (int) b);
    }

    public static class TextureInfo
    {
        public final Material material;
        public final int customModelData;

        public TextureInfo(Material material, int customModelData)
        {
            this.material = material;
            this.customModelData = customModelData;
        }
    }
}
