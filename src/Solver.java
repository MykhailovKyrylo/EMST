import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;

public class Solver {
    public final Double eps = 1e-9;
    private Vector<Line> triangulationLines = new Vector<>();
    private Vector<Edge> graph = new Vector<>();
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

    public void pringTriangulation() {

        Iterator it = this.triangulation.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Edge edge = (Edge) pair.getKey();


            System.out.println(edge.v1 + " " + edge.v2);

        }
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

        for(CVec2 point: this.points) {
            System.out.println(point.x + " " + point.y);
        }

        for(int i = 0; i < this.points.size(); i++) {
            this.recursionStack.add(new Edge());
        }

        if( crossProduct(this.points.get(1).minus( this.points.get(0)), this.points.get(2).minus( this.points.get(0) )) < 0.0 ) {
            this.points.get(0).left = 2;
            this.points.get(0).right = 1;
            this.points.get(1).left = 0;
            this.points.get(1).right = 2;
            this.points.get(2).left = 1;
            this.points.get(2).right = 0;
        } else {
            this.points.get(0).left = 1;
            this.points.get(0).right = 2;
            this.points.get(1).left = 2;
            this.points.get(1).right = 0;
            this.points.get(2).left = 0;
            this.points.get(2).right = 1;
        }

        insert(new Edge(1, 2), 0);
        insert(new Edge(0, 1), 2);
        insert(new Edge(0, 2), 1);

        pringTriangulation();

        for( int i = 3; i < this.points.size(); i++) {
            int currentPt = i - 1;
            while( crossProduct( this.points.get(currentPt).minus( this.points.get(i) ), this.points.get(this.points.get(currentPt).right).minus( this.points.get(i) ) ) > -eps ) {
                restructure( currentPt, this.points.get(currentPt).right, i );
                currentPt = this.points.get(currentPt).right;
            }
            this.points.get(i).right = currentPt;

            currentPt = i - 1;

            while( crossProduct( this.points.get(currentPt).minus(this.points.get(i) ), this.points.get(this.points.get(currentPt).left).minus(this.points.get(i) ) ) < eps ) {
                restructure( this.points.get(currentPt).left, currentPt, i );
                currentPt = this.points.get(currentPt).left;
            }
            this.points.get(i).left = currentPt;

            this.points.get(this.points.get(i).right).left = i;
            this.points.get(currentPt).right = i;

        }

        HashSet<Integer> convexHull = new HashSet<>();
        convexHull.add(0);
        int hullPoint = 0;
        while( this.points.get(hullPoint).right != 0 ) {
            hullPoint = this.points.get(hullPoint).right;
            convexHull.add( hullPoint );
        }

        HashSet<Edge> triangulationEdges = new HashSet<>();
        triangulationEdges.clear();

        Iterator it = this.triangulation.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Edge edge = (Edge) pair.getKey();
            TwoVertices twoVertices = (TwoVertices) pair.getValue();

            this.graph.add( edge );

            System.out.println("Edge " + edge.v1 + " " + edge.v2);
            System.out.println("twoVertices " + twoVertices.outer1 + " " + twoVertices.outer2);
            System.out.println("\n");

            this.triangulationLines.add(new Line( this.points.get(edge.v1).x, this.points.get(edge.v1).y, this.points.get(edge.v2).x, this.points.get(edge.v2).y));

        }


    }

    public void restructure( int left, int right, int cur) {

        this.recursionStack.set(0, new Edge(left, right));

        int stackSize = 1;
        while( stackSize > 0 ) {
            left = this.recursionStack.get(stackSize - 1).v1; right = this.recursionStack.get(stackSize - 1).v2;
            --stackSize;


            int innerPt = triangulation.get(new Edge(Math.min( left, right), Math.max(left, right))).getMin();
            if( check( left, right, cur, innerPt ) ) {
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
