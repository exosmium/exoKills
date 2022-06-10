package lv.exosmium.exoKills;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private final FileConfiguration config;

    public Main() { this.config = getConfig(); }

    @Override
    public void onEnable() {
        System.out.println("§4§nCoded by§8:§r §cExosmium§7 (vk.com/prodbyhakin)");
        setupConfig();
        getServer().getPluginManager().registerEvents(new KillListener(config), this);
    }

    private void setupConfig() {
        this.config.options().copyDefaults(true);
        this.saveDefaultConfig();
    }
}
