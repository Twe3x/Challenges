package net.codingarea.challenges.plugin.challenges.type;

import net.codingarea.challenges.plugin.challenges.type.helper.GoalHelper;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.animation.SoundSample;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public abstract class SettingGoal extends Setting implements Goal {

	public SettingGoal() {
		super(MenuType.GOAL);
	}

	public SettingGoal(boolean enabledByDefault) {
		super(MenuType.GOAL);
		setEnabled(enabledByDefault);
	}

	@Nonnull
	public SoundSample getStartSound() {
		return SoundSample.DRAGON_BREATH;
	}

	@Override
	public final void setEnabled(boolean enabled) {
		if (isEnabled() == enabled) return;
		GoalHelper.handleSetEnabled(this, enabled);
		super.setEnabled(enabled);
	}

}