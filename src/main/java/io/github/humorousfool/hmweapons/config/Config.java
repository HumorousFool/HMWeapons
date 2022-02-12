package io.github.humorousfool.hmweapons.config;

import org.bukkit.configuration.file.FileConfiguration;

public class Config
{
    public static String Locale = "en";
    public static int MaxShapeUses = 10;

    public static void update(FileConfiguration file)
    {
        Locale = file.getString("Locale", "en");
        MaxShapeUses = file.getInt("MaxShapeUses", 10);
    }
}
