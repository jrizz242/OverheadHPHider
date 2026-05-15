package com.hphider;

import com.google.inject.Provides;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HealthBarConfig;
import net.runelite.api.SpritePixels;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.PostClientTick;
import net.runelite.api.events.PostHealthBarConfig;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "HP Hider"
)

public class HPHiderPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private HPhiderConfig config;

    @Inject
    private SpriteManager spriteManager;

    private SpritePixels[] defaultCrossSprites;

    @Provides
    HPhiderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(HPhiderConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        queueUpdateAllOverrides();
    }

    @Override
    protected void shutDown() throws Exception {
        clientThread.invoke(() ->
        {
            restoreHealthBars();
        });
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (gameStateChanged.getGameState() == GameState.STARTING) {
            queueUpdateAllOverrides();
        }
    }

    private void queueUpdateAllOverrides() {
        clientThread.invoke(() ->
        {
            // Cross sprites and widget sprite cache are not setup until login screen
            if (client.getGameState().getState() < GameState.LOGIN_SCREEN.getState()) {
                return false;
            }
            updateAllOverrides();
            return true;
        });
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged config) {
        if (config.getGroup().equals("interfaceStyles")) {
            clientThread.invoke(this::updateAllOverrides);
        }
    }

    @Subscribe
    public void onPostHealthBarConfig(PostHealthBarConfig postHealthBar)
    {
        if (!config.hideHealthBar())
        {
            return;
        }

        HealthBarConfig healthBar = postHealthBar.getHealthBarConfig();
        HealthbarOverride override = HealthbarOverride.get(healthBar.getHealthBarFrontSpriteId());

        // Check if this is the health bar we are replacing
        if (override != null)
        {
            // Increase padding to show some more green at very low hp percentages
            healthBar.setPadding(override.getPadding());
        }
    }
    private void updateAllOverrides()
    {
        overrideHealthBars();
    }

    private void overrideHealthBars()
    {
        if (config.hideHealthBar())
        {
            spriteManager.addSpriteOverrides(HealthbarOverride.values());
            // Reset health bar caches to apply the override
            clientThread.invokeLater(client::resetHealthBarCaches);
        }
        else
        {
            restoreHealthBars();
        }
    }

    private void restoreHealthBars()
    {
        spriteManager.removeSpriteOverrides(HealthbarOverride.values());
        clientThread.invokeLater(client::resetHealthBarCaches);
    }
}
/*public class HPHiderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HPhiderConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private HPBarOverlay overlay;

	@Inject
	private EventBus eventBus;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);

		eventBus.register(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);

		eventBus.unregister(overlay);
	}

	@Provides
	HPhiderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HPhiderConfig.class);
	}
}

@Singleton
class HPBarOverlay extends Overlay
{
	private static final Color BAR_FILL_COLOR = Color.green;
	private static final Color BAR_BG_COLOR = Color.red;
	private static final Dimension HP_BAR_SIZE = new Dimension(30, 5);

	private final Client client;
	private HPhiderConfig config;

	private boolean showHPBar;

	@Inject
	private HPBarOverlay(final Client client, final HPhiderConfig config)
	{
		this.client = client;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(PRIORITY_HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!showHPBar || !config.showLocalPlayer())
		{
			return null;
		}

		final int height = client.getLocalPlayer().getLogicalHeight() + 28;
		final LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
		final Point canvasPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);

		final float ratio = (float) client.getBoostedSkillLevel(Skill.HITPOINTS) / client.getRealSkillLevel(Skill.HITPOINTS);

		// Draw bar
		final int barX = canvasPoint.getX() - 15;
		final int barY = canvasPoint.getY();
		final int barWidth = HP_BAR_SIZE.width;
		final int barHeight = HP_BAR_SIZE.height;

		// Restricted by the width to prevent the bar from being too long while you are boosted above your real HP level.
		final int progressFill = (int) Math.ceil(Math.min((barWidth * ratio), barWidth));

		graphics.setColor(BAR_BG_COLOR);
		graphics.fillRect(barX, barY, barWidth, barHeight);
		graphics.setColor(BAR_FILL_COLOR);
		graphics.fillRect(barX, barY, progressFill, barHeight);

		return null;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		// Hide bar by default
		showHPBar = false;

		// If the OG Vanilla HP bar isn't currently being shown
		if (client.getLocalPlayer().getHealthScale() == -1)
		{
			// Show this one
			showHPBar = true;
		}
	}

	@Provides
	HPhiderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HPhiderConfig.class);
	}
}*/

/*
	private void updateConfig()
	{
		hideHPBar = config.hideHPBar();
	}
	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof Player)
		{
			if (player == local)
			{
				return !(drawingUI ? hideHPBar : hideLocalPlayer);
			}
			}
		return true;
	}*/
/*
	@Provides
    HPhiderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HPhiderConfig.class);
	}
	private void overrideHealthBar()
	{
		if (config.hideHPBar())
		{
			spriteManager.addSpriteOverrides(HealthbarOverride.values());
			// Reset health bar caches to apply the override
			clientThread.invokeLater(client::resetHealthBarCaches);
		}
		else
		{
			restoreHealthBars();
		}
	}

	private void restoreHealthBars()
	{
		spriteManager.removeSpriteOverrides(HealthbarOverride.values());
		clientThread.invokeLater(client::resetHealthBarCaches);
	}

 */

