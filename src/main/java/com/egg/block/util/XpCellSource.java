package com.egg.block.util;

public interface XpCellSource {
    int getCellCount();
    int getXp(int index);
    void setXp(int index, int xp);
}