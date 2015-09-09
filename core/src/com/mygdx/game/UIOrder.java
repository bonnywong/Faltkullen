package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import faltkullen.Leader;

/**
 * Created by Anton on 2015-08-28.
 */
public class UIOrder {
    private Sprite visual;
    private UIOrder originOrder;
    private PlacedGroup originGroup;
    private Vector3 originPosition;
    private Vector3 position;

    public UIOrder(Sprite s, Vector3 p){
        visual = s;
        position = p;
    }

    public void setPosition(float x, float y){
        position = new Vector3(x, y, 0);
    }

    public Vector3 getPosition(){
        return position;
    }

    public void setOriginOrder(UIOrder o){
        originOrder = o;
    }

    public void setLeader(Leader l){
        if(l.inGroup != null){
            originGroup = l.inGroup.onMap;
        }
        else if(l.commands.size() > 0){
            originPosition = l.getWeightedPoint();
            //System.out.println("Set originPosition for Leader " + l.name + " to (" + originPosition.x + " / " + originPosition.y + ")");
        }
    }

    private void updateSprite(){
        Vector3 start, end, result;
        end = position.cpy();
        if(originOrder != null){
            start = originOrder.getPosition();
        }
        else if(originGroup != null){
            start = originGroup.position;
        }
        else if(originPosition != null){
            start = originPosition;
        }
        else{
            start = new Vector3(0, 0, 0);
        }
        //visual.setCenter(0, 0);
        result = end.sub(start);
        float dist = result.len();
        float scale = dist/100;
        visual.setScale(scale, 1);
        visual.setOrigin(0, 50);
        visual.setPosition(start.x, start.y - 50);
        //System.out.println("visual.x = " + start.x);
        //System.out.println("visual.y = " + start.y);
        result = result.nor();
        float dx = result.x;
        float dy = result.y;
        double angle = Math.acos(dx);
        double state = Math.asin(dy);
        if(state < 0){
            angle = Math.PI * 2 - angle;
        }
        visual.setRotation((float) (angle * (180/Math.PI)));
        //visual.setRotation(45);
    }

    public void draw(SpriteBatch batch){
        updateSprite();
        visual.draw(batch);
    }
}
