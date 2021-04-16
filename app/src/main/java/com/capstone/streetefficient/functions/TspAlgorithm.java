package com.capstone.streetefficient.functions;

import java.util.ArrayList;

public class TspAlgorithm {
    private final int ROUTE_SIZE;
    private final int START_INDEX;
    private final int FINISHED_STATE;

    private final double[][] DISTANCE_MATRIX;
    private final ArrayList<Integer> SEQUENCED_ROUTE = new ArrayList<>();

    private double routeDistance = Double.POSITIVE_INFINITY;
    private boolean ranSolver = false;

    public TspAlgorithm(int startIndex, double[][] distance){
        this.DISTANCE_MATRIX = distance;
        ROUTE_SIZE = distance.length;
        START_INDEX = startIndex;

        //THIS STATE IS WHEN ALL BITS ARE SET TO ONE
        //WHEN ALL BITS ARE 1, ALL INDEXES(DELIVERY NODES) HAVE BEEN VISITED
        FINISHED_STATE = (1 << ROUTE_SIZE) - 1;
    }

    public ArrayList<Integer> getSequence(){
        if(!ranSolver) solveSequence();
        return SEQUENCED_ROUTE;
    }

    public double getRouteDistance(){
        if(!ranSolver) solveSequence();
        return routeDistance;
    }

    private void solveSequence() {
        // Run the solver
        int state = 1 << START_INDEX;

        //sequence cache
        Double[][] memo = new Double[ROUTE_SIZE][1 << ROUTE_SIZE];

        //previous index
        Integer[][] prev = new Integer[ROUTE_SIZE][1 << ROUTE_SIZE];

        routeDistance = sequenceRoute(START_INDEX, state, memo, prev);

        // Regenerate sequence
        int index = START_INDEX;
        while (true) {
            SEQUENCED_ROUTE.add(index);
            Integer nextIndex = prev[index][state];
            if (nextIndex == null) break;
            state = state | (1 << nextIndex);
            index = nextIndex;
        }
        SEQUENCED_ROUTE.add(START_INDEX);
        ranSolver = true;
    }

    private double sequenceRoute(int start_index, int state, Double[][] memo, Integer[][] prev) {
        // Done this sequence. Return distance of going back to start node.
        if (state == FINISHED_STATE) return DISTANCE_MATRIX[start_index][START_INDEX];

        // Return answer from cached memo array if already computed.
        if (memo[start_index][state] != null) return memo[start_index][state];

        double minCost = Double.POSITIVE_INFINITY;
        int index = -1;
        for (int next = 0; next < ROUTE_SIZE; next++) {

            // Skip if the next node has already been visited.
            if ((state & (1 << next)) != 0) continue;

            int nextState = state | (1 << next);
            double newCost = DISTANCE_MATRIX[start_index][next] + sequenceRoute(next, nextState, memo, prev);
            if (newCost < minCost) {
                minCost = newCost;
                index = next;
            }
        }

        prev[start_index][state] = index;
        return memo[start_index][state] = minCost;
    }
}
