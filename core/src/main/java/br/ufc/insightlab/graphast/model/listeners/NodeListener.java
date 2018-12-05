package br.ufc.insightlab.graphast.model.listeners;

import br.ufc.insightlab.graphast.model.Node;

public abstract class NodeListener extends GraphListener {

    public abstract void onInsert(Node n);

    public abstract void onRemove(Node n);

}
