package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.challenges.type.Modifier;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.lang.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.MaterialWrapper;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public class DeathMessageSetting extends Modifier {

	public DeathMessageSetting() {
		super(MenuType.SETTINGS, 1, 3, 2);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.BOW, Message.forName("item-death-message-setting"));
	}

	@Nonnull
	@Override
	public ItemBuilder createSettingsItem() {
		switch (getValue()) {
			default:    return DefaultItem.disabled();
			case 2:     return DefaultItem.enabled();
			case 3:     return new ItemBuilder(MaterialWrapper.SIGN, DefaultItem.name("§6Vanilla"));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDeath(@Nonnull PlayerDeathEvent event) {
		String original = event.getDeathMessage();
		event.setDeathMessage(null);
		Player entity = event.getEntity();
		switch (getValue()) {
			default:
				return;
			case 2:
				EntityDamageEvent cause = entity.getLastDamageCause();
				if (cause != null) {
					Message.forName("death-message-cause").broadcast(Prefix.CHALLENGES, NameHelper.getName(entity), DamageDisplaySetting.getCause(cause));
				} else {
					Message.forName("death-message").broadcast(Prefix.CHALLENGES, NameHelper.getName(entity));
				}
				return;
			case 3:
				if (original != null) {
					Bukkit.broadcastMessage(Prefix.CHALLENGES + "§7" + original);
				}
		}
	}

}