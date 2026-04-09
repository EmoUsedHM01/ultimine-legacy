package com.ftbultimine.shape;

public enum ShapeType {

    SHAPELESS("Shapeless"),
    SMALL_TUNNEL("Small Tunnel"),
    LARGE_TUNNEL("Large Tunnel"),
    SMALL_SQUARE("Small Square"),
    MINING_TUNNEL("Mining Tunnel"),
    ESCAPE_TUNNEL("Escape Tunnel");

    private final String displayName;

    ShapeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
