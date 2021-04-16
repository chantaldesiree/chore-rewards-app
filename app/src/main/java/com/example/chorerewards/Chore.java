package com.example.chorerewards;

import java.io.Serializable;

//Defines what a chore is.
public class Chore implements Serializable
{
    String name;
    Long pointValue;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setPointValue(Long pointValue)
    {
        this.pointValue = pointValue;
    }

    public Long getPointValue()
    {
        return pointValue;
    }
}