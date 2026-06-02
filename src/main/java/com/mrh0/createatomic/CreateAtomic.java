package com.mrh0.createatomic;

import com.mojang.brigadier.CommandDispatcher;
import com.mrh0.createatomic.index.*;
import com.simibubi.create.api.boiler.BoilerHeater;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.api.stress.BlockStressValues;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingBlockEntity;
import com.mrh0.createatomic.blocks.reactor_casing.ReactorCasingRenderer;
import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyRenderer;
import com.mrh0.createatomic.blocks.turbine.TurbineRenderer;
import com.mrh0.createatomic.config.AtomicConfigs;
import com.mrh0.createatomic.network.ClientPayloadHandler;
import com.mrh0.createatomic.network.ObservePacketPayload;
import com.mrh0.createatomic.network.ReactorPacketPayload;
import com.mrh0.createatomic.network.ServerPayloadHandler;
import com.mrh0.createatomic.ponder.AtomicPonderPlugin;

import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CreateAtomic.MODID)
public class CreateAtomic {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "createatomic";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAtomic.MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private static final ItemLike[] excludedItemsList = new ItemLike[]{};

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(MODID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> AtomicBlocks.REACTOR_CASING.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.createatomic.main"))
            .displayItems((itemDisplayParameters, output) -> REGISTRATE.getAll(Registries.ITEM).forEach((item -> {
                for (ItemLike excluded : excludedItemsList) {
                    if (item.is(excluded.asItem())) {
                        output.accept(item.get(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                        return;
                    }
                }
                output.accept(item.get());
            })))
            .build());

    public CreateAtomic(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::onRegister);
        eventBus.addListener(RegisterCapabilitiesEvent.class, ReactorCasingBlockEntity::registerCapabilities);
        eventBus.addListener(RegisterPayloadHandlersEvent.class, CreateAtomic::registerPackets);
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, CARecipes::register);

        NeoForge.EVENT_BUS.register(this);

        AtomicConfigs.register(container);
        REGISTRATE.registerEventListeners(eventBus);
        AtomicBlocks.register();
        AtomicBlockEntities.register();
        AtomicItems.register();
        AtomicSounds.register(eventBus);
        CREATIVE_MODE_TABS.register(eventBus);
        //CAFluids.register();
        //CAEffects.register(eventBus);
        // AtomicRecipes.register(eventBus);
        //CASounds.register(eventBus);
        //CASchedule.register();
        //CADamageTypes.register();
        AtomicDisplaySources.register();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> AtomicPartials::init);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // BlockStressValues.CAPACITIES.registerProvider(MODID, AllConfigs.server().kinetics.stressValues);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new AtomicPonderPlugin());
        event.enqueueWork(() -> {
            BlockEntityRenderers.register(AtomicBlockEntities.REACTOR_CASING.get(), ReactorCasingRenderer::new);
            BlockEntityRenderers.register(AtomicBlockEntities.TURBINE.get(), TurbineRenderer::new);
            BlockEntityRenderers.register(AtomicBlockEntities.ROD_ASSEMBLY.get(), RodAssemblyRenderer::new);
        });
        AtomicPartials.init();
    }

    public void postInit(FMLLoadCompleteEvent evt) {
        //Network.registerMessage(0, ObservePacketLegacy.class, ObservePacketLegacy::encode, ObservePacketLegacy::decode, ObservePacketLegacy::handle);
        //Network.registerMessage(1, EnergyNetworkPacket.class, EnergyNetworkPacket::encode, EnergyNetworkPacket::decode, EnergyNetworkPacket::handle);

        BoilerHeater.REGISTRY.register(AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR_KINDLED.get(), (level, pos, state) -> 1);
        BoilerHeater.REGISTRY.register(AtomicBlocks.RADIOISOTOPE_HEAT_GENERATOR_SMOULDERING.get(), (level, pos, state) -> 0);
        // BoilerHeater.REGISTRY.register(AtomicBlocks.RAW_URANIUM_BLOCK.get(), (level, pos, state) -> 0);

        // Steam turbine: 2 SU capacity per RPM (1/4 of original 8).
        // Balance: 2 turbines are required to fully absorb 1 effective power.
        BlockStressValues.CAPACITIES.register(AtomicBlocks.TURBINE.get(), () -> 2.0);
        // Inform Create's tooltip system that the turbine can generate up to 256 RPM
        BlockStressValues.RPM.register(AtomicBlocks.TURBINE.get(),
                new BlockStressValues.GeneratedRpm(256, true));

        AtomicDisplaySources.associateWithBlockEntities();
        LOGGER.info("Create Atomic Initialized!");
    }

    public void onRegister(final RegisterEvent event) {
        AtomicArmInteractionPointTypes.register();
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispather = event.getDispatcher();
    }

    private static final String PROTOCOL = "1";
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL);
        registrar = registrar.executesOn(HandlerThread.MAIN);
        registrar.playBidirectional(
                ObservePacketPayload.TYPE,
                ObservePacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleObservePayload,
                        ServerPayloadHandler::handleObservePayload
                )
        );

        registrar.playBidirectional(
                ReactorPacketPayload.TYPE,
                ReactorPacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleReactorPayload,
                        ServerPayloadHandler::handleReactorPayload
                )
        );
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
