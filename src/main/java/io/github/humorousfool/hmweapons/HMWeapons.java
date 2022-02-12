package io.github.humorousfool.hmweapons;

import io.github.humorousfool.hmweapons.commands.CraftersCommand;
import io.github.humorousfool.hmweapons.commands.MaterialsCommand;
import io.github.humorousfool.hmweapons.commands.ShapesCommand;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.crafting.ColourManager;
import io.github.humorousfool.hmweapons.crafting.forging.Forge;
import io.github.humorousfool.hmweapons.crafting.infusion.Infuser;
import io.github.humorousfool.hmweapons.crafting.infusion.recipes.RecipeManager;
import io.github.humorousfool.hmweapons.crafting.forging.ShapeManager;
import io.github.humorousfool.hmweapons.listener.PlayerListener;
import io.github.humorousfool.hmweapons.listener.BlockListener;
import io.github.humorousfool.hmweapons.localisation.I18nSupport;
import io.github.humorousfool.hmweapons.crafting.infusion.materials.MaterialManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class HMWeapons extends JavaPlugin
{
    private static HMWeapons instance;

    @Override
    public void onEnable()
    {
        instance = this;

        saveDefaultConfig();
        Config.update(getConfig());

        String[] localisations = {"en", "fr"};
        for (String s : localisations) {
            if (!new File(getDataFolder()
                    + "/localisation/hmweaponslang_" + s + ".properties").exists()) {
                this.saveResource("localisation/hmweaponslang_" + s + ".properties", false);
            }
        }
        I18nSupport.init();

        File materialFile = new File(getDataFolder(), "materials.yml");
        if (!materialFile.exists()) {
            this.saveResource("materials.yml", false);
        }
        MaterialManager.loadMaterials(YamlConfiguration.loadConfiguration(materialFile));

        File recipeFile = new File(getDataFolder(), "recipes.yml");
        if (!recipeFile.exists()) {
            this.saveResource("recipes.yml", false);
        }
        RecipeManager.loadRecipes(YamlConfiguration.loadConfiguration(recipeFile));

        File colourFile = new File(getDataFolder(), "colours.yml");
        if (!colourFile.exists()) {
            this.saveResource("colours.yml", false);
        }
        ColourManager.loadColours(YamlConfiguration.loadConfiguration(colourFile));

        File shapeFile = new File(getDataFolder(), "shapes.yml");
        if(!shapeFile.exists()) {
            this.saveResource("shapes.yml", false);
        }
        ShapeManager.loadShapes(YamlConfiguration.loadConfiguration(shapeFile));

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new Infuser(), this);
        getServer().getPluginManager().registerEvents(new Forge(), this);

        getCommand("materials").setExecutor(new MaterialsCommand());
        getCommand("crafters").setExecutor(new CraftersCommand());
        getCommand("shapes").setExecutor(new ShapesCommand());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    public static HMWeapons getInstance()
    {
        return instance;
    }
}
