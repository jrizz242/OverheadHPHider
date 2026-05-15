package com.hphider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hpHider")
public interface HPhiderConfig extends Config
{
	@ConfigItem(
			keyName = "Hide player HealthBar",
			name = "health bar hider",
			description = "Replaces health bars with the RuneScape high detail mode design."
	)
	default boolean hideHealthBar()
	{
		return false;
	}
	/*@ConfigItem(
			position = 1,
			keyName = "hideHPBar",
			name = "Hide players health bar",
			description = "Configures whether or not the local player's 2D elements are hidden."
	)
	default boolean showLocalPlayer()
	{
		return true;
	}*/
}
