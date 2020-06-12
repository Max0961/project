package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.maxim.Application;

public class HelpScreen extends AbstractMenuScreen {
    private TextButton back;
    private Table table;
    private String text =
            "D - accelerate\n" +
                    "S - brake\n" +
                    "K - lean backward\n" +
                    "L - lean forward\n";

    public HelpScreen(final Application game) {
        super(game);
    }

    @Override
    public void show() {
        table = new Table();
        table.setFillParent(true);

        back = new TextButton("Back", skin, "defaultStyle");
        back.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.vibrate(20);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        table.add(new Label(text, skin, "defaultStyle")).row();
        table.add(back).row();
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
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
        Gdx.input.setInputProcessor(null);
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
