package com.maxim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.maxim.screens.HUD;

public class Application extends Game {
    public static final String APP_TITLE = "Trials";
    public static final double VERSION = 0.5;
    public static float INITIAL_CAMERA_COVERAGE = 7;
    public static float MAX_CAMERA_COVERAGE = 21;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;
    public static final float TIME_STEP = 1f / 600;

    public SpriteBatch batch;
    public Track track;
    public PlayerInput playerInput;
    public ScreenManager screenManager;
    public HUD hud;

    @Override
    public void create() {
        screenManager = new ScreenManager(this);
        screenManager.setScreen(ScreenManager.State.MAIN_MENU);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        track.dispose();
        batch.dispose();
        screenManager.dispose();
    }
}
