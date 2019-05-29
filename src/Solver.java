import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.*;

public class Solver {
    public final double eps = 1e-9;
    public final double inf =  1e9;

    private Vector<Line> triangulationLines = new Vector<>();
    private Vector<Vector<GraphEdge> > graph = new Vector<>();
    private Vector<Line> EMST = new Vector<>();
    private Vector<Circle> shapePoints = new Vector<>();
    private Vector<CVec2> points = new Vector<>();
    private Vector<Edge> recursionStack = new Vector<>();
    HashMap<Edge, TwoVertices> triangulation = new HashMap<>();

    public Solver() {
    }

    public Vector<Line> getTriangulationLines() {
        return triangulationLines;
    }

    public void solve(Vector<Circle> circles_points) {

        this.triangulationLines.clear();
        this.shapePoints = circles_points;
        this.graph.clear();

        this.points.clear();
        this.recursionStack.clear();
        this.triangulation.clear();

        for(Circle circle: circles_points) {
            this.points.add( new CVec2(circle.getCenterX(), circle.getCenterY()));
        }

        Collections.sort(this.points, CVec2.cVec2Comparator);

        for(int i = 0; i < this.points.size(); i++) {
            this.recursionStack.add(new Edge());
        }

        if( crossProduct(this.points.get(1).minus( this.points.get(0)), this.points.get(2).minus( this.points.get(0) )) < 0.0 ) {
            setLeft(0, 2);
            setRight(0, 1);
            setLeft(1, 0);
            setRight(1, 2);
            setLeft(2, 1);
            setRight(2, 0);
        } else {
            setLeft(0, 1);
            setRight(0, 2);
            setLeft(1, 2);
            setRight(1, 0);
            setLeft(2, 0);
            setRight(2, 1);
        }

        insert(new Edge(1, 2), 0);
        insert(new Edge(0, 1), 2);
        insert(new Edge(0, 2), 1);

        for( int i = 3; i < this.points.size(); i++) {
            int currentPt = i - 1;
            while( crossProduct( this.points.get(currentPt).minus( this.points.get(i) ), this.points.get(this.points.get(currentPt).right).minus( this.points.get(i) ) ) > -eps ) {
                restructure( currentPt, this.points.get(currentPt).right, i );
                currentPt = this.points.get(currentPt).right;
            }
            setRight(i, currentPt);

            currentPt = i - 1;

            while( crossProduct( this.points.get(currentPt).minus(this.points.get(i) ), this.points.get(this.points.get(currentPt).left).minus(this.points.get(i) ) ) < eps ) {
                restructure( this.points.get(currentPt).left, currentPt, i );
                currentPt = this.points.get(currentPt).left;
            }
            setLeft(i, currentPt);

            setLeft(this.points.get(i).right, i);
            setRight(currentPt, i);

        }

        HashSet<Integer> convexHull = new HashSet<>();
        convexHull.add(0);
        int hullPoint = 0;
        while( this.points.get(hullPoint).right != 0 ) {
            hullPoint = this.points.get(hullPoint).right;
            convexHull.add( hullPoint );
        }

        for(int i = 0; i < this.points.size(); i++) {
            this.graph.add(new Vector<>());
        }

        Iterator it = this.triangulation.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Edge edge = (Edge) pair.getKey();

            this.graph.get(edge.v1).add(new GraphEdge(edge.v2, length(edge)));
            this.graph.get(edge.v2).add(new GraphEdge(edge.v1, length(edge)));

            this.triangulationLines.add(createLine(edge.v1, edge.v2));

        }

        buildEMST();
    }
    
    public void setRight(int i, int right) {
        CVec2 point = this.points.get(i);
        point.right = right;
        this.points.set(i, point);
    }

    public void setLeft(int i, int left) {
        CVec2 point = this.points.get(i);
        point.left = left;
        this.points.set(i, point);
    }

    public Line createLine(int a, int b) {
        return new Line( this.points.get(a).x, this.points.get(a).y, this.points.get(b).x, this.points.get(b).y);
    }

    public Vector<Line> getEMST() {
        return EMST;
    }

    public void buildEMST() {

        int n = this.graph.size();

        Vector<Double> min_e = new Vector<>();
        Vector<Integer> sel_e = new Vector<>();
        Vector<Boolean> used = new Vector<>();

        for(int i = 0; i < n; i++) {
            min_e.add(inf);
            sel_e.add(-1);
            used.add(false);
        }

        PriorityQueue<GraphEdge> q = new PriorityQueue<>();
        q.clear();
        min_e.set(0, 0.0);
        q.add(new GraphEdge(0, 0.0));

        for(int i = 0; i < n; i++) {
            GraphEdge edge = q.poll();
            int v = edge.to;

            if(used.get(v)) {
                i--;
                continue;
            }

            used.set(v, true);

            if(sel_e.get(v) != -1) {
                used.set(v, true);
                used.set(sel_e.get(v), true);
                this.EMST.add(createLine(v, sel_e.get(v)));
            }

            for(int j = 0; j < graph.get(v).size(); j++) {
                int to = graph.get(v).get(j).to;
                double length = graph.get(v).get(j).length;

                if(length < min_e.get(to)) {
                    q.remove(new GraphEdge(to, min_e.get(to)));
                    min_e.set(to, length);
                    sel_e.set(to, v);
                    q.add(new GraphEdge(to, min_e.get(to)));
                }
            }

        }
    }

    public double length(Edge edge) {

        return Math.sqrt( Math.pow((this.points.get(edge.v1).x - this.points.get(edge.v2).x), 2) + Math.pow((this.points.get(edge.v1).y - this.points.get(edge.v2).y), 2) );
    }

    public void restructure( int left, int right, int cur) {

        this.recursionStack.set(0, new Edge(left, right));

        int stackSize = 1;
        while( stackSize > 0 ) {
            left = this.recursionStack.get(stackSize - 1).v1; right = this.recursionStack.get(stackSize - 1).v2;
            --stackSize;

            TwoVertices innerTwoVectives = this.triangulation.get(new Edge(Math.min( left, right), Math.max(left, right)));
            int innerPt = innerTwoVectives.getMin();
            if( innerPt != -1 && check( left, right, cur, innerPt ) ) {
                insert( new Edge( right, cur ),  left );
                insert( new Edge( left, cur ),  right );
                insert( new Edge( Math.min( left, right ), Math.max( left, right ) ),  cur );
                continue;
            }

            erase( new Edge( right, cur ),  left );
            insert( new Edge( right, cur ),  innerPt );
            erase( new Edge( left, cur ),  right );
            insert( new Edge( left, cur ),  innerPt );
            erase( new Edge( Math.min( innerPt, left ), Math.max( innerPt, left ) ),  right );
            insert( new Edge( Math.min( innerPt, left ), Math.max( innerPt, left ) ),  cur );
            erase( new Edge( Math.min( innerPt, right ), Math.max( innerPt, right ) ),  left );
            insert( new Edge( Math.min( innerPt, right ), Math.max( innerPt, right ) ),  cur );
            this.triangulation.remove(new Edge( Math.min( left, right ), Math.max( left, right ) ));

            this.recursionStack.set(stackSize++, new Edge(left, innerPt));
            this.recursionStack.set(stackSize++, new Edge(innerPt, right ));
        }
    }

    public boolean check( int left, int right, int cur, int innerPt) {
        
        if( innerPt == cur || crossProduct( this.points.get(left).minus(this.points.get(cur)) , this.points.get(innerPt).minus( this.points.get(cur) ) ) < 0 ||
                crossProduct( this.points.get(right).minus( this.points.get(cur) ), this.points.get(innerPt).minus( this.points.get(cur) ) ) > 0 )
        {
            return true;
        }
        double Sa = ( this.points.get(cur).x - this.points.get(right).x ) * ( this.points.get(cur).x - this.points.get(left).x ) +
                ( this.points.get(cur).y - this.points.get(right).y ) * ( this.points.get(cur).y - this.points.get(left).y );
        double Sb = ( this.points.get(innerPt).x - this.points.get(right).x ) * ( this.points.get(innerPt).x - this.points.get(left).x ) +
                ( this.points.get(innerPt).y - this.points.get(right).y ) * ( this.points.get(innerPt).y - this.points.get(left).y );
        if( Sa > -eps && Sb > -eps ) {
            return true;
        } else if( !( Sa < 0 && Sb < 0 ) ) {
            double Sc = ( this.points.get(cur).x - this.points.get(right).x ) * ( this.points.get(cur).y - this.points.get(left).y ) -
                    ( this.points.get(cur).y - this.points.get(right).y ) * ( this.points.get(cur).x - this.points.get(left).x );
            double Sd = ( this.points.get(innerPt).x - this.points.get(right).x ) * ( this.points.get(innerPt).y - this.points.get(left).y ) -
                    ( this.points.get(innerPt).y - this.points.get(right).y ) * ( this.points.get(innerPt).x - this.points.get(left).x );
            if( Sc < 0 ) Sc = -Sc; if( Sd < 0 ) Sd = -Sd;
            if( Sc * Sb + Sa * Sd > -eps ) {
                return true;
            }
        }
        return false;
    }

    public void insert( Edge key, int v) {
        if(key.v1 > key.v2) key = new Edge(key.v2, key.v1);
        if(this.triangulation.containsKey(key)) {
            TwoVertices value = this.triangulation.get(key);
            value.insert(v);
            this.triangulation.put(key, value);
        } else {
            TwoVertices value = new TwoVertices();
            value.insert(v);
            this.triangulation.put(key, value);
        }

    }

    public void erase( Edge key, int v) {
        if(this.triangulation.containsKey(key)) {
            TwoVertices value = this.triangulation.get(key);
            value.erase(v);
            this.triangulation.put(key, value);
        }
    }

    public double crossProduct( final CVec2 a, final CVec2 b) {
        return (double) (a.x * b.y - a.y * b.x);
    }
}
