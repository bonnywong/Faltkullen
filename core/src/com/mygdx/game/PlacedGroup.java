package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;
import faltkullen.Group;

/**
 * Created by Anton on 2015-08-26.
 */
public class PlacedGroup {
    public Group group;
    public Vector3 position;
    public Sprite sprite;
    public PlacedGroup(Group g, Vector3 v, Sprite s){
        group = g;
        position = v;
        sprite = s;
    }
}
