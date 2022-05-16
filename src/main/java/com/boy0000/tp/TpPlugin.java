package com.boy0000.tp;

import org.bukkit.plugin.java.JavaPlugin;

public final class TpPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("tpa").setExecutor(new TpCommands());
        getCommand("tpaccept").setExecutor(new TpCommands());
        getCommand("tpdeny").setExecutor(new TpCommands());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
