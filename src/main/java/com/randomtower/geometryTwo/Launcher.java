package com.randomtower.geometryTwo;

import java.io.IOException;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import it.marteEngine.ME;
import it.marteEngine.ResourceManager;

public class Launcher extends StateBasedGame {

	public static final int MENU_STATE = 0;
	public static final int GAME_STATE = 1;
	public static final int GAME_OVER_STATE = 2;
	public static final int WIN_STATE = 3;

	public Launcher(String title) throws IOException {
		super(title);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		try {
			ResourceManager.loadResources("resources.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		// add states
		addState(new MenuWorld(MENU_STATE, container));
		addState(new GameWorld(GAME_STATE, container));

	}

	public static void main(String[] argv) throws IOException {
		try {
			AppGameContainer container = new AppGameContainer(new Launcher("GeometryTwo"));
			ME.keyToggleDebug = Input.KEY_1;
			container.setDisplayMode(640, 480, false);
			container.setTargetFrameRate(20);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

}