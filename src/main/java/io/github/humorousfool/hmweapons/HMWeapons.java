package io.github.humorousfool.hmweapons;

import io.github.humorousfool.hmweapons.commands.*;
import io.github.humorousfool.hmweapons.config.Config;
import io.github.humorousfool.hmweapons.crafting.ColourManager;
import io.github.humorousfool.hmweapons.crafting.forging.Forge;
import io.github.humorousfool.hmweapons.crafting.infusion.Infuser;
import io.github.humorousfool.hmweapons.crafting.infusion.recipes.RecipeManager;
import io.github.humorousfool.hmweapons.crafting.forging.ShapeManager;
import io.github.humorousfool.hmweapons.items.ItemRegistry;
import io.github.humorousfool.hmweapons.listener.CraftListener;
import io.github.humorousfool.hmweapons.listener.ItemListener;
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

        reloadMaterialsAndColours();
        reloadRecipes();
        reloadShapes();
        reloadItems();

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new CraftListener(), this);
        getServer().getPluginManager().registerEvents(new Infuser(), this);
        getServer().getPluginManager().registerEvents(new Forge(), this);

        getCommand("materials").setExecutor(new MaterialsCommand());
        getCommand("crafters").setExecutor(new CraftersCommand());
        getCommand("shapes").setExecutor(new ShapesCommand());
        getCommand("items").setExecutor(new ItemsCommand());
        getCommand("hmweapons").setExecutor(new HMWeaponsCommand());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    public void reloadMaterialsAndColours()
    {
        ColourManager.ingotColours.clear();
        ColourManager.netheriteColours.clear();
        File file = new File(getDataFolder(), "materials.yml");
        if (!file.exists()) {
            this.saveResource("materials.yml", false);
        }
        MaterialManager.loadMaterials(YamlConfiguration.loadConfiguration(file));

        file = new File(getDataFolder(), "colours.yml");
        if (!file.exists()) {
            this.saveResource("colours.yml", false);
        }
        ColourManager.loadColours(YamlConfiguration.loadConfiguration(file));
    }

    public void reloadRecipes()
    {
        File file = new File(getDataFolder(), "recipes.yml");
        if (!file.exists()) {
            this.saveResource("recipes.yml", false);
        }
        RecipeManager.loadRecipes(YamlConfiguration.loadConfiguration(file));
    }

    public void reloadShapes()
    {
        File file = new File(getDataFolder(), "shapes.yml");
        if(!file.exists()) {
            this.saveResource("shapes.yml", false);
        }
        ShapeManager.loadShapes(YamlConfiguration.loadConfiguration(file));
    }

    public void reloadItems()
    {
        File file = new File(getDataFolder(), "items.yml");
        if(!file.exists()) {
            this.saveResource("items.yml", false);
        }
        ItemRegistry.loadItems(YamlConfiguration.loadConfiguration(file));
    }

    public static HMWeapons getInstance()
    {
        return instance;
    }
}
