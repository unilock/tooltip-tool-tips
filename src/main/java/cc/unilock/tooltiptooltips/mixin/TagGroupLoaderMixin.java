package cc.unilock.tooltiptooltips.mixin;

import cc.unilock.tooltiptooltips.HarvestLevelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {
	@Shadow
	@Final
	private String dataType;
	
	@Unique
	private boolean isBlockTags = false;

	@Inject(method = "loadTags", at = @At("HEAD"))
	private void loadTags(CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir) {
		if (Objects.equals(this.dataType, "tags/block")) {
			this.isBlockTags = true;
			HarvestLevelManager.clear();
		} else {
			this.isBlockTags = false;
		}
	}

	@WrapOperation(method = "loadTags", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/tag/TagFile;entries()Ljava/util/List;"))
	private List<TagEntry> loadTags(TagFile instance, Operation<List<TagEntry>> original, @Local(ordinal = 0) Identifier identifier) {
		List<TagEntry> ret = original.call(instance);

		if (this.isBlockTags) {
			HarvestLevelManager.loadTag(identifier, ret);
		}

		return original.call(instance);
	}
}
