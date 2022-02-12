package io.github.humorousfool.hmweapons.config;

import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
    public static String Locale = "en";

    public static int MaxShapeUses = 10;
    public static int MaxItemLives = 10;

    public static boolean OverrideKeepInventory = true;

    public static void update(FileConfiguration file)
    {
        Locale = file.getString("Locale", "en");
        MaxShapeUses = file.getInt("MaxShapeUses", 10);
        MaxItemLives = file.getInt("MaxItemLives", 10);
        OverrideKeepInventory = file.getBoolean("OverrideKeepInventory", true);
    }
}
