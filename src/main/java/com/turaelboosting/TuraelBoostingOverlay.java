package com.turaelboosting;

import net.runelite.api.Client;
import net.runelite.api.geometry.RectangleUnion;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class TuraelBoostingOverlay extends OverlayPanel {
    private final Client client;
    private final TuraelBoostingPlugin plugin;
    private final TuraelBoostingConfig config;

    private static final int OVERLAY_WIDTH = 140;
    private static final int OVERLAY_HEIGHT = 0;
    private static final String TURAEL_NAME = "Turael";

    @Inject
    private TuraelBoostingOverlay(final Client client, final TuraelBoostingPlugin plugin, final TuraelBoostingConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.setPreferredSize(new Dimension(OVERLAY_WIDTH, OVERLAY_HEIGHT));

        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text("Turael Boosting")
                        .build()
        );

        Color color = (plugin.isShouldGoToTurael()) ? Color.GREEN : Color.RED;
        String slayerMasterName = (plugin.isShouldGoToTurael()) ? TURAEL_NAME : config.master().name();


        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Do task from:")
                        .right(slayerMasterName)
                        .rightColor(color)
                        .build()
        );

        int tasksInARow = plugin.getSlayerTasksCompleted();
        panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Tasks in a row:")
                        .right(String.valueOf(tasksInARow))
                        .rightColor(color)
                        .build()
        );

        if(!plugin.isOnSlayerTask()) {
            panelComponent.getChildren().add(
                    TitleComponent.builder()
                            .text("TASK COMPLETE!")
                            .color(Color.RED)
                            .build()
            );
        }

        return super.render(graphics);
    }
}
