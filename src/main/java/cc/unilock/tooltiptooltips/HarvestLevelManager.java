package cc.unilock.tooltiptooltips;

import cc.unilock.tooltiptooltips.mixin.TagEntryAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HarvestLevelManager {
	private static final Identifier WOODEN = BlockTags.INCORRECT_FOR_WOODEN_TOOL.id();
	private static final Identifier STONE = BlockTags.INCORRECT_FOR_STONE_TOOL.id();
	private static final Identifier IRON = BlockTags.INCORRECT_FOR_IRON_TOOL.id();
	private static final Identifier GOLD = BlockTags.INCORRECT_FOR_GOLD_TOOL.id();
	private static final Identifier DIAMOND = BlockTags.INCORRECT_FOR_DIAMOND_TOOL.id();
	private static final Identifier NETHERITE = BlockTags.INCORRECT_FOR_NETHERITE_TOOL.id();

	private static final Object2IntArrayMap<Identifier> tagTiers = new Object2IntArrayMap<>();

	public static void clear() {
		tagTiers.clear();
	}

	public static void loadTag(Identifier tagId, List<TagEntry> tagEntries) {
		Set<Identifier> set = tagEntries.stream()
				.filter(tagEntry -> ((TagEntryAccessor) tagEntry).getTag())
				.map(tagEntry -> ((TagEntryAccessor) tagEntry).getId())
				.collect(Collectors.toUnmodifiableSet());

		if (set.contains(WOODEN) || set.contains(GOLD)) {
			tagTiers.put(tagId, 1);
		} else if (set.contains(STONE)) {
			tagTiers.put(tagId, 2);
		} else if (set.contains(IRON)) {
			tagTiers.put(tagId, 3);
		} else if (set.contains(DIAMOND)) {
			tagTiers.put(tagId, 4);
		} else if (set.contains(NETHERITE)) {
			tagTiers.put(tagId, 5);
		}
	}

	public static String getTier(Identifier id) {
		if (WOODEN.equals(id) || GOLD.equals(id)) {
			return "wooden";
		}
		if (STONE.equals(id)) {
			return "stone";
		}
		if (IRON.equals(id)) {
			return "iron";
		}
		if (DIAMOND.equals(id)) {
			return "diamond";
		}
		if (NETHERITE.equals(id)) {
			return "netherite";
		}

		return switch (tagTiers.getInt(id)) {
			case 1 -> "wooden";
			case 2 -> "stone";
			case 3 -> "iron";
			case 4 -> "diamond";
			case 5 -> "netherite";
			default -> null;
		};
	}
}
