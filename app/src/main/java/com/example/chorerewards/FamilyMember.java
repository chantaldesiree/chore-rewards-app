package com.example.chorerewards;

import java.io.Serializable;

//Defines a family member.
public class FamilyMember implements Serializable
{
    String name;
    int points;

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return name;
    }

    public void setPointValue(int points)
    {
        this.points = points;
    }

    public int getPointValue()
    {
        return points;
    }
}