package com.learnopengles.android.program;


public enum UniformVariable {

    MVP_MATRIX("u_MVPMatrix");

    private final String name;

    UniformVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
