package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.maxim.Application;
import com.maxim.ScreenManager;

public class PauseScreen extends AbstractMenuScreen {

    public PauseScreen(final Application game) {
        super(game);
        init();
    }

    private void init(){
        Table table = new Table(skin);
        table.setFillParent(true);

        TextButton resume = new TextButton("Continue", skin, "defaultStyle");
        resume.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.screenManager.getScreen(ScreenManager.State.PLAY).resume();
                game.screenManager.setScreen(ScreenManager.State.PLAY);
            }
        });

        TextButton restart = new TextButton("Restart", skin, "defaultStyle");
        restart.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                PlayScreen playScreen = (PlayScreen)game.screenManager.getScreen(ScreenManager.State.PLAY);
                playScreen.restart();
                game.screenManager.getScreen(ScreenManager.State.PLAY).resume();
                game.screenManager.setScreen(ScreenManager.State.PLAY);
            }
        });

        TextButton mainMenu = new TextButton("Back to main menu", skin, "defaultStyle");
        mainMenu.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.screenManager.setScreen(ScreenManager.State.MAIN_MENU);
                game.track.getPlayer().disposeBodies();
            }
        });

        table.add(resume).row();
        table.add(restart).row();
        table.add(mainMenu).row();
        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
