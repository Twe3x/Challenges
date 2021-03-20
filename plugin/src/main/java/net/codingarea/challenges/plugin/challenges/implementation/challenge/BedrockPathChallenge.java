package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.codingarea.challenges.plugin.challenges.type.Setting;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.LeatherArmorBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public class BedrockPathChallenge extends Setting {

	public BedrockPathChallenge() {
		super(MenuType.CHALLENGES);
	}

	@EventHandler
	public void onMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		Location location = event.getTo();
		if (BlockUtils.isSameBlockIgnoreHeight(event.getFrom(), location)) return;
		if (event.getTo() == null) return;
		event.getTo().clone().subtract(0, 0.0626, 0).getBlock().setType(Material.BEDROCK);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new LeatherArmorBuilder(Material.LEATHER_BOOTS, Message.forName("item-bedrock-path-challenge")).setColor(Color.GRAY);
	}


}