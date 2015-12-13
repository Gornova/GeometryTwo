package com.randomtower.geometryTwo;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import it.marteEngine.ME;
import it.marteEngine.ResourceManager;
import it.marteEngine.World;

public class GameWorld extends World {

	public static final String ENEMY_ONE = "enemyOne";
	public static final String BACKGROUND = "background";
	public static final String PLAYER = "player";
	public static final String BACKGROUNDMOVING = "backgroundMoving";
	public static final String KILLS = "KILLS";
	public static final String ENEMY_TWO = "enemyTwo";
	public static final String ENEMY_THREE = "enemyThree";
	public static final String EXPLOSION_ANIM = "explosionAnim";
	public static final String EXPLOSION = "explosion";

	public int level = 1;
	public Player player;
	private boolean started;

	public GameWorld(int id, GameContainer container) throws SlickException {
		super(id, container);
	}

	private void buildDefault() {
		clear();
		if (ME.attributes != null) {
			ME.attributes.clear();
		}

		Background background = new Background(0, 0, 640, 480, ResourceManager.getImage(BACKGROUND), false);
		background.depth = -100;
		add(background, BELOW);

		Background backgroundMoving = new Background(0, 0, 640, 480, ResourceManager.getImage(BACKGROUNDMOVING), true);
		backgroundMoving.depth = 100;
		add(backgroundMoving, BELOW);

		player = new Player(320, 240, ResourceManager.getImage(PLAYER).getWidth(),
				ResourceManager.getImage(PLAYER).getHeight(), ResourceManager.getImage(PLAYER));
		add(player, GAME);

		for (int i = 0; i < level; i++) {

			for (int j = 0; j < 4; j++) {

				Vector2f pos = getRandomPosition();
				float ex = pos.x;
				float ey = pos.y;

				Enemy enemyOne = new Enemy(ex, ey, ResourceManager.getImage(ENEMY_ONE).getWidth(),
						ResourceManager.getImage(ENEMY_ONE).getHeight(), ResourceManager.getImage(ENEMY_ONE),
						Enemy.RANDOM);
				add(enemyOne, GAME);

				if (level > 2) {
					Enemy enemyTwo = new Enemy(ex, ey, ResourceManager.getImage(ENEMY_TWO).getWidth(),
							ResourceManager.getImage(ENEMY_TWO).getHeight(), ResourceManager.getImage(ENEMY_TWO),
							Enemy.TOPLAYER);
					add(enemyTwo, GAME);
				}

				if (level > 4) {
					Enemy enemyTwo = new Enemy(ex, ey, ResourceManager.getImage(ENEMY_THREE).getWidth(),
							ResourceManager.getImage(ENEMY_THREE).getHeight(), ResourceManager.getImage(ENEMY_THREE),
							Enemy.TOPLAYER);
					add(enemyTwo, GAME);
				}
			}
		}

	}

	private Vector2f getRandomPosition() {
		Random rnd = new Random();
		Vector2f pos = new Vector2f();
		int max = 100;
		int i = 0;
		float dist = 0;
		do {
			i++;
			float ex = rnd.nextInt(container.getWidth() - 100) + 50;
			float ey = rnd.nextInt(container.getHeight() - 100) + 50;
			pos = new Vector2f(ex, ey);
			Vector2f current = new Vector2f(player.x, player.y);
			dist = current.distance(pos);
		} while (i < max && dist < 100);

		return pos;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		super.render(container, game, g);
		// render gui
		g.drawString("Level " + level, 10, 10);
		if (player != null) {
			g.drawString("Life " + player.hp, container.getWidth() - 120, 10);
		}
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		buildDefault();
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		super.init(container, game);
		buildDefault();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		super.update(container, game, delta);

		if (getEntities(Enemy.ENEMY).isEmpty()) {
			level++;
			buildDefault();
		}
		if (player == null || player.dead) {
			System.out.println("GAME OVER!");
			level = 1;
			game.enterState(Launcher.MENU_STATE);
		}
	}

}