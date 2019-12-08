package com.zp4rker.bungeenotify

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler
import java.io.File
import java.nio.file.Files

class BungeeNotify : Plugin() {
    override fun onEnable() {
        proxy.pluginManager.registerListener(this, SwitchListener(this))
        logger.info("Successfully enabled.")
    }
}

class SwitchListener(private val plugin: Plugin): Listener {
    private var config: Configuration
    init {
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
        with(File(plugin.dataFolder, "config.yml")) {
            if (!exists()) {
                createNewFile()
                Files.copy(this.javaClass.getResourceAsStream("config.yml"), toPath())
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(this)
        }
    }

    @EventHandler
    fun onConenct(event: ServerConnectEvent) {
        if (event.reason == ServerConnectEvent.Reason.JOIN_PROXY) return
        if (!event.player.hasPermission("bungeenotify.watch")) return

        val message = config.getString("message-format").replace("%player%", event.player.displayName).replace("%server%", event.target.name)

        for (player in plugin.proxy.players.filter { it.isConnected && it.hasPermission("bungeenotify.notify") }) {
            player.sendMessage(ChatMessageType.CHAT, TextComponent(ChatColor.translateAlternateColorCodes('&', message)))
        }
    }
}