package com.maxim.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class LandscapeGenerator {
    public static final float WIDTH = 4;
    private static final float PERSISTENCE = 0.5f;

    private int seed;
    private static LandscapeGenerator landscapeGenerator = new LandscapeGenerator();

    private static float noise(int x, int y) {
        int n = x + landscapeGenerator.seed * y;
        n = (n << 13) ^ n;
        return (1.0f - ((n * (n * n * 15731 + 789221) + 1376312589) & 0X7fffffff) / 1073741824f);
    }

    private static Vector2 getPseudoRandomGradientVector(int x, int y) {
        float angle = MathUtils.PI * noise(x, y);
        return new Vector2(MathUtils.cos(angle), MathUtils.sin(angle));
    }

    private static int fastFloor(double x) {
        return x > 0 ? (int) x : (int) x - 1;
    }

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private static float cosineInterpolation(float a, float b, float x) {
        float ft = x * (float) Math.PI;
        float f = (1 - (float) Math.cos(ft)) * .5f;
        return a * (1 - f) + b * f;
    }

    private static float dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    private static float interpolatedNoise(float x, float y) {
        int integerX = fastFloor(x);
        float fractionalX = x - integerX;
        int integerY = fastFloor(y);
        float fractionalY = y - integerY;
        Vector2 v0 = getPseudoRandomGradientVector(integerX, integerY);
        Vector2 v1 = getPseudoRandomGradientVector(integerX + 1, integerY);
        Vector2 v2 = getPseudoRandomGradientVector(integerX, integerY + 1);
        Vector2 v3 = getPseudoRandomGradientVector(integerX + 1, integerY + 1);
        Vector2 distanceToTopLeft = new Vector2(fractionalX, fractionalY);
        Vector2 distanceToTopRight = new Vector2(fractionalX - 1, fractionalY);
        Vector2 distanceToBottomLeft = new Vector2(fractionalX, fractionalY - 1);
        Vector2 distanceToBottomRight = new Vector2(fractionalX - 1, fractionalY - 1);
        float tx1 = dot(distanceToTopLeft, v0);
        float tx2 = dot(distanceToTopRight, v1);
        float bx1 = dot(distanceToBottomLeft, v2);
        float bx2 = dot(distanceToBottomRight, v3);
        fractionalX = fade(fractionalX);
        fractionalY = fade(fractionalY);
        float tx = MathUtils.lerp(tx1, tx2, fractionalX);
        float bx = MathUtils.lerp(bx1, bx2, fractionalX);
        return MathUtils.lerp(tx, bx, fractionalY);
    }

    private float getNoise(float x, float y, int octaves, float steepness) {
        float total = 0;
        float amplitude = 1;
        for (int i = 0; i < octaves; ++i) {
            total += steepness * interpolatedNoise(x, y) * amplitude;
            amplitude *= PERSISTENCE;
            x *= 2;
            y *= 2;
        }
        return total;
    }

    public static float[][] generate(float startX, int seed, int bumpiness, float scope, float steepness, float length, int segmentsPerMeter) {
        float[][] heights = new float[(int) WIDTH * segmentsPerMeter][(int) ((length + 2 * startX) * segmentsPerMeter)];
        landscapeGenerator.seed = seed;
        for (int i = 0; i < heights.length; ++i)
            for (int j = 0; j < heights[0].length; ++j) {
                float x = i / (float) segmentsPerMeter / scope;
                float y = j / (float) segmentsPerMeter / scope;
                heights[i][j] = landscapeGenerator.getNoise(x * MathUtils.sin(0.5f *
                        MathUtils.PI * (i - heights.length / 8) / heights.length), y, bumpiness, steepness);
                heights[i][j] += 4 * MathUtils.cos(0.5f * MathUtils.PI *
                        (i - heights.length / 8) / heights.length);
            }
        return heights;
    }
}
