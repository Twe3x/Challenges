package net.codingarea.challengesplugin.manager.lang;

import net.codingarea.challengesplugin.manager.lang.LanguageManager.Language;
import net.codingarea.challengesplugin.utils.Replacement;

import java.util.ArrayList;
import java.util.List;

import static net.codingarea.challengesplugin.utils.Replacement.replace;

/**
 * @author anweisen & Dominik
 * Challenges developed on 06-12-2020
 * https://github.com/anweisen
 * https://github.com/KxmischesDomi
 */

public enum ItemTranslation {

	ANVIL_RAIN(a("§8Anvilrain", "§8Ambossregen"), b(a("The setted count of *anvils* ", "will fall per *second*"), a("Die eingestellte *Anzahl* von *Ambossen*", "wird jede *Sekunde* vom Himmel fallen"))),
	BACKPACK(a("§6Backpack", "§6Rucksack"), b(a("With */bp* you can open", "a *private* or *team* backpack"), a("Mit */bp* kannst du einen", "*privaten* oder *gemeinsamen* Rucksack öffnen"))),
	BEDROCK_PATH(a("§8Bedrock path"), b(a("Every block you stand on will", "be transformed to *bedrock*"), a("Jeder Block, auf dem du stehst,", "wird zu *Bedrock*"))),
	BEDROCK_WALL(a("§8Bedrock walls"), b(a("Behind you *giant bedrock walls*", "will be spawned"), a("Hinter dir werden *riesige Bedrock-Wände* spawnen"))),
	BLOCK_BREAK_DAMAGE(a("§8Block break damage"), b(a("You will get the setted amount", "of *damage* if you *break* a block"), a("Du bekommst die gesetzte *Anzahl* an *Schaden*", "wenn du einen *Block abbaust*"))),
	BLOCK_DROP_RANDOMIZER(a("§4Block randomizer"), b(a("If you break a *block* a", "random *item* will be dropped"), a("Wenn du einen *Block* abbaust, ", "wird ein zufälliges Item gedroppt."))),
	BREAK_BLOCKS(a("§cBreak blocks", "§cBlöcke abbauen"), b(a("Mine the most *blocks* to win"), a("baue die meisten *Blöcke* ab", "um zu gewinnen"))),
	CHUNK_DECONSTRUCT(a("§bChunk Deconstruction"), b(a("*Chunks* with players in are *degrading* from *above*.", "The *setted number* says how fast"), a("*Chunks*, in denen sich Spieler befinden, *bauen* sich von *oben aus* ab.", "Die *eingestellte Zahl* sagt in welchem *Sekundentakt*"))),
	CHUNK_RACE(a(""), b(a("", ""), a("", ""))),
	COLLECT_ITEMS(a("§dCollect items", "§dItems sammeln"), b(a("Collect as many *different items*", "as you can in order to *win*"), a("Sammel die meisten *verschiedenen Items*", "um zu *gewinnen*"))),
	COLLECT_WOOD(a("§bCollect wood"), b(a("Find all *6* types of *wood*"), a("Finde alle *6 Arten* von *Holz*"))),
	CRAFTING_RANDOMIZER(a("§6Crafting Randomizer"), b(a("When you *craft* something", "you will get a *random item*"), a("Wenn du ein Item *craftest*,", "bekommst du ein *zufälliges Item*"))),
	DAMAGE_DISPLAY(a("§eDamage display"), b(a("When somebody takes *damage* it ", "will be shown *in chat*"), a("Wenn jemand *Schaden bekommt*, wird", "es im *Chat* angezeigt"))),
	DAMAGE_INVENTORY_CLEAR(a("§cDamage Inventory Clear"), b(a("If a player takes *damage* the *inventories*", "of *all players* will be cleared"), a("Wenn ein Spieler *Schaden* erleidet, wird das", "*Inventar* von *allen Spielern* geleert"))),
	DAMAGE_MULTIPLIER(a("§cDamage multiplier"), b(a("Sets how much *damage* the", "player receives should *multiply*"), a("Stellt ein, um wie viel sich der *Schaden*,", "den der Spieler bekommt, *multiplizieren* soll"))),
	DAMAGE_RULE(a("Angelo ist cool und Domi auch (nicht) hehe"), b(a("You wont take *%info%*"), a("Du wirst kein *%info%* bekommen"))),
	DIFFICULTY(a("§bDifficulty"), b(a("Sets the *difficulty*"), a("Stell die *Schwierigkeit* ein"))),
	DUPED_SPAWN(a("§cDuped Spawn", "§cDoppeltes Spawning"), b(a("When a mob *spawns*,", "it spawnes *twice*"), a("Wenn ein Monster *spawnt*,", "spawnt es *zweimal*"))),
	FLOOR_HOLE(a("§cFloor hole"), b(a("When you *walk*, every block from *top* to *bottom*", "will be destroyed after a *certain time*"), a("Jeder Block, auf dem du läufst, wird von oben bis unten", "nach einer *bestimmten Zeit* hinter dir zerstört"))),
	FORCE_BLOCK(a("§bForce block"), b(a("You *must* be on *specific blocks*", "at a *specific time*"), a("Du musst zu *bestimmten Zeiten*", "auf *bestimmten Blöcken* sein"))),
	FORCE_CORDS(a("§bForce coords"), b(a("You *must* be on *specific coordinates*", "at a *specific time*"), a("Du musst zu *bestimmten Zeiten*", "auf *bestimmten Koordinaten* sein"))),
	FORCE_HIGH(a("§bForce height"), b(a("You *must* be on *specific heights*", "at a *specific time*"), a("Du musst zu *bestimmten Zeiten*", "auf *bestimmten Höhen* sein"))),
	HYDRA(a("§5Hydra"), b(a("When you kill a *mob*,", "it will spawn *twice*"), a("Wenn du ein *Mob* tötest,", "spawnt es *zweimal*"))),
	ITEM_ONE_TIME_USE(a("§4One durability", "§4Eine Haltbarkeit"), b(a("Tools will be *destroyed* after one use"), a("Items gehen nach *einer* Benutzung *kaputt*"))),
	JUMP_HIGHER(a("§aHigh jumps"), b(a("Every time you *jump* you will", "jump *higher* at the next time"), a("Wenn du *springst* wirst du beim nächsten", "mal *höher* springen"))),
	KEEP_INVENTORY(a("§5KeepInventory"), b(a("You won't *lose*", "your *inventory* if you *die*"), a("Du verlierst keine *Items*,", "wenn du *stirbst*"))),
	KILL_WHITER(a("§5Kill a wither", "§5Töte einen Wither"), b(a("Summon and *kill* a *wither* to *win*"), a("Erschaffe und *tote* einen *Wither*"))),
	Kill_ENDERDRAGON(a("§5Kill the dragon", "§5Töte den Enderdrachen"), b(a("*Kill* the *enderdragon* to win"), a("Töte den *Enderdrachen* um tu *gewinnen"))),
	MATERIAL_RULE(a("Wenn du das hier liest solltest du das Plugin updaten xd"), b(a("You are not able to use *%info%*"), a("Du wirst kein *%info%* bekommen"))),
	MAX_HEALTH(a("§aMax health", "§aMaximale Herzen"), b(a("Sets how many *health*", "you have"), a("Stellt ein, wie viele", "*Herzen* man hat"))),
	MINE_DIAMONDS(a("§bMine diamonds"), b(a("Mine the most *diamonds* to win"), a("Baue die meisten *Diamanten*", "ab um zu *gewinnen*"))),
	MOB_RANDOMIZER(a("§dMob randomizer"), b(a("Mobs will be *switched*.", "ex: instead of a *creeper* a *ghast* will spawn"), a("Mobs sind *getauscht*.", "z.B.: Anstatt von eines *Creepers* wird", "ein *Ghast* spawnen"))),
	MOST_DEATHS(a("§6Most deaths", "§6Meiste Tode"), b(a("*Die* as often as you can in *different* ways"), a("Sammel die meisten *verschiedene Tode*"))),
	NO_HUNGER(a("§cNo hunger", "§cKein Hunger"), b(a("You won't get any *hunger*"), a("Du bekommst keinen *Hunger*"))),
	NO_ITEM_DAMAGE(a("§8No item damage", "§8Kein Itemschaden"), b(a("All *items* are *unbreakable*"), a("*Items* werden *unzerstörbar* sein"))),
	NO_JUMP(a("§6No jump"), b(a("When you *jump* you get the setted", "amount of *damage*"), a("Wenn du *springst* bekommst du die", "die gesetzte Anzahl *Schaden*"))),
	NO_SNEAK(a("§cNo sneak"), b(a("When you *sneak* you get the setted", "amount of *damage*"), a("Wenn du *sneakst* bekommst du die", "die gesetzte Anzahl *Schaden*"))),
	NO_TRADING(a("§2No trading", "§2Kein Handeln"), b(a("You won't be able to", "*trade* with *villagers*"), a("Du kannst *nicht* mit", "*Villagern* handeln"))),
	NO_XP(a("§aNo XP", "§aKeine XP"), b(a("If you pickup *XP*, you will *die*"), a("Wenn du *XP* aufsammelst, *stirbst* du"))),
	ONE_TEAM_LIVE(a("§8One team life", "§8Ein Team-Leben"), b(a("Sets if the *team* should have", "only *one life*"), a("Stellt ein, ob alle alle *Spieler*", "nur ein gemeinsames Leben haben"))),
	ONLY_DIRT(a("§cOnly dirt"), b(a("You may *only* stand on *dirt*.", " Otherwise you will *die*"), a("Wenn du nicht auf *Erde* stehst,", "dann *stirbst* du"))),
	PLAYER_GLOW(a("§fPlayerglow"), b(a("*Players* are *visible* through *walls*"), a("*Spieler* sind durch die *Wand sichtbar*"))),
	PVP(a("§9PvP"), b(a("This toggles the ability", "to *damage* other *players*"), a("Schaltet *PvP* ein/aus"))),
	REGENERATION(a("§cRegeneration"), b(a("Sets how and whether to", "*regenerate*"), a("Stellt ein, wie und ob man", "*regeneriert*"))),
	RESPAWN(a("§4Respawn"), b(a("Sets if the *player*", "should *respawn*"), a("Stellt ein, ob der *Spieler*", "*wiederbelebt* werden soll"))),
	REVERSE_DAMAGE(a("§cReversed damage"), b(a("If you *damage* a mob", "you will get the same *damage*"), a("Wenn du ein *Mob* schlägst dann", "bekommst du den gleichen *Schaden* auch"))),
	SOUP_HEALTH(a("§cSoup healing", "§cSuppenheilung"), b(a("A *mushroom soup* gives", "you *4 hearts* healing"), a("*Pilzsuppen heilen* dich um *4 Herzen*"))),
	SPLIT_HEALTH(a("§dSplit health"), b(a("Sets if all *players*", "have the same *health*"), a("Stellt ein, ob alle *Spieler*", "die gleiche *Gesundheit* haben"))),
	THE_FLOOR_IS_LAVA(a("§6The floor is lava", "§6Der Boden ist Lava"), b(a("*Every block* you stand on will be", "transformed to *magma* and later to *lava*"), a("Alle *Blöcke* unter dir verwandeln sich", "erst zu *Magma* und dann zu *Lava*"), a())),
	TIMBER(a("§bTimber"), b(a("You can destroy *trees* by", "destroying *one block* of the tree"), a("Du kannst einen *Baum* zerstören,", "indem du *einen Block* des Baums abbaust"))),
	UP_COMMAND(a("§dUpCommand"), b(a("With */top* you can teleport", "yourself to the *top* of the world"), a("Mit */top* kannst du dich", "zur *Oberfläche* teleportieren"))),
	WATER_MLG(a("§9Water MLG"), b(a("Every few *minutes* you have to", "do a *WaterMLG*. If you *die*, you *lose*"), a("Du musst alle *paar Minuten* einen *Wasser-MLG* machen.", "Wenn diesen nicht schaffst, *stirbst* du"))),
	GUESS_THE_FLAG(a("§1Guess the flag"), b(a("You have to *guess* a random *flag* in", "the *chat* or you *die*"), a("Du musst alle paar Minuten", "eine *Flagge* im *Chat* erraten"))),
	ACHIEVMENT_DAMAGE(a("§cAchievment damage"), b(a("Every time you make an *achievment*", "you will get *0.5 hearts* damage"), a("Du bekommst du jedes neue *Achievment*", "ein *halbes Herz* Schaden"))),
	NO_HIT_DELAY(a("§fNoHitDelay"), b(a("You will *instantly* be able to", "get *damaged* after you got damaged"), a("Nachdem du Schaden bekommen hast kannst", "du *sofort* wieder *Schaden bekommen*"))),
	SNAKE(a("§9Snake"), b(a("*Every* player draws", "a *deadly* line behind him"), a("*Jeder* Spieler zieht eine", "*tödliche* linie hinter sich her"))),
	;


	private final String[] name;
	private final String[][] lore;

	ItemTranslation(String[] name, String[][] lore) {
		this.name = name;
		this.lore = lore;
	}

	private String[] getLore(Language language) {
		return lore[language.getID()];
	}

	public String getName() {
		return getName(LanguageManager.getLanguage());
	}

	private String getName(Language language) {
		try {
			if (name.length == 1) {
				return name[0];
			} else {
				return name[language.getID()];
			}
		} catch (Exception ignored) {
			return "§4§l404";
		}
	}

	public String[] getLore() {
		return getLore(LanguageManager.getLanguage());
	}

	@SafeVarargs
	public static List<String> formatLore(String[] loreLines, String color, Replacement<String>... replacements) {

		if (loreLines == null) return new ArrayList<>();
		if (loreLines.length == 0) return new ArrayList<>();

		String[] lore = loreLines.clone();

		List<String> format = new ArrayList<>();

		for (int i = 0; i < lore.length; i++) {
			lore[i] = replace(loreLines[i], replacements);
		}

		format.add(" ");
		boolean colored = false;
		boolean nextIsColorCode = false;
		String lastColor = "7";
		for (String currentLore : lore) {
			StringBuilder builder = new StringBuilder();
			builder.append("§7");
			for (String currentChar : currentLore.split("")) {

				if (currentChar.equals("§")) {
					nextIsColorCode = true;
				}
				if (nextIsColorCode) {
					nextIsColorCode = false;
					lastColor = currentChar;
				}

				if (currentChar.equals("*")) {
					if (colored) {
						colored = false;
						builder.append("§").append(lastColor);
					} else {
						colored = true;
						builder.append(color);
					}
				} else {
					builder.append(currentChar);
				}
			}
			format.add(builder.toString());
		}
		format.add(" ");

		return format;
	}

	// Transforms String... to String[]
	public static String[] a(String... strings) {
		return strings;
	}

	// Transforms String[]... to String[][]
	public static String[][] b(String[]... stringArrays) {
		String[][] array = new String[stringArrays.length][];
		System.arraycopy(stringArrays, 0, array, 0, stringArrays.length);
		return array;
	}

}
