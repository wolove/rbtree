package brtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/09/04 0004.
 */
public class BRTree {
    private List<Node> member = new ArrayList<>();

    private Node root;

    public BRTree() {

    }

    /**
     * 获取节点的后续节点(中序遍历)
     *
     * @param node
     * @return
     */
    public static Node getSuccessor(BRTree tree, Node node) {
        if (node == null) {
            return null;
        }
        if (node == tree.root) {
            return tree.root.right;
        }
        //有右子树
        if (node.right != null) {
            Node curNode = node.right;
            while (curNode.left != null) {
                curNode = curNode.left;
                continue;
            }
            return curNode;
        }
        if (node.parent.left == node) {
            return node.parent;
        }
        return null;
    }

    public String inOrderTraversalToString() {
        List<Node> nods = BRTree.inOrderTraversal(this);
        StringBuilder sb = new StringBuilder();
        for (Node n : nods) {
            sb.append(n.value);
            sb.append("->");
        }
        return sb.toString();
    }

    /**
     * 中序遍历
     *
     * @param tree
     * @return
     */
    public static List<Node> inOrderTraversal(BRTree tree) {
        List<Node> nodes = new ArrayList<>();
        if (tree.root == null) {
            return nodes;
        }
        LinkedList<Node> stack = new LinkedList<>();
        stack.push(tree.root);

        /*start inOrder Traversal*/
        while (!stack.isEmpty()) {
            Node curNode = stack.peek();
            //先遍历左子树，将其依次入栈
            if (!Node.isNIL(curNode.left) && !nodes.contains(curNode.left)) {
                stack.push(curNode.left);
                continue;
            }

            //当没有左子树的时候，将该节点加入结果表，并出遍历栈
            nodes.add(curNode);
            stack.pop();

            if (!Node.isNIL(curNode.right) && !nodes.contains(curNode.right)) {
                stack.push(curNode.right);
            }
        }

        return nodes;
    }

    public void add(Node node) {
        if (node == null || Node.isNIL(node)||member.contains(node)) {
            return;
        }
        if (this.root == null) {
            this.root = node;
            this.root.parent = Node.getNIL(null);
            return;
        }
        //1.找到插入的节点
        Node curNode = this.root;
        while (!Node.isNIL(curNode)) {
            if (node.value > curNode.value) {
                curNode = curNode.right;
            } else {
                curNode = curNode.left;
            }
        }
        curNode = curNode.parent;

        //将节点插入
        node.color = Node.COLOR.RED;
        if (curNode.value > node.value) {
            curNode.addLeft(node);
        } else {
            curNode.addRight(node);
        }
        //进行红黑树调整
        BR_INSERT_FIXUP(node);
    }

    public void BR_INSERT_FIXUP(Node node) {
        //1.如果node是parent的左子树
    }

    /**
     *                A                  A
     *               /                  /
     *  ->对C左旋   C         ==>      E
     *            /  \               /
     *           B    E             C
     *               /             / \
     *             D              B   D
     * @param node
     */
    public void LEFT_ROTATE(Node node) {
        //没有右子树无法左旋
        if (node == null || Node.isNIL(node) || Node.isNIL(node.right)||!member.contains(node)) {
            return;
        }

        Node nodeRight = node.right;
        //将nodeRight的左孩子挂到node下
        node.right = nodeRight.left;
        nodeRight.left.parent = node;

        nodeRight.parent = node.parent;
        //如果node本身是root,将右孩子变为root
        if(Node.isNIL(node.parent)){
            this.root = nodeRight;
        } else if (node == node.parent.left) {
            node.parent.left = nodeRight;
        }else{
            node.parent.right = nodeRight;
        }
        //将node挂在nodeRight下
        nodeRight.left = node;
        node.parent = nodeRight;
    }

    public void RIGHT_ROTATE(Node node) {
        //没有左子树无法右旋
        if (node == null || Node.isNIL(node) || Node.isNIL(node.left) || !member.contains(node)) {
            return ;
        }

        Node nodeLeft = node.left;

        node.left = nodeLeft.right;
        nodeLeft.right.parent = node;

        nodeLeft.parent = node.parent;

    }

    public static void main(String[] args) {
        BRTree tree = new BRTree();
        Node root = new Node(-1);
        tree.root = root;

        Node rootLeft = new Node(12);
        Node rootRight = new Node(23);
        Node child = new Node(3993);
        root.addLeft(rootLeft);
        root.addRight(rootRight);
        rootLeft.addLeft(child);

        System.out.println(tree.inOrderTraversalToString());

        Node successor = BRTree.getSuccessor(tree, rootRight);
        System.out.println(successor.value);
    }

}
