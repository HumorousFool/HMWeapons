package io.github.humorousfool.hmweapons.skills;

public class SkillSet
{
    public int strength;
    public int precision;
    public int constitution;
    public int agility;
    public int wisdom;

    public int sparePoints;

    public SkillSet(int strength, int precision, int constitution, int agility, int wisdom, int sparePoints)
    {
        this.strength = strength;
        this.precision = precision;
        this.constitution = constitution;
        this.agility = agility;
        this.wisdom = wisdom;
        this.sparePoints = sparePoints;
    }
}
