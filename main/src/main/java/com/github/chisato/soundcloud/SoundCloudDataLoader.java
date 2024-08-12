package com.github.chisato.soundcloud;

import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;

import java.io.IOException;

public interface SoundCloudDataLoader {
    JsonBrowser load(HttpInterface httpInterface, String url) throws IOException;
}
