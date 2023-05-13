package github.mrh0.createatomic;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import github.mrh0.createatomic.groups.CreateAtomicGroup;
import github.mrh0.createatomic.index.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateAtomic.MODID)
public class CreateAtomic {

    public static final String MODID = "createatomic";

    private static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(CreateAtomic.MODID);

    public static boolean CC_ACTIVE = false;

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateAtomic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
        // Register the setup method for modloading
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        CC_ACTIVE = ModList.get().isLoaded("computercraft");

        new CreateAtomicGroup("main");

        AtomicBlocks.register();
        AtomicBlockEntities.register();
        AtomicItems.register();
        AtomicArmInteractionPointTypes.register();
    }

    public static CreateRegistrate registrate() {
        return registrate.get();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // BlockStressValues.registerProvider(MODID, AllConfigs.SERVER.kinetics.stressValues);
        AtomicBoilerHeaters.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    public void postInit(FMLLoadCompleteEvent evt) {
        System.out.println("Create: Atomic Initialized!");
    }
}
