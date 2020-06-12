package com.maxim;

import com.badlogic.gdx.Screen;
import com.maxim.screens.MainMenuScreen;
import com.maxim.screens.PauseScreen;
import com.maxim.screens.PlayMenuScreen;
import com.maxim.screens.PlayScreen;

import java.util.HashMap;

public class ScreenManager {
    private final Application game;
    private HashMap<State, Screen> gameScreens;

    public enum State {
        MAIN_MENU,
        PLAY_MENU,
        PLAY,
        PAUSE,
    }

    public ScreenManager(final Application game) {
        this.game = game;
        gameScreens = new HashMap<State, Screen>();
        this.gameScreens.put(State.MAIN_MENU, new MainMenuScreen(game));
        this.gameScreens.put(State.PLAY_MENU, new PlayMenuScreen(game));
        this.gameScreens.put(State.PAUSE, new PauseScreen(game));
        this.gameScreens.put(State.PLAY, new PlayScreen(game));
    }

    public Application getGame() {
        return game;
    }

    public void setScreen(State nextScreen) {
        game.setScreen(gameScreens.get(nextScreen));
    }

    public Screen getScreen(State screen) {
        return gameScreens.get(screen);
    }

    public void dispose() {
        for (Screen screen : gameScreens.values()) {
            if (screen != null) {
                screen.dispose();
            }
        }
    }
}
