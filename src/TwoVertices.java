public class TwoVertices {
    public Integer outer1;
    public Integer outer2;

    public TwoVertices() {
        this.outer1 = -1;
        this.outer2 = -1;
    }

    public TwoVertices(Integer outer1, Integer outer2) {
        this.outer1 = outer1;
        this.outer2 = outer2;
    }

    public void insert(int v ) {
        if( this.outer1 == v || this.outer2 == v ) return;
        if( this.outer1 == -1) {
            this.outer1 = v;
            return;
        }
        if( this.outer2 == -1) {
            this.outer2 = v;
            return;
        }
    }

    public void erase(int v) {
        if(this.outer1 == v) {
            this.outer1 = -1;
            return;
        }
        if(this.outer2 == v) {
            this.outer2 = -1;
            return;
        }
    }

    public int getMax() {
        if(this.outer1 == -1) return this.outer2;
        if(this.outer2 == -1) return this.outer1;

        return (this.outer1 > this.outer2) ? this.outer1 : this.outer2;
    }

    public int getMin() {
        if(this.outer1 != -1 && this.outer2 != -1) {
            return Math.min(this.outer1, this.outer2);
        }
        if(this.outer1 != -1) return this.outer1;
        if(this.outer2 != -1) return this.outer2;
        return -1;
    }

    public int size() {
        int size = 0;
        if(this.outer1 != -1) size++;
        if(this.outer2 != -1) size++;

        return size;
    }
}
