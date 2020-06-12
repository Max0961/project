package com.maxim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.maxim.screens.PlayScreen;

public class PlayerInput implements InputProcessor {

    private int
            accelerateKey,
            brakeKey,
            backKey,
            forwardKey;

    private PlayScreen playScreen;

    public void set(int accelerateKey, int brakeKey, int backKey, int forwardKey) {
        this.accelerateKey = accelerateKey;
        this.brakeKey = brakeKey;
        this.backKey = backKey;
        this.forwardKey = forwardKey;
    }

    public PlayerInput(PlayScreen playScreen) {
        this.playScreen = playScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == accelerateKey) playScreen.game.track.getPlayer().accelerates = true;
        if (keycode == brakeKey) playScreen.game.track.getPlayer().brakes = true;
        if (keycode == backKey) playScreen.game.track.getPlayer().backwardLean = true;
        if (keycode == forwardKey) playScreen.game.track.getPlayer().forwardLean = true;
        if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
            playScreen.pause();
            playScreen.game.screenManager.setScreen(ScreenManager.State.PAUSE);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == accelerateKey) playScreen.game.track.getPlayer().accelerates = false;
        if (keycode == brakeKey) playScreen.game.track.getPlayer().brakes = false;
        if (keycode == backKey) playScreen.game.track.getPlayer().backwardLean = false;
        if (keycode == forwardKey) playScreen.game.track.getPlayer().forwardLean = false;
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float space = Gdx.graphics.getWidth() / 8;
        if (Gdx.input.isTouched() && screenY > 0.75 * Gdx.graphics.getHeight()) {
            if (screenX <= space) {
                playScreen.game.track.getPlayer().accelerates = true;
            } else if (screenX < 2 * space) {
                playScreen.game.track.getPlayer().brakes = true;
            }
            if (screenX >= 7 * space) {
                playScreen.game.track.getPlayer().forwardLean = true;
            } else if (screenX > 6 * space) {
                playScreen.game.track.getPlayer().backwardLean = true;
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float space = Gdx.graphics.getWidth() / 8;
        if (screenY > 0.75 * Gdx.graphics.getHeight()) {
            if (screenX <= space) {
                playScreen.game.track.getPlayer().accelerates = false;
            } else if (screenX < 2 * space) {
                playScreen.game.track.getPlayer().brakes = false;
            }
            if (screenX >= 7 * space) {
                playScreen.game.track.getPlayer().forwardLean = false;
            } else if (screenX > 6 * space) {
                playScreen.game.track.getPlayer().backwardLean = false;
            }
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
