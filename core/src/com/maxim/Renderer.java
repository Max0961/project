package com.maxim;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.maxim.utils.Box2DSprite;
import com.maxim.utils.LandscapeGenerator;

public class Renderer {
    private Application game;
    private Camera camera;

    private float[][] h;
    private float spm;

    private final float[] vertices = new float[20];

    private Vector2 v0 = new Vector2();
    private Vector2 v1 = new Vector2();
    private Vector2 v2 = new Vector2();
    private Vector2 v3 = new Vector2();

    private Color c0 = new Color();
    private Color c1 = new Color();
    private Color c2 = new Color();
    private Color c3 = new Color();

    private Texture texture;

    public Renderer(Application game, Camera cam) {
        this.camera = cam;
        this.game = game;
        texture = game.track.getGroundTexture();
        h = game.track.getHeights();
    }

    public void render() {
        spm = game.track.getSegmentsPerMeter();
        float leftScreenBounding = camera.position.x - 0.5f * camera.viewportWidth;
        float rightScreenBounding = leftScreenBounding + camera.viewportWidth;
        for (int i = 1; i < h.length - 2; ++i) {
            if (i == game.track.getFixtureLineIndex() + 1) {
                Box2DSprite.draw(game.batch, game.track.getWorld());
            }

            v0.x = leftScreenBounding;
            v0.y = 0;
            perspectiveUpdate(camera, v0, (game.track.getFixtureLineIndex() - i) / spm);
            float lvc = v0.x;

            v0.x = rightScreenBounding;
            v0.y = 0;
            perspectiveUpdate(camera, v0, (game.track.getFixtureLineIndex() - i) / spm);
            float rvc = v0.x;

            for (int j = Math.max(1, (int) (lvc * spm - 2)); j < Math.min(h[0].length - 2,
                    rvc * spm + 2); ++j) {
                float light = 0.5f;

                // upper right
                v0.x = j / spm;
                v0.y = h[i - 1][j];
                perspectiveUpdate(camera, v0, (i - 1 - game.track.getFixtureLineIndex()) / spm);

                float f = getShadow(i, j);
                c0.r = light + f;
                c0.g = (light + f) * 0.98f;
                c0.b = (light + f) * 0.94f;
                c0.a = 1;

                // bottom right
                v1.x = j / spm;
                v1.y = h[i][j];
                perspectiveUpdate(camera, v1, (i - game.track.getFixtureLineIndex()) / spm);

                f = getShadow(i + 1, j);
                c1.r = light + f;
                c1.g = (light + f) * 0.98f;
                c1.b = (light + f) * 0.94f;;
                c1.a = 1;

                if (j > lvc * spm - 2) {
                    setUp(i, j, v3, v2, v0, v1);
                    game.batch.draw(texture, vertices, 0, 20);
                }

                // upper left
                v2.x = v0.x;
                v2.y = v0.y;
                c2.r = c0.r;
                c2.g = c0.g;
                c2.b = c0.b;
                c2.a = c0.a;

                // bottom left
                v3.x = v1.x;
                v3.y = v1.y;
                c3.r = c1.r;
                c3.g = c1.g;
                c3.b = c1.b;
                c3.a = c1.a;
            }
        }
    }

    private float getShadow(int i, int j) {
        float x = ((h[i][j] - h[i][j - 1]) + (h[i][j + 1] - h[i][j]));
        float y = ((h[i][j] - h[i - 1][j]) + (h[i + 1][j] - h[i][j]));
        return 0.8f * MathUtils.atan2((-x + 2 / spm + 1.0f * y), 1 / spm) / (float) Math.PI;
    }

    private void perspectiveUpdate(Camera camera, Vector2 point, float distance) {
        point.sub(new Vector2(camera.position.x, camera.position.y));
        point.scl(2 * LandscapeGenerator.WIDTH / (2 * LandscapeGenerator.WIDTH - distance));
        point.add(camera.position.x, camera.position.y);
    }

    public void setUp(int i, int j, Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3) {
        int idx = 0;

        final float u = (game.track.getMetersPerTextureSize() * j % spm) / spm;
        final float v = (game.track.getMetersPerTextureSize() * i % spm + game.track.getMetersPerTextureSize()) / spm;
        final float u_2 = (game.track.getMetersPerTextureSize() * j % spm + game.track.getMetersPerTextureSize()) / spm;
        final float v_2 = (game.track.getMetersPerTextureSize() * i % spm) / spm;

        vertices[idx++] = v0.x;
        vertices[idx++] = v0.y;
        vertices[idx++] = c3.toFloatBits();
        vertices[idx++] = u;
        vertices[idx++] = v;

        vertices[idx++] = v1.x;
        vertices[idx++] = v1.y;
        vertices[idx++] = c2.toFloatBits();
        vertices[idx++] = u;
        vertices[idx++] = v_2;

        vertices[idx++] = v2.x;
        vertices[idx++] = v2.y;
        vertices[idx++] = c0.toFloatBits();
        vertices[idx++] = u_2;
        vertices[idx++] = v_2;

        vertices[idx++] = v3.x;
        vertices[idx++] = v3.y;
        vertices[idx++] = c1.toFloatBits();
        vertices[idx++] = u_2;
        vertices[idx++] = v;
    }
}