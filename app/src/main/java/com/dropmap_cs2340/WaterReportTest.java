package com.dropmap_cs2340;
/*
 * Created by Austin Spalding on 4/11/17
 * WaterReportTest.java
 */

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * @author Austin Spalding
 */
public class WaterReportTest {
    /**
     * Test fixtures
     */
    WaterReport wrTest;

    /**
     * Test init
     */
    @Before
    public void setUp() throws Exception {
        wrTest = new WaterReport();
    }

    /**
     * Test method for {@link com.dropmap_cs2340.WaterReport#getCondition()}.
     * @author Austin Spalding
     */
    @Test
    public void testGetCondition() {
        Assert.assertEquals("Initial Condition wrong", null, wrTest.getCondition());
        try {
            wrTest.setCondition("Waste");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to retrieve new condition", "Waste", wrTest.getCondition());
    }

    /**
     * Test method for {@link com.dropmap_cs2340.WaterReport#setCondition()}.
     * @author Austin Spalding
     */
    @Test
    public void testSetCondition() {
        Assert.assertEquals("Initial Condition wrong", null, wrTest.getCondition());
        try {
            wrTest.setCondition("Waste");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to set new condition", "Waste", wrTest.getCondition());
        try {
            wrTest.setCondition("Potable");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to alter prior condition", "Potable", wrTest.getCondition());
    }

    /**
     * Test method for {@link com.dropmap_cs2340.WaterReport#getType()}.
     * @author John Britti
     */
    @Test
    public void testGetType() {
        Assert.assertEquals("Initial Type wrong", null, wrTest.getType());
        try {
            wrTest.setType("Spring");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to retrieve new type", "Spring", wrTest.getType());
    }

    /**
     * Test method for {@link com.dropmap_cs2340.WaterReport#setType()}.
     * @author John Britti
     */
    @Test
    public void testSetType() {
        Assert.assertEquals("Initial Condition wrong", null, wrTest.getType());
        try {
            wrTest.setType("Spring");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to set new condition", "Spring", wrTest.getType());
        try {
            wrTest.setType("Lake");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to alter prior condition", "Lake", wrTest.getType());
    }
}