package net.codingarea.challenges.plugin.management.blocks;

import net.codingarea.challenges.plugin.utils.logging.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class BlockDropManager {

	public static final class DropPriority {

		private DropPriority() {}

		public static final byte    CUT_CLEAN = 10,
									RANDOMIZER = 5,
									CHANCE = -128;

	}

	private static abstract class RegisteredOptions<T> {

		private final SortedMap<Byte, T> optionByPriority = new TreeMap<>(Collections.reverseOrder());

		public void setOption(byte priority, @Nonnull T option) {
			optionByPriority.put(priority, option);
		}

		public void resetOption(byte priority) {
			optionByPriority.remove(priority);
		}

		@Nonnull
		public Optional<T> getFirst() {
			return optionByPriority.values().stream().findFirst();
		}

		public boolean isEmpty() {
			return optionByPriority.isEmpty();
		}

	}

	private static class RegisteredDrops extends RegisteredOptions<List<Material>> {}
	private static class RegisteredChance extends RegisteredOptions<BooleanSupplier> {}

	private final Map<Material, RegisteredDrops> drops = new HashMap<>();
	private final Map<Material, RegisteredChance> chance = new HashMap<>();

	@Nonnull
	public Collection<ItemStack> getDrops(@Nonnull Block block) {
		if (!getDropChance(block.getType()).getAsBoolean()) return new ArrayList<>();
		List<Material> customDrops = getCustomDrops(block.getType());
		if (!customDrops.isEmpty()) return customDrops.stream().map(ItemStack::new).collect(Collectors.toList());
		return block.getDrops();
	}

	@Nonnull
	public Collection<ItemStack> getDrops(@Nonnull Block block, @Nonnull ItemStack tool) {
		if (!getDropChance(block.getType()).getAsBoolean()) return new ArrayList<>();
		List<Material> customDrops = getCustomDrops(block.getType());
		if (!customDrops.isEmpty()) return customDrops.stream().map(ItemStack::new).collect(Collectors.toList());
		return block.getDrops(tool);
	}

	@Nonnull
	public Collection<ItemStack> getDrops(@Nonnull Block block, @Nonnull ItemStack tool, @Nonnull Entity entity) {
		if (!getDropChance(block.getType()).getAsBoolean()) return new ArrayList<>();
		List<Material> customDrops = getCustomDrops(block.getType());
		if (!customDrops.isEmpty()) return customDrops.stream().map(ItemStack::new).collect(Collectors.toList());
		return block.getDrops(tool, entity);
	}

	@Nonnull
	public List<Material> getCustomDrops(@Nonnull Material block) {
		RegisteredDrops option = drops.get(block);
		if (option == null) return new ArrayList<>();
		return option.getFirst().orElse(new ArrayList<>());
	}

	public void setCustomDrops(@Nonnull Material block, @Nonnull Material item, byte priority) {
		setCustomDrops(block, Collections.singletonList(item), priority);
	}

	public void setCustomDrops(@Nonnull Material block, @Nonnull List<Material> items, byte priority) {
		Logger.debug("Setting block drop for " + block + " to " + items + " at priority " + priority);

		RegisteredDrops option = this.drops.computeIfAbsent(block, key -> new RegisteredDrops());
		option.setOption(priority, items);
	}

	public void resetCustomDrop(@Nonnull Material block, byte priority) {
		Logger.debug("Resetting block drop for " + block + " at priority " + priority);

		RegisteredDrops option = drops.get(block);
		if (option == null) return;

		option.resetOption(priority);
		if (option.isEmpty()) drops.remove(block);
	}

	public void resetCustomDrops(byte priority) {
		Logger.debug("Resetting block drops at priority " + priority);

		List<Material> remove = new ArrayList<>();
		for (Entry<Material, RegisteredDrops> entry : drops.entrySet()) {

			RegisteredDrops option = entry.getValue();
			option.resetOption(priority);
			if (option.isEmpty()) remove.add(entry.getKey());
		}

		remove.forEach(drops::remove);
	}

	@Nonnull
	public BooleanSupplier getDropChance(@Nonnull Material block) {
		RegisteredChance option = chance.get(block);
		if (option == null) return () -> true;
		return option.getFirst().orElse(() -> true);
	}

	public void setDropChance(@Nonnull Material block, byte priority, @Nonnull BooleanSupplier chance) {
		Logger.debug("Setting block drop chance for " + block + " at priority " + priority);

		RegisteredChance option = this.chance.computeIfAbsent(block, key -> new RegisteredChance());
		option.setOption(priority, chance);
	}

	public void resetDropChance(byte priority) {
		Logger.debug("Resetting block drop chance at priority " + priority);

		List<Material> remove = new ArrayList<>();
		for (Entry<Material, RegisteredChance> entry : chance.entrySet()) {

			RegisteredChance option = entry.getValue();
			option.resetOption(priority);
			if (option.isEmpty()) remove.add(entry.getKey());
		}

		remove.forEach(drops::remove);
	}

}
