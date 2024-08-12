package com.github.chisato.soundcloud.plugin;

import com.github.chisato.soundcloud.SoundCloudSourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SoundcloudPlugin implements AudioPlayerManagerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(com.github.chisato.soundcloud.plugin.SoundcloudPlugin.class);

    private final SoundCloudSourceManager soundCloud;

    public SoundcloudPlugin() {
        log.info("Loading Soundcloud RU Bypass plugin...");
        this.soundCloud = SoundCloudSourceManager.createDefault();
    }

    @NotNull
    @Override
    public AudioPlayerManager configure(@NotNull AudioPlayerManager manager) {
        log.info("Registering Sound Cloud audio source manager...");
        manager.registerSourceManager(this.soundCloud);
        return manager;
    }
}
