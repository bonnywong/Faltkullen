package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Anton on 2015-08-23.
 */
public class CustomSprite extends Sprite {
    private int id;
    private float rotationPerSecond = 0.0f;
    private float x, y;
    private float angleRad = 0.0f;  //Current angle from middle
    private float angleSpeed = 1.0f;
    private float angleDelta = 1.0f; //Direction of spin
    private float radius = 50.0f;
    private float size = 50.0f;

    public CustomSprite(Texture t, int i){
        super(t);
        setOrigin(25, 25);
        id = i;
    }

    public void update(float dt){
        angleRad += MathUtils.PI * dt * angleDelta * angleSpeed;
        if(angleRad >= MathUtils.PI2){
            angleRad -= MathUtils.PI2;
        }
        else if(angleRad <= 0.0f){
            angleRad += MathUtils.PI2;
        }
        setCenter(x + MathUtils.cos(angleRad) * radius, y + MathUtils.sin(angleRad) * radius);
        rotate(rotationPerSecond * dt);
    }

    public void setCoords(float a, float b){
        x = a;
        y = b;
    }

    public float[] getCoords(){
        return new float[]{x, y};
    }

    public void setAngleSpeed(float a){
        angleSpeed = a;
    }

    public void reverse(){
        angleDelta *= -1.0f;
    }

    public boolean spinningClockwise(){
        return angleDelta < 0.0f;
    }

    public float getAngleSpeed(){
        return angleSpeed;
    }

    public void setRotationSpeed(float a){
        rotationPerSecond = a;
    }

    public float getRotationSpeed(){
        return rotationPerSecond;
    }

    public void setRadius(float a){
        radius = a;
    }

    public float getRadius(){
        return radius;
    }

    public float getScaleSize(){
        return size;
    }

    public void scaleTo(float s){
        size = s;
        setScale(size/50);
    }

    public boolean isClicked(float a, float b){
        if(Math.abs(x - a) <= size/2 && Math.abs(y - b) <= size/2){
            return true;
        }
        else{
            return false;
        }
    }

    public int getID(){
        return id;
    }
}
