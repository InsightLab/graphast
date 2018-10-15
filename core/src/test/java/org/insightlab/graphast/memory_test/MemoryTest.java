package org.insightlab.graphast.memory_test;

import org.insightlab.graphast.model.Graph;
import org.insightlab.graphast.model.Node;
import org.junit.Test;

public class MemoryTest {

    @Test
    public void test() {
        Graph g = new Graph();
        for (int i = 0; i < 1000000; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Node n = new Node(i);
            n.addComponent(new MemoryTestNodeComponent(i));
            g.addNodes(n);
        }
    }

}
