package net.codingarea.challenges.plugin.challenges.type;

import net.codingarea.challenges.plugin.challenges.type.helper.ChallengeHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.menu.info.ChallengeMenuClickInfo;
import net.codingarea.challenges.plugin.management.scheduler.Scheduled;
import net.codingarea.challenges.plugin.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.logging.Logger;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public abstract class TimedChallenge extends SettingModifier {

	private int secondsUntilActivation;
	private boolean timerStatus = false;
	private boolean startedBefore = false;

	public TimedChallenge(@Nonnull MenuType menu) {
		super(menu);
	}

	public TimedChallenge(@Nonnull MenuType menu, int max) {
		super(menu, max);
	}

	public TimedChallenge(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max);
	}

	public TimedChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue);
	}

	@Override
	public void setValue(int value) {
		super.setValue(value);
		restartTimer();
	}

	@Scheduled(ticks = 20)
	public void onSecond() {

		if (!startedBefore) {
			restartTimer();
		}

		if (timerStatus) {

			if (getTimerCondition()) {
				secondsUntilActivation--;
				if (secondsUntilActivation <= 0) {
					secondsUntilActivation = 0;
					timerStatus = false;
					onTimeActivation();
				}
			} else {
				Logger.debug("getTimerCondition returned false for " + this.getClass().getSimpleName());
			}
		}

	}

	protected boolean getTimerCondition() {
		return true;
	}

	protected abstract int getSecondsUntilNextActivation();

	protected void restartTimer(int seconds) {
		Logger.debug("Restarting timer of " + this.getClass().getSimpleName() + " with " + seconds + " second(s)");

		startedBefore = true;
		secondsUntilActivation = seconds;
		timerStatus = true;
	}

	protected void restartTimer() {
		restartTimer(getSecondsUntilNextActivation());
	}

	protected abstract void onTimeActivation();

	@Nonnull
	@Override
	public ItemStack getSettingsItem() {
		return DefaultItem.status(isEnabled()).build();
	}

	@Nonnull
	@Override
	public ItemStack getDisplayItem() {
		return createDisplayItem().amount(getValue()).build();
	}

	@Override
	public void handleClick(@Nonnull ChallengeMenuClickInfo event) {
		if (event.isUpperItemClick() && isEnabled()) {
			ChallengeHelper.handleModifierClick(event, this);
		} else {
			setEnabled(!isEnabled());
			SoundSample.playEnablingSound(event.getPlayer(), isEnabled());
			playStatusUpdateTitle();
		}
	}

}
