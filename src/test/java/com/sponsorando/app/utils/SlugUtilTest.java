package com.sponsorando.app.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SlugUtilTest {

    private SlugUtil slugUtil;

    private String DEFAULT_SLUG = "spnsrnd";

    private int MIN_LENGTH_UUID = 8;

    @BeforeEach
    void setup() {
        slugUtil = new SlugUtil();
    }

    @Test
    public void testNullInput(){
        String input = null;
        String expectedSlug = DEFAULT_SLUG;

        String slug = slugUtil.generateSlug(input,false);
        assertNotNull(slug);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertNotNull(slug);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testEmptyInput(){
        String input = "";
        String expectedSlug = DEFAULT_SLUG;

        String slug = slugUtil.generateSlug(input,false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testShortInput(){
        String input = "N";
        String expectedSlug = "n";

        String slug = slugUtil.generateSlug(input,false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertTrue(slug.length() >= (expectedSlug.length() + (MIN_LENGTH_UUID * 2)));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testNormalInput(){
        String input = "Normal input";
        String expectedSlug = "normal-input";

        String slug = slugUtil.generateSlug(input,false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testLongInput(){
        String input = "This is a very long input that should not be shortened because there is no input max length";
        String expectedSlug = "this-is-a-very-long-input-that-should-not-be-shortened-because-there-is-no-input-max-length";

        String slug = slugUtil.generateSlug(input,false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testInputMaxLength() {
        String input = "This is a long input that should be shorten";
        String expectedSlug = "this-is-a-long-input";
        int inputMaxLength = 20;

        String slug = slugUtil.generateSlug(input, false, inputMaxLength);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input, true, inputMaxLength);
        assertEquals((expectedSlug.length() + 1 + MIN_LENGTH_UUID), slug.length());
        assertTrue(slug.startsWith(expectedSlug + "-"), slug);

        expectedSlug = "this-is-a-long-input-that-should-be-shorten";
        inputMaxLength = 0;

        slug = slugUtil.generateSlug(input, false, inputMaxLength);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        inputMaxLength = -1;
        slug = slugUtil.generateSlug(input, false, inputMaxLength);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);
    }

    @Test
    public void testDigitsInput(){
        String input = "Digits 12  680 input";
        String expectedSlug = "digits-input";

        String slug = slugUtil.generateSlug(input,false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input,true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testSpecialCharacters() {
        String input = "Input, with. :special; characters! @#$%^&*()";
        String expectedSlug = "input-with-special-characters";

        String slug = slugUtil.generateSlug(input, false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input, true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testUmlautCharacters() {
        String input = "Über Ökonom Ästhetik Straße";
        String expectedSlug = "ueber-oekonom-aesthetik-strasse";

        String slug = slugUtil.generateSlug(input, false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input, true);
        assertTrue(slug.startsWith(expectedSlug + "-"));
    }

    @Test
    public void testNonAsciiCharacters() {
        String input = "Input with non-ASCII characters: Héllö Wörld";
        String expectedSlug = "input-with-non-ascii-characters-helloe-woerld";

        String slug = slugUtil.generateSlug(input, false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input, true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"), slug);
    }

    @Test
    public void testConsecutiveSpacesAndHyphens() {
        String input = " Input with   multiple spaces and---hyphens   ";
        String expectedSlug = "input-with-multiple-spaces-and-hyphens";

        String slug = slugUtil.generateSlug(input, false);
        assertEquals(expectedSlug.length(), slug.length());
        assertEquals(expectedSlug, slug);

        slug = slugUtil.generateSlug(input, true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"), slug);
    }

    @Test
    public void testAllSpecialCharacters() {
        String input = "!@#$%^&*()_+.;,:=";
        String expectedSlug = DEFAULT_SLUG;

        String slug = slugUtil.generateSlug(input, false);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"), slug);

        slug = slugUtil.generateSlug(input, true);
        assertTrue(slug.length() >= (expectedSlug.length() + 1 + MIN_LENGTH_UUID));
        assertTrue(slug.startsWith(expectedSlug + "-"), slug);
    }
}
