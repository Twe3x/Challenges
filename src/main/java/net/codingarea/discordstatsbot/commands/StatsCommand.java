package net.codingarea.discordstatsbot.commands;

import net.codingarea.challengesplugin.manager.players.stats.PlayerStats;
import net.codingarea.challengesplugin.manager.players.stats.StatsWrapper;
import net.codingarea.challengesplugin.utils.ImageUtils;
import net.codingarea.challengesplugin.utils.Utils;
import net.codingarea.discordstatsbot.commandmanager.CommandEvent;
import net.codingarea.discordstatsbot.commandmanager.commands.Command;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;

/**
 * @author anweisen
 * Challenges developed on 07-12-2020
 * https://github.com/anweisen
 */

public class StatsCommand extends Command {

	private final BufferedImage background = ImageUtils.getImage(new File("background.png"));

	public StatsCommand() {
		super("stats", "s");
		darkImage();
	}

	private void darkImage() {
		for (int i = 0; i < background.getWidth(); i++) {
			for (int j = 0; j < background.getHeight(); j++) {
				background.setRGB(i, j, new Color(background.getRGB(i, j)).darker().getRGB());
			}
		}
	}

	@Override
	public void onCommand(CommandEvent event) {

		if (event.getArgs().length != 1) {
			event.queueReply("Benutze `" + event.getPrefix() + "stats <player>`");
			return;
		}

		if (event.getArg(0).length() > 16) {
			event.queueReply("`" + event.getArg(0) + "` ist kein gültiger Spielername");
			return;
		}

		try {

			event.getChannel().sendTyping().queue();

			File folder = new File("./images");
			if (!folder.exists()) folder.mkdirs();
			File file = new File(folder + "/stats.png");
			if (!file.exists()) file.createNewFile();

			ImageIO.write(getImage(event.getArg(0)), "png", file);
			event.getChannel().sendFile(file, "stats.png").queue();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private BufferedImage getImage(String playerName) {

		String uuid = Utils.getUUID(playerName);

		int width = 2048;
		int height = 1824;

		Color backgroundColor = Color.decode("#2C2F33");
		Color frameColor = Color.decode("#ffffff");
		Color playerNameColor = Color.decode("#D0D3D5");
		Color textColor = Color.decode("#99999A");
		Color attributeColor = Color.decode("#c4c2c2");

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();

		// Drawing background color and image if available
		graphics.setColor(backgroundColor);
		graphics.fillRect(0, 0, width, height);

		if (background != null) graphics.drawImage(background, 0, 0, width, height, null);

		// Drawing a circle wich will be around the players head
		graphics.setColor(frameColor);
		graphics.fillOval(748, 80, 552, 552);

		// Pasting the players head as circle
		Image image = ImageUtils.getHeadByUUID(uuid);
		graphics.setClip(new Ellipse2D.Float(768, 100, 512, 512));
		graphics.drawImage(image, 768, 100, 512, 512, null);
		graphics.setClip(0, 0, width, height);

		// Adding the player's name centered under the image
		graphics.setFont(new Font("Arial", Font.BOLD, 100));
		graphics.setColor(playerNameColor);
		int playerNameWidth = ImageUtils.addCenteredText(graphics, playerName, 775, width);

		// Adding a line under the player's name
		graphics.setColor(frameColor);
		int x1 = width / 2 - playerNameWidth / 2;
		graphics.fillRect(x1 - 20, 815, playerNameWidth + 40, 10);

		// Adding stats text
		int attributeTextSize = 80;
		int attributePaddingTop = 23;
		graphics.setFont(new Font("Arial", Font.BOLD, attributeTextSize));

		DecimalFormat format = new DecimalFormat("0.0");
		PlayerStats playerStats = StatsWrapper.getStatsByUUID(uuid);

		String challengesPlayed = String.valueOf(playerStats.getChallengesPlayed());
		String challengesWon = String.valueOf(playerStats.getChallengesWon());
		String damageDealt = format.format(playerStats.getDamageDealt() / 2); // Dividing by 2 because we are saving the exact
		String damageTaken = format.format(playerStats.getDamageTaken() / 2); // damage and we want to display the damage
		String blocksBroken = String.valueOf(playerStats.getBlocksBroken());
		String entityKills = String.valueOf(playerStats.getEntityKills());
		String timeSneaked = getTime(playerStats.getTimeSneaked());
		String itemsCollected = String.valueOf(playerStats.getItemsCollected());
		String jumps = String.valueOf(playerStats.getTimesJumped());

		char splitter = '»';
		int firstAttribute = 930;

		addAttribute(graphics, "Challenges gespielt", challengesPlayed, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 0, width, textColor, attributeColor);
		addAttribute(graphics, "Challenges gewonnen", challengesWon, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 1, width, textColor, attributeColor);
		addAttribute(graphics, "Schaden ausgeteilt", damageDealt, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 2, width, textColor, attributeColor);
		addAttribute(graphics, "Schaden genommen", damageTaken, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 3, width, textColor, attributeColor);
		addAttribute(graphics, "Blöcke abgebaut", blocksBroken, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 4, width, textColor, attributeColor);
		addAttribute(graphics, "Mobs getötet", entityKills, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 5, width, textColor, attributeColor);
		addAttribute(graphics, "Items aufgesammelt", itemsCollected, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 6, width, textColor, attributeColor);
		addAttribute(graphics, "Sprünge", jumps, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 7, width, textColor, attributeColor);
		addAttribute(graphics, "Zeit gesneaked", timeSneaked, splitter, firstAttribute + (attributePaddingTop + attributeTextSize) * 8, width, textColor, attributeColor);

		graphics.dispose();

		// Downscaling the image by 3 so that it can be send faster
		// Could have made the image smaller from the beginning but I don't care I'm too lazy
		BufferedImage scaled = new BufferedImage(width / 3, height / 3, BufferedImage.TYPE_INT_RGB);
		Graphics2D scaledGraphics = scaled.createGraphics();

		scaledGraphics.drawImage(bufferedImage, 0, 0, width / 3, height / 3, null);
		scaledGraphics.dispose();

		return scaled;
	}

	private void addAttribute(Graphics2D graphics, String label, String value, char splitter, int height, int imageWidth, Color textColor, Color valueColor) {
		graphics.setColor(textColor);
		ImageUtils.addTextEndingAtMid(graphics, label + " " + splitter, height, imageWidth);
		graphics.setColor(valueColor);
		graphics.drawString(" " + value, imageWidth / 2, height);
	}

	private String getTime(int seconds) {

		int minutes = seconds / 60;
		int hours = minutes / 60;
		seconds %= 60;
		minutes %= 60;

		boolean showHours = hours > 0;
		boolean showMinutes = !showHours && minutes > 0;
		boolean showSeconds = !showMinutes;

		return (showHours ? (hours > 1  ? hours + " Stunden " : hours + " Stunde") : "")
			 + (showMinutes ? (minutes > 1 ? minutes + " Minuten" : minutes + " Minute ") : "")
			 + (showSeconds ? (seconds > 1  || seconds == 0 ? seconds + " Sekunden" : seconds + " Sekunde") : "");

	}

}