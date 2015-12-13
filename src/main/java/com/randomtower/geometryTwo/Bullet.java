package com.randomtower.geometryTwo;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import it.marteEngine.entity.Entity;

public class Bullet extends Entity {

	public float fireSpeed = 0.5f;
	private static final String BULLET = "BULLET";

	public int damage = 50;

	public Bullet(float x, float y, String ref, int angle, float scale) throws SlickException {
		super(x, y);
		this.angle = angle;
		currentImage = new Image(ref);
		this.scale = scale;

		addType(SOLID, BULLET);
		setHitBox(0, 0, (int) (currentImage.getWidth() * scale), (int) (currentImage.getHeight() * scale));
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		float dx = 0;
		float dy = 0;
		Vector2f vectorSpeed = calculateVector(angle, 20);
		dx += vectorSpeed.x;
		dy += vectorSpeed.y;
		x += dx;
		y += dy;

		collide(SOLID, x, y);

		super.update(container, delta);
	}

}
