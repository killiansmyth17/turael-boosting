package com.turaelboosting;

import com.google.common.collect.ImmutableSet;
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
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@PluginDescriptor(
	name = "Turael Boosting"
)
public class TuraelBoostingPlugin extends Plugin
{
	private static final Set<MenuAction> NPC_MENU_TYPES = ImmutableSet.of(
		MenuAction.NPC_FIRST_OPTION,
		MenuAction.NPC_SECOND_OPTION,
		MenuAction.NPC_THIRD_OPTION,
		MenuAction.NPC_FOURTH_OPTION,
		MenuAction.NPC_FIFTH_OPTION
	);

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

		if(tasksInARowChanged) {
			updateSlayerData();
		}
	}

	/**
	 * Detect opened right-click menu of Turael/Aya.
	 * @param menuOpened The MenuOpened event.
	 */
	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened) {
		MenuEntry firstEntry = menuOpened.getFirstEntry();
		boolean isNPCMenu = NPC_MENU_TYPES.contains(firstEntry.getType());
		boolean npcIsTurael = isNPCMenu && firstEntry.getNpc().getId() == NpcID.SLAYER_MASTER_1_TUREAL;
		boolean npcIsAya = isNPCMenu && firstEntry.getNpc().getId() == NpcID.SLAYER_MASTER_1_AYA;

		if(npcIsTurael || npcIsAya) {
			hideAssignmentOption();
		}
	}

	/**
	 * Detect left-click menu of Turael/Aya and remove Assignment option.
	 * @param clientTick The ClientTick event.
	 */
	@Subscribe
	public void onClientTick(ClientTick clientTick) {
		if(client.getGameState() != GameState.LOGGED_IN || client.isMenuOpen()) {
			return;
		}

		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		for(MenuEntry menuEntry: menuEntries) {
			if(NPC_MENU_TYPES.contains(menuEntry.getType())) {
				final int npcID = menuEntry.getNpc().getId();
				if(menuEntry.getOption().equals("Assignment") && (npcID == NpcID.SLAYER_MASTER_1_TUREAL || npcID == NpcID.SLAYER_MASTER_1_AYA)) {
					menu.removeMenuEntry(menuEntry);
				}
			}
		}

	}

	/**
	 * Remove Assignment option from the menu of Turael/Aya.
	 */
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
