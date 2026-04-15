package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Represents a topic tag attached to an article (e.g. "climate", "recycling").
 */
public class Tag {
    private final String name;

    /**
     * Creates a tag with the given name.
     *
     * @param name the tag label
     */
    public Tag(String name) {
        this.name = name;
    }

    /** Returns the tag name. */
    public String getName() {
        return name;
    }

    /** Returns the tag name as a string. */
    @Override
    public String toString() {
        return name;
    }
}
