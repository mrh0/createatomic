package github.mrh0.createatomic.groups;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import github.mrh0.createatomic.CreateAtomic;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class CreateAtomicGroup extends CreativeModeTab {
	public static CreateAtomicGroup MAIN;;

	public CreateAtomicGroup(String name) {
		super(CreateAtomic.MODID+":"+name);
		MAIN = this;
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(Items.ACACIA_BOAT);
	}
}
