package com.turaelboosting;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.util.EnumSet;
import java.util.Set;

@ConfigGroup("turaelboosting")
public interface TuraelBoostingConfig extends Config
{

	@ConfigItem(
		keyName = "master",
		name = "Slayer Master",
		description = "The slayer master that you wish to Turael boost with.",
		position = 0
	)
	default SlayerMaster master(){
		return SlayerMaster.Duradel;
	}

	@ConfigItem(
			keyName = "hideTuraelAssignmentOption",
			name = "Hide Turael's Assignment option",
			description = "Hide Turael's Assignment option when your next task has boosted points.",
			position = 1
	)
	default boolean hideTuraelAssignmentOption() {
		return true;
	}
}
