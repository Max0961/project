package com.maxim.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

public final class Box2DSprite extends Sprite {
    public float z = 0;
    public String name;

    public Box2DSprite() {
        super();
    }

    public Box2DSprite(Texture texture) {
        super(texture);
    }

    private static BodyComparator bodyComparator = new BodyComparator();
    private static Array<Body> tmpBodies = new Array<Body>();
    private static Box2DSprite sprite;

    public static void draw(Batch batch, World world) {
        world.getBodies(tmpBodies);
        tmpBodies.sort(bodyComparator);
        for (Body body : tmpBodies) {
            if (body.getUserData() instanceof Box2DSprite) {
                sprite = (Box2DSprite) body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getOriginX(), body.getPosition().y - sprite.getOriginY());
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(batch);
            }
        }
    }

    public static class BodyComparator implements Comparator<Body> {
        @Override
        public int compare(Body body1, Body body2) {
            if (body1.getUserData() instanceof Box2DSprite && body2.getUserData() instanceof Box2DSprite) {
                Box2DSprite s1 = (Box2DSprite) body1.getUserData();
                Box2DSprite s2 = (Box2DSprite) body2.getUserData();
                return (s1.z - s2.z) > 0 ? 1 : (s1.z - s2.z) < 0 ? -1 : 0;
            }
            return 0;
        }
    }
}