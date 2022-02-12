package io.github.humorousfool.hmweapons.crafting.infusion.recipes;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;

public class RecipeManager
{
    public static final ArrayList<InfusionRecipe> recipes = new ArrayList<>();

    public static void loadRecipes(YamlConfiguration file)
    {
        recipes.clear();

        for(String name : file.getValues(false).keySet())
        {
            Object section = file.get(name);
            if(!(section instanceof MemorySection)) return;

            InfusionRecipe recipe = new InfusionRecipe((MemorySection) section);
            recipes.add(recipe);
        }
    }
}
