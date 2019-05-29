import java.util.Comparator;

public class GraphEdgeComparator implements Comparator<GraphEdge> {

    @Override
    public int compare(GraphEdge o1, GraphEdge o2) {
        return (int) ( (o1.length - o2.length) * 1e9 );
    }
}
