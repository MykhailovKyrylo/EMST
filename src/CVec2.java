import java.util.Comparator;

public class CVec2 {
    public final Double eps = 1e-9;

    public Double x;
    public Double y;
    public Integer left;
    public Integer right;

    public CVec2(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.left = 0;
        this.right = 0;
    }

    public CVec2(Double x, Double y, Integer left, Integer right) {
        this.x = x;
        this.y = y;
        this.left = left;
        this.right = right;
    }

    public CVec2 plus( final CVec2 other) {
        return new CVec2(this.x + other.x, this.y + other.y);
    }

    public CVec2 minus( final CVec2 other) {
        return new CVec2(this.x - other.x, this.y - other.y);
    }

    public CVec2 inverse() {
        return new CVec2(-this.x, -this.y);
    }

    public int compare(CVec2 o1, CVec2 o2) {

        int o1x = (int ) (o1.x / o1.eps);
        int o2x = (int ) (o2.x / o2.eps);

        return (o1x - o2x);
    }

    public static Comparator<CVec2> cVec2Comparator
            = new Comparator<CVec2>() {


        @Override
        public int compare(CVec2 o1, CVec2 o2) {

            int o1x = (int ) ( (double) o1.x );
            int o2x = (int ) ( (double) o2.x );

            return (o1x - o2x);
        }
    };
}
