package org.eol.globi.domain;

import org.neo4j.graphdb.Node;

public class Season extends NodeBacked {

    public static final String TITLE = "title";

    public Season(Node node, String title) {
        this(node);
        getUnderlyingNode().setProperty(TITLE, title);
        getUnderlyingNode().setProperty(PropertyAndValueDictionary.TYPE, Season.class.getSimpleName());
    }

    public Season(Node node) {
        super(node);
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", getTitle());
    }

    public String getTitle() {
        return (String)getUnderlyingNode().getProperty(TITLE);
    }


}
