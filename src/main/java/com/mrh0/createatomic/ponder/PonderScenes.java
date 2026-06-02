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

		scene.markAsFinished();
	}
}
