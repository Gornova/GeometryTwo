package com.randomtower.geometryTwo;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import it.marteEngine.ME;
import it.marteEngine.ResourceManager;
import it.marteEngine.SFX;
import it.marteEngine.World;
import it.marteEngine.actor.StaticActor;
import it.marteEngine.entity.Entity;

public class Player extends StaticActor {

	private static final String FIRE = "fire";
	private static final String REDUCE = "reduce";

	private int growLevel = 1;

	public int hp = 100;
	public int damage = 10;
	public boolean dead;

	public Player(float x, float y, int width, int height, Image image) {
		super(x, y, width, height, image);
		setCentered(true);
		define(FIRE, Input.MOUSE_LEFT_BUTTON);
		define(REDUCE, Input.MOUSE_RIGHT_BUTTON);

		addType(SOLID, PLAYER);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {

		if (dead && getCurrentAnim(GameWorld.EXPLOSION_ANIM) != null
				&& getCurrentAnim(GameWorld.EXPLOSION_ANIM).isStopped()) {
			ME.world.remove(this);
			return;
		}

		// calculate heading of turret
		Input input = container.getInput();
		float mx = input.getMouseX();
		float my = input.getMouseY();
		angle = (int) calculateAngle(x, y, mx, my);

		// add new Missile when player fire
		if (check(FIRE)) {
			Bullet b = null;
			if (growLevel < 2) {
				b = new Bullet(x, y, "singleBullet.png", angle, scale);
			} else if (growLevel > 2 && growLevel < 4) {
				b = new Bullet(x, y, "doubleBullet.png", angle, scale);
			} else {
				b = new Bullet(x, y, "tripleBullet.png", angle, scale);
			}
			b.setCentered(true);
			ME.world.add(b, World.GAME);
			SFX.playSound("fire");
		}

		if (check(REDUCE)) {
			if (scale - 0.25 > 0.5) {
				scale -= 0.25;
				setCentered(true);
				setHitBox(-(int) (width * scale) / 2, -(int) (height * scale) / 2, (int) (width * scale),
						(int) (height * scale));
			}
		}

		if (ME.attributes.get(GameWorld.KILLS) != null) {
			Integer kills = (Integer) ME.attributes.get(GameWorld.KILLS);
			if (kills == growLevel) {
				growLevel++;
				scale += 0.25;
				setCentered(true);
				// setHitBox((int) (width * scale) / scale, (int) (height *
				// scale) / scale,
				// (int) (width * scale / scale), (int) (height * scale /
				// scale));
				setHitBox(-(int) (width / 2), -(int) (height / 2), (int) (width), (int) (height));
				SFX.playSound("grow");
			}
		}

		super.update(container, delta);
	}

	@Override
	public void collisionResponse(Entity other) {
		if (other instanceof Enemy && !dead) {
			Enemy e = (Enemy) other;
			hp -= e.damage;
			if (hp <= 0) {
				setGraphic(ResourceManager.getSpriteSheet(GameWorld.EXPLOSION));
				addAnimation(GameWorld.EXPLOSION_ANIM, false, 0, 0, 1, 2, 3, 4, 5, 6, 7);
				getCurrentAnim(GameWorld.EXPLOSION_ANIM).setSpeed(4);
				dead = true;
			}
		}
	}

}
