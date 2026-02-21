package com.turaelboosting;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.gameval.DBTableID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

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
	private TuraelBoostingConfig config;

	private int slayerTasksCompleted;
	private boolean shouldGoToTurael;

	@Override
	protected void startUp() throws Exception
	{
		if(client.getGameState() == GameState.LOGGED_IN) {
			updateSlayerData();
		}
		log.debug("Turael Boosting started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Turael Boosting stopped!");
	}

	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		var gameState = gameStateChanged.getGameState();
		if(gameState == GameState.LOGGED_IN) {
			updateSlayerData();
		}
	}

	private void updateSlayerData() {
		this.slayerTasksCompleted = getSlayerTasksCompleted();
		this.shouldGoToTurael = determineShouldGoToTurael();
	}

	private boolean determineShouldGoToTurael() {
		this.slayerTasksCompleted = getSlayerTasksCompleted();
		return this.slayerTasksCompleted % 10 != 9;
	}

	private int getSlayerTasksCompleted() {
		return client.getVarbitValue(VarbitID.SLAYER_TASKS_COMPLETED);
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged) {
		int varpID = varbitChanged.getVarpId();
		int varbitID = varbitChanged.getVarbitId();

		boolean tasksInARowChanged = varbitID == VarbitID.SLAYER_TASKS_COMPLETED;
		boolean slayerTaskChanged = varbitID == VarPlayerID.SLAYER_TARGET;
		boolean slayerTaskWasObtained = slayerTaskChanged && client.getVarpValue(VarPlayerID.SLAYER_TARGET) != -1; // TODO: What is the ID of no slayer target?

		if(tasksInARowChanged) {
			updateSlayerData();
			// TODO: Show UI element
		}

		else if(slayerTaskWasObtained){
			// TODO: Hide UI element
		}
	}

	public void hideAssignmentOption() {
		if(!client.isMenuOpen()) {
			log.warn("Attempted to remove Assignment option when menu wasn't open.");
			return;
		}

		Menu menu = client.getMenu();
		MenuEntry assignment = null;
		for(MenuEntry menuEntry: menu.getMenuEntries()) {
			if(Objects.equals(menuEntry.getOption(), "Assignment")) {
				assignment = menuEntry;
			}
		}

		if(assignment == null) {
			log.warn("Attempted to remove Assignment option when it wasn't an option in the menu.");
			return;
		}

		menu.removeMenuEntry(assignment);
	}

	@Subscribe
	public void onMenuOpened(MenuOpened menuOpened) {
		MenuEntry firstEntry = menuOpened.getFirstEntry();
		boolean isNPCMenu = firstEntry.getType() == MenuAction.EXAMINE_NPC;
		boolean npcIsTurael = isNPCMenu && firstEntry.getNpc().getId() == NpcID.SLAYER_MASTER_1_TUREAL;

		if(npcIsTurael) {
			hideAssignmentOption();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		Actor npc = npcSpawned.getNpc();

		if(Objects.equals(npcSpawned.getNpc().getId(), NpcID.SLAYER_MASTER_1_TUREAL)) {

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", npc.getName(), null);
		}
	}

	@Provides
	TuraelBoostingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TuraelBoostingConfig.class);
	}


}
