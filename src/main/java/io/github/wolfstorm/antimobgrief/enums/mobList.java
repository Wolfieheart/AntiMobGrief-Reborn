package io.github.wolfstorm.antimobgrief.enums;

public enum mobList {

    CREEPER("CreeperGrief"),
    GHAST("GhastGrief"),
    ENDERMAN("EndermanGrief"),
    DOOR("DoorGrief"),
    RAVAGER("RavagerGrief"),
    BED("BedGrief")
    ;

    private String name;

    mobList(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
