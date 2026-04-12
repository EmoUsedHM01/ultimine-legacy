package com.ftbultimine;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {

    public static Configuration config;

    // Server settings
    public static int maxBlocks = 64;
    public static double exhaustionPerBlock = 20.0;
    public static double experiencePerBlock = 0.0;
    public static boolean preventToolBreak = true;
    public static boolean cancelOnBlockBreakFail = true;
    public static boolean requireTool = true;
    public static int ultimineCooldown = 0;
    public static boolean mergeTagsShapeless = true;

    // Right-click features
    public static boolean rightClickAxe = true;
    public static boolean rightClickShovel = true;
    public static boolean rightClickHoe = true;
    public static boolean rightClickHarvesting = true;

    // Client settings
    public static int renderOutlineMax = 256;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();

        // Server
        maxBlocks = config.getInt("maxBlocks", "server", 64, 1, 8192,
            "Maximum number of blocks that can be mined at once.");
        exhaustionPerBlock = config.getFloat("exhaustionPerBlock", "server", 20.0f, 0.0f, 10000.0f,
            "Hunger exhaustion added per extra block mined (vanilla dig = 5). Set to 0 to disable.");
        experiencePerBlock = config.getFloat("experiencePerBlock", "server", 0.0f, 0.0f, 1000.0f,
            "Experience levels consumed per extra block mined. Set to 0 to disable.");
        preventToolBreak = config.getBoolean("preventToolBreak", "server", true,
            "Prevent tool from breaking by stopping ultimine when durability is low.");
        cancelOnBlockBreakFail = config.getBoolean("cancelOnBlockBreakFail", "server", true,
            "Cancel remaining blocks if one block fails to break (e.g. protected area).");
        requireTool = config.getBoolean("requireTool", "server", true,
            "Require the player to hold a damageable tool to use ultimine.");
        ultimineCooldown = config.getInt("ultimineCooldown", "server", 0, 0, 72000,
            "Cooldown in ticks between ultimine operations. 0 = no cooldown.");
        mergeTagsShapeless = config.getBoolean("mergeTagsShapeless", "server", true,
            "In shapeless mode, treat blocks that share ore dictionary entries as the same block.");

        // Right-click
        rightClickAxe = config.getBoolean("rightClickAxe", "rightclick", true,
            "Enable multi-block log stripping with axes (1.7.10: no stripping, acts as multi-break).");
        rightClickShovel = config.getBoolean("rightClickShovel", "rightclick", true,
            "Enable multi-block grass path creation with shovels.");
        rightClickHoe = config.getBoolean("rightClickHoe", "rightclick", true,
            "Enable multi-block farmland tilling with hoes.");
        rightClickHarvesting = config.getBoolean("rightClickHarvesting", "rightclick", true,
            "Enable multi-block crop harvesting by right-clicking.");

        // Client
        renderOutlineMax = config.getInt("renderOutlineMax", "client", 256, 0, 8192,
            "Maximum blocks to render the outline for. Set to 0 to disable outline.");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
