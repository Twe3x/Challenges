package net.codingarea.challenges.plugin.challenges.implementation.goal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.anweisen.utilities.bukkit.utils.item.ItemUtils;
import net.anweisen.utilities.common.annotations.Since;
import net.anweisen.utilities.common.config.Document;
import net.anweisen.utilities.common.config.document.GsonDocument;
import net.anweisen.utilities.common.misc.StringUtils;
import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifierGoal;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.policy.TimerPolicy;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.management.scheduler.task.TimerTask;
import net.codingarea.challenges.plugin.management.scheduler.timer.TimerStatus;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerInventoryClickEvent;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.InventoryUtils;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.3
 */
@Since("2.1.3")
public class ForceItemBattleGoal extends SettingModifierGoal {

	private final Map<UUID, List<Material>> foundItems = new HashMap<>();
	private final Map<UUID, Material> currentItem = new HashMap<>();
	private final Map<UUID, Integer> jokerUsed = new HashMap<>();

	private Map<Player, ArmorStand> displayStands;
	private ItemStack jokerItem;
	private Material[] itemsPossibleToFind;

	public ForceItemBattleGoal() {
		super(MenuType.GOAL, 1, 20, 5);
	}

	@Override
	protected void onEnable() {
		jokerItem = new ItemBuilder(Material.BARRIER, "§cJoker").build();
		List<Material> materials = new ArrayList<>(Arrays.asList(Material.values()));
		materials.removeIf(material -> !material.isItem());
		materials.removeIf(material -> !ItemUtils.isObtainableInSurvival(material));
		itemsPossibleToFind = materials.toArray(new Material[0]);

		displayStands = new HashMap<>();

		scoreboard.setContent((board, player) -> {
			List<Player> ingamePlayers = ChallengeAPI.getIngamePlayers();
			int emptyLinesAvailable = 15 - ingamePlayers.size();

			if (emptyLinesAvailable > 0) {
				board.addLine("");
				emptyLinesAvailable--;
			}

			for (int i = 0; i < ingamePlayers.size() && i < 15; i++) {
				Player ingamePlayer = ingamePlayers.get(i);
				Material material = currentItem.get(ingamePlayer.getUniqueId());
				String display = material == null ? Message.forName("none").asString()
						: StringUtils.getEnumName(material);
				board.addLine(NameHelper.getName(ingamePlayer) + " §8» §e" + display);
			}

			if (emptyLinesAvailable > 0) {
				board.addLine("");
			}
		});
		scoreboard.show();
		broadcastFiltered(this::updateJokersInInventory);
		broadcastFiltered(this::updateDisplayStand);
	}

	@Override
	protected void onDisable() {
		if (jokerItem == null) return; // Disable through plugin disable
		broadcastFiltered(this::updateJokersInInventory);
		scoreboard.hide();
		jokerItem = null;
		itemsPossibleToFind = null;
		displayStands.values().forEach(Entity::remove);
		displayStands = null;
	}

	@Override
	protected void onValueChange() {
		broadcastFiltered(this::updateJokersInInventory);
	}

	@Override
	public void writeGameState(@NotNull Document document) {

		List<Document> playersDocuments = new LinkedList<>();
		for (Entry<UUID, Material> entry : currentItem.entrySet()) {
			List<Material> foundItems = this.foundItems.get(entry.getKey());
			int jokerUsed = this.jokerUsed.getOrDefault(entry.getKey(), 0);
			GsonDocument playerDocument = new GsonDocument();
			playerDocument.set("uuid", entry.getKey());
			playerDocument.set("currentItem", entry.getValue());
			playerDocument.set("foundItems", foundItems);
			playerDocument.set("jokerUsed", jokerUsed);
			playersDocuments.add(playerDocument);
		}

		document.set("players", playersDocuments);
	}

	@Override
	public void loadGameState(@NotNull Document document) {
		this.currentItem.clear();
		this.jokerUsed.clear();
		this.foundItems.clear();

		List<Document> players = document.getDocumentList("players");
		for (Document player : players) {
			UUID uuid = player.getUUID("uuid");

			Material currentItem = player.getEnum("currentItem", Material.class);
			if (currentItem != null) {
				this.currentItem.put(uuid, currentItem);
			}
			List<Material> foundItems = player.getEnumList("foundItems", Material.class);
			this.foundItems.put(uuid, foundItems);
			int jokerUsed = player.getInt("jokerUsed");
			this.jokerUsed.put(uuid, jokerUsed);
		}

		if (isEnabled()) {
			if (ChallengeAPI.isStarted()) {
				broadcastFiltered(this::setRandomItemIfCurrentlyNone);
			}
			scoreboard.update();
			broadcastFiltered(this::updateJokersInInventory);
			broadcastFiltered(this::updateDisplayStand);
		}
	}

	@Override
	public void getWinnersOnEnd(@NotNull List<Player> winners) {

		Bukkit.getScheduler().runTask(plugin, () -> {
			int place = 0;
			int placeValue = -1;

			List<Entry<UUID, List<Material>>> list = foundItems.entrySet().stream()
					.sorted(Comparator.comparingInt(value -> value.getValue().size()))
					.collect(Collectors.toList());
			Collections.reverse(list);

			Message.forName("force-item-battle-leaderboard").broadcast(Prefix.CHALLENGES);

			for (Entry<UUID, List<Material>> entry : list) {
				if (entry.getValue().size() > placeValue) {
					place++;
					placeValue = entry.getValue().size();
				}
				UUID uuid = entry.getKey();
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
				ChatColor color = getPlaceColor(place);
				Message.forName("force-item-battle-leaderboard-entry")
						.broadcast(Prefix.CHALLENGES, color, place, NameHelper.getName(offlinePlayer), entry.getValue().size());
			}

		});

	}

	ChatColor getPlaceColor(int place) {
		switch (place) {
			case 1:
				return ChatColor.GOLD;
			case 2:
				return ChatColor.YELLOW;
			case 3:
				return ChatColor.RED;
			default:
				return ChatColor.GRAY;
		}
	}

	@NotNull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.END_CRYSTAL, Message.forName("item-force-item-battle-goal"));
	}

	private int getUsableJokers(UUID uuid) {
		return Math.max(0, getValue() - jokerUsed.getOrDefault(uuid, 0));
	}

	private void handleItemFound(Player player) {
		Material foundItem = currentItem.get(player.getUniqueId());
		if (foundItem != null) {
			List<Material> list = foundItems
					.computeIfAbsent(player.getUniqueId(), uuid -> new LinkedList<>());
			list.add(foundItem);
			Message.forName("force-item-battle-found")
					.send(player, Prefix.CHALLENGES, StringUtils.getEnumName(foundItem));
		}
		setRandomItem(player);
	}

	private void setRandomItemIfCurrentlyNone(Player player) {
		if (currentItem.containsKey(player.getUniqueId())) {
			return;
		}
		setRandomItem(player);
	}

	private void setRandomItem(Player player) {
		Material material = globalRandom.choose(itemsPossibleToFind);
		currentItem.put(player.getUniqueId(), material);
		scoreboard.update();
		updateDisplayStand(player);
		Message.forName("force-item-battle-new-item")
				.send(player, Prefix.CHALLENGES, StringUtils.getEnumName(material));
		SoundSample.PLING.play(player);
	}

	private void handleJokerUse(Player player) {
		int jokerUsed = this.jokerUsed.getOrDefault(player.getUniqueId(), 0);
		jokerUsed++;
		this.jokerUsed.put(player.getUniqueId(), jokerUsed);
		handleItemFound(player);
		updateJokersInInventory(player);
	}

	private void updateJokersInInventory(Player player) {
		PlayerInventory inventory = player.getInventory();
		boolean enabled = isEnabled() && ChallengeAPI.isStarted();
		int usableJokers = getUsableJokers(player.getUniqueId());

		int jokersInInventory = 0;

		for (ItemStack itemStack : new LinkedList<>(Arrays.asList(inventory.getContents()))) {
			if (jokerItem.isSimilar(itemStack)) {
				if (!enabled) {
					inventory.removeItem(itemStack);
				} else {
					jokersInInventory += itemStack.getAmount();
					if (jokersInInventory >= usableJokers) {
						int jokersToSubtract = jokersInInventory - usableJokers;
						jokersInInventory -= jokersToSubtract;
						itemStack.setAmount(itemStack.getAmount() - jokersToSubtract);
					}
				}
			}

		}

		if (enabled) {
			if (jokersInInventory < usableJokers) {
				ItemStack clone = jokerItem.clone();
				clone.setAmount(usableJokers - jokersInInventory);
				InventoryUtils.dropOrGiveItem(inventory, player.getLocation(), clone);
			}
		}

	}

	private void updateDisplayStand(Player player) {
		ArmorStand armorStand = displayStands.computeIfAbsent(player, player1 -> {
			World world = player1.getWorld();
			ArmorStand entity = (ArmorStand) world
					.spawnEntity(player1.getLocation().clone().add(0, 1, 0), EntityType.ARMOR_STAND);
			entity.setInvisible(true);
			entity.setInvulnerable(true);
			entity.setGravity(false);
			entity.setMarker(true);
			return entity;
		});
		Material item = currentItem.get(player.getUniqueId());
		if (item == null) {
			item = Material.AIR;
		}
		armorStand.getEquipment().setHelmet(new ItemStack(item));
		armorStand.teleport(player.getLocation().clone().add(0, 1, 0));
	}

	@TimerTask(status = TimerStatus.RUNNING, async = false)
	public void onStart() {
		broadcastFiltered(this::setRandomItemIfCurrentlyNone);
		broadcastFiltered(this::updateJokersInInventory);
	}

	@EventHandler
	public void onStatusChange(PlayerIgnoreStatusChangeEvent event) {
		if (!shouldExecuteEffect()) return;
		if (event.isNotIgnored()) {
			setRandomItemIfCurrentlyNone(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		setRandomItemIfCurrentlyNone(event.getPlayer());
	}

	@ScheduledTask(ticks = 1, async = false, timerPolicy = TimerPolicy.ALWAYS)
	public void onTick() {
		if (!isEnabled()) {
			return;
		}
		for (Player player : displayStands.keySet()) {
			updateDisplayStand(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onClick(PlayerInventoryClickEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		if (event.getClickedInventory() == null) {
			return;
		}
		if (event.getCurrentItem() == null) {
			return;
		}
		Material material = currentItem.get(event.getPlayer().getUniqueId());
		if (material == null) {
			return;
		}
		if (material == event.getCurrentItem().getType()) {
			handleItemFound(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		Material material = currentItem.get(event.getPlayer().getUniqueId());
		if (material == null) {
			return;
		}
		if (material == event.getItem().getItemStack().getType()) {
			handleItemFound(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_AIR
				&& event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (jokerItem.isSimilar(event.getItem())) {
			handleJokerUse(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDropItem(PlayerDropItemEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		if (jokerItem.isSimilar(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (!shouldExecuteEffect()) {
			return;
		}
		if (ignorePlayer(event.getPlayer())) {
			return;
		}
		if (jokerItem.isSimilar(event.getItemInHand())) {
			event.setCancelled(true);
		}
	}

}
