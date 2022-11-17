package pl.kielce.tu.mergeservice;

public class Tuple<T> {
    private T left;
    private T right;

    public Tuple() {

    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }
}
