package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.maxim.Application;
import com.maxim.ScreenManager;

public class MainMenuScreen extends AbstractMenuScreen {

    private Sprite bg;

    public MainMenuScreen(final Application game) {
        super(game);
        init();
    }

    private void init() {
        Label title = new Label(Application.APP_TITLE, skin, "bigStyle");
        bg = new Sprite(new Texture("img/title_bg.jpg"));

        Table table = new Table();
        table.setFillParent(true);

        TextButton play = new TextButton("Play", skin, "defaultStyle");
        play.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.screenManager.setScreen(ScreenManager.State.PLAY_MENU);
            }
        });

        TextButton help = new TextButton("Help", skin, "defaultStyle");
        help.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new HelpScreen(game));
            }
        });

        TextButton quit = new TextButton("Quit", skin, "defaultStyle");
        quit.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

        table.add(title).spaceBottom(50).row();
        table.add(play).row();
        table.add(help).row();
        table.add(quit).row();
        table.add(new Label("Version: " + Double.toString(Application.VERSION), skin, "defaultStyle")).bottom();
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
