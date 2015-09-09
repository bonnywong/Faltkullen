package faltkullen;

import java.io.Serializable;
import java.util.ArrayList;

public class Square implements Serializable {


    ArrayList<String> activeLayers;

    double reducedMovementSpeedInfantry;
    double reducedMovementSpeedVehicles;
    double reducedLineOfSightGroundunits;
    double reducedLineOfSightUAV;
    boolean impassableTerrainInfantry;
    boolean impassableTerrainVehicles;


    public Square() {
        this.activeLayers = new ArrayList<String>();
        this.reducedLineOfSightGroundunits = 1;
        this.reducedLineOfSightUAV = 1;
        this.reducedMovementSpeedInfantry = 1;
        this.reducedMovementSpeedVehicles = 1;
        this.impassableTerrainInfantry = false;
        this.impassableTerrainVehicles = false;
    }


    public void addLayerToLayerList(String layerName) {
        this.activeLayers.add(layerName);
    }

    public ArrayList<String> getActiveLayers() {
        return this.activeLayers;
    }


    public boolean isImpassableTerrainInfantry() {
        return impassableTerrainInfantry;
    }


    public void setImpassableTerrainInfantry(boolean impassableTerrainInfantry) {
        this.impassableTerrainInfantry = impassableTerrainInfantry;
    }


    public boolean isImpassableTerrainVehicles() {
        return impassableTerrainVehicles;
    }


    public void setImpassableTerrainVehicles(boolean impassableTerrainVehicles) {
        this.impassableTerrainVehicles = impassableTerrainVehicles;
    }

    public double getReducedMovementSpeedInfantry() {
        return reducedMovementSpeedInfantry;
    }


    public void setReducedMovementSpeedInfantry(double reducedMovementSpeedInfantry) {
        this.reducedMovementSpeedInfantry = reducedMovementSpeedInfantry;
    }


    public double getReducedMovementSpeedVehicles() {
        return reducedMovementSpeedVehicles;
    }


    public void setReducedMovementSpeedVehicles(double reducedMovementSpeedVehicles) {
        this.reducedMovementSpeedVehicles = reducedMovementSpeedVehicles;
    }


    public double getReducedLineOfSightGroundunits() {
        return reducedLineOfSightGroundunits;
    }


    public void setReducedLineOfSightGroundunits(double reducedLineOfSightGroundunits) {
        this.reducedLineOfSightGroundunits = reducedLineOfSightGroundunits;
    }


    public double getReducedLineOfSightUAV() {
        return reducedLineOfSightUAV;
    }


    public void setReducedLineOfSightUAV(double reducedLineOfSightUAV) {
        this.reducedLineOfSightUAV = reducedLineOfSightUAV;
    }


}
