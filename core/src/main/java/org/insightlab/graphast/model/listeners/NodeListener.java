package org.insightlab.graphast.model.listeners;

import org.insightlab.graphast.model.Node;

public abstract class NodeListener extends GraphListener {

    public abstract void onInsert(Node n);

    public abstract void onRemove(Node n);

}
