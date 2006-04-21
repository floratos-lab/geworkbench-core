package org.geworkbench.bison.datastructure.bioobjects.markers.goterms;

/**
 * Represents a Gene Ontology Term.
 *
 * @author John Watkinson
 */
public class GOTerm {

    private int id;
    private String name;
    private String definition;
    private GOTerm[] parents;
    private GOTerm[] children;

    public GOTerm(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public GOTerm[] getParents() {
        return parents;
    }

    public void setParents(GOTerm[] parents) {
        this.parents = parents;
    }

    public GOTerm[] getChildren() {
        return children;
    }

    public void setChildren(GOTerm[] children) {
        this.children = children;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GOTerm goTerm = (GOTerm) o;

        if (id != goTerm.id) return false;

        return true;
    }

    public int hashCode() {
        return id;
    }

    public String toString() {
        return id + " - " + name + " - " + definition;
    }
}
