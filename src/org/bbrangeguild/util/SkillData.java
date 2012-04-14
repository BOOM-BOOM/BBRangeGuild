package org.bbrangeguild.util;

import org.powerbot.game.api.methods.tab.Skills;

/**
 * @author BOOM BOOM
 */
public class SkillData {

    private int skill, startXP, startLevel;
    private long startTime;

    public SkillData(final int skill, final long startTime) {
        this.skill = skill;
        this.startTime = startTime;
        startXP = Skills.getExperience(skill);
        startLevel = Skills.getRealLevel(skill);
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    public int getGainedXP() {
        return Skills.getExperience(skill) - startXP;
    }

    public int getGainedLevels() {
        return Skills.getRealLevel(skill) - startLevel;
    }

    public int getPercentToNextLevel() {
        final int level = Skills.getRealLevel(skill);
        final int nextLevel = level + 1;
        if (level == 99 || nextLevel > 99)
            return 0;
        final int xpTotal = Skills.getExperienceRequired(nextLevel) - Skills.getExperienceRequired(level);
        if (xpTotal == 0)
            return 0;
        final int xpDone = Skills.getExperience(skill) - Skills.getExperienceRequired(level);
        return xpDone * 100 / xpTotal;
    }

    public int getExperienceToLevel() {
        return Skills.getExperienceRequired(Skills.getRealLevel(skill) + 1) - Skills.getExperience(skill);
    }

}
