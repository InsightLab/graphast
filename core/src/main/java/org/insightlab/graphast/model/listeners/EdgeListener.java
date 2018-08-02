package org.insightlab.graphast.model.listeners;

import org.insightlab.graphast.model.Edge;
import org.insightlab.graphast.model.Node;

public abstract class EdgeListener extends GraphListener {

    public abstract void onInsert(Edge e);

    public abstract void onRemove(Edge e);

}
