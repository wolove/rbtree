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
        if (node == null||!tree.member.contains(node)) {
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
            sb.append("(" + n.color + ")");
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
            this.root.color = Node.COLOR.BLACK;
            member.add(node);
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

        member.add(node);
        //进行红黑树调整
        if (node.parent.color == Node.COLOR.RED) {
            BR_INSERT_FIXUP(node);
        }
    }

    public void BR_INSERT_FIXUP(Node node) {
        Node curNode = node;

        while (!Node.isNIL(curNode)) {
            if (curNode.parent.color == Node.COLOR.BLACK) {
                return;
            }
            if (curNode == this.root) {
                curNode.color = Node.COLOR.BLACK;
                return;
            }
            //如果添加的节点在祖父看来是左边
            if (curNode.parent == curNode.parent.parent.left) {
                Node uncle = curNode.parent.parent.right;
                //1.如果node的parent和uncle都是红色，将parent和uncle变为黑色，祖父变为红色，当前节点变为祖父
                if (uncle.color == Node.COLOR.RED) {
                    curNode.parent.color = Node.COLOR.BLACK;
                    uncle.color = Node.COLOR.BLACK;
                    curNode.parent.parent.color = Node.COLOR.RED;
                    curNode = node.parent.parent;
                    continue;
                } else if (curNode == curNode.parent.right) {
                //2.如果node的parent是红色，uncle是黑色，且node是parent的右孩子
                    //红黑树的调整算法是希望红红在同一边，这样在对祖父进行旋转着色后就是一个合法的红黑树而不需要进一步的调整，故该情况要求将红色节点通过旋转的方式归到长高的那一边
                    //2.1以node.parent进行左旋,并以其为新的curNode进行红黑树调整
                    curNode = curNode.parent;
                    LEFT_ROTATE(curNode);
                } else {
                //3.红红已经在同一边了，且uncle仍是黑色或NIL,对祖父进行右旋并着色
                    curNode.parent.color = Node.COLOR.BLACK;
                    curNode.parent.parent.color = Node.COLOR.RED;
                    curNode = curNode.parent.parent;
                    RIGHT_ROTATE(curNode);
                }
            }else if(curNode.parent == curNode.parent.parent.right){
                Node uncle = curNode.parent.parent.left;
                if (uncle.color == Node.COLOR.RED) {
                    curNode.parent.color = Node.COLOR.BLACK;
                    uncle.color = Node.COLOR.BLACK;
                    curNode.parent.parent.color = Node.COLOR.RED;
                    curNode = curNode.parent.parent;
                    continue;
                } else if (curNode == curNode.parent.left) {
                    curNode = curNode.parent;
                    RIGHT_ROTATE(curNode);
                }else{
                    curNode.parent.parent.color = Node.COLOR.RED;
                    curNode.parent.color = Node.COLOR.BLACK;
                    curNode = curNode.parent.parent;
                    LEFT_ROTATE(curNode);
                }
            }
        }
    }


    public void delete(Node node) {
        if (!member.contains(node)) {
            return;
        }

        Node delNode;
        if (Node.isNIL(node.left) || Node.isNIL(node.right)) {
            delNode = node;
        }else{
            delNode = getSuccessor(this, node);
        }

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
        //如果node本身是root,将左孩子变为root
        if (Node.isNIL(node.parent)) {
            this.root = nodeLeft;
        } else if (node == node.parent.left) {
            node.parent.left = nodeLeft;
        } else {
            node.parent.right = nodeLeft;
        }
        //将node挂在nodeLeft下
        nodeLeft.right = node;
        node.parent = nodeLeft;
    }

    public static void main(String[] args) {
        BRTree tree = new BRTree();
        Node root = new Node(-1);

        Node rootLeft = new Node(12);
        Node rootRight = new Node(23);
        Node child = new Node(3993);
        Node gc = new Node(-3);
        tree.add(root);
        tree.add(rootLeft);
        tree.add(rootRight);
        tree.add(child);
        tree.add(gc);

        System.out.println(tree.inOrderTraversalToString());

        System.out.println(tree.root.value);
        tree.LEFT_ROTATE(root);
        System.out.println(tree.root.value);
        tree.RIGHT_ROTATE(tree.root);
        System.out.println(tree.root.value);

    }

}
