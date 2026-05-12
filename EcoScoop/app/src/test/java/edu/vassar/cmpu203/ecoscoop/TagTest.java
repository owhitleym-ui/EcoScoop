package edu.vassar.cmpu203.ecoscoop;

import org.junit.Test;
import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Tag;

/**
 * Unit tests for the non-trivial behaviour of {@link Tag}.
 *
 * Verifies that the tag name is stored correctly, that {@code toString()} returns
 * the name, and that edge-case inputs (empty string, special characters) are
 * handled without error.
 */
public class TagTest {
// getName

    /**
     * Verifies {@code getName()} returns the exact name passed to the constructor.
     */
    @Test
    public void testGetName_returnsConstructorValue() {
        Tag tag = new Tag("climate");
        assertEquals("climate", tag.getName());
    }

    /**
     * Verifies that a tag constructed with an empty string returns an empty string.
     */
    @Test
    public void testGetName_emptyString() {
        Tag tag = new Tag("");
        assertEquals("", tag.getName());
    }

    /**
     * Verifies that special characters are preserved in the tag name.
     */
    @Test
    public void testGetName_specialCharacters() {
        Tag tag = new Tag("CO₂ & climate");
        assertEquals("CO₂ & climate", tag.getName());
    }

    /**
     * Verifies that whitespace-only names are stored as-is (no trimming).
     */
    @Test
    public void testGetName_whitespaceOnly() {
        Tag tag = new Tag("  ");
        assertEquals("  ", tag.getName());
    }

    // toString

    /**
     * Verifies that {@code toString()} returns the tag name — used when tags are
     * displayed in lists.
     */
    @Test
    public void testToString_returnsName() {
        Tag tag = new Tag("renewable");
        assertEquals("renewable", tag.toString());
    }

    /**
     * Verifies that {@code toString()} on an empty-name tag returns an empty string.
     */
    @Test
    public void testToString_emptyName() {
        Tag tag = new Tag("");
        assertEquals("", tag.toString());
    }
}
