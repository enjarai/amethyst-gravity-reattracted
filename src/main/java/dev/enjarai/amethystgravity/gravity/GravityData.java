package dev.enjarai.amethystgravity.gravity;

import gravity_changer.GravityComponent;

import java.util.ArrayList;

public interface GravityData {
    ArrayList<GravityEffect> getFieldList();
    ArrayList<GravityEffect> getLowerFieldList();
    void setFieldGravity(GravityEffect gravityEffect);
    GravityEffect getFieldGravity();
    void updateGravity(GravityComponent component);
}
