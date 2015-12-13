package com.randomtower.geometryTwo;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import it.marteEngine.actor.StaticActor;

public class Background extends StaticActor {

	private boolean move;

	public Background(float x, float y, int width, int height, Image image, boolean move) {
		super(x, y, width, height, image);
		this.move = move;
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);
		if (move) {
			int dx = -1;
			x += dx * 2;
			if (x + width < -10) {
				x = container.getWidth() + 10;
			}
		}
	}

}
