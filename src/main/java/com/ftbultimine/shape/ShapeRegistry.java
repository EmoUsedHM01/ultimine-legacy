package com.ftbultimine.shape;

/**
 * Maps ShapeType enum values to Shape implementations.
 */
public class ShapeRegistry {

    public static Shape getShape(ShapeType type) {
        switch (type) {
            case SHAPELESS:      return ShapelessShape.INSTANCE;
            case SMALL_TUNNEL:   return ShapedShapes.SMALL_TUNNEL;
            case LARGE_TUNNEL:   return ShapedShapes.LARGE_TUNNEL;
            case SMALL_SQUARE:   return ShapedShapes.SMALL_SQUARE;
            case MINING_TUNNEL:  return ShapedShapes.MINING_TUNNEL;
            case ESCAPE_TUNNEL:  return ShapedShapes.ESCAPE_TUNNEL;
            default:             return ShapelessShape.INSTANCE;
        }
    }
}
