package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Scaling;
import faltkullen.*;

import java.io.*;
import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {
    boolean MOUSE_PAN = true;

    SpriteBatch batch;
	Texture img;
    Stage stage;
    Skin skin;
    OrthographicCamera cam;
    int width, height;
    int centerLineY, centerLineX;
    CustomSprite selected;
    ArrayList<CustomSprite> sprites;

    private ValuesGroup values;
    private CompositionGroup composition;
    private EvolutionGroup evolution;
    private OrganogramGroup organogram;
    private FocusGroup focusGroup;
    private int focus;

    private Main main;
    private Label battlesSimulated;
    private Label generationsCompleted;
    private Texture redTeam, blueTeam;
    private TestOfMap battlefield;

    private FocusGroup under;
    private FocusGroup normalUnder;
    private OrganogramUnder organogramUnder;
    private StrategyUnder strategyUnder;

    private ArmyComposition redArmy, blueArmy, selectedArmy;
    private Leader redLeader, blueLeader;
    private Sprite redCircle, blueCircle;
    private Label selectedLabel;
    private TextButton play;

    private ArrayList<TextButton> groupsToPlace;
    private ArrayList<PlacedGroup> placedGroups;
    private Group draggingGroup;
    private TextButton draggingButton;
    private Sprite draggingSprite;

    private float leftWidth = 150;
    private float bottomHeight = 200;
    private float topHeight = 65;

    private Table leftSideUI, topSideUI, bottomSideUI;
	
	@Override
	public void create () {
        main = new Main(this);
        sprites = new ArrayList<CustomSprite>();
        placedGroups = new ArrayList<PlacedGroup>();
        groupsToPlace = new ArrayList<TextButton>();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        centerLineY = Gdx.graphics.getHeight()/2;
        centerLineX = Gdx.graphics.getWidth()/2;
        cam = new OrthographicCamera(width, height);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        batch = new SpriteBatch();
        img = new Texture("map.png");
        /*
		img = new Texture("spintest.png");
        CustomSprite sprite = new CustomSprite(img, 0);
        sprite.setCoords(width / 2, height / 2);
        sprites.add(sprite);
        selected = sprite;
        sprite = new CustomSprite(img, 1);
        sprite.setCoords(width / 2 + 200, height / 2);
        sprites.add(sprite);
        sprite = new CustomSprite(img, 2);
        sprite.setCoords(width / 2 - 200, height / 2);
        sprites.add(sprite);
        */
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        pixmap = new Pixmap(3, 3, Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        redTeam = new Texture(pixmap);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();
        blueTeam = new Texture(pixmap);

        pixmap = new Pixmap(1, 7, Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("selection", new Texture(pixmap));

        Texture tex = new Texture("checkboxEmpty.png");
        skin.add("checkboxOff", tex);
        tex = new Texture("checkboxChecked.png");
        skin.add("checkboxOn", tex);
        String[] strs = new String[]{"buttonUp",
                "buttonDown",
                "buttonOver",
                "start",
                "stop",
                "plus",
                "minus",
                "wideButtonUp",
                "wideButtonDown",
                "paneHandle",
                "paneUp",
                "paneDown",
                "paneBackground",
                "paneHandleHoriz"};
        for(int a=0;a<strs.length;a++){
            tex = new Texture(strs[a]+".png");
            skin.add(strs[a], tex);
        }
        Texture rep = new Texture("repeatTest.png");
        rep.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        TextureRegion texReg = new TextureRegion(rep, 10000, 10000);
        skin.add("repeat", texReg);

        // Store the default libgdx font under the name "default".
        createSkin();

        redCircle = new Sprite(new Texture("redCircle.png"));
        blueCircle = new Sprite(new Texture("blueCircle.png"));

        // We create a panel for our UI where we can place buttons and the like
        // We should probably do something like have a table or other Group-widget here to ease the changing of the buttons

        // Add an image actor. Have to set the size, else it would be the size of the drawable (which is the 1x1 texture).
        float leftSideWidth = 150f;
        float topSideHeight = 35;

        //Image panel = new Image(skin.newDrawable("white", Color.WHITE));
        Image panel = new Image(skin.getDrawable("repeat"), Scaling.fillY);
        Table regret = new Table();
        regret.setSize(leftSideWidth, Gdx.graphics.getHeight());
        regret.addActor(panel);
        regret.setClip(true);
        stage.addActor(regret);
        leftSideUI = regret;

        panel = new Image(skin.getDrawable("repeat"), Scaling.fillY);
        regret = new Table();
        regret.setSize(width - leftSideWidth, topSideHeight);
        regret.setPosition(leftSideWidth, height - topSideHeight);
        regret.addActor(panel);
        regret.setClip(true);
        stage.addActor(regret);
        topSideUI = regret;

        panel = new Image(skin.getDrawable("repeat"), Scaling.fillY);
        regret = new Table();
        regret.setSize(width - leftSideWidth, 200);
        regret.setPosition(leftSideWidth, 0);
        regret.addActor(panel);
        regret.setClip(true);
        stage.addActor(regret);
        bottomSideUI = regret;

        panel = new Image(skin.newDrawable("white", Color.BLACK));
        panel.setSize(3, height - 200 - topSideHeight + 2);
        panel.setPosition(leftSideWidth - 1, 199);
        stage.addActor(panel);

        panel = new Image(skin.newDrawable("white", Color.BLACK));
        panel.setSize(width - leftSideWidth + 1, 3);
        panel.setPosition(leftSideWidth - 1, height - topSideHeight);
        stage.addActor(panel);

        panel = new Image(skin.newDrawable("white", Color.BLACK));
        panel.setSize(width - leftSideWidth + 1, 3);
        panel.setPosition(leftSideWidth - 1, 199);
        stage.addActor(panel);

        createUnder();

        String[] overheadButtonStrings = new String[]{"Unit Settings",
                "Design of Units",
                "Organogram",
                "Unit Deployment",
                "Simulation Settings",
                "Strategy"};
        final TextButton[] overheadButtons = new TextButton[overheadButtonStrings.length];
        for(int a=0;a<overheadButtonStrings.length;a++){
            TextButton tb = new TextButton(overheadButtonStrings[a], skin, "wideButton");
            tb.setPosition(10.0f + 160*a, height - 30);
            tb.setSize(150, 25);
            stage.addActor(tb);
            overheadButtons[a] = tb;
        }
        ChangeListener listen = new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if (focusGroup != null) {
                    focusGroup.save(main);
                    focusGroup.remove();
                    focusGroup = null;
                }
                if (actor == overheadButtons[0]) {
                    focus = 0;
                    focusGroup = values;
                    values.loadArmy(selectedArmy);
                } else if (actor == overheadButtons[1]) {
                    focusGroup = composition;
                    composition.loadComposition(selectedArmy);
                    focus = 1;
                } else if (actor == overheadButtons[2]) {
                    focusGroup = organogram;
                    organogram.loadCommand(selectedArmy.leader, selectedArmy);
                    under.remove();
                    focus = 2;
                } else if (actor == overheadButtons[3]){
                    focusGroup = null;
                    loadGroups();
                    focus = 3;
                } else if (actor == overheadButtons[4]){
                    focusGroup = evolution;
                    focus = 4;
                } else if (actor == overheadButtons[5]){
                    //Strategy
                    focusGroup = null;
                    focus = 5;
                    under.remove();
                }
                if(focusGroup != null){
                    stage.addActor(focusGroup);
                    if(focusGroup != organogram && under != normalUnder){
                        under.remove();
                        under = normalUnder;
                        stage.addActor(under);
                    }
                    else if(focusGroup == organogram && under != organogramUnder){
                        under.remove();
                        under = organogramUnder;
                        stage.addActor(under);
                    }
                }
                else if(focus == 5 && under != strategyUnder){
                    under.remove();
                    under = strategyUnder;
                    strategyUnder.loadArmy(selectedArmy);
                    stage.addActor(under);
                }
                else if(under != normalUnder){
                    under.remove();
                    under = normalUnder;
                    stage.addActor(under);
                    System.out.println("Done");
                }
            }
        };
        for(int a=0;a<overheadButtons.length;a++){
            overheadButtons[a].addListener(listen);
        }

        /*
        TextButton openValues = new TextButton("Values", skin);
        openValues.setPosition(25.0f, height - 25);
        stage.addActor(openValues);
        openValues.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                if(focusGroup != null){
                    focusGroup.remove();
                }
                focusGroup = values;
                stage.addActor(focusGroup);
            }
        });
        */

        float valuesHeight = height - 200 - topSideHeight - 2;
        float valuesWidth = width - leftSideWidth - 1;
        float intX = leftSideWidth + 2;
        float intY = 202;
        values = new ValuesGroup(skin, valuesWidth, valuesHeight);

        /*
        for(int a=0;a<10;a++){
            for(int b=0;b<10;b++){
                Label l = new Label(""+a+"/"+b, skin);
                l.setPosition(25*a, 25*b);
                values.addActor(l);
            }
        }
        */
        values.setPosition(intX, intY);
        //values.generateComponents234();

        evolution = new EvolutionGroup(skin, valuesWidth, valuesHeight);
        evolution.setPosition(intX, intY);

        organogramUnder = new OrganogramUnder(skin);
        organogramUnder.setPosition(leftSideWidth, 0);
        organogramUnder.setSize(width - leftSideWidth, 199);

        organogram = new OrganogramGroup(skin, valuesWidth, valuesHeight, organogramUnder);
        organogram.setPosition(intX, intY);
        organogramUnder.organogram = organogram;

        composition = new CompositionGroup(skin, valuesWidth, valuesHeight);
        composition.setPosition(intX, intY);

        strategyUnder = new StrategyUnder(skin);
        strategyUnder.setPosition(150, 0);
        strategyUnder.setSize(width - 150, 199);

        battlesSimulated = new Label("Battles simualted : 0", skin, "black");
        battlesSimulated.setPosition(0, 0);
        battlesSimulated.setSize(200, 15);
        stage.addActor(battlesSimulated);

        generationsCompleted = new Label("Generations completed : 0", skin, "black");
        generationsCompleted.setPosition(0, 15);
        generationsCompleted.setSize(200, 15);
        stage.addActor(generationsCompleted);

        redLeader = new Leader(3.0, false);
        redLeader.name = "Red Leader";
        redArmy = new ArmyComposition(redLeader, main);

        blueLeader = new Leader(3.0, false);
        blueLeader.name = "Blue Leader";
        blueArmy = new ArmyComposition(blueLeader, main);

        selectedArmy = redArmy;
        selectedLabel = new Label("", skin, "select");
        selectedLabel.setSize(30, 30);
        selectedLabel.setPosition(25, height - 85);
        stage.addActor(selectedLabel);

        createArmyButtons();

        //testScroll();
    }

    boolean mouseDown = false;
    private long lastClick = 0;
    private PlacedGroup selectedGroup;

    public void handleInput(){
        /**
         *   Clicking
         */
        if(!mouseDown && Gdx.input.isTouched()){
            mouseDown = true;
        }
        else if(mouseDown && !Gdx.input.isTouched()){
            long clickAt = System.currentTimeMillis();
            boolean doubleClick = false;
            if(clickAt - 200 < lastClick){
                doubleClick = true;
            }
            lastClick = clickAt;
            //A click has been made
            mouseDown = false;
            boolean debug = false; //In case we want to print things regarding our click
            boolean debug2 = false; //In case we want to print things regarding selection of a PlacedGroup
            if(debug2){
                System.out.println("PlacedGroups.size() = " + placedGroups.size());
            }
            //Where was the click?
            int x = Gdx.input.getX();
            //x += cam.position.x - width;
            int y = height - Gdx.input.getY();
            //y += cam.position.y - height;
            cam.update();
            Vector3 worldVec = cam.unproject(new Vector3(x, y, 0));
            cam.update();
            if(debug) {
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                System.out.println("Where in the world is that?");
                System.out.println("world x = " + worldVec.x);
                System.out.println("world y = " + worldVec.y);
                System.out.println("cameras position");
                System.out.println("cam.x = " + cam.position.x);
                System.out.println("cam.y = " + cam.position.y);
                System.out.println("cam.zoom = " + cam.zoom);
                System.out.println("Refined Position");
                System.out.println("ref.x = " + ((x - centerLineX) * cam.zoom + cam.position.x));
                System.out.println("ref.y = " + ((y - centerLineY) * cam.zoom + cam.position.y));
            }
            Vector3 ref = new Vector3((x - centerLineX)*cam.zoom + cam.position.x,(y - centerLineY)*cam.zoom + cam.position.y,0);

            //Was the click within the camera "window"?
            if(x > leftWidth && y < height - topHeight && y > bottomHeight){
                //Yes, were we dragging something?
                if(draggingGroup != null){
                    if(debug2) {
                        System.out.println("We were dragging something");
                    }
                    //Yes, we were, remove the button we were dragging and create a Placed Group
                    draggingButton.remove();
                    groupsToPlace.remove(draggingButton);
                    PlacedGroup pg = new PlacedGroup(draggingGroup, ref, draggingSprite);
                    placedGroups.add(pg);
                    refreshGroupsToPlace();
                    pg.group.onMap = pg;
                }
                else{
                    if(debug2) {
                        System.out.println("We were not dragging something");
                    }
                    //Did we click on something inside?
                    int loop = placedGroups.size();
                    for(int a=0;a<loop;a++){
                        PlacedGroup pg = placedGroups.get(a);
                        if(pg.group == null){
                            if(debug2) {
                                System.out.println("pg.group is null 1");
                            }
                        }
                        Vector3 pgCopy = pg.position.cpy();
                        float dist = pgCopy.sub(ref).len();
                        if(dist < 25){
                            if(debug2) {
                                System.out.println("Within");
                            }
                            //We clicked on this group
                            //Did we double-click it?
                            if(selectedGroup == pg && doubleClick){
                                //We did, so return it to <under>
                                placedGroups.remove(a);
                                createButtonFor(selectedGroup.group);
                                refreshGroupsToPlace();
                                selectedGroup.group.onMap = null;
                                selectedGroup = null;
                                if(debug2) {
                                    System.out.println("Removing a PlacedGroup");
                                }
                            }
                            else {
                                if(debug2) {
                                    System.out.println("Selection Made");
                                }
                                selectedGroup = pg;
                            }
                            if(pg.group == null){
                                if(debug2) {
                                    System.out.println("pg.group is null 2");
                                }
                            }
                            break;
                        }
                        if(pg.group == null){
                            if(debug2) {
                                System.out.println("pg.group is null 3");
                            }
                        }
                    }

                    //Does our FocusGroup react to a click inside the map?
                    if(focusGroup != null) {
                        focusGroup.mapClick(ref.x, ref.y);
                    }
                    //Does our Under react to a click inside the map?
                    if(under != null){
                        under.mapClick(ref.x, ref.y);
                    }
                }
            }
            draggingButton = null;
            draggingGroup = null;
        }


        /**
         * Zooming
         */

        //Zoom out
        if(Gdx.input.isKeyPressed(Input.Keys.X)) {
            cam.zoom += 0.15;
        }
        //Zoom in
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if(cam.zoom >= 1) { //So we don't zoom too muc.
                cam.zoom -= 0.15;
                if(cam.zoom < 1){
                    cam.zoom = 1;
                }
            }
        }
        //Zoom in and out faster
        if(Gdx.input.isKeyPressed(Input.Keys.X) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            cam.zoom += 0.3;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            if (cam.zoom >= 1) {
                cam.zoom -= 0.3;
                if(cam.zoom < 1){
                    cam.zoom = 1;
                }
            }
        }

        /**
         * Moving the camera using keyboard.
         */

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cam.translate(-3 - cam.zoom/2, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cam.translate(3 + cam.zoom/2, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cam.translate(0, 3 + cam.zoom/2, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cam.translate(0, -3 - cam.zoom/2, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            cam.translate(-6, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            cam.translate(6, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            cam.translate(0, 6, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            cam.translate(0, -6, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            cam.position.set(0, 0, 0);
        }

        /**
         * Panning the camera using the mouse.
         */
        if(MOUSE_PAN) {
            if(Gdx.input.getX() < centerLineX-0.65*centerLineX) {
                //Left
                cam.translate(-1-cam.zoom/2, 0, 0);
            }
            if(Gdx.input.getX() > centerLineX+0.65*centerLineX) {
                //Right
                cam.translate(1+cam.zoom/2, 0, 0);
            }
            if(Gdx.input.getY() < centerLineY-0.65*centerLineY) {
                //Up
                cam.translate(0, 1+cam.zoom/2, 0);
            }
            if(Gdx.input.getY() > centerLineY+0.65*centerLineY) {
                //Down
                cam.translate(0, -1-cam.zoom/2, 0);
            }

        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if(MOUSE_PAN) {
                System.out.println("MOUSE PAN WAS: " + MOUSE_PAN);
                MOUSE_PAN = false;
            } else {
                System.out.println("MOUSE PAN WAS: " + MOUSE_PAN);
                MOUSE_PAN = true;
            }
        }
    }

	@Override
	public void render () {
        float dt = Gdx.graphics.getDeltaTime();
        cam.update();
        handleInput();
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.30f, 0.30f, 0.85f, 1);
		batch.begin();
		//batch.draw(img, Gdx.graphics.getWidth()/2 - size/2 + MathUtils.cos(angleRad) * radius, Gdx.graphics.getHeight()/2 - size/2 + MathUtils.sin(angleRad) * radius, size, size);
        //batch.draw(sprite, Gdx.graphics.getWidth()/2 - size/2 + MathUtils.cos(angleRad) * radius, Gdx.graphics.getHeight()/2 - size/2 + MathUtils.sin(angleRad) * radius, size, size);
        //sprite.update(Gdx.graphics.getDeltaTime());
        //sprite.draw(batch);
        /*
        for(CustomSprite sprite : sprites){
            sprite.update(dt);
            sprite.draw(batch);
        }
        */
        batch.draw(img, 0, 0, 2000, 2000);
        //batch.draw(redCircle, -25, -25);

        if(focus==5){
            strategyUnder.drawOrders(batch);
        }

        if(main.startNew) {
            for (int a = 0; a < placedGroups.size(); a++) {
                PlacedGroup pg = placedGroups.get(a);
                if (pg.group == null) {
                    System.out.println("pg.group is null 4");
                    placedGroups.remove(a);
                    a--;
                } else {
                    batch.draw(pg.sprite, pg.position.x - 25, pg.position.y - 25);
                }
            }
        }

        if(battlefield != null) {
            //System.out.println("Drawing units");
            ArrayList<Army> armies = battlefield.fetchArmies();
            for (int a = 0; a < armies.size(); a++) {
                ArrayList<Unit> units = armies.get(a).units;
                //System.out.println("Units size = " + units.size());
                for (int b = 0; b < units.size(); b++) {
                    Unit u = units.get(b);
                    if (u.alive) {
                        Position p = u.getPosition();
                        Texture imgdraw = redTeam;
                        if (a == 1) {
                            imgdraw = blueTeam;
                        }
                        if(u instanceof Soldier) {
                            batch.draw(imgdraw, (float) p.x - 1, (float) p.y - 1, 3, 3);
                        }
                        else if(u instanceof Vehicle){
                            batch.draw(imgdraw, (float) p.x - 2, (float) p.y - 2, 5, 5);
                        }
                    }
                }
            }
        }
		batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
	}

    public final static float originalWidth = 1200, originalHeight = 800;
    public static float currentWidth, currentHeight;

    @Override
    public void resize(int width, int height) {
        currentWidth = width;
        currentHeight = height;
        System.out.println("WIDTH = " + width);
        System.out.println("HEIGHT = " + height);
        if(leftSideUI != null){
            System.out.println("LEFTSIDEUI WIDTH " + leftSideUI.getWidth());
            System.out.println("LEFTSIDEUI SCALEX " + leftSideUI.getScaleX());
            System.out.println("LEFTSIDEUI SCALEY " + leftSideUI.getScaleY());
            leftSideUI.setScale(1.0f);
            System.out.println("LEFTSIDEUI WIDTH AFTER " + leftSideUI.getWidth());
            System.out.println("LEFTSIDEUI SCALEX AFTER " + leftSideUI.getScaleX());
            System.out.println("LEFTSIDEUI SCALEY AFTER " + leftSideUI.getScaleY());
            topSideUI.setScale(1.0f);
            System.out.println("BOTTOMSIDEUI POSX " + bottomSideUI.getX());
            bottomSideUI.setScale(1.0f);
            bottomSideUI.setPosition(leftWidth * (originalWidth/width), 0);
            bottomSideUI.setSize(width - leftWidth * ((originalWidth - leftWidth)/(width - leftWidth)), 200);
            System.out.println("BOTTOMSIDEUI POSX AFTER " + bottomSideUI.getX());
        }
    }

    @Override
    public void dispose(){
        stage.dispose();
        skin.dispose();
    }

    public void updateBattlesSimulated(int s){
        battlesSimulated.setText("Battles simulated : " + s);
    }

    public void updateGenerationsCompleted(int a){
        generationsCompleted.setText("Generations completed : " + a);
    }

    public void setMap(TestOfMap t){
        battlefield = t;
    }

    private void createUnder(){
        under = new FocusGroup();
        under.setSize(width - 150, 200);
        under.setPosition(150, 0);

        play = new TextButton("Play", skin, "button");
        play.setPosition(width/2 - 250, 50);
        play.setSize(200, 50);
        under.addActor(play);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (play.isChecked()) {
                    if (main.startNew) {
                        //Start the big boy process
                        System.out.println("GO");
                        if(focusGroup != null){
                            focusGroup.save(main);
                        }
                        main.setEvolutionValues(evolution.getValues(), evolution.isStepwise(), evolution.getStepLength());
                        //main.startEvolutionThread(values.getSettings(main));
                        main.startEvolutionThread(redArmy, blueArmy);
                    } else {
                        System.out.println("Unpausing");
                        main.unpauseThreads();
                    }
                    play.setText("Stop");
                } else {
                    main.pauseThreads();
                    play.setText("Resume");
                }
            }
        });

        final TextButton save = new TextButton("Save", skin, "button");
        save.setPosition(width/2 + 150, 100);
        under.addActor(save);
        save.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                saveSetup("Test");
            }
        });

        final TextButton load = new TextButton("Load", skin, "button");
        load.setPosition(width/2 + 150, 50);
        under.addActor(load);
        load.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                loadSetup("Test");
            }
        });

        stage.addActor(under);

        normalUnder = under;
    }

    private void createArmyButtons(){
        Color[] colors = new Color[]{Color.RED, Color.BLUE};
        String[] names = new String[]{"Red Team", "Blue Team"};
        ArmyComposition[] armies = new ArmyComposition[]{redArmy, blueArmy};
        for(int a=0;a<colors.length;a++){
            final ArmyComposition army = armies[a];
            final float h = height - 90 - (40*a);

            TextButtonStyle buttonStyle = new TextButtonStyle();
            //buttonStyle.up = skin.newDrawable("wideButtonUp", colors[a]);
            //buttonStyle.down = skin.newDrawable("wideButtonDown", colors[a]);
            buttonStyle.up = skin.newDrawable("buttonTest", colors[a]);
            buttonStyle.down = skin.newDrawable("buttonTestDown", colors[a]);
            buttonStyle.font = skin.getFont("default");
            buttonStyle.fontColor = Color.WHITE;
            skin.add(names[a], buttonStyle);

            TextButton tb = new TextButton(names[a], skin, names[a]);
            tb.setPosition(55, h);
            tb.setSize(90, 40);
            tb.addListener(new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor){
                    if(focusGroup != null){
                        focusGroup.save(main);
                    }
                    selectedArmy = army;
                    if(focusGroup == organogram){
                        organogram.loadCommand(selectedArmy.leader, selectedArmy);
                    }
                    else if(focusGroup == composition){
                        composition.loadComposition(selectedArmy);
                    }
                    else if(focus == 3){
                        loadGroups();
                    }
                    else if(focusGroup == values){
                        values.loadArmy(selectedArmy);
                    }
                    else if(focus == 5){
                        strategyUnder.loadArmy(selectedArmy);
                    }
                    selectedLabel.setPosition(25, h + 5);
                }
            });
            stage.addActor(tb);
        }
    }

    public void loadGroups(){
        for(int a=0;a<groupsToPlace.size();a++){
            groupsToPlace.get(a).remove();
        }
        groupsToPlace.clear();

        for(int a=0;a<selectedArmy.groups.size();a++){
            Group g = selectedArmy.groups.get(a);
            if(g.onMap == null) {
                createButtonFor(g);
            }
        }
        refreshGroupsToPlace();
    }

    public void refreshGroupsToPlace(){
        float totalX = 5;
        for(int a=0;a<groupsToPlace.size();a++){
            TextButton tb = groupsToPlace.get(a);
            tb.setPosition(totalX + 25, 130);
            totalX += 5 + tb.getPrefWidth();
        }
        System.out.println("Buttons Refreshed");
    }

    public void createButtonFor(final Group g){
        final TextButton tb = new TextButton(g.name, skin, "button");
        tb.addListener(new ClickListener() {
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                draggingButton = tb;
                draggingGroup = g;
                if (selectedArmy == redArmy) {
                    draggingSprite = redCircle;
                } else if (selectedArmy == blueArmy) {
                    draggingSprite = blueCircle;
                }
            }
        });
        normalUnder.addActor(tb);
        groupsToPlace.add(tb);
        System.out.println("Button Created For a Group");
    }

    public void createSkin(){
        skin.add("default", new BitmapFont());

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        Texture test = new Texture("buttonTest2.png");
        skin.add("buttonTest", test);
        test = new Texture("buttonTest2Down.png");
        skin.add("buttonTestDown", test);
        test = new Texture("buttonTest4.png");
        skin.add("buttonTestWide", test);

        textButtonStyle = new TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("buttonTestWide");
        textButtonStyle.down = skin.getDrawable("buttonTestDown");
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("wideButton", textButtonStyle);

        //Start/Stop Button
        textButtonStyle = new TextButtonStyle();
        //textButtonStyle.up = skin.getDrawable("buttonUp");
        //textButtonStyle.down = skin.getDrawable("buttonDown");
        //textButtonStyle.over = skin.getDrawable("buttonOver");
        textButtonStyle.up = skin.getDrawable("buttonTest");
        textButtonStyle.down = skin.getDrawable("buttonTestDown");
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.fontColor = Color.BLACK;
        skin.add("button", textButtonStyle);

        final LabelStyle playStyle = new LabelStyle();
        playStyle.background = skin.getDrawable("start");
        playStyle.font = skin.getFont("default");
        playStyle.fontColor = Color.BLACK;
        skin.add("play", playStyle);

        final LabelStyle stopStyle = new LabelStyle();
        stopStyle.background = skin.getDrawable("stop");
        stopStyle.font = skin.getFont("default");
        stopStyle.fontColor = Color.BLACK;
        skin.add("stop", stopStyle);

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        test = new Texture("testTextField1.png");
        skin.add("textFieldTest", test);
        //textFieldStyle.background = skin.newDrawable("white", Color.BLACK);
        textFieldStyle.background = skin.newDrawable("textFieldTest", Color.BLACK);
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.font = skin.getFont("default");
        //textFieldStyle.focusedBackground = skin.newDrawable("white", Color.LIGHT_GRAY);
        textFieldStyle.focusedBackground = skin.newDrawable("textFieldTest", Color.LIGHT_GRAY);
        textFieldStyle.selection = skin.newDrawable("selection");
        skin.add("default", textFieldStyle);

        // Configure a LabelStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        LabelStyle labelStyle = new LabelStyle();
        //labelStyle.background = skin.newDrawable("white", Color.WHITE);
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);

        labelStyle = new LabelStyle();
        labelStyle.fontColor = Color.BLACK;
        labelStyle.font = skin.getFont("default");
        skin.add("black", labelStyle);

        labelStyle = new LabelStyle();
        labelStyle.fontColor = Color.BLACK;
        labelStyle.font = skin.getFont("default");
        labelStyle.background = skin.getDrawable("start");
        skin.add("select", labelStyle);

        CheckBoxStyle checkStyle = new CheckBoxStyle();
        checkStyle.font = skin.getFont("default");
        checkStyle.fontColor = Color.BLACK;
        checkStyle.checkboxOff = skin.getDrawable("checkboxOff");
        checkStyle.checkboxOn = skin.getDrawable("checkboxOn");
        skin.add("default", checkStyle);

        TreeStyle treeStyle = new TreeStyle();
        treeStyle.plus = skin.getDrawable("plus");
        treeStyle.minus = skin.getDrawable("minus");
        skin.add("default", treeStyle);

        SelectBoxStyle selBoxStyle = new SelectBoxStyle();
        //selBoxStyle.background = skin.newDrawable("white", Color.BLACK);
        test = new Texture("selectBoxTest1.png");
        skin.add("selectBoxTest", test);
        selBoxStyle.background = skin.getDrawable("selectBoxTest");
        selBoxStyle.font = skin.getFont("default");
        selBoxStyle.fontColor = Color.WHITE;
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.background = skin.newDrawable("white", Color.BLACK);
        listStyle.font = skin.getFont("default");
        listStyle.fontColorSelected = Color.BLACK;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.selection = skin.newDrawable("selection");
        skin.add("default", listStyle);
        selBoxStyle.listStyle = listStyle;
        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        //scrollStyle.background = skin.getDrawable("paneBackground");
        //scrollStyle.hScroll = skin.getDrawable("paneHandle");
        scrollStyle.hScrollKnob = skin.getDrawable("paneHandle");
        //scrollStyle.vScroll = skin.getDrawable("paneUp");
        scrollStyle.vScrollKnob = skin.getDrawable("paneHandle");
        skin.add("default", scrollStyle);
        selBoxStyle.scrollStyle = scrollStyle;
        skin.add("default", selBoxStyle);
    }

    public void testScroll(){

        WidgetGroup wg = new WidgetGroup();
        wg.setHeight(100000);
        for(int a=0;a<100;a++){
            Label l = new Label("Test Label " + a, skin);
            l.setSize(150, 25);
            l.setPosition(0, 25*a);
            wg.addActor(l);
        }
        ScrollPane scroll = new ScrollPane(wg, skin);
        scroll.setPosition(100, 100);
        scroll.setSize(200, 200);
        scroll.setScrollingDisabled(true, false);
        scroll.setForceScroll(false, true);
        scroll.setScrollPercentY(100f);
        scroll.setClamp(false);
        WidgetGroup wg2 = new WidgetGroup();
        wg2.setPosition(0, 100);
        wg2.addActor(scroll);
        System.out.println("widgetHeight1 = " + wg.getHeight());
        scroll.layout();
        System.out.println("widgetHeight2 = " + wg.getHeight());
        stage.addActor(wg2);

        System.out.println("Widget is Layout = " + (wg instanceof Layout));
        System.out.println("widgetHeight3 = " + wg.getHeight());
        System.out.println("widgetPrefHeight = " + wg.getPrefHeight());
        System.out.println("scroll.scrollHeight = " + scroll.getScrollHeight());
        System.out.println("scroll.maxY = " + scroll.getMaxY());
    }

    public void saveSetup(String fileName) {
        File f = new File(fileName + ".txt");
        saveSetup(f);
    }

    public void saveSetup(File f){
        ArmyComposition[] armies = new ArmyComposition[]{redArmy, blueArmy};

        //We get the linebreak for whatever system we are currently on
        String linebreak = System.getProperty("line.separator");

        StringBuilder builder = new StringBuilder("");

        //We iterate through each army
        for(int a=0;a<armies.length;a++){
            builder.append(armies[a].getSaveInfo(linebreak));
        }

        try{
            FileWriter outWriter = new FileWriter(f);
            BufferedWriter buffWrite = new BufferedWriter(outWriter);
            buffWrite.write(builder.toString());
            buffWrite.close();
        }
        catch(IOException e){
            System.out.println("Failed to Save File");
        }
    }

    public void loadSetup(String fileName) {
        File f = new File(fileName + ".txt");
        loadSetup(f);
    }

    public void loadSetup(File f){
        placedGroups.clear();

        ArmyComposition[] armies = new ArmyComposition[]{redArmy, blueArmy};
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String currentLine = "";
            String[] splitted;
            int current = 0;
            Sprite[] sprites = {redCircle, blueCircle};
            while ((currentLine = br.readLine()) != null) {
                if(currentLine.equals("NEWARMY")){
                    loadArmy(new ArmyComposition(br, main, sprites[current], skin), current);
                    current++;
                }
            }
        }
        catch (FileNotFoundException fnfe){
            System.out.println("File could not be found");
        }
        catch (IOException e) {
            System.out.println("Something went wrong (File found)");
        }
    }

    public void loadArmy(ArmyComposition ac, int num){
        if(num == 0){
            redArmy.loadArmy(ac);
            System.out.println("redArmy.groups.size() " + redArmy.groups.size());
            placeGroups(ac);
        }
        else if(num == 1){
            blueArmy.loadArmy(ac);
            System.out.println("blueArmy.groups.size() " + blueArmy.groups.size());
            placeGroups(ac);
        }
    }

    public void placeGroups(ArmyComposition ac){
        for(int a=0;a<ac.groups.size();a++){
            Group g = ac.groups.get(a);
            if(g.onMap != null){
                placedGroups.add(g.onMap);
            }
        }
    }

    public void stepsCompleted(){
        if(play.isChecked()){
            play.setChecked(false);
            play.setText("Resume");
        }
    }
}
