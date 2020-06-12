package com.maxim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;
import com.maxim.entities.Player;
import com.maxim.handlers.GameContactListener;
import com.maxim.utils.Box2DSprite;
import com.maxim.utils.LandscapeGenerator;

import static com.badlogic.gdx.physics.box2d.Box2D.init;

public class Track {
    public static float startX = 20;
    public static GameContactListener contactListener;

    private Application game;
    private World world;
    private Player player;
    private Box2DSprite background;
    private Texture groundTexture;
    private Body groundBody;
    private float leftBounding, rightBounding, trackLength;
    private float[][] heights;
    private int segmentsPerMeter;
    private int fixtureLineIndex = 0;
    private float backgroundAspectRatio = 0;
    private float screenAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
    private float metersPerTextureSize;
    private String name;

    public float[][] getHeights() {
        return heights;
    }

    public int getSegmentsPerMeter() {
        return segmentsPerMeter;
    }

    public World getWorld() {
        return world;
    }

    public Player getPlayer() {
        return player;
    }

    public Body getGroundBody() {
        return groundBody;
    }

    public float getLeftBounding() {
        return leftBounding;
    }

    public float getRightBounding() {
        return rightBounding;
    }

    public float getTrackLength() {
        return trackLength;
    }

    public float getStartX() {
        return startX;
    }

    public Application getGame() {
        return game;
    }

    public int getFixtureLineIndex() {
        return fixtureLineIndex;
    }

    public Texture getGroundTexture() {
        return groundTexture;
    }

    public float getMetersPerTextureSize() {
        return metersPerTextureSize;
    }

    public Track(Application game) {
        init();
        world = new World(new Vector2(0, 1 * -9.8f), false);
        world.setContactListener(new GameContactListener(this));
        this.game = game;
        contactListener = new GameContactListener(this);
    }

    public void createPlayer() {
        player = new Player(world);
        player.createPlayer(new Vector2(startX, findPlayerY()));
    }

    public float findPlayerY() {
        return heights[fixtureLineIndex][Math.round(startX * segmentsPerMeter)] + 2;
    }

    public void drawBackground(OrthographicCamera camera) {
        float height = Application.MAX_CAMERA_COVERAGE / screenAspectRatio;
        float width = height * backgroundAspectRatio;
        background.setSize(width, height);
        background.setPosition(
                camera.position.x - 0.5f * Application.MAX_CAMERA_COVERAGE
                        - (width - Application.MAX_CAMERA_COVERAGE) / (Math.max(trackLength + startX, width * 10)) * (camera.position.x - startX),
                camera.position.y - 0.5f * height
        );
        background.draw(game.batch);
    }

    public void createTrack(int index) {
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element root = xmlReader.parse(Gdx.files.internal("tracks/options.xml"));
        XmlReader.Element trackElement = root.getChild(index - 1);
        name = trackElement.getAttribute("name");
        background = new Box2DSprite(new Texture(trackElement.get("bg")));
        backgroundAspectRatio = background.getWidth() / background.getHeight();
        background.setOrigin(background.getWidth() / 2, background.getHeight() / 2);
        groundTexture = new Texture(trackElement.get("ground"));
        segmentsPerMeter = (int) (16 * Math.tan((float) trackElement.getInt("bumpiness") / 16));
        metersPerTextureSize = trackElement.getFloat("tpm");
        heights = LandscapeGenerator.generate(startX,
                trackElement.getInt("seed"),
                trackElement.getInt("bumpiness"),
                trackElement.getInt("scope"),
                trackElement.getInt("steepness"),
                trackElement.getInt("length"),
                segmentsPerMeter);
        createGroundBody();
        leftBounding = startX / 2;
        rightBounding = heights[0].length / segmentsPerMeter - startX;
        trackLength = heights[0].length / segmentsPerMeter - 2 * startX;
    }

    private void createGroundBody() {
        if (groundBody != null) world.destroyBody(groundBody);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        groundBody = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureLineIndex = heights.length / 8;
        EdgeShape edgeShape = new EdgeShape();
        Filter filter = new Filter();
        filter.categoryBits = 0xfff;
        Vector2 v0 = new Vector2();
        Vector2 v1 = new Vector2();
        for (int i = 1; i < heights[0].length; ++i) {
            v0.x = (i - 1) / (float) segmentsPerMeter;
            v0.y = heights[fixtureLineIndex][i - 1];
            v1.x = i / (float) segmentsPerMeter;
            v1.y = heights[fixtureLineIndex][i];
            edgeShape.set(v0, v1);
            fixtureDef.shape = edgeShape;
            fixtureDef.friction = 1.0f;
            fixtureDef.restitution = 0;
            groundBody.createFixture(fixtureDef).setFilterData(filter);
        }
    }

    public void dispose() {
        if (world != null) world.dispose();
    }
}
