package com.turaelboosting;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
@PluginDescriptor(
	name = "Turael Boosting"
)
public class TuraelBoostingPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private TuraelBoostingConfig config;


	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TuraelBoostingOverlay overlay;

	@Getter
    private int slayerTasksCompleted;

	@Getter
	private boolean shouldGoToTurael;

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(this::updateSlayerData);
		addOverlay();
		log.debug("Turael Boosting started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		removeOverlay();
		log.debug("Turael Boosting stopped!");
	}

	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		var gameState = gameStateChanged.getGameState();
		if(gameState == GameState.LOGGED_IN) {
			updateSlayerData();
		}
	}

	/**
	 * Updates slayer data on this instance.
	 */
	private void updateSlayerData() {
		setSlayerTasksCompleted();
		setShouldGoToTurael();
	}

	/**
	 * Stores whether the player should go to Turael for their next task.
	 * @throws AssertionError If called outside the ClientThread.
	 */
	private void setShouldGoToTurael() {
		this.shouldGoToTurael = this.slayerTasksCompleted % 10 != 9;
	}

	/**
	 * Stores the number of slayer tasks completed.
	 * @throws AssertionError If called outside the ClientThread.
	 */
	private void setSlayerTasksCompleted() {
		this.slayerTasksCompleted = client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
	}

	private void addOverlay() {
		overlayManager.add(overlay);
	}

	private void removeOverlay() {
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged) {
		int varpID = varbitChanged.getVarpId();
		int varbitID = varbitChanged.getVarbitId();

		boolean tasksInARowChanged = varbitID == VarbitID.SLAYER_TASKS_COMPLETED;
		boolean slayerTaskChanged = varbitID == VarPlayerID.SLAYER_TARGET;

		if(tasksInARowChanged) {
			updateSlayerData();
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened) {
		MenuEntry firstEntry = menuOpened.getFirstEntry();
		boolean isNPCMenu = firstEntry.getType() == MenuAction.NPC_FIRST_OPTION;
		boolean npcIsTurael = isNPCMenu && firstEntry.getNpc().getId() == NpcID.SLAYER_MASTER_1_TUREAL;
		boolean npcIsAya = isNPCMenu && firstEntry.getNpc().getId() == NpcID.SLAYER_MASTER_1_AYA;

		if(npcIsTurael || npcIsAya) {
			hideAssignmentOption();
		}
	}

	private void hideAssignmentOption() {
		if(!config.hideTuraelAssignmentOption()) {
			return;
		}

		Menu menu = client.getMenu();
		MenuEntry assignment = null;
		for(MenuEntry menuEntry: menu.getMenuEntries()) {
			if(Objects.equals(menuEntry.getOption(), "Assignment")) {
				assignment = menuEntry;
			}
		}

		menu.removeMenuEntry(assignment);
	}

	@Provides
	TuraelBoostingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TuraelBoostingConfig.class);
	}
}
