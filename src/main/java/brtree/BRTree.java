package brtree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/09/04 0004.
 */
public class BRTree {
    private List<String> member = new ArrayList<>();

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
        if (node == null || !tree.member.contains(node.id)) {
            return null;
        }
        if (node == tree.root) {
            return tree.root.right;
        }
        //有右子树
        if (!Node.isNIL(node.right)) {
            Node curNode = node.right;
            while (!Node.isNIL(node.left)) {
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
            sb.append("(" + n.color);
            if (n == root) {
                sb.append("[root]");
            }
            sb.append(")");
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
        if (node == null || Node.isNIL(node) || member.contains(node.id)) {
            return;
        }
        if (this.root == null || Node.isNIL(root)) {
            this.root = node;
            this.root.parent = Node.getNIL(null);
            this.root.color = Node.COLOR.BLACK;
            member.add(node.id);
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

        member.add(node.id);
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
            } else if (curNode.parent == curNode.parent.parent.right) {
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
                } else {
                    curNode.parent.parent.color = Node.COLOR.RED;
                    curNode.parent.color = Node.COLOR.BLACK;
                    curNode = curNode.parent.parent;
                    LEFT_ROTATE(curNode);
                }
            }
        }
    }


//    public void delete(Node node) {
//
//        if (!member.contains(node.id)) {
//            return;
//        }
//
//        boolean hasTwoChild = false;
//        Node successorNode = null;
//        Node delNode = null;
//        if (!Node.isNIL(node.left) && !Node.isNIL(node.right)) {
//            successorNode = getSuccessor(this, node);
//            hasTwoChild = true;
//            delNode = successorNode;
//        }
//
//        //有两个孩子的节点的后继节点是不会有左孩子的
//        if (hasTwoChild) {
//            if (node == root) {
//                successorNode.parent = root.parent;
//                root = successorNode;
//                node.left.parent = successorNode;
//                successorNode.left = node.left;
//            } else {
//                successorNode.right.parent = successorNode.parent;
//                successorNode.parent.left = successorNode.right;
//
//                successorNode.parent = node.parent;
//                if (node.parent.left == node) {
//                    node.parent.left = successorNode;
//                } else {
//                    node.parent.right = successorNode;
//                }
//
//                successorNode.left = node.left;
//                node.left.parent = successorNode;
//
//                successorNode.right = node.right;
//                node.right.parent = successorNode;
//            }
//        } else {
//            delNode = node;
//            successorNode = Node.isNIL(node.left) ? node.right : node.left;
//
//            successorNode.parent = node.parent;
//            if (node.parent.left == node) {
//                node.parent.left = successorNode;
//            } else {
//                node.parent.right = successorNode;
//            }
//        }
//        //清除被删掉节点的所有引用
//        node.clearReference();
//        member.remove(node.id);
//
//        if (delNode.color == Node.COLOR.BLACK) {
//            BR_DELETE_FIXUP();
//        }
//    }

    public void delete2edition(Node node) {
        if (!member.contains(node.id)) {
            return;
        }

        Node realDelNode;
        if (Node.isNIL(node.left) || Node.isNIL(node.right)) {
            realDelNode = node;
        } else {
            realDelNode = getSuccessor(this, node);
        }

        Node movedNode;
        if (!Node.isNIL(realDelNode.left)) {
            movedNode = realDelNode.left;
        } else {
            movedNode = realDelNode.right;
        }

        if (Node.isNIL(realDelNode.parent)) {
            //这种情况只对应删除的节点是根且根只有一个孩子或没有孩子
            root = movedNode;
        } else if (realDelNode.parent.left == realDelNode) {
            realDelNode.parent.left = movedNode;
        } else {
            realDelNode.parent.right = movedNode;
        }
        movedNode.parent = realDelNode.parent;

        member.remove(node.id);
        //将node进行逻辑删除，将realDelNode的value和id赋给node
        if (node != realDelNode) {
            node.value = realDelNode.value;
            node.id = realDelNode.id;
        }
        realDelNode.clearReference();

        if (movedNode.color == Node.COLOR.BLACK) {
            BR_DELETE_FIXUP(movedNode);
        }

    }

    /**
     * node 的选择依据：
     * 1.通过该node的黑节点少了一个
     * 2.以该node为起点的所有路径满足红黑树的所有性质
     * @param node
     */
    private void BR_DELETE_FIXUP(Node node) {
        Node curNode = node;
        //TODO 恢复红黑树性质
        while (!Node.isNIL(curNode)) {
            if (Node.isNIL(curNode.parent)) {
                curNode.color = Node.COLOR.BLACK;
                return;
            }

            if (curNode.color == Node.COLOR.BLACK) {
                if (curNode.parent.left == curNode) {
                    curNode = fixLeftCase(curNode);
                } else {
                    curNode = fixRightCase(curNode);
                }
            } else {//如果该节点是红色，直接换成黑色就能将所有性质恢复
                curNode.color = Node.COLOR.BLACK;
                break;
            }
        }
    }

    /**
     * 当前节点是父节点的左孩子的情况
     */
    private Node fixLeftCase(Node node) {
        Node brother = node.parent.right;
        if (brother.color == Node.COLOR.RED) {//node = 黑,brother = 红, parent=黑 这种情况下的思路是，如何在不破坏右子树红黑性质的情况下，将当前节点的情况转化成brother为黑色的情况（因为可以成功恢复的情况brother都为黑色)
            //对父节点进行左旋,并置换brother和父节点的颜色，这可以保证通过原右子树的所有路径黑高度不变
            node.parent.color = Node.COLOR.RED;
            brother.color = Node.COLOR.BLACK;
            LEFT_ROTATE(node.parent);
        } else{//node = 黑,brother = 黑
            if (brother.left.color == Node.COLOR.BLACK && brother.right.color == Node.COLOR.BLACK) {//brother.left = 黑 brother.right = 黑
                if(brother.parent.color == Node.COLOR.BLACK) {//parent = 黑
                    //这种情况下将brother的颜色设置为红色，curNode 变为parent,这样parent之下的子树满足红黑性质,但通过parent节点的路径都少一个黑节点
                    brother.color = Node.COLOR.RED;
                    return node.parent;
                }else{//parent = 红
                    //这种情况下互换brother和parent的颜色，所有事情都变得平衡了起来:通过brother的路径黑高度不变，而node的父变成了黑色，黑高度+1,QED
                    brother.color = Node.COLOR.RED;
                    brother.color = Node.COLOR.BLACK;
                    return Node.getNIL(null);//返回nil停止循环
                }
            }

            if (brother.right.color == Node.COLOR.RED) {//brother.right = 黑, 不管brother.left 和 brother.parent的颜色,这里的操作都能解决
                //互换brother和brother.parent的颜色，对parent进行左旋，此时通过brother.left的黑高度不变,并且由于node多了一个黑父节点，左边的红黑性质已恢复
                Node.COLOR temp = brother.parent.color;
                brother.parent.color = brother.color;
                brother.color = temp;
                LEFT_ROTATE(brother.parent);
                //但是右边的红黑性质被破坏了，brother.right为红且通过它的路径少了一个黑色，此时只要把brother.right涂黑就完成了
                brother.right.color = Node.COLOR.BLACK;
                return Node.getNIL(null);
            }else if(brother.left.color == Node.COLOR.RED){//brother.left = 红,brother.right = 黑
                //这种情况无法直接求解，但是可以通过旋转把情况转换为上一种情况，而上一种情况可以直接返回;
                //互换brother.left 和brother的颜色
                brother.color = Node.COLOR.RED;
                brother.left.color = Node.COLOR.BLACK;
                RIGHT_ROTATE(brother);
            }
        }
        return node;
    }

    /**
     * 当前节点是父节点的右孩子的情况
     */
    private Node fixRightCase(Node node) {
        return null;
    }

    /**
     * <pre>
     *               A                  A
     *              /                  /
     * ->对C左旋    C         ==>      E
     *            /  \               /
     *           B    E             C
     *               /             / \
     *              D             B   D
     * </pre>
     *
     * @param node
     */
    public void LEFT_ROTATE(Node node) {
        //没有右子树无法左旋
        if (node == null || Node.isNIL(node) || Node.isNIL(node.right) || !member.contains(node.id)) {
            return;
        }

        Node nodeRight = node.right;
        //将nodeRight的左孩子挂到node下
        node.right = nodeRight.left;
        nodeRight.left.parent = node;

        nodeRight.parent = node.parent;
        //如果node本身是root,将右孩子变为root
        if (Node.isNIL(node.parent)) {
            this.root = nodeRight;
        } else if (node == node.parent.left) {
            node.parent.left = nodeRight;
        } else {
            node.parent.right = nodeRight;
        }
        //将node挂在nodeRight下
        nodeRight.left = node;
        node.parent = nodeRight;
    }

    public void RIGHT_ROTATE(Node node) {
        //没有左子树无法右旋
        if (node == null || Node.isNIL(node) || Node.isNIL(node.left) || !member.contains(node.id)) {
            return;
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

        tree.delete2edition(tree.root);
        System.out.println(tree.inOrderTraversalToString());
    }

}
