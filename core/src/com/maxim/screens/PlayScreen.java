package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.maxim.Application;
import com.maxim.Track;
import com.maxim.PlayerInput;
import com.maxim.entities.Player;
import com.maxim.Renderer;

public class PlayScreen implements Screen {
    public final Application game;

    private OrthographicCamera cam;
    private Box2DDebugRenderer debugRenderer;
    private Renderer renderer;
    private boolean paused = false;
    private float dz = 0;
    private float accumulator = 0;
    private float aspectRatio = Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

    public PlayScreen(final Application game) {
        this.game = game;
        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        game.track = new Track(game);
        game.playerInput = new PlayerInput(this);
        game.playerInput.set(com.badlogic.gdx.Input.Keys.D, com.badlogic.gdx.Input.Keys.S, com.badlogic.gdx.Input.Keys.K, com.badlogic.gdx.Input.Keys.L);
    }

    public void start(int index) {
        game.track.createTrack(index);
        game.track.createPlayer();
        game.batch.begin();
        renderer = new Renderer(game, cam);
        game.batch.end();
        cameraInit();
        game.hud = new HUD(game);
    }

    public void restart() {
        game.track.getPlayer().respawn();
        game.hud = new HUD(game);
        cameraInit();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(game.playerInput);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

        game.batch.begin();
        game.track.drawBackground(cam);
        renderer.render();
        game.batch.end();
        game.hud.render(delta);
        //debugRenderer.render(game.track.getWorld(), cam.combined);

        if (!paused) {
            doPhysicsStep(delta);
        }

        game.batch.setProjectionMatrix(cam.combined);
    }

    private void doPhysicsStep(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= Application.TIME_STEP) {
            accumulator -= Application.TIME_STEP;
            game.track.getWorld().step(Application.TIME_STEP, Application.VELOCITY_ITERATIONS, Application.POSITION_ITERATIONS);
            game.track.getPlayer().updateState(Application.TIME_STEP);
            cameraUpdate(game.track.getPlayer());
        }
    }

    private void cameraInit() {
        cam.viewportWidth = Application.INITIAL_CAMERA_COVERAGE;
        cam.position.x = game.track.getPlayer().getPosition().x + cam.viewportWidth * 0.25f;
        cam.position.y = game.track.getPlayer().getPosition().y;
        cam.update();
    }

    private void cameraUpdate(Player plr) {
        cam.viewportWidth = Math.min(Application.INITIAL_CAMERA_COVERAGE + dz, Application.MAX_CAMERA_COVERAGE);
        cam.viewportHeight = cam.viewportWidth / aspectRatio;
        dz += 0.002f * (0.5f * plr.getVelocity().len() - dz);

        float dx = MathUtils.clamp(plr.getPosition().x, game.track.getLeftBounding(), game.track.getRightBounding())
                - cam.position.x + 0.25f * cam.viewportWidth;
        float dy = plr.getPosition().y - cam.position.y;

        cam.position.x += 0.02f * Math.signum(dx) * (float) Math.pow(dx, 2);
        cam.position.y += 0.02f * Math.signum(dy) * (float) Math.pow(dy, 2);

        this.cam.update();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
    }
}
