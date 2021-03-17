package net.codingarea.challenges.plugin.challenges.implementation.goal;

import net.codingarea.challenges.plugin.ChallengeAPI;
import net.codingarea.challenges.plugin.challenges.type.CollectionGoal;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.lang.Prefix;
import net.codingarea.challenges.plugin.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public class CollectMostDeathsGoal extends CollectionGoal {

	public CollectMostDeathsGoal() {
		super((Object[]) DamageCause.values());
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.LAVA_BUCKET, Message.forName("item-most-deaths-goal"));
	}

	@EventHandler
	public void onDeath(@Nonnull PlayerDeathEvent event) {
		if (!isEnabled() || ChallengeAPI.isPaused()) return;

		EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
		if (lastCause == null) return;

		DamageCause cause = lastCause.getCause();
		collect(event.getEntity(), cause, () -> {
			Message.forName("death-collected").send(event.getEntity(), Prefix.CHALLENGES, StringUtils.getEnumName(cause));
			SoundSample.PLING.play(event.getEntity());
		});
	}

}