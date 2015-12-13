package com.randomtower.geometryTwo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import it.marteEngine.ME;
import it.marteEngine.ResourceManager;
import it.marteEngine.SFX;
import it.marteEngine.actor.StaticActor;
import it.marteEngine.entity.Entity;
import it.marteEngine.tween.Ease;
import it.marteEngine.tween.LinearMotion;

public class Enemy extends StaticActor {

	public static final String ENEMY = "enemy";

	private static final float TIME = 10;

	public int hp = 100;

	public int timer = 0;

	public int UPDATE_TIMER = 0;

	private Random rnd = new Random();

	private boolean moveUp;

	private boolean moveRight;

	private boolean moveDown;

	private boolean moveLeft;

	private Vector2f velocity;

	public LinearMotion motion;

	private boolean dead;

	public int damage = 10;

	private String behaviour;

	public static final String RANDOM = "random";

	public static final String DANCE = "dance";

	public static final String TOPLAYER = "toPlayer";

	public enum DIR {
		LEFT, RIGHT, UP, DOWN, NONE
	}

	public Enemy(float x, float y, int width, int height, Image image, String behaviour) {
		super(x, y, width, height, image);
		setCentered(true);
		this.behaviour = behaviour;

		setHitBox(-currentImage.getWidth() / 2, -currentImage.getHeight() / 2, currentImage.getWidth(),
				currentImage.getHeight());
		addType(SOLID, ENEMY);
		Random rnd = new Random();
		UPDATE_TIMER = rnd.nextInt(2000) + 1000;

		velocity = new Vector2f(50, 50);
	}

	@Override
	public void collisionResponse(Entity other) {
		if (other instanceof Bullet && !dead) {
			Bullet b = (Bullet) other;
			hp -= b.damage;
			if (hp <= 0) {
				setGraphic(ResourceManager.getSpriteSheet(GameWorld.EXPLOSION));
				addAnimation(GameWorld.EXPLOSION_ANIM, false, 0, 0, 1, 2, 3, 4, 5, 6, 7);
				getCurrentAnim(GameWorld.EXPLOSION_ANIM).setSpeed(4);
				dead = true;
				addKill();
				SFX.playSound("explosion");
			} else {
				ME.world.remove(b);
			}
		}
		if (other instanceof Player && !dead) {
			hp = 0;
			setGraphic(ResourceManager.getSpriteSheet(GameWorld.EXPLOSION));
			addAnimation(GameWorld.EXPLOSION_ANIM, false, 0, 0, 1, 2, 3, 4, 5, 6, 7);
			getCurrentAnim(GameWorld.EXPLOSION_ANIM).setSpeed(4);
			dead = true;
			addKill();
			SFX.playSound("explosion");
		}
	}

	private void addKill() {
		if (ME.attributes.get(GameWorld.KILLS) == null) {
			ME.attributes.put(GameWorld.KILLS, new Integer(1));
		} else {
			int k = (Integer) ME.attributes.get(GameWorld.KILLS) + 1;
			ME.attributes.put(GameWorld.KILLS, k);
		}
		System.out.println(ME.attributes.get(GameWorld.KILLS));

	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		super.update(container, delta);

		if (!dead) {
			timer += delta;
			if (timer > UPDATE_TIMER) {
				timer = 0;
				switch (behaviour) {
				case RANDOM:
					moveRandom();
					break;
				case DANCE:
					dance();
					break;
				case TOPLAYER:
					moveToPlayer();
					break;
				default:
					moveRandom();
					break;
				}

				updateTween(delta);

			}
			move(delta);
		}

		if (getCurrentAnim(GameWorld.EXPLOSION_ANIM) != null && getCurrentAnim(GameWorld.EXPLOSION_ANIM).isStopped()) {
			ME.world.remove(this);
		}

		collide(PLAYER, x, y);
	}

	private void moveToPlayer() {
		makeMove(chooseMoveToPlayer());
	}

	private DIR chooseMoveToPlayer() {
		// find player position and make move to move to it
		List<DIR> values = new ArrayList<DIR>();
		int cx = (int) x;
		int cy = (int) y;
		Player player = ((GameWorld) ME.world).player;
		if (cx < player.x) {
			values.add(DIR.RIGHT);
		} else {
			values.add(DIR.LEFT);
		}
		if (cy > player.y) {
			values.add(DIR.UP);
		} else {
			values.add(DIR.DOWN);
		}
		if (values.size() > 0) {
			DIR value = values.get(rnd.nextInt(values.size()));
			return value;
		}

		return DIR.NONE;
	}

	private void dance() {
		Player player = ((GameWorld) ME.world).player;
		float dx = player.x - x;
		float dy = player.y - y;
		if (dx > 0) {
			moveLeft = true;
		} else if (dx < 0) {
			moveRight = true;
		}
		if (dy < 0) {
			moveUp = true;
		} else if (dy > 0) {
			moveDown = true;
		}

	}

	private void move(int delta) {
		updateMotion(delta);
	}

	private void moveRandom() {
		makeMove(chooseMove());
	}

	private DIR chooseMove() {
		// get data from world using radius
		// note: no diagonal movements!
		// eliminate move that lead into a wall
		List<DIR> values = new ArrayList<DIR>();
		int cx = (int) x;
		int cy = (int) y;
		values = randomMove(values, cx, cy);
		// pick one random
		if (values.size() > 0) {
			DIR value = values.get(rnd.nextInt(values.size()));
			return value;
		}

		return DIR.NONE;
	}

	private List<DIR> randomMove(List<DIR> values, int cx, int cy) {
		if (canMove(cx - velocity.x, cy)) {
			values.add(DIR.LEFT);
		}
		if (canMove(cx + velocity.x, cy)) {
			values.add(DIR.RIGHT);
		}
		if (canMove(cx, cy - velocity.y)) {
			values.add(DIR.UP);
		}
		if (canMove(cx, cy + velocity.y)) {
			values.add(DIR.DOWN);
		}
		return values;
	}

	private boolean canMove(float tx, float ty) {
		return tx > 0 && tx < 640 && ty + width > 0 && ty + height < 480 && collide(ENEMY, tx, ty) == null;
	}

	protected void makeMove(DIR dir) {
		System.out.println("Move dir: " + dir.toString());
		switch (dir) {
		case UP:
			// move(0, -1);
			moveUp = true;
			break;
		case RIGHT:
			// move(1, 0);
			moveRight = true;
			break;
		case DOWN:
			// move(0, 1);
			moveDown = true;
			break;
		case LEFT:
			// move(-1, 0);
			moveLeft = true;
			break;
		default:
			break;
		}
	}

	public void updateTween(int delta) {
		if (!moveRight && !moveLeft && !moveUp && !moveDown) {
			return;
		}
		float tx = 0;
		float ty = 0;

		if (motion != null) {
			return;
		}
		if (moveRight) {
			tx = x + velocity.x;
			ty = y;
			if (canMove(tx, ty)) {
				motion = new LinearMotion(x, y, tx, ty, TIME, Ease.EXPO_IN);
			}
			moveRight = false;
		}
		if (moveLeft) {
			tx = x - velocity.x;
			ty = velocity.y;
			if (canMove(tx, ty)) {
				motion = new LinearMotion(x, y, tx, ty, TIME, Ease.EXPO_IN);
			}
			moveLeft = false;
		}
		if (moveUp) {
			tx = x;
			ty = y - velocity.y;
			if (canMove(tx, ty)) {
				motion = new LinearMotion(x, y, tx, ty, TIME, Ease.EXPO_IN);
			}
			moveUp = false;
		}
		if (moveDown) {
			tx = x;
			ty = y + velocity.y;
			if (canMove(tx, ty)) {
				motion = new LinearMotion(x, y, tx, ty, TIME, Ease.EXPO_IN);
			}
			moveDown = false;
		}
	}

	public void updateMotion(int delta) {
		if (motion != null) {
			motion.update(delta);
			setPosition(motion.getPosition());
			if (motion.isFinished()) {
				motion = null;
			}
		}
	}

}
