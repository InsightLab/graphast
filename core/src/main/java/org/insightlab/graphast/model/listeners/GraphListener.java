package org.insightlab.graphast.model.listeners;

import org.insightlab.graphast.model.Graph;

public abstract class GraphListener {

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
