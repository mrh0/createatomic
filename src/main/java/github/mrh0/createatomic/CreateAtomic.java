package github.mrh0.createatomic;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import github.mrh0.createatomic.index.*;
import github.mrh0.createatomic.network.ObservePacket;
import github.mrh0.createatomic.network.SyncReactorPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateAtomic.MODID)
public class CreateAtomic {
    public static final String MODID = "createatomic";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAtomic.MODID);

    private static final String PROTOCOL = "1";
    public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

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
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        CC_ACTIVE = ModList.get().isLoaded("computercraft");

        AtomicCreativeModeTabs.register(eventBus);
        REGISTRATE.registerEventListeners(eventBus);
        AtomicBlocks.register();
        AtomicBlockEntities.register();
        AtomicItems.register();
        AtomicArmInteractionPointTypes.register();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    private void setup(final FMLCommonSetupEvent event) {
        //BlockStressValues.registerProvider(MODID, AllConfigs.SERVER.kinetics.stressValues);
        AtomicBoilerHeaters.register();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {}

    public void postInit(FMLLoadCompleteEvent evt) {
        Network.registerMessage(0, ObservePacket.class, ObservePacket::encode, ObservePacket::decode, ObservePacket::handle);
        Network.registerMessage(1, SyncReactorPacket.class, SyncReactorPacket::encode, SyncReactorPacket::decode, SyncReactorPacket::handle);

        System.out.println("Create: Atomic Initialized!");
    }
}
