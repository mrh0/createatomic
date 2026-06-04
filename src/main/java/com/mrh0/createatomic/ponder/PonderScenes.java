package com.mrh0.createatomic.ponder;

import com.mrh0.createatomic.blocks.rod_assembly.RodAssemblyBlockEntity;
import com.mrh0.createatomic.index.AtomicItems;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class PonderScenes {
    public static void reactor(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("reactor", "Nuclear Reactor");
        scene.configureBasePlate(1, 0, 6);
        scene.showBasePlate();
        scene.idle(10);

        // Rod assembly positions (top of the 2×2 reactor)
        BlockPos rod00 = new BlockPos(3, 5, 2);
        BlockPos rod10 = new BlockPos(4, 5, 2);
        BlockPos rod01 = new BlockPos(3, 5, 3);
        BlockPos rod11 = new BlockPos(4, 5, 3);

        // ── Phase 1: Reactor Multiblock ───────────────────────────────────────

        // Build casings one layer at a time
        for (int y = 1; y <= 4; y++) {
            scene.world().showSection(util.select().fromTo(3, y, 2, 4, y, 3), Direction.DOWN);
            scene.idle(4);
        }
        scene.idle(5);

        scene.overlay().showText(60)
                .attachKeyFrame()
                .sharedText("createatomic.ponder.reactor.text_1")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(new BlockPos(3, 2, 2)));
        scene.idle(70);

        // Show all four rod assemblies
        scene.world().showSection(util.select().position(rod00), Direction.DOWN);
        scene.world().showSection(util.select().position(rod10), Direction.DOWN);
        scene.world().showSection(util.select().position(rod01), Direction.DOWN);
        scene.world().showSection(util.select().position(rod11), Direction.DOWN);
        scene.idle(8);

        scene.overlay().showText(60)
                .sharedText("createatomic.ponder.reactor.text_2")
                .placeNearTarget()
                .pointAt(util.vector().topOf(rod00));
        scene.idle(70);

        // Insert 3 fuel rods
        ItemStack fuelRod     = AtomicItems.FUEL_ROD.asStack();
        ItemStack largeCtrlRod = AtomicItems.LARGE_CONTROL_ROD.asStack();

        scene.overlay().showControls(util.vector().topOf(rod00), Pointing.DOWN, 25).rightClick().withItem(fuelRod);
        scene.idle(6);
        scene.world().modifyBlockEntity(rod00, RodAssemblyBlockEntity.class, be -> be.updateRod(fuelRod.copy()));

        scene.overlay().showControls(util.vector().topOf(rod10), Pointing.DOWN, 25).rightClick().withItem(fuelRod);
        scene.idle(6);
        scene.world().modifyBlockEntity(rod10, RodAssemblyBlockEntity.class, be -> be.updateRod(fuelRod.copy()));

        scene.overlay().showControls(util.vector().topOf(rod01), Pointing.DOWN, 25).rightClick().withItem(fuelRod);
        scene.idle(6);
        scene.world().modifyBlockEntity(rod01, RodAssemblyBlockEntity.class, be -> be.updateRod(fuelRod.copy()));

        // Insert 1 large control rod
        scene.overlay().showControls(util.vector().topOf(rod11), Pointing.DOWN, 25).rightClick().withItem(largeCtrlRod);
        scene.idle(6);
        scene.world().modifyBlockEntity(rod11, RodAssemblyBlockEntity.class, be -> be.updateRod(largeCtrlRod.copy()));
        scene.idle(10);

        // Highlight fuel rods
        Object fuelSlot = new Object();
        scene.overlay().showOutline(PonderPalette.GREEN, fuelSlot,
                util.select().position(rod00).add(util.select().position(rod10)).add(util.select().position(rod01)), 65);
        scene.overlay().showText(60)
                .colored(PonderPalette.GREEN)
                .sharedText("createatomic.ponder.reactor.text_3")
                .placeNearTarget()
                .pointAt(util.vector().topOf(rod00));
        scene.idle(70);

        // Highlight control rod
        Object ctrlSlot = new Object();
        scene.overlay().showOutline(PonderPalette.RED, ctrlSlot, util.select().position(rod11), 65);
        scene.overlay().showText(60)
                .colored(PonderPalette.RED)
                .sharedText("createatomic.ponder.reactor.text_4")
                .placeNearTarget()
                .pointAt(util.vector().topOf(rod11));
        scene.idle(70);

        scene.overlay().showText(70)
                .colored(PonderPalette.RED)
                .sharedText("createatomic.ponder.reactor.text_5")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(new BlockPos(3, 3, 2)));
        scene.idle(80);

        // ── Phase 2: Water, Turbines & Redstone ──────────────────────────────
        scene.addKeyframe();

        // Water / pump system
        scene.world().showSection(util.select().fromTo(0, 0, 2, 0, 0, 5), Direction.EAST);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 2, 2, 5), Direction.EAST);
        scene.idle(10);

        scene.overlay().showText(65)
                .attachKeyFrame()
                .sharedText("createatomic.ponder.reactor.text_6")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(new BlockPos(1, 1, 4)));
        scene.idle(75);

        // Turbines
        scene.world().showSection(util.select().position(new BlockPos(4, 1, 1)), Direction.SOUTH);
        scene.idle(4);
        scene.world().showSection(util.select().position(new BlockPos(3, 1, 1)), Direction.SOUTH);
        scene.idle(4);
        scene.world().showSection(util.select().position(new BlockPos(4, 1, 0)), Direction.SOUTH);
        scene.idle(4);
        scene.world().showSection(util.select().position(new BlockPos(3, 1, 0)), Direction.SOUTH);
        scene.idle(10);

        scene.overlay().showText(65)
                .sharedText("createatomic.ponder.reactor.text_7")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(new BlockPos(3, 1, 0)));
        scene.idle(75);

        // Redstone interfaces
        scene.world().showSection(util.select().position(new BlockPos(4, 2, 1)), Direction.SOUTH);
        scene.idle(4);
        scene.world().showSection(util.select().position(new BlockPos(3, 2, 1)), Direction.SOUTH);
        scene.idle(10);

        scene.overlay().showText(65)
                .attachKeyFrame()
                .sharedText("createatomic.ponder.reactor.text_8")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(new BlockPos(4, 2, 1), Direction.NORTH));
        scene.idle(75);

        scene.overlay().showText(65)
                .sharedText("createatomic.ponder.reactor.text_9")
                .placeNearTarget()
                .pointAt(util.vector().blockSurface(new BlockPos(3, 2, 1), Direction.NORTH));
        scene.idle(75);

        // ── Phase 3: Automated Refueling ─────────────────────────────────────
        scene.addKeyframe();

        // Output side: chest + funnel
        scene.world().showSection(util.select().position(new BlockPos(1, 3, 4)), Direction.SOUTH);
        scene.idle(5);
        scene.world().showSection(util.select().position(new BlockPos(1, 4, 4)), Direction.DOWN);
        scene.idle(5);

        // Switch two rod assemblies to depleted to illustrate the need for replacement
        ItemStack depletedRod = AtomicItems.DEPLETED_FUEL_ROD.asStack();
        scene.world().modifyBlockEntity(rod00, RodAssemblyBlockEntity.class, be -> be.updateRod(depletedRod.copy()));
        scene.world().modifyBlockEntity(rod10, RodAssemblyBlockEntity.class, be -> be.updateRod(depletedRod.copy()));

        scene.overlay().showText(65)
                .attachKeyFrame()
                .sharedText("createatomic.ponder.reactor.text_10")
                .placeNearTarget()
                .pointAt(util.vector().topOf(rod00));
        scene.idle(75);

        // Output arm + cog (remove depleted rods)
        scene.world().showSection(util.select().position(new BlockPos(4, 4, 4)), Direction.NORTH);
        scene.world().showSection(util.select().position(new BlockPos(3, 4, 5)), Direction.NORTH);
        scene.idle(8);

        BlockPos outputArmPos = new BlockPos(4, 4, 4);
        scene.world().instructArm(outputArmPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(22);
        scene.world().modifyBlockEntity(rod10, RodAssemblyBlockEntity.class, be -> be.updateRod(ItemStack.EMPTY));
        scene.world().instructArm(outputArmPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, depletedRod, -1);
        scene.idle(8);
        scene.world().instructArm(outputArmPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, depletedRod, 0);
        scene.idle(22);
        scene.world().instructArm(outputArmPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);

        scene.overlay().showText(65)
                .sharedText("createatomic.ponder.reactor.text_11")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(outputArmPos));
        scene.idle(75);

        // Input side: depot + refuel arm
        scene.world().showSection(util.select().position(new BlockPos(6, 4, 3)), Direction.WEST);
        scene.world().showSection(util.select().position(new BlockPos(6, 5, 3)), Direction.WEST);
        scene.world().showSection(util.select().position(new BlockPos(6, 5, 4)), Direction.WEST);
        scene.idle(5);
        scene.world().showSection(util.select().position(new BlockPos(3, 4, 4)), Direction.NORTH);
        scene.idle(8);

        scene.world().createItemOnBeltLike(new BlockPos(6, 4, 3), Direction.WEST, fuelRod);
        scene.idle(5);

        BlockPos refuelArmPos = new BlockPos(3, 4, 4);
        scene.world().instructArm(refuelArmPos, ArmBlockEntity.Phase.MOVE_TO_INPUT, ItemStack.EMPTY, 0);
        scene.idle(22);
        scene.world().removeItemsFromBelt(new BlockPos(6, 4, 3));
        scene.world().instructArm(refuelArmPos, ArmBlockEntity.Phase.SEARCH_OUTPUTS, fuelRod, -1);
        scene.idle(8);
        scene.world().instructArm(refuelArmPos, ArmBlockEntity.Phase.MOVE_TO_OUTPUT, fuelRod, 0);
        scene.idle(22);
        scene.world().modifyBlockEntity(rod10, RodAssemblyBlockEntity.class, be -> be.updateRod(fuelRod.copy()));
        scene.world().instructArm(refuelArmPos, ArmBlockEntity.Phase.SEARCH_INPUTS, ItemStack.EMPTY, -1);

        scene.overlay().showText(65)
                .sharedText("createatomic.ponder.reactor.text_12")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(refuelArmPos));
        scene.idle(75);

        scene.markAsFinished();
    }
}
