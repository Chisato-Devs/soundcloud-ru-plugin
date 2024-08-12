package com.github.chisato.soundcloud;

public interface SoundCloudTrackFormat {
    String getTrackId();

    String getProtocol();

    String getMimeType();

    String getLookupUrl();
}
