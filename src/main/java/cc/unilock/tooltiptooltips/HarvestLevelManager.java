package cc.unilock.tooltiptooltips;

import cc.unilock.tooltiptooltips.mixin.TagEntryAccessor;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HarvestLevelManager {
	private static final int PREFIX = "tags/block/".length();
	private static final int SUFFIX = ".json".length();

	private static final Identifier WOODEN = BlockTags.INCORRECT_FOR_WOODEN_TOOL.id();
	private static final Identifier STONE = BlockTags.INCORRECT_FOR_STONE_TOOL.id();
	private static final Identifier IRON = BlockTags.INCORRECT_FOR_IRON_TOOL.id();
	private static final Identifier GOLD = BlockTags.INCORRECT_FOR_GOLD_TOOL.id();
	private static final Identifier DIAMOND = BlockTags.INCORRECT_FOR_DIAMOND_TOOL.id();
	private static final Identifier NETHERITE = BlockTags.INCORRECT_FOR_NETHERITE_TOOL.id();

	private static final Multimap<Identifier, TagEntry> tagLookup = MultimapBuilder.hashKeys().hashSetValues().build();
	private static final Object2IntArrayMap<Identifier> tagTiers = new Object2IntArrayMap<>();

	public static void clear() {
		tagLookup.clear();
		tagTiers.clear();
	}

	public static void loadTag(Identifier tagId, List<TagEntry> tagEntries) {
		tagLookup.putAll(Identifier.of(tagId.getNamespace(), tagId.getPath().substring(PREFIX, tagId.getPath().length() - SUFFIX)), tagEntries);
	}

	public static void tagsLoaded() {
		tagLookup.asMap().entrySet().parallelStream().forEach(entry -> {
			tagTiers.put(entry.getKey(), processTagEntries(entry.getValue()));
		});
	}

	private static int processTagEntries(Collection<TagEntry> tagEntries) {
		Set<Identifier> tagIds = tagEntries.parallelStream()
				.filter(tagEntry -> ((TagEntryAccessor) tagEntry).getTag())
				.map(tagEntry -> ((TagEntryAccessor) tagEntry).getId())
				.collect(Collectors.toUnmodifiableSet());

		if (tagIds.contains(WOODEN) || tagIds.contains(GOLD)) {
			return 1;
		} else if (tagIds.contains(STONE)) {
			return 2;
		} else if (tagIds.contains(IRON)) {
			return 3;
		} else if (tagIds.contains(DIAMOND)) {
			return 4;
		} else if (tagIds.contains(NETHERITE)) {
			return 5;
		} else if (!tagIds.isEmpty()) {
			Set<TagEntry> resolved = tagIds.parallelStream()
					.map(tagLookup::get)
					.flatMap(Collection::parallelStream)
					.collect(Collectors.toUnmodifiableSet());
			return processTagEntries(resolved);
		}

		return 0;
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
