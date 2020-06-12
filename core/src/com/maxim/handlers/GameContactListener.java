package com.maxim.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.maxim.Track;

public class GameContactListener implements ContactListener {
    private Track track;

    public GameContactListener(Track track) {
        super();
        this.track = track;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;

        if (isRacerContact(fa, fb)) {
            track.getPlayer().crashed = true;
            track.getGame().hud.isCrashed = true;
        }

        if (isPlayerLanded(fa, fb)) {
            track.getGame().hud.setStartTime(System.currentTimeMillis());
        }
    }

    public boolean isPlayerLanded(Fixture a, Fixture b) {
        if (b.getBody() == track.getGroundBody())
            for (int i = 0; i < 2; ++i) {
                if (a.getBody() == track.getPlayer().getWheelsBodies()[i])
                    return true;
            }
        if (a.getBody() == track.getGroundBody())
            for (int i = 0; i < 2; ++i) {
                if (b.getBody() == track.getPlayer().getWheelsBodies()[i])
                    return true;
            }
        return false;
    }

    public boolean isRacerContact(Fixture a, Fixture b) {
        if (b.getBody() == track.getGroundBody())
            for (int i = 0; i < 2; ++i) {
                if (a.getBody() == track.getPlayer().getRacerBodies()[i])
                    return true;
            }
        if (a.getBody() == track.getGroundBody())
            for (int i = 0; i < 2; ++i) {
                if (b.getBody() == track.getPlayer().getRacerBodies()[i])
                    return true;
            }
        return false;
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
