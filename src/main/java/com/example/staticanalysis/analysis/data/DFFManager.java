package com.example.staticanalysis.analysis.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DFFManager {
    private static DFFManager instance = null;
    private Set<DFF> dffs;

    private DFFManager() {
        this.dffs = new HashSet<>();
    }

    public static DFFManager getInstance() {
        if (instance == null) {
            instance = new DFFManager();
        }
        return instance;
    }

    public void addDFF(DFF dff) {
        this.dffs.add(dff);
    }

    public Set<DFF> getDFFs() {
        return Collections.unmodifiableSet(this.dffs);
    }
}
