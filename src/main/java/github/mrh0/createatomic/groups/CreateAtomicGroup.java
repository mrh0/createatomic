package github.mrh0.createatomic.groups;

import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class CreateAtomicGroup extends CreativeModeTab {
	public static CreateAtomicGroup MAIN;

	public CreateAtomicGroup(String name) {
		super(CreateAtomic.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public @NotNull ItemStack makeIcon() {
		return new ItemStack(Items.OAK_SAPLING);
	}
}
