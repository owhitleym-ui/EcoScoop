package edu.vassar.cmpu203.ecoscoop.src.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link Author}.
 *
 * Covers: name retrieval, {@code toString()} output, and edge cases such as
 * an empty name string.
 */
public class AuthorTest {

    /**
     * Verifies {@code getName()} returns exactly the name passed to the
     * constructor.
     */
    @Test
    public void testGetNameReturnsConstructorValue() {
        Author author = new Author("Jane Doe");
        assertEquals("Jane Doe", author.getName());
    }

    /**
     * Verifies {@code toString()} returns the author's name, matching the
     * value used when authors are displayed in the article feed.
     */
    @Test
    public void testToStringMatchesName() {
        Author author = new Author("Jane Doe");
        assertEquals("Jane Doe", author.toString());
    }

    /**
     * Verifies that an empty string is accepted as a name and returned
     * correctly, covering the edge case of an article with an unnamed author.
     */
    @Test
    public void testEmptyNameHandledWithoutException() {
        Author author = new Author("");
        assertEquals("", author.getName());
        assertEquals("", author.toString());
    }

    /**
     * Verifies that names containing special characters (spaces, accents,
     * punctuation) are stored and returned without modification.
     */
    @Test
    public void testNameWithSpecialCharacters() {
        Author author = new Author("María García-López");
        assertEquals("María García-López", author.getName());
    }
}
