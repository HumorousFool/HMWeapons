package io.github.humorousfool.hmweapons.localisation;

import io.github.humorousfool.hmweapons.HMWeapons;
import io.github.humorousfool.hmweapons.config.Config;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

public class I18nSupport {
    private static Properties languageFile;

    public static void init()
    {
        languageFile = new Properties();

        File localisationDirectory = new File(HMWeapons.getInstance().getDataFolder().getAbsolutePath() + "/localisation");

        if (!localisationDirectory.exists())
        {
            localisationDirectory.mkdirs();
        }

        InputStream is = null;
        try
        {
            is = new FileInputStream(localisationDirectory.getAbsolutePath() + "/hmweaponslang" + "_" + Config.Locale + ".properties");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        if (is == null)
        {
            HMWeapons.getInstance().getLogger().log(Level.SEVERE, "Critical Error in Localisation System");
            HMWeapons.getInstance().getServer().shutdown();
            return;
        }

        try
        {
            languageFile.load(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getInternationalisedString(String key)
    {
        String s = languageFile.getProperty(key);
        if (s != null)
        {
            return s;
        }
        else
        {
            return key;
        }
    }
}
