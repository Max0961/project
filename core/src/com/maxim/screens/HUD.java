package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.maxim.Application;
import com.maxim.entities.Player;

public class HUD implements Screen {

    protected Stage stage;
    protected Label label, state;
    protected BitmapFont font;
    protected long startTime;
    public boolean isStart = false;
    public boolean isCrashed = false;
    public boolean isFinish = false;
    private float raceTime;
    private Sprite button;
    private Application game;

    public HUD(Application game) {
        this.game = game;
        font = new BitmapFont();
        label = new Label("", new Label.LabelStyle(font, Color.RED));
        state = new Label("", new Label.LabelStyle(font, Color.RED));
        state.setPosition(Gdx.graphics.getWidth() / 2 - state.getPrefWidth() / 2,
                Gdx.graphics.getHeight() / 2 - state.getPrefHeight() / 2);

        stage = new Stage();
        stage.addActor(label);
        stage.addActor(state);
        button = new Sprite(new Texture("img/button.png"));
    }

    public void setStartTime(long time) {
        if (!isStart)
            startTime = time;
        isStart = true;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        StringBuilder builder = new StringBuilder();
        builder.append(Gdx.graphics.getFramesPerSecond()).append(" FPS");
        if (isStart && !isFinish) {
            raceTime = (TimeUtils.millis() - startTime) / (float) 1000;
            builder.append("\ntime: ").append(raceTime);
        }
        builder.append("\nspeed: ").append((float) Math.round(3600 * game.track.getPlayer().getVelocity().len()) / 1000)
                .append("\nacceleration: ").append((float) Math.round(1000 * game.track.getPlayer().getAcceleration()) / 1000)
                .append("\ngear: ").append(game.track.getPlayer().getGear())
                .append("\ntorque: ").append(Math.round(1000 * game.track.getPlayer().getCurrentEngineTorque()) / 1000f)
                .append("\nprogress: ").append(Math.round(10 * (game.track.getPlayer().getPosition().x - game.track.getStartX())) / 10f)
                .append("/").append(Math.round(10 * (game.track.getTrackLength())) / 10f)
                .append("\nheight: ").append(game.track.getPlayer().getPosition().y);

        label.setText(builder);
        label.setPosition(0, Gdx.graphics.getHeight() - 100);
        if (isCrashed) {
            state.setText("CRASHED");
        } else {
            state.setText("");
        }
        if (game.track.getPlayer().getPosition().x > game.track.getRightBounding() - game.track.getLeftBounding()) {
            state.setText("FINISH");
            isFinish = true;
        }
        stage.draw();
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
        stage.dispose();
        font.dispose();
    }
}
