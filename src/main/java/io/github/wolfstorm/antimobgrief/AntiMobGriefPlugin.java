package io.github.wolfstorm.antimobgrief;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;

public class AntiMobGriefPlugin extends JavaPlugin {

    private static AntiMobGriefPlugin instance;

    //----------- Server Version Detection: Paper or Spigot and Invalid NMS Version
    String nmsVersion;
    public boolean hasSpigot = false;
    public boolean hasPaper = false;
    String nmsVersionNotLatest = null;
    PluginDescriptionFile pdfFile = this.getDescription();
    static final String SEPARATOR_FIELD = "================================";

    //----------- Config Options here -----------

    //Output for Updates
    boolean opUpdateNotification = false;
    boolean runTheUpdateChecker = false;
    double updateCheckerInterval;

    //Simple MobGriefingTypes - Reimplementation of what was there
    boolean allowCreeperGrief;
    boolean allowGhastGrief;
    boolean allowEndermanGrief;
    boolean allowDoorGrief;
    boolean allowRavagerGrief;

    public AntiMobGriefPlugin(){
        instance = this;
    }

    @Override
    public void onEnable(){
        
        //Get NMS Version
        nmsVersion = getNmsVersion();

        //Load Messages in Console
        getLogger().info("======= AntiMobGrief =======");
        getLogger().info("Plugin Version: " + pdfFile.getVersion());

        // Check if the Minecraft version is supported
        if (nmsVersion.compareTo("v1_13") < 0) {
            getLogger().log(Level.WARNING,"Minecraft Version: {0}",nmsVersion);
            getLogger().warning("AntiMobGrief is not compatible with this version of Minecraft. Please update to at least version 1.13. Loading failed.");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(SEPARATOR_FIELD);
            return;
        }

        //Also Warn People to Update if using nmsVersion lower than latest
        if (nmsVersion.compareTo("v1_20") < 0) {
            getLogger().log(Level.WARNING,"Minecraft Version: {0}",nmsVersion);
            getLogger().warning("AntiMobGrief is compatible with this version of Minecraft, but it is not the latest supported version.");
            getLogger().warning("Loading continuing, but please consider updating to the latest version.");
        } else {
            getLogger().log(Level.INFO, "Minecraft Version: {0}",nmsVersion);
            getLogger().info("AntiMobGrief is compatible with this version of Minecraft. Loading continuing.");
        }
        //Spigot Check
        hasSpigot = getHasSpigot();
        hasPaper = getHasPaper();

        //If Paper and Spigot are both FALSE - Disable the plugin
        if (!hasPaper && !hasSpigot){
            getLogger().severe("This plugin requires either Paper, Spigot or one of its forks to run. This is not an error, please do not report this!");
            getServer().getPluginManager().disablePlugin(this);
            getLogger().info(SEPARATOR_FIELD);
            return;
        } else {
            if (hasSpigot) {
                getLogger().log(Level.INFO,"SpigotMC: {0}",hasSpigot);
            } else {
                getLogger().log(Level.INFO,"PaperMC: {0}",hasPaper);
            }
        }

        getServer().getPluginManager().enablePlugin(this);
        getLogger().info(SEPARATOR_FIELD);

        updateConfig("","config.yml");

        //Simple Mob Types
        allowCreeperGrief = getConfig().getBoolean("allowCreeperGrief", true);
        allowEndermanGrief = getConfig().getBoolean("allowEndermanGrief", true);
        allowGhastGrief = getConfig().getBoolean("allowGhastGrief", true);
        allowDoorGrief = getConfig().getBoolean("allowDoorGrief", true);
        allowRavagerGrief = getConfig().getBoolean("allowRavagerGrief", true);

        //Add ability to enable ot Disable the running of the Updater
        runTheUpdateChecker = getConfig().getBoolean("runTheUpdateChecker", true);

        //Add Ability to check for UpdatePerms that Notify Ops
        opUpdateNotification = getConfig().getBoolean("opUpdateNotification", true);
        updateCheckerInterval = getConfig().getDouble("updateCheckerInterval", 24);

        //Run UpdateChecker - Reports out to Console on Startup ONLY!
        if(runTheUpdateChecker) {

            if(opUpdateNotification){
                runUpdateCheckerWithOPNotifyOnJoinEnabled();
            } else {
                runUpdateCheckerConsoleUpdateCheck();
            }

        }

        //TODO - Metrics

        //---- Executors and Listeners
        CommandEx execute = new CommandEx(this);

        getServer().getPluginManager().registerEvents(new MobGriefListener(this), this);
        Objects.requireNonNull(getCommand("amg")).setExecutor(execute);
    }

    @Override
    public void onDisable(){
        saveConfig();
    }

    private void runUpdateCheckerConsoleUpdateCheck() {
        if (getAntiMobGriefVersion().contains(".x")) {
            getLogger().warning(SEPARATOR_FIELD);
            getLogger().warning("Note from the development team: ");
            getLogger().warning("It appears that you are using the development version of AntiMobGrief");
            getLogger().warning("This version can be unstable and is not recommended for Production Environments.");
            getLogger().warning("Please, report bugs to: https://github.com/Wolfieheart/AntiMobGrief . ");
            getLogger().warning("This warning is intended to be displayed when using a Dev build and is NOT A BUG!");
            getLogger().warning("Update Checker does not work on Development Builds.");
            getLogger().warning(SEPARATOR_FIELD);
        } else {
            /*new UpdateChecker(this, UpdateCheckSource.SPIGET, "" + SPIGOT_RESOURCE_ID + "")
                    .setDownloadLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/")
                    .setChangelogLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/history")
                    .setColoredConsoleOutput(true)
                    .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
                    .checkEveryXHours(updateCheckerInterval)
                    .checkNow();*/
        }
    }

    private void runUpdateCheckerWithOPNotifyOnJoinEnabled() {
        if (getAntiMobGriefVersion().contains(".x")) {
            getLogger().warning(SEPARATOR_FIELD);
            getLogger().warning("Note from the development team: ");
            getLogger().warning("It appears that you are using the development version of AntiMobGrief");
            getLogger().warning("This version can be unstable and is not recommended for Production Environments.");
            getLogger().warning("Please, report bugs to: https://github.com/Wolfieheart/AntiMobGrief . ");
            getLogger().warning("This warning is intended to be displayed when using a Dev build and is NOT A BUG!");
            getLogger().warning("Update Checker does not work on Development Builds.");
            getLogger().warning(SEPARATOR_FIELD);
        } else {
            /*new UpdateChecker(this, UpdateCheckSource.SPIGET, "" + SPIGOT_RESOURCE_ID + "")
                    .setDownloadLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/")
                    .setChangelogLink("https://www.spigotmc.org/resources/armorstandeditor-reborn.94503/history")
                    .setColoredConsoleOutput(true)
                    .setNotifyOpsOnJoin(true)
                    .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion().addServerVersion())
                    .checkEveryXHours(updateCheckerInterval)
                    .checkNow();*/
        }
    }


    //---- Getters for Config Values
    public String getAntiMobGriefVersion()   { return getConfig().getString("version"); }
    public boolean getCreeperGriefStatus()   { return getConfig().getBoolean("allowCreeperGrief"); }
    public boolean getDoorGriefStatus()      { return getConfig().getBoolean("allowDoorGrief"); }
    public boolean getEndermanGriefStatus()  { return getConfig().getBoolean("allowEndermanGrief"); }
    public boolean getRavagerGriefStatus()   { return getConfig().getBoolean("allowRavagerGrief"); }
    public boolean getGhastGriefStatus()     { return getConfig().getBoolean("allowGhastGrief"); }

    public String getNmsVersion(){
        return this.getServer().getClass().getPackage().getName().replace(".",",").split(",")[3];
    }

    public boolean getHasSpigot(){
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            nmsVersionNotLatest = "SpigotMC ASAP.";
            return true;
        } catch (ClassNotFoundException e){
            nmsVersionNotLatest = "";
            return false;
        }
    }

    public boolean getHasPaper(){
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            nmsVersionNotLatest = "SpigotMC ASAP.";
            return true;
        } catch (ClassNotFoundException e){
            nmsVersionNotLatest = "";
            return false;
        }
    }

    private void updateConfig(String folder, String config) {
        if(!new File(getDataFolder() + File.separator + folder + config).exists()){
            saveResource(folder  + config, false);
        }
    }

    public void performReload() {

        //TODO: WRITE THIS
    }

}
