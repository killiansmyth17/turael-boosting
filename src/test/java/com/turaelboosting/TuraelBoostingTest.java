package com.turaelboosting;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TuraelBoostingTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TuraelBoostingPlugin.class);
		RuneLite.main(args);
	}
}