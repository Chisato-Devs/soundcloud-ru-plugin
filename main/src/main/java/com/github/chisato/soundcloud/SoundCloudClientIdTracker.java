package com.github.chisato.soundcloud;

import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoundCloudClientIdTracker {
  private static final Logger log = LoggerFactory.getLogger(SoundCloudClientIdTracker.class);

  private static final String ID_FETCH_CONTEXT_ATTRIBUTE = "sc-raw";
  private static final long CLIENT_ID_REFRESH_INTERVAL = TimeUnit.HOURS.toMillis(1);
  private static final String PAGE_APP_CLIENT_ID_REGEX = "[^_]clientId\":\"([a-zA-Z0-9-_]+)\"";

  private static final Pattern pageAppClientIdPattern = Pattern.compile(PAGE_APP_CLIENT_ID_REGEX);

  private final Object clientIdLock = new Object();
  private final HttpInterfaceManager httpInterfaceManager;
  private String clientId;
  private long lastClientIdUpdate;

  public SoundCloudClientIdTracker(HttpInterfaceManager httpInterfaceManager) {
    this.httpInterfaceManager = httpInterfaceManager;
  }

  /**
   * Updates the clientID if more than {@link #CLIENT_ID_REFRESH_INTERVAL} time has passed since last updated.
   */
  public void updateClientId() {
    synchronized (clientIdLock) {
      long now = System.currentTimeMillis();
      if (now - lastClientIdUpdate < CLIENT_ID_REFRESH_INTERVAL) {
        log.debug("Client ID was recently updated, not updating again right away.");
        return;
      }

      lastClientIdUpdate = now;
      log.info("Updating SoundCloud client ID (current is {}).", clientId);

      try {
        clientId = findClientIdFromSite();
        log.info("Updating SoundCloud client ID succeeded, new ID is {}.", clientId);
      } catch (Exception e) {
        log.error("SoundCloud client ID update failed.", e);
      }
    }
  }

  public String getClientId() {
    synchronized (clientIdLock) {
      if (clientId == null) {
        updateClientId();
      }

      return clientId;
    }
  }

  public boolean isIdFetchContext(HttpClientContext context) {
    return context.getAttribute(ID_FETCH_CONTEXT_ATTRIBUTE) == Boolean.TRUE;
  }

  private String findClientIdFromSite() throws IOException {
    try (HttpInterface httpInterface = httpInterfaceManager.getInterface()) {
      httpInterface.getContext().setAttribute(ID_FETCH_CONTEXT_ATTRIBUTE, true);

      return findApplicationId(httpInterface);
    }
  }

  private String findApplicationId(HttpInterface httpInterface) throws IOException {
    try (CloseableHttpResponse response = httpInterface.execute(new HttpGet("https://m.soundcloud.com"))) {
      int statusCode = response.getStatusLine().getStatusCode();
      if (!HttpClientTools.isSuccessWithContent(statusCode)) {
        throw new IOException("Invalid status code for main page response: " + statusCode);
      }

      String page = EntityUtils.toString(response.getEntity());
      Matcher applicationIdMatcher = pageAppClientIdPattern.matcher(page);

      if (applicationIdMatcher.find()) {
        return applicationIdMatcher.group(1);
      } else {
        throw new IllegalStateException("Could not find client ID string from main page.");
      }
    }
  }
}