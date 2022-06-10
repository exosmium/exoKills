package lv.exosmium.exoKills;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class KillListener implements Listener {
    private final FileConfiguration config;
    private final List<String> groupList = new ArrayList<>();
    
    public KillListener(FileConfiguration config) {
        this.config = config;
        this.groupList.addAll(config.getConfigurationSection("Die.groups").getKeys(false));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player p = event.getEntity();
        if (event.getEntity().getKiller() instanceof Player) {
            Player killerPlayer = event.getEntity().getKiller();
            String killerGroup = getPlayerGroup(p.getKiller(), groupList);

            List<String> commandArray = config.getStringList("Kill.groups." + killerGroup + ".commands");
            String msg = formatPlaceholders(config.getString("Kill.groups." + killerGroup + ".killer.message"), killerPlayer, p);
            String actionBarMsg = formatPlaceholders(config.getString("Kill.groups." + killerGroup + ".killer.actionbar-message"), killerPlayer, p);

            if (commandArray != null) {
                for (String cmd : commandArray) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', formatPlaceholders(cmd, killerPlayer, p)));
                }
            }
            if (!msg.isEmpty()) killerPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            if (!actionBarMsg.isEmpty()) sendActionbarMessage(killerPlayer, ChatColor.translateAlternateColorCodes('&', actionBarMsg));

            String msgTarget = formatPlaceholders(config.getString("Kill.groups." + killerGroup + ".target.message"), killerPlayer, p);
            String actionBarMsgTarget = formatPlaceholders(config.getString("Kill.groups." + killerGroup + ".target.actionbar-message"), killerPlayer, p);
            if (!msgTarget.isEmpty()) p.sendMessage(ChatColor.translateAlternateColorCodes('&', msgTarget));
            if (!actionBarMsgTarget.isEmpty()) sendActionbarMessage(p, ChatColor.translateAlternateColorCodes('&', actionBarMsgTarget));
            return;
        }

        String diedGroup = getPlayerGroup(p, groupList);
        List<String> commandArray = config.getStringList("Die.groups." + diedGroup + ".commands");
        String msg = config.getString("Die.groups." + diedGroup + ".message");
        String actionBarMsg = config.getString("Die.groups." + diedGroup + ".actionbar-message");

        if (commandArray != null) {
            for (String cmd : commandArray) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), ChatColor.translateAlternateColorCodes('&', cmd.replace("%player%", p.getName())));
            }
        }
        if (!msg.isEmpty()) p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
        if (!actionBarMsg.isEmpty()) sendActionbarMessage(p, ChatColor.translateAlternateColorCodes('&', actionBarMsg));
    }


    private String getPlayerGroup(Player user, List<String> possibleGroups) {
        for (int i = possibleGroups.size()-  1; i >= 0; i--) {
            String perm = "group" + "." + possibleGroups.get(i);
            if (user.hasPermission(perm)) {
                return possibleGroups.get(i);
            }
        }
        return null;
    }

    private void sendActionbarMessage(Player p, String msg) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
    }

    private String formatPlaceholders(String str, Player killer, Player target) {
        try {
            str = str.replace("%killer%", killer.getName());
        } catch (Exception ignored) {}
        try {
            str = str.replace("%target%", target.getName());
        } catch (Exception ignored) {}
        return str;
    }
}
