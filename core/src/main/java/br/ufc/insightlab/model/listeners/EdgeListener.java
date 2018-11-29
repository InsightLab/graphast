package br.ufc.insightlab.model.listeners;

import br.ufc.insightlab.model.Edge;

public abstract class EdgeListener extends GraphListener {

    public abstract void onInsert(Edge e);

    public abstract void onRemove(Edge e);

}
