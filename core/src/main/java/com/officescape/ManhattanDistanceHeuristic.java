package com.officescape;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class ManhattanDistanceHeuristic implements Heuristic<Vector2> {
    @Override
    public float estimate(Vector2 node, Vector2 endNode) {
        return Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y);
    }
}
