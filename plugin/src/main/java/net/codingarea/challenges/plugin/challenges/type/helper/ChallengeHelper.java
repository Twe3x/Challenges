package net.codingarea.challenges.plugin.challenges.type.helper;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.AbstractChallenge;
import net.codingarea.challenges.plugin.challenges.type.IChallenge;
import net.codingarea.challenges.plugin.challenges.type.Modifier;
import net.codingarea.challenges.plugin.language.ItemDescription;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class ChallengeHelper {

	private ChallengeHelper() {}

	public static void updateItems(@Nonnull IChallenge challenge) {
		Challenges.getInstance().getMenuManager().getMenu(challenge.getType()).updateItem(challenge);
	}

	public static void handleModifierClick(@Nonnull ChallengeMenuClickInfo info, @Nonnull Modifier modifier) {
		int newValue = modifier.getValue();
		int amount = info.isShiftClick()
				? (modifier.getValue() == modifier.getMinValue() || info.isRightClick() && modifier.getValue() == (10 - (modifier.getMinValue() - 1)) ? 9 : 10)
				: 1;
		newValue += info.isRightClick() ? -amount : amount;

		if (newValue > modifier.getMaxValue())
			newValue = modifier.getMinValue();
		if (newValue < modifier.getMinValue())
			newValue = modifier.getMaxValue();

		modifier.setValue(newValue);
		modifier.playValueChangeTitle();
		SoundSample.CLICK.play(info.getPlayer());
	}

	@Nonnull
	public static String getColoredChallengeName(@Nonnull AbstractChallenge challenge) {
		ItemBuilder item = challenge.createDisplayItem();
		if (item == null) return Message.NULL;
		ItemDescription description = item.getBuiltByItemDescription();
		if (description == null) return Message.NULL;
		String name = description.getOriginalName();
		if (name.startsWith("§8"))
			name = name.replaceFirst("§7", "§e");
		return name;
	}

	public static void breakBlock(@Nonnull Block block, @Nonnull ItemStack tool) {

		if (!Challenges.getInstance().getBlockDropManager().getDropChance(block.getType()).getAsBoolean()) return;

		List<Material> customDrops = Challenges.getInstance().getBlockDropManager().getCustomDrops(block.getType());
		if (!customDrops.isEmpty()) {
			Location location = block.getLocation().clone().add(0.5, 0, 0.5);
			customDrops.forEach(drop -> block.getWorld().dropItem(location, new ItemStack(drop)));
			block.setType(Material.AIR);
			return;
		}

		block.breakNaturally(tool);

	}

	public static void playToggleChallengeTitle(@Nonnull AbstractChallenge challenge) {
		playToggleChallengeTitle(challenge, challenge.isEnabled());
	}

	public static void playToggleChallengeTitle(@Nonnull AbstractChallenge challenge, boolean enabled) {
		Challenges.getInstance().getTitleManager().sendChallengeStatusTitle(enabled ? Message.forName("title-challenge-enabled") : Message.forName("title-challenge-disabled"), getColoredChallengeName(challenge));
	}

	public static void playChangeChallengeValueTitle(@Nonnull Modifier modifier) {
		playChangeChallengeValueTitle(modifier, modifier.getValue());
	}

	public static void playChangeChallengeValueTitle(@Nonnull AbstractChallenge modifier, @Nullable Object value) {
		Challenges.getInstance().getTitleManager().sendChallengeStatusTitle(Message.forName("title-challenge-value-changed"), getColoredChallengeName(modifier), value);
	}

	public static void playChallengeHeartsValueChangeTitle(@Nonnull AbstractChallenge challenge, int health) {
		playChangeChallengeValueTitle(challenge, (health / 2f) + " §c❤");
	}

	public static void playChallengeHeartsValueChangeTitle(@Nonnull Modifier modifier) {
		playChallengeHeartsValueChangeTitle(modifier, modifier.getValue());
	}

	public static void playChallengeSecondsValueChangeTitle(@Nonnull AbstractChallenge challenge, int seconds) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds").asString(seconds));
	}

	public static void playChallengeSecondsRangeValueChangeTitle(@Nonnull AbstractChallenge challenge, int min, int max) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-seconds-range").asString(min, max));
	}

	public static void playChallengeMinutesValueChangeTitle(@Nonnull AbstractChallenge challenge, int seconds) {
		playChangeChallengeValueTitle(challenge, Message.forName("subtitle-time-minutes").asString(seconds));
	}

	@Nonnull
	public static String[] getTimeRangeSettingsDescription(@Nonnull Modifier modifier, @Nonnegative int multiplier, @Nonnegative int range) {
		return Message.forName("item-time-seconds-range-description").asArray(modifier.getValue() * multiplier - range, modifier.getValue() * multiplier + range);
	}

	@Nonnull
	public static String[] getTimeRangeSettingsDescription(@Nonnull Modifier modifier, @Nonnegative int range) {
		return getTimeRangeSettingsDescription(modifier, 1, range);
	}

}
