package com.dropmap_cs2340;

/**
 * Created by Cwoodall6 on 4/12/17.
 */

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/*
 * @author Chaz Woodall
 */

public class UserTest {
    User userTest;

    /**
     * Test init
     */
    @Before
    public void setUp() throws Exception {
        userTest = new User();
    }

    /**
     * Test method for {@link User#getAuthLevel()}.
     * @author Chaz Woodall
     */
    @org.testng.annotations.Test
    public void testGetAuthLevel() {
        Assert.assertEquals("Initial Condition wrong", null, userTest.getAuthLevel());
        try {
            userTest.setAuthLevel("User");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to retrieve new authlevel", "User", userTest.getAuthLevel());
    }

    /**
     * Test method for {@link com.dropmap_cs2340.User#setAuthLevel(String)}.
     * @author Chaz Woodall
     */
    @Test
    public void testSetCondition() {
        Assert.assertEquals("Initial Condition wrong", null, userTest.getAuthLevel());
        try {
            userTest.setAuthLevel("User");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to set new authlevel", "User", userTest.getAuthLevel());
        try {
            userTest.setAuthLevel("Worker");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Should not have thrown exception here");
        }
        Assert.assertEquals("Failed to alter prior condition", "Worker", userTest.getAuthLevel());
    }

}
