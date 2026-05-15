package com.hphider;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Skin
{
    DEFAULT("Default"),
    AROUND_2005("2005"),
    AROUND_2006("2006", AROUND_2005),
    AROUND_2010("2010");

    private String name;
    private Skin extendSkin;

    Skin(String name)
    {
        this(name, null);
    }

    @Override
    public String toString()
    {
        return getName();
    }
}