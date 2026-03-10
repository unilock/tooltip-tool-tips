package cc.unilock.tooltiptooltips.mixin;

import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryAccessor {
	@Accessor
	Identifier getId();

	@Accessor
	boolean getTag();
}
