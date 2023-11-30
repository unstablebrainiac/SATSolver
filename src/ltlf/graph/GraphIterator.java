package ltlf.graph;

import java.util.Iterator;

public abstract class GraphIterator implements Iterator<Node> {
    public abstract boolean hasNext();
    public abstract Node next();
}
