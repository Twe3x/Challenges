package net.codingarea.challenges.plugin.management.stats;

import net.anweisen.utilities.database.exceptions.DatabaseException;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.policy.ChallengeStatusPolicy;
import net.codingarea.challenges.plugin.spigot.listener.StatsListener;
import net.codingarea.challenges.plugin.utils.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class StatsManager implements Listener {

	private final boolean enabled;
	private final boolean noStatsAfterCheating;

	private final Map<UUID, PlayerStats> cache = new ConcurrentHashMap<>();

	public StatsManager() {
		enabled = Challenges.getInstance().getConfigDocument().getBoolean("save-player-stats") && Challenges.getInstance().getDatabaseManager().getDatabase() != null;
		noStatsAfterCheating = Challenges.getInstance().getConfigDocument().getBoolean("no-stats-after-cheating");
	}

	public void register() {
		if (enabled) {
			Challenges.getInstance().getScheduler().register(this);
			Challenges.getInstance().registerListener(this, new StatsListener());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@Nonnull PlayerQuitEvent event) {
		if (Challenges.getInstance().getWorldManager().isShutdownBecauseOfReset()) return;
		PlayerStats cached = cache.remove(event.getPlayer().getUniqueId());
		if (cached == null) return;
		store(event.getPlayer().getUniqueId(), cached);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		PlayerStats stats = getStats(event.getPlayer().getUniqueId());
		cache.put(event.getPlayer().getUniqueId(), stats);
	}

	@ScheduledTask(ticks = 30 * 20, challengePolicy = ChallengeStatusPolicy.ALWAYS)
	public void storeCached() {
		for (Entry<UUID, PlayerStats> entry : cache.entrySet()) {
			store(entry.getKey(), entry.getValue());
		}
	}

	private void store(@Nonnull UUID uuid, @Nonnull PlayerStats stats)  {
		try {
			Challenges.getInstance().getDatabaseManager().getDatabase()
					.insertOrUpdate("challenges")
					.where("uuid", uuid)
					.set("stats", stats.asDocument())
					.execute();
			Logger.debug("Saved stats for uuid '" + uuid + "': " + stats);
		} catch (DatabaseException ex) {
			Logger.severe("Could not save player stats for uuid '" + uuid + "'", ex);
		}
	}

	@Nonnull
	public PlayerStats getStats(@Nonnull UUID uuid) {
		PlayerStats cached = cache.get(uuid);
		if (cached != null) return cached;

		try {
			PlayerStats stats = getStatsFromDatabase(uuid);
			cache.put(uuid, stats);
			Logger.debug("Loaded stats for uuid '" + uuid + "': " + stats);
			return stats;
		} catch (DatabaseException ex) {
			Logger.severe("Could not get player stats for uuid " + uuid, ex);
			return new PlayerStats(uuid);
		}
	}

	@Nonnull
	private PlayerStats getStatsFromDatabase(@Nonnull UUID uuid) throws DatabaseException {
		return Challenges.getInstance().getDatabaseManager().getDatabase()
				.query("challenges")
				.select("stats")
				.where("uuid", uuid)
				.execute().first()
				.map(result -> new PlayerStats(uuid, result.getDocument("stats")))
				.orElse(new PlayerStats(uuid));
	}

	@Nonnull
	private List<PlayerStats> getAllStats() throws DatabaseException {
		return Challenges.getInstance().getDatabaseManager().getDatabase()
				.query("challenges")
				.select("uuid", "stats")
				.execute().all()
				.filter(result -> result.getUUID("uuid") != null)
				.map(result -> new PlayerStats(result.getUUID("uuid"), result.getDocument("stats")))
				.collect(Collectors.toList());
	}

	@Nonnull
	public LeaderboardInfo getLeaderboardInfo(@Nonnull UUID uuid) {
		try {
			List<PlayerStats> stats = getAllStats();
			LeaderboardInfo info = new LeaderboardInfo();
			for (Statistic statistic : Statistic.values()) {
				int place = determineIndex(new ArrayList<>(stats), PlayerStats::getPlayer, uuid, Comparator.<PlayerStats>comparingDouble(value -> value.getStatisticValue(statistic)).reversed()) + 1;
				info.setPlace(statistic, place);
			}

			return info;
		} catch (DatabaseException ex) {
			Logger.severe("Could not get player leaderboard information for uuid " + uuid, ex);
			return new LeaderboardInfo();
		}
	}

	private <T, U> int determineIndex(@Nonnull List<T> list, @Nonnull Function<T, U> extractor, @Nonnull U target, @Nonnull Comparator<T> sort) {
		list.sort(sort);
		int index = 0;
		for (T t : list) {
			U u = extractor.apply(t);
			if (target.equals(u)) return index;
			index++;
		}
		return index;
	}

	public boolean isEnabled() {
		return enabled && Challenges.getInstance().getDatabaseManager().isConnected();
	}

	public boolean isNoStatsAfterCheating() {
		return noStatsAfterCheating;
	}

}
