package com.fidespn.view;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility for showing SystemTray notifications when available.
 */
public final class TrayNotifier {
    private TrayNotifier() {}

    public static void show(String title, String message) {
        if (!SystemTray.isSupported()) return;
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            TrayIcon trayIcon = new TrayIcon(image, "FidESPN");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            // Remove icon after showing message
            tray.remove(trayIcon);
        } catch (Exception ignored) {
        }
    }
}


