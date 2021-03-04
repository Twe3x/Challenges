package net.codingarea.challenges.plugin.utils.misc;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class StringUtils {

	private StringUtils() {
	}

	@Nonnull
	public static String format(@Nonnull String sequence, @Nonnull String... args) {
		char start = '{', end = '}';
		boolean inArgument = false;
		StringBuilder argument = new StringBuilder();
		StringBuilder builder = new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (c == end && inArgument) {
				inArgument = false;
				try {
					int arg = Integer.parseInt(argument.toString());
					builder.append(args[arg]);
				} catch (NumberFormatException | IndexOutOfBoundsException ex) {
					Logger.warn("Invalid argument index '" + argument + "'");
					builder.append(start).append(argument).append(end);
				}
				argument = new StringBuilder();
				continue;
			}
			if (c == start && !inArgument) {
				inArgument = true;
				continue;
			}
			if (inArgument) {
				argument.append(c);
				continue;
			}
			builder.append(c);
		}
		return builder.toString();
	}

	@Nonnull
	public static String[] format(@Nonnull String[] array, @Nonnull String... args) {
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = format(array[i], args);
		}
		return result;
	}

	@Nonnull
	public static String getArrayAsString(@Nonnull String[] array) {
		StringBuilder builder = new StringBuilder();
		for (String string : array) {
			if (builder.length() != 0) builder.append('\n');
			builder.append(string);
		}
		return builder.toString();
	}

	@Nonnull
	public static String[] getStringAsArray(@Nonnull String string) {
		return string.split("\n");
	}

	@Nonnull
	public static <T> String getIterableAsString(@Nonnull Iterable<T> iterable, @Nonnull Function<T, String> mapper) {
		StringBuilder builder = new StringBuilder();
		for (T t : iterable) {
			if (builder.length() > 0) builder.append(", ");
			String string = mapper.apply(t);
			builder.append(string);
		}
		return builder.toString();
	}

	public static boolean isValidColorCode(char code) {
		for (ChatColor color : ChatColor.values()) {
			if (color.isColor() && color.getChar() == code)
				return true;
		}
		return false;
	}

	public static boolean isValidColorCode(@Nonnull String code) {
		if (code.length() != 1) return false;
		return isValidColorCode(code.toCharArray()[0]);
	}

}
