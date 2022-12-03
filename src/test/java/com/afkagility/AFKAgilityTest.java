package com.afkagility;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AFKAgilityTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(AFKAgilityPlugin.class);
		RuneLite.main(args);
	}
}