package com.example.storyline;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void storyObject() {
        Story story = new Story("1", "1", "new", "72");
        assertNotNull(story);
        assertEquals(story.getDetail(), "72 node(s)");
    }

    @Test
    public void passwordAndUsername() {
        assertTrue(RegisterActivity.isEmptyString(""));
        assertFalse(RegisterActivity.isEmptyString("something"));
        assertTrue(RegisterActivity.isPasswordConfirmOk("123", "123"));
        assertFalse(RegisterActivity.isPasswordConfirmOk("", ""));
        assertFalse(RegisterActivity.isPasswordConfirmOk("12", "123"));
    }

}