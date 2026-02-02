package com.fde.google_drive_organizer.adapter.outbound.drive;

@FunctionalInterface
public interface AccessTokenProvider {

    String getAccessToken();
}

