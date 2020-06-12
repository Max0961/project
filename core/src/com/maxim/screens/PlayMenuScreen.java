package com.maxim.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;
import com.maxim.Application;
import com.maxim.ScreenManager;

import java.util.LinkedList;

public class PlayMenuScreen extends AbstractMenuScreen {

    public PlayMenuScreen(final Application game) {
        super(game);
        init();
    }

    private void init(){
        Table table = new Table(skin);
        table.setFillParent(true);

        XmlReader xmlReader = new XmlReader();
        XmlReader.Element root = xmlReader.parse(Gdx.files.internal("tracks/options.xml"));

        LinkedList<String> names = new LinkedList<String>();

        for (int i = 0; i < root.getChildCount(); ++i) {
            XmlReader.Element trackElement = root.getChild(i);
            names.add(trackElement.getAttribute("name"));
        }

        TextButton[] tracks = new TextButton[names.size()];
        for (int i = 0; i < root.getChildCount(); ++i) {
            tracks[i] = new TextButton(names.get(i), skin, "defaultStyle");
            final Integer index = i;
            tracks[i].addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    PlayScreen playScreen = (PlayScreen)game.screenManager.getScreen(ScreenManager.State.PLAY);
                    playScreen.start(index + 1);
                    game.screenManager.setScreen(ScreenManager.State.PLAY);
                }
            });
        }

        TextButton back = new TextButton("Back", skin, "defaultStyle");
        back.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.screenManager.setScreen(ScreenManager.State.MAIN_MENU);
            }
        });
        table.add(new Label("Select track", skin, "bigStyle")).colspan(2).expandX().spaceBottom(50).row();
        for (int i = 0; i < tracks.length; ++i) {
            table.add(tracks[i]).expandX().row();
        }
        table.add(back).expandX();
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
