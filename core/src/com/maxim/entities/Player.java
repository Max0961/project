package com.maxim.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.maxim.utils.Box2DSprite;

public class Player {
    public boolean forwardLean, backwardLean, brakes, accelerates, crashed;

    private static final float REAR_BRAKE_MAX_TORQUE = 330;
    private static final float FRONT_BRAKE_MAX_TORQUE = 330;
    private static final float CONTROL_RATE = 0.1f;

    private World world;
    private PhysicsShapeCache motorcyclePhysicsBodies, racerPhysicsBodies;
    private Body[] motorcycle, racer;
    private WheelJoint rearSpring, frontSpring;
    private FrictionJoint rearBrake;
    private RevoluteJoint racerMovement[];
    private Vector2 startPosition;
    private TextureAtlas motorcycleTextureAtlas, racerTextureAtlas;
    private Array<TextureAtlas.AtlasRegion> motorcycleRegions, racerRegions;
    private float engineRate, brakesRate;
    private float scale;
    private float acceleration;
    private float velocity;

    public Player(World world) {
        this.world = world;
    }

    public Body[] getRacerBodies() {
        return racer;
    }

    public Vector2 getPosition() {
        return  motorcycle[0].getWorldCenter();
    }

    public Body[] getWheelsBodies() {
        return new Body[]{motorcycle[5], motorcycle[7]};
    }

    public Vector2 getVelocity() {
        return motorcycle[0].getLinearVelocity();
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getGear() {
        return Transmission.gear;
    }

    public float getCurrentEngineTorque() {
        return rearSpring.getMotorTorque(60);
    }

    public float getRearWheelSpeed() {
        return rearSpring.getJointSpeed();
    }

    public float getRacerMass() {
        float total = 0;
        for (Body body : racer) total += body.getMass();
        return total;
    }

    public float getMotorcycleMass() {
        float total = 0;
        for (Body body : motorcycle) total += body.getMass();
        return total;
    }

    public void createPlayer(Vector2 playerPosition) {
        startPosition = playerPosition;
        motorcyclePhysicsBodies = new PhysicsShapeCache("models/motorcycle.xml");
        motorcycleTextureAtlas = new TextureAtlas("img/motorcycle.txt");
        motorcycleRegions = motorcycleTextureAtlas.getRegions();
        racerPhysicsBodies = new PhysicsShapeCache("models/racer.xml");
        racerTextureAtlas = new TextureAtlas("img/racer.txt");
        racerRegions = racerTextureAtlas.getRegions();
        scale = 1 / motorcyclePhysicsBodies.getPTM();
        memoryAllocation();
        createBodies(startPosition);
        createJoints();
    }

    public void respawn() {
        crashed = false;
        disposeBodies();
        createBodies(startPosition);
        createJoints();
    }

    public void disposeBodies() {
        for (Body body : motorcycle) {
            body.getWorld().destroyBody(body);
        }
        for (Body body : racer) {
            body.getWorld().destroyBody(body);
        }
    }

    private void memoryAllocation() {
        motorcycle = new Body[motorcycleRegions.size];
        racer = new Body[racerRegions.size];
        racerMovement = new RevoluteJoint[7];
    }

    private void createBodies(Vector2 pos) {
        Box2DSprite sprite;
        int i = 0;
        for (TextureAtlas.AtlasRegion region : motorcycleRegions) {
            sprite = new Box2DSprite();
            sprite.set(motorcycleTextureAtlas.createSprite(region.name));
            sprite.setScale(scale);
            sprite.setOrigin(0, 0);
            sprite.name = region.name;
            motorcycle[i] = motorcyclePhysicsBodies.createBody(region.name, world, scale, scale);
            motorcycle[i].setUserData(sprite);
            ++i;
        }
        i = 0;
        for (TextureAtlas.AtlasRegion region : racerRegions) {
            sprite = new Box2DSprite();
            sprite.set(racerTextureAtlas.createSprite(region.name));
            sprite.setScale(scale);
            sprite.setOrigin(0, 0);
            sprite.name = region.name;
            racer[i] = racerPhysicsBodies.createBody(region.name, world, scale, scale);
            racer[i].setUserData(sprite);
            ++i;
        }

        // rear wheel
        sprite = (Box2DSprite) motorcycle[7].getUserData();
        sprite.z = 0;
        motorcycle[7].setTransform(pos.x - motorcycle[7].getWorldCenter().x, pos.y - motorcycle[7].getWorldCenter().y, 0);

        // front wheel
        sprite = (Box2DSprite) motorcycle[5].getUserData();
        sprite.z = 0;
        motorcycle[5].setTransform(pos.x - motorcycle[5].getWorldCenter().x + 1.32f, pos.y - motorcycle[5].getWorldCenter().y, 0);

        // fork
        sprite = (Box2DSprite) motorcycle[2].getUserData();
        sprite.z = 4;
        motorcycle[2].setTransform(pos.x + 0.954f, pos.y - 0.028f, 0);

        // body
        sprite = (Box2DSprite) motorcycle[0].getUserData();
        sprite.z = 3;
        motorcycle[0].setTransform(pos.x - 0.2205f, pos.y - 0.012f, 0);

        // swingarmTorque
        sprite = (Box2DSprite) motorcycle[10].getUserData();
        sprite.z = 2;
        motorcycle[10].setTransform(pos.x - 0.029f, pos.y - 0.035f, 0);

        // rear brake disk
        sprite = (Box2DSprite) motorcycle[6].getUserData();
        sprite.z = 1;
        motorcycle[6].setTransform(pos.x - motorcycle[6].getWorldCenter().x, pos.y - motorcycle[6].getWorldCenter().y, 0);

        // front brake disk
        sprite = (Box2DSprite) motorcycle[4].getUserData();
        sprite.z = -2;
        motorcycle[4].setTransform(pos.x - motorcycle[4].getWorldCenter().x + 1.32f, pos.y - motorcycle[4].getWorldCenter().y, 0);

        // sprocket
        sprite = (Box2DSprite) motorcycle[9].getUserData();
        sprite.z = -2;
        motorcycle[9].setTransform(pos.x - motorcycle[9].getWorldCenter().x, pos.y - motorcycle[9].getWorldCenter().y, 0);

        // shell
        sprite = (Box2DSprite) motorcycle[8].getUserData();
        sprite.z = -3;
        motorcycle[8].setTransform(pos.x - motorcycle[8].getWorldCenter().x + 1.32f, pos.y - motorcycle[8].getWorldCenter().y, 0);

        // chain
        sprite = (Box2DSprite) motorcycle[1].getUserData();
        sprite.z = -1;
        motorcycle[1].setTransform(pos.x - motorcycle[1].getWorldCenter().x, pos.y - motorcycle[1].getWorldCenter().y, 0);

        // brake
        sprite = (Box2DSprite) motorcycle[3].getUserData();
        sprite.z = -1;
        motorcycle[3].setTransform(pos.x - motorcycle[3].getWorldCenter().x + 1.31f, pos.y - motorcycle[3].getWorldCenter().y + 0.08f, 0);

//        createWheel (motorcycle[7]);
//        createWheel (motorcycle[5]);

        // arm
        sprite = (Box2DSprite) racer[0].getUserData();
        sprite.z = 5;
        racer[0].setTransform(pos.x + 0.6f, pos.y + 0.765f, 0);

        // head
        sprite = (Box2DSprite) racer[1].getUserData();
        sprite.z = -1;
        racer[1].setTransform(pos.x + 0.505f, pos.y + 1.16f, 0);

        // hip
        sprite = (Box2DSprite) racer[2].getUserData();
        sprite.z = 4;
        racer[2].setTransform(pos.x + 0.14f, pos.y + 0.47f, 0);

        // leg
        sprite = (Box2DSprite) racer[3].getUserData();
        sprite.z = 5;
        racer[3].setTransform(pos.x + 0.316f, pos.y + 0.01f, 0);

        // shoulder
        sprite = (Box2DSprite) racer[4].getUserData();
        sprite.z = 4;
        racer[4].setTransform(pos.x + 0.46f, pos.y + 0.89f, 0);

        // torso
        sprite = (Box2DSprite) racer[5].getUserData();
        sprite.z = 0;
        racer[5].setTransform(pos.x + 0.1f, pos.y + 0.64f, 0);
    }

    private void createJoints() {
        WheelJointDef whjd = new WheelJointDef();
        RevoluteJointDef rjd = new RevoluteJointDef();
        PrismaticJointDef pjd = new PrismaticJointDef();
        WeldJointDef wjd = new WeldJointDef();
        FrictionJointDef fjd = new FrictionJointDef();
        DistanceJointDef djd = new DistanceJointDef();

        // wheels begin
        whjd.initialize(motorcycle[0], motorcycle[7], motorcycle[7].getWorldCenter(), new Vector2(0, 0));
        whjd.frequencyHz = 0.0f;
        whjd.dampingRatio = 0.0f;
        rearSpring = (WheelJoint) world.createJoint(whjd);
        rearSpring.enableMotor(true);

        whjd.initialize(motorcycle[0], motorcycle[5], motorcycle[5].getWorldCenter(), new Vector2(-0.3907311285f, 1.0f));
        whjd.frequencyHz = 7.0f;
        whjd.dampingRatio = 0.5f;
        frontSpring = (WheelJoint) world.createJoint(whjd);
        frontSpring.enableMotor(true);
        // wheels end

        // swingarmTorque
        rjd.initialize(motorcycle[10], motorcycle[0], motorcycle[7].getWorldCenter().add(0.52f, 0.08f));
        rjd.enableLimit = true;
        rjd.enableMotor = false;
        rjd.upperAngle = 0.34f;
        world.createJoint(rjd);
        rjd.initialize(motorcycle[7], motorcycle[10], motorcycle[7].getWorldCenter());
        rjd.enableLimit = false;
        rjd.enableMotor = false;
        world.createJoint(rjd);
        djd.initialize(motorcycle[0], motorcycle[7], motorcycle[7].getWorldCenter(), motorcycle[7].getWorldCenter());
        djd.frequencyHz = 7.0f;
        djd.dampingRatio = 0.5f;
        world.createJoint(djd);

        // rear brake
        fjd.initialize(motorcycle[0], motorcycle[7], motorcycle[7].getWorldCenter());
        rearBrake = (FrictionJoint) world.createJoint(fjd);

        // fork
        pjd.initialize(motorcycle[0], motorcycle[2], motorcycle[0].getPosition().add(0.999f, 0.682f), new Vector2(-0.390731128f, 1.0f));
        pjd.enableLimit = true;
        pjd.upperTranslation = 0.17f;
        world.createJoint(pjd);
        rjd.initialize(motorcycle[5], motorcycle[2], motorcycle[5].getWorldCenter());
        world.createJoint(rjd);

        // rest
        wjd.initialize(motorcycle[7], motorcycle[6], motorcycle[7].getWorldCenter());
        world.createJoint(wjd);

        wjd.initialize(motorcycle[5], motorcycle[4], motorcycle[5].getWorldCenter());
        world.createJoint(wjd);

        wjd.initialize(motorcycle[7], motorcycle[9], motorcycle[7].getWorldCenter());
        world.createJoint(wjd);

        wjd.initialize(motorcycle[2], motorcycle[8], motorcycle[2].getWorldCenter());
        world.createJoint(wjd);

        wjd.initialize(motorcycle[10], motorcycle[1], motorcycle[10].getWorldCenter());
        world.createJoint(wjd);

        wjd.initialize(motorcycle[2], motorcycle[3], motorcycle[2].getWorldCenter());
        world.createJoint(wjd);

        // racer begin

        // neck
        rjd.initialize(racer[5], racer[1], racer[5].getPosition().add(0.47f, 0.585f));
        rjd.enableLimit = true;
        rjd.enableMotor = true;
        rjd.lowerAngle = -0.1f * MathUtils.PI;
        rjd.upperAngle = 0.15f * MathUtils.PI;
        racerMovement[0] = (RevoluteJoint) world.createJoint(rjd);

        // hip
        rjd.initialize(racer[5], racer[2], racer[5].getPosition().add(0.11f, 0.09f));
        rjd.enableLimit = true;
        rjd.enableMotor = true;
        rjd.lowerAngle = -0.5f * MathUtils.PI;
        rjd.upperAngle = 0.4f * MathUtils.PI;
        racerMovement[1] = (RevoluteJoint) world.createJoint(rjd);

        // knee
        rjd.initialize(racer[2], racer[3], racer[2].getPosition().add(0.395f, 0.05f));
        rjd.enableLimit = true;
        rjd.enableMotor = true;
        rjd.upperAngle = 0.4f * MathUtils.PI;
        rjd.lowerAngle = -0.4f * MathUtils.PI;
        racerMovement[2] = (RevoluteJoint) world.createJoint(rjd);

        // footrest
        rjd.initialize(motorcycle[0], racer[3], motorcycle[0].getPosition().add(0.635f, 0.03f));
        rjd.enableLimit = false;
        racerMovement[3] = (RevoluteJoint) world.createJoint(rjd);

        // shoulder
        rjd.initialize(racer[5], racer[4], racer[5].getPosition().cpy().add(0.43f, 0.542f));
        rjd.enableLimit = true;
        rjd.enableMotor = true;
        rjd.lowerAngle = -0.6f * MathUtils.PI;
        rjd.upperAngle = 0.5f * MathUtils.PI;
        racerMovement[4] = (RevoluteJoint) world.createJoint(rjd);

        // elbow
        rjd.initialize(racer[4], racer[0], racer[4].getPosition().add(0.17f, 0.0355f));
        rjd.enableLimit = true;
        rjd.enableMotor = true;
        rjd.lowerAngle = -0.1f * MathUtils.PI;
        racerMovement[5] = (RevoluteJoint) world.createJoint(rjd);

        // handle
        rjd.initialize(motorcycle[0], racer[0], motorcycle[0].getPosition().add(1.13f, 0.805f));
        rjd.enableLimit = false;
        rjd.enableMotor = false;
        racerMovement[6] = (RevoluteJoint) world.createJoint(rjd);

        // racer end
    }

    public void updateState(float deltaTime) {
        acceleration = (getVelocity().len() - velocity) / deltaTime;
        velocity = getVelocity().len();

        if (!crashed) {

            // neck
            float allowedAngle = (float) Math.asin(MathUtils.sin(racer[1].getAngle()));
            racerMovement[0].setMotorSpeed(3 * (-allowedAngle));
            racerMovement[0].setMaxMotorTorque(30 * Math.abs(-0.3f - allowedAngle) + 30);

            // hip
            racerMovement[1].setMotorSpeed(3 * -(racerMovement[1].getJointAngle()));
            racerMovement[1].setMaxMotorTorque(100 * Math.abs(racerMovement[1].getJointAngle())
                    + 20 * (Math.abs(getVelocity().x)) + 200);

            //knee
            racerMovement[2].setMotorSpeed(4 * -(racerMovement[2].getJointAngle()));
            racerMovement[2].setMaxMotorTorque(100 * Math.abs(racerMovement[2].getJointAngle())
                    + 15 * (Math.abs(getVelocity().x)) + 70);

            // shoulder
            racerMovement[4].setMotorSpeed(5 * -(racerMovement[4].getJointAngle()));
            racerMovement[4].setMaxMotorTorque(100 * Math.abs(racerMovement[4].getJointAngle()) +
                    10f * (float) Math.sqrt(getVelocity().len()) + 20f);

            // elbow
            racerMovement[5].setMotorSpeed(5 * -(racerMovement[5].getJointAngle()));
            racerMovement[5].setMaxMotorTorque(150 * Math.abs(racerMovement[5].getJointAngle()) +
                    20f * (float) Math.sqrt(getVelocity().len()) + 10);

            if (backwardLean) {

                // hip
                racerMovement[1].setMotorSpeed(3 * -(racerMovement[1].getJointAngle() - 0.3f * (float) Math.PI));
                racerMovement[1].setMaxMotorTorque(100);

                // knee
                racerMovement[2].setMotorSpeed(4 * -(racerMovement[2].getJointAngle() - 0.2f * (float) Math.PI));
                racerMovement[2].setMaxMotorTorque(130 + 10 * (float) Math.sqrt(getVelocity().len()));

                // shoulder
                racerMovement[4].setMotorSpeed(5 * -(racerMovement[4].getJointAngle() - 0.4f * (float) Math.PI));
                racerMovement[4].setMaxMotorTorque(125 + 10 * (float) Math.sqrt(getVelocity().len()));

                // elbow
                racerMovement[5].setMotorSpeed(5 * -(racerMovement[5].getJointAngle() + 0.2f * (float) Math.PI));
                racerMovement[5].setMaxMotorTorque(150 + 10 * (float) Math.sqrt(getVelocity().len()));

            }

            if (forwardLean) {

                // hip
                racerMovement[1].setMotorSpeed(3 * -(racerMovement[1].getJointAngle()));
                racerMovement[1].setMaxMotorTorque(100f * Math.abs(racerMovement[1].getJointAngle())
                        + 20 * (Math.abs(getVelocity().x)) + 60);

                // knee
                racerMovement[2].setMotorSpeed(4 * -(racerMovement[2].getJointAngle() - 0.05f * (float) Math.PI));
                racerMovement[2].setMaxMotorTorque(10 * Math.abs(racerMovement[1].getJointAngle() - 0.05f * (float) Math.PI)
                        + 20 * (Math.abs(getVelocity().x)) + 300);

                // shoulder
                racerMovement[4].setMotorSpeed(5 * -(racerMovement[4].getJointAngle() + 0.5f * (float) Math.PI));
                racerMovement[4].setMaxMotorTorque(250 + 20 * (float) Math.sqrt(getVelocity().len()));

                // elbow
                racerMovement[5].setMotorSpeed(7 * -(racerMovement[5].getJointAngle() - 0.35f * (float) Math.PI));
                racerMovement[5].setMaxMotorTorque(150 + 10 * (float) Math.sqrt(getVelocity().len()));

            }

            if (brakes) {
                brakesRate += 1 / CONTROL_RATE * deltaTime;
                if (brakesRate > 1) brakesRate = 1;
            } else {
                brakesRate -= 1 / CONTROL_RATE * deltaTime;
                if (brakesRate < 0) brakesRate = 0;
            }

            if (accelerates) {
                engineRate += 1 / CONTROL_RATE * deltaTime;
                if (engineRate > 1) engineRate = 1;
            } else {
                engineRate -= 1 / CONTROL_RATE * deltaTime;
                if (engineRate < 0) engineRate = 0;
            }

            Transmission.update(this);

            rearSpring.setMotorSpeed(Transmission.currentMaxSpeed);
            rearSpring.setMaxMotorTorque(engineRate * Transmission.totalReduction * Transmission.currentTorque);

            rearBrake.setMaxTorque(brakesRate * REAR_BRAKE_MAX_TORQUE);

            frontSpring.setMotorSpeed(0);
            frontSpring.setMaxMotorTorque(FRONT_BRAKE_MAX_TORQUE * brakesRate);

        } else {

            rearSpring.enableMotor(false);
            rearBrake.setMaxTorque(0);
            frontSpring.enableMotor(false);
            for (RevoluteJoint joint : racerMovement)
                if (joint != null) {
                    joint.enableMotor(false);
                }
            if (racerMovement[3] != null) {
                world.destroyJoint(racerMovement[3]);
            }
            racerMovement[3] = null;
            if (racerMovement[6] != null) {
                world.destroyJoint(racerMovement[6]);
            }
            racerMovement[6] = null;

        }
    }

    public static class Transmission {
        // watts
        static float power = 15000;
        // H*m
        static float maxTorque = 20;
        static float[] gearRatio = {
                3.0f,
                2.25f,
                1.6875f,
                1.265625f,
                0.94921875f
        };
        static float primaryReduction = 4.0f;
        static float secondaryReduction =2.0f;

        static int gear = 1;
        static float currentTorque;
        static float totalReduction = primaryReduction * gearRatio[gear - 1] * secondaryReduction;
        static float currentMaxSpeed = -power / (totalReduction * maxTorque);

        static long switchTime = 500;
        static long switching = 0;

        static float getReduction(int n) {
            totalReduction = primaryReduction * gearRatio[n - 1] * secondaryReduction;
            return totalReduction;
        }

        static float getMaxSpeed(int n) {
            return -power / (getReduction(n) * maxTorque);
        }

        static void setUpCurrentTorque(WheelJoint joint) {
            currentTorque = maxTorque * 0.25f * (MathUtils.sin(MathUtils.PI * joint.getJointSpeed() /
                    currentMaxSpeed + 0.5f) + 3);
        }

        static void update(Player plr) {
            setUpCurrentTorque(plr.rearSpring);
//            if (-plr.getVelocity ().x / (0.3f) < setMaxSpeed (gear) && gear < gearRatio.length) {
//                gear++;
//            }
//            if (gear > 1 && -plr.getVelocity ().x / (0.3f) - 6 > -power / (getReduction (Math.max (0, gear - 1)) * maxTorque)) {
//                gear--;
//            }m

            if (TimeUtils.millis() - switching >= switchTime) {
                if (plr.rearSpring.getJointSpeed() < 0.93f * getMaxSpeed(gear) && gear < gearRatio.length) {
                    currentMaxSpeed = getMaxSpeed(++gear);
                }
                if (gear > 1 && plr.rearSpring.getJointSpeed() > 0.93f * getMaxSpeed(gear - 1)) {
                    currentMaxSpeed = getMaxSpeed(--gear);
                }

                switching = TimeUtils.millis();
            }
        }
    }
}

