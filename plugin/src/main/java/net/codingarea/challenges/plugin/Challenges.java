package net.codingarea.challenges.plugin;

import net.anweisen.utilities.bukkit.core.BukkitModule;
import net.anweisen.utilities.common.version.Version;
import net.codingarea.challenges.plugin.challenges.custom.settings.CustomSettingsLoader;
import net.codingarea.challenges.plugin.content.loader.*;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager;
import net.codingarea.challenges.plugin.management.bstats.MetricsLoader;
import net.codingarea.challenges.plugin.management.challenges.ChallengeLoader;
import net.codingarea.challenges.plugin.management.challenges.ChallengeManager;
import net.codingarea.challenges.plugin.management.challenges.CustomChallengesLoader;
import net.codingarea.challenges.plugin.management.cloud.CloudSupportManager;
import net.codingarea.challenges.plugin.management.database.DatabaseManager;
import net.codingarea.challenges.plugin.management.files.ConfigManager;
import net.codingarea.challenges.plugin.management.inventory.PlayerInventoryManager;
import net.codingarea.challenges.plugin.management.menu.MenuManager;
import net.codingarea.challenges.plugin.management.scheduler.ScheduleManager;
import net.codingarea.challenges.plugin.management.scheduler.timer.ChallengeTimer;
import net.codingarea.challenges.plugin.management.server.GeneratorWorldPortalManager;
import net.codingarea.challenges.plugin.management.server.GameWorldStorage;
import net.codingarea.challenges.plugin.management.server.ScoreboardManager;
import net.codingarea.challenges.plugin.management.server.ServerManager;
import net.codingarea.challenges.plugin.management.server.TitleManager;
import net.codingarea.challenges.plugin.management.server.WorldManager;
import net.codingarea.challenges.plugin.management.stats.StatsManager;
import net.codingarea.challenges.plugin.spigot.command.*;
import net.codingarea.challenges.plugin.spigot.listener.*;
import net.codingarea.challenges.plugin.utils.bukkit.command.ForwardingCommand;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public final class Challenges extends BukkitModule {

	private static Challenges instance;

	@Nonnull
	public static Challenges getInstance() {
		return instance;
	}

	private PlayerInventoryManager playerInventoryManager;
	private ScoreboardManager scoreboardManager;
	private ChallengeManager challengeManager;
	private BlockDropManager blockDropManager;
	private ChallengeLoader challengeLoader;
	private CustomChallengesLoader customChallengesLoader;
	private CustomSettingsLoader customSettingsLoader;
	private DatabaseManager databaseManager;
	private CloudSupportManager cloudSupportManager;
	private ServerManager serverManager;
	private ConfigManager configManager;
	private ScheduleManager scheduler;
	private StatsManager statsManager;
	private WorldManager worldManager;
	private TitleManager titleManager;
	private MenuManager menuManager;
	private ChallengeTimer timer;
	private LoaderRegistry loaderRegistry;
	private MetricsLoader metricsLoader;
	private GameWorldStorage gameWorldStorage;
	private GeneratorWorldPortalManager generatorWorldPortalManager;

	@Override
	protected void handleLoad() {
		checkConfig();
		createManagers();
		loadManagers();
	}

	private void checkConfig() {
		if (getConfigDocument().getVersion("config-version", Version.parse("1.0")).isOlderThan(Version.parse("2.0"))) {
			saveResource("config.yml", true);
			reloadConfig();
			getLogger().info("A deprecated config was found and replaced with a new one");
		}
	}

	@Override
	protected void handleEnable() {
		enableManagers();

		registerCommands();
		registerListeners();
	}

	private void createManagers() {

		configManager = new ConfigManager();
		configManager.loadConfigs();

		loaderRegistry = new LoaderRegistry(
				new LanguageLoader(),
				new PrefixLoader(),
				new UpdateLoader(),
				new ServiceLoader()
		);

		databaseManager = new DatabaseManager();
		worldManager = new WorldManager();
		serverManager = new ServerManager();
		scheduler = new ScheduleManager();
		scoreboardManager = new ScoreboardManager();
		cloudSupportManager = new CloudSupportManager();
		titleManager = new TitleManager();
		timer = new ChallengeTimer();
		blockDropManager = new BlockDropManager();
		challengeManager = new ChallengeManager();
		challengeLoader = new ChallengeLoader();
		customChallengesLoader = new CustomChallengesLoader();
		customSettingsLoader = new CustomSettingsLoader();
		menuManager = new MenuManager();
		playerInventoryManager = new PlayerInventoryManager();
		statsManager = new StatsManager();
		metricsLoader = new MetricsLoader();
		gameWorldStorage = new GameWorldStorage();
		generatorWorldPortalManager = new GeneratorWorldPortalManager();

	}

	private void loadManagers() {
		loaderRegistry.load();
		worldManager.load();
	}

	private void enableManagers() {
		gameWorldStorage.enable();
		challengeLoader.enable();
		customSettingsLoader.enable();
		databaseManager.enable();
		worldManager.enable();
		timer.loadSession();
		timer.enable();
		challengeManager.enable();
		statsManager.register();
		scheduler.start();
		metricsLoader.start();

		loaderRegistry.enable();
	}

	private void registerCommands() {
		registerCommand(new HelpCommand(), "help");
		registerCommand(new ChallengesCommand(), "challenges");
		registerCommand(new TimerCommand(), "timer");
		registerCommand(new ForwardingCommand("timer start"), "start");
		registerCommand(new ForwardingCommand("timer pause"), "pause");
		registerCommand(new ResetCommand(), "reset");
		registerCommand(new StatsCommand(), "stats");
		registerCommand(new LeaderboardCommand(), "leaderboard");
		registerCommand(new DatabaseCommand(), "database");
		registerCommand(new GamestateCommand(), "gamestate");
		registerCommand(new VillageCommand(), "village");
		registerCommand(new HealCommand(), "heal");
		registerCommand(new SearchCommand(), "search");
		registerListenerCommand(new InvseeCommand(), "invsee");
		registerCommand(new FlyCommand(), "fly");
		registerCommand(new WorldCommand(), "world");
		registerListenerCommand(new BackCommand(), "back");
		registerCommand(new GamemodeCommand(), "gamemode");
		registerCommand(new ForwardingCommand("gamemode 0", false), "gms");
		registerCommand(new ForwardingCommand("gamemode 1", false), "gmc");
		registerCommand(new ForwardingCommand("gamemode 2", false), "gma");
		registerCommand(new ForwardingCommand("gamemode 3", false), "gmsp");
		registerCommand(new WeatherCommand(), "weather");
		registerCommand(new ForwardingCommand("weather sun"), "sun");
		registerCommand(new ForwardingCommand("weather rain"), "rain");
		registerCommand(new ForwardingCommand("weather thunder"), "thunder");
		registerCommand(new TimeCommand(), "time");
		registerCommand(new ForwardingCommand("time set day"), "day");
		registerCommand(new ForwardingCommand("time set night"), "night");
		registerCommand(new ForwardingCommand("time set noon"), "noon");
		registerCommand(new ForwardingCommand("time set midnight"), "midnight");
	}

	private void registerListeners() {
		registerListener(
				new PlayerConnectionListener(),
				new RestrictionListener(),
				new ExtraWorldRestrictionListener(),
				new CheatListener(),
				new BlockDropListener(),
				new CustomEventListener(),
				new HelpListener(),
				new ChatInputListener(),
				new GeneratorWorldsListener(),
				new ScoreboardUpdateListener()
		);
	}

	@Override
	protected void handleDisable() {
		boolean shutdownBecauseOfReset = worldManager != null && worldManager.isShutdownBecauseOfReset();
		boolean restoreDefaultsOnReset = getConfigDocument().getBoolean("restore-defaults-on-reset");

		if (playerInventoryManager != null) playerInventoryManager.handleDisable();
		if (timer != null && !shutdownBecauseOfReset) timer.saveSession(false);
		if (scheduler != null) scheduler.stop();
		if (loaderRegistry != null) loaderRegistry.disable();
		if (databaseManager != null) databaseManager.disconnectIfConnected();
		if (scoreboardManager != null) scoreboardManager.disable();

		if (challengeManager != null) {
			challengeManager.shutdownChallenges();
			if (shutdownBecauseOfReset && restoreDefaultsOnReset) {
				challengeManager.restoreDefaults();
			}
			challengeManager.saveLocalSettings(false);
			challengeManager.saveLocalCustomChallenges(false);

			if (!shutdownBecauseOfReset) {
				challengeManager.saveGamestate(false);
			}
			challengeManager.clearChallengeCache();
		}
	}

	public GeneratorWorldPortalManager getGeneratorWorldManager() {
		return generatorWorldPortalManager;
	}

	public GameWorldStorage getGameWorldStorage() {
		return gameWorldStorage;
	}

	@Nonnull
	public CustomSettingsLoader getCustomSettingsLoader() {
		return customSettingsLoader;
	}

	@Nonnull
	public CustomChallengesLoader getCustomChallengesLoader() {
		return customChallengesLoader;
	}

	@Nonnull
	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	@Nonnull
	public MenuManager getMenuManager() {
		return menuManager;
	}

	@Nonnull
	public ChallengeTimer getChallengeTimer() {
		return timer;
	}

	@Nonnull
	public ServerManager getServerManager() {
		return serverManager;
	}

	@Nonnull
	public ScheduleManager getScheduler() {
		return scheduler;
	}

	@Nonnull
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Nonnull
	public PlayerInventoryManager getPlayerInventoryManager() {
		return playerInventoryManager;
	}

	@Nonnull
	public WorldManager getWorldManager() {
		return worldManager;
	}

	@Nonnull
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	@Nonnull
	public StatsManager getStatsManager() {
		return statsManager;
	}

	@Nonnull
	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	@Nonnull
	public CloudSupportManager getCloudSupportManager() {
		return cloudSupportManager;
	}

	@Nonnull
	public ChallengeLoader getChallengeLoader() {
		return challengeLoader;
	}

	@Nonnull
	public BlockDropManager getBlockDropManager() {
		return blockDropManager;
	}

	@Nonnull
	public TitleManager getTitleManager() {
		return titleManager;
	}

	@Nonnull
	public LoaderRegistry getLoaderRegistry() {
		return loaderRegistry;
	}

}
