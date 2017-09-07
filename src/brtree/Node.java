package brtree;

/**
 * Created by Administrator on 2017/09/04 0004.
 */
public class Node {
    public Node parent;
    public Node left;
    public Node right;
    public Integer value;
    public COLOR color;

    public Node() {

    }

    /**
     * 默认颜色是红色
     *
     * @param value
     */
    public Node(int value) {
        left = getNIL(this);
        right = getNIL(this);
        this.value = value;
        this.color = COLOR.RED;
    }

    public enum COLOR {
        RED("red"), BLACK("black");
        private String color;

        COLOR(String color) {
            this.color = color;
        }
    }

    public boolean addLeft(Node node) {
        if (node == null) {
            return false;
        }
        this.left = node;
        node.parent = this;
        return true;
    }

    public boolean addRight(Node node) {
        if (node == null) {
            return false;
        }

        this.right = node;
        node.parent = this;
        return true;
    }

    private static class NIL extends Node{

    }

    public static NIL getNIL(Node parent) {
        NIL nil = new NIL();
        nil.parent = parent;
        return nil;
    }

    public static boolean isNIL(Node node) {
        return node instanceof NIL;
    }

}
