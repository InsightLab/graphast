package br.ufc.insightlab.graphast.model.listeners;

import br.ufc.insightlab.graphast.model.Graph;

public abstract class GraphListener {

    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }
}
