package edu.vassar.cmpu203.ecoscoop.src.model;

/**
 * Represents the author of a news article.
 */
public class Author {
    private final String name;

    /**
     * Creates an author with the given name.
     *
     * @param name the author's name
     */
    public Author(String name){
        this.name = name;
    }

    /**
     * Returns the author's name.
     */
    public String getName(){
        return name;
    }

    /**
     * Returns the author's name as a string.
     */
    public String toString(){
        return name;
    }
}
