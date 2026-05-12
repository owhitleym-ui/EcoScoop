package edu.vassar.cmpu203.ecoscoop;

import org.junit.Test;
import static org.junit.Assert.*;

import edu.vassar.cmpu203.ecoscoop.src.model.Source;

/**
 * Unit tests for the non-trivial behaviour of {@link Source}.
 *
 * Verifies that all three fields (websiteName, url, publishDate) are stored
 * correctly and returned by their respective getters, that empty-string values
 * round-trip cleanly, and that {@code toString()} includes all three values.
 */
public class SourceTest {

    // Getters

    /**
     * Verifies {@code getWebsiteName()} returns the exact value passed to the
     * constructor.
     */
    @Test
    public void testGetWebsiteName_returnsConstructorValue() {
        Source s = new Source("Grist", "https://grist.org", "2024-01-15");
        assertEquals("Grist", s.getWebsiteName());
    }

    /**
     * Verifies {@code getUrl()} returns the exact URL passed to the constructor.
     */
    @Test
    public void testGetUrl_returnsConstructorValue() {
        Source s = new Source("Grist", "https://grist.org", "2024-01-15");
        assertEquals("https://grist.org", s.getUrl());
    }

    /**
     * Verifies {@code getPublishDate()} returns the exact date string passed to
     * the constructor.
     */
    @Test
    public void testGetPublishDate_returnsConstructorValue() {
        Source s = new Source("Grist", "https://grist.org", "2024-01-15");
        assertEquals("2024-01-15", s.getPublishDate());
    }

    /**
     * Verifies that all three fields independently store different values without
     * cross-contamination.
     */
    @Test
    public void testAllFields_independentlyStored() {
        Source s = new Source("Carbon Brief", "https://carbonbrief.org", "2023-06-30");
        assertEquals("Carbon Brief",           s.getWebsiteName());
        assertEquals("https://carbonbrief.org", s.getUrl());
        assertEquals("2023-06-30",             s.getPublishDate());
    }

    // -------------------------------------------------------------------------
    // Edge cases
    // -------------------------------------------------------------------------

    /**
     * Verifies that empty-string values for all three fields are stored and
     * returned without modification.
     */
    @Test
    public void testEmptyStrings_storedAndReturned() {
        Source s = new Source("", "", "");
        assertEquals("", s.getWebsiteName());
        assertEquals("", s.getUrl());
        assertEquals("", s.getPublishDate());
    }

    // toString

    /**
     * Verifies that {@code toString()} includes the website name, URL, and
     * publish date so that logged output contains all three values.
     */
    @Test
    public void testToString_containsAllThreeFields() {
        Source s = new Source("Earth911", "https://earth911.com", "2024-03-10");
        String result = s.toString();
        assertTrue(result.contains("Earth911"));
        assertTrue(result.contains("https://earth911.com"));
        assertTrue(result.contains("2024-03-10"));
    }
}
