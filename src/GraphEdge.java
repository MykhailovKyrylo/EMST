import java.util.Comparator;
import java.util.Objects;

public class GraphEdge implements Comparable<GraphEdge>{
    public Integer to;
    public Double length;

    public GraphEdge(Integer to, Double length) {
        this.to = to;
        this.length = length;
    }

    public static Comparator<GraphEdge> graphEdgeComparator = new Comparator<GraphEdge>() {

        @Override
        public int compare(GraphEdge o1, GraphEdge o2) {
            return (int) ( (double) (o1.length - o2.length) );
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphEdge graphEdge = (GraphEdge) o;
        return Objects.equals(to, graphEdge.to) &&
                Objects.equals(length, graphEdge.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, length);
    }

    @Override
    public int compareTo(GraphEdge o) {
        return (int) ( (double) (this.length - o.length) * 1e9 );
    }
}
