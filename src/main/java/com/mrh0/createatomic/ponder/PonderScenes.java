package com.mrh0.createatomic.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class PonderScenes {
    public static void reactor(SceneBuilder builder, SceneBuildingUtil util) {
		CreateSceneBuilder scene = new CreateSceneBuilder(builder);
		scene.title("reactor", "Reactor");
		scene.configureBasePlate(1, 0, 6);
		scene.showBasePlate();
		scene.idle(15);

		BlockPos cIn = new BlockPos(1, 3, 1);
		BlockPos cOut = new BlockPos(2, 3, 2);

		var reactor = util.select().fromTo(3, 1, 2, 3+1, 1+3, 2+1);
		//scene.world().showSection(reactor, Direction.EAST);
		ElementLink<WorldSectionElement> reactorLink = scene.world().showIndependentSection(reactor, Direction.EAST);
		scene.idle(15);
		scene.overlay().showOutline(PonderPalette.GREEN, reactorLink, reactor, 50);

		scene.overlay().showText(50)
			.text("The reactor is a multiblock")
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cIn));
		scene.idle(60);
		scene.overlay().showText(50)
			.text("It can store large amounts of energy")
			.placeNearTarget()
			.pointAt(util.vector().centerOf(cIn));
		scene.idle(60);
		scene.world().showSection(util.select().position(cIn), Direction.DOWN);
		scene.idle(5);
		scene.world().showSection(util.select().position(cOut), Direction.DOWN);

		// Pump
		scene.world().showSection(util.select().fromTo(0, 0, 2, 0, 0, 5), Direction.EAST);
		scene.world().showSection(util.select().fromTo(0, 1, 2, 2, 2, 5), Direction.EAST);

		// Turbines
		scene.idle(5);
		scene.world().showSection(util.select().position(4, 1, 1), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(3, 1, 1), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(4, 1, 0), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(3, 1, 0), Direction.SOUTH);

		// Redstone
		scene.idle(5);
		scene.world().showSection(util.select().position(4, 2, 1), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(3, 2, 1), Direction.SOUTH);

		// Output
		scene.idle(5);
		scene.world().showSection(util.select().position(1, 3, 4), Direction.SOUTH);
		scene.idle(5);
		scene.world().showSection(util.select().position(1, 4, 4), Direction.DOWN);

		// Input
		scene.world().showSection(util.select().position(6, 4, 3), Direction.WEST);
		scene.world().showSection(util.select().position(6, 5, 3), Direction.WEST);
		scene.world().showSection(util.select().position(6, 5, 4), Direction.WEST);

		// Arms
		scene.world().showSection(util.select().position(3, 4, 4), Direction.NORTH);
		scene.world().showSection(util.select().position(4, 4, 4), Direction.NORTH);
		scene.world().showSection(util.select().position(3, 4, 5), Direction.NORTH);

		scene.markAsFinished();
	}
}
