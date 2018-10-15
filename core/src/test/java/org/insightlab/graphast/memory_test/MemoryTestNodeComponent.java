package org.insightlab.graphast.memory_test;

import org.insightlab.graphast.model.components.NodeComponent;

public class MemoryTestNodeComponent extends NodeComponent {

    private int i;

public MemoryTestNodeComponent(int i) {
        this.i = i;
        }

public int getInt() {
        return i;
        }

        }
