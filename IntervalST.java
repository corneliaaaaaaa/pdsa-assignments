import java.util.List;
import java.util.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;

class IntervalST<Key extends Comparable<Key>, Value> {
    private Node root;

    private class Node {
        private Key lo, hi, max;
        private Value val;
        private int size;
        private Node left, right;

        public Node(Key lo, Key hi, Value val) {
            // initializes the node if required.
            this.lo = lo;
            this.hi = hi;
            this.val = val;
            // this.size = 1;
            this.max = hi;
        }
    }

    public IntervalST() {
        // initializes the tree if required.
    }

    public int size(Node x) {
        if (x == null) {
            return 0;
        }
        return x.size;
    }

    public void put(Key lo, Key hi, Value val) {
        root = put(root, lo, hi, val);
        // System.out.println("after put");
        // printRecursive(root);
    }

    public void printRecursive(Node cur) {
        Queue<Node> s = new LinkedList<>();
        s.offer(cur);

        while (!s.isEmpty()) {
            Node n = s.poll();
            if (n != null) {
                System.out.println(n.val + " " + n.lo + " " + n.hi + " " + n.max);

            } else {
                System.out.println("n is null");
            }
            if (n != null && n.left != null) {
                s.offer(n.left);
            }
            if (n != null && n.right != null) {
                s.offer(n.right);
            }
        }
        System.out.println("================================");

    }

    private Node put(Node cur, Key lo, Key hi, Value val) {
        if (cur == null) {
            return new Node(lo, hi, val);
        }

        int cmpLo = lo.compareTo(cur.lo);
        int cmpHi = hi.compareTo(cur.hi);

        if (cmpLo < 0) {
            cur.left = put(cur.left, lo, hi, val);
        } else if (cmpLo > 0) {
            cur.right = put(cur.right, lo, hi, val);
        } else {
            if (cmpHi < 0) {
                cur.left = put(cur.left, lo, hi, val);
            } else if (cmpHi > 0) {
                cur.right = put(cur.right, lo, hi, val);
            } else {
                cur.val = val;
            }
        }

        cur.max = maxOf(cur.max, hi);

        return cur;
    }

    private Key maxOf(Key a, Key b) {
        return (a.compareTo(b) >= 0) ? a : b;
    }

    public void delete(Key lo, Key hi) {
        root = delete(root, lo, hi);
        // System.out.println("after delete");
        // printRecursive(root);
    }

    private Node delete(Node cur, Key lo, Key hi) {
        if (cur == null) {
            return null;
        }

        int cmpLo = lo.compareTo(cur.lo);
        int cmpHi = hi.compareTo(cur.hi);

        if (cmpLo < 0) {
            cur.left = delete(cur.left, lo, hi);
        } else if (cmpLo > 0) {
            cur.right = delete(cur.right, lo, hi);
        } else {
            if (cmpHi < 0) {
                cur.left = delete(cur.left, lo, hi);
            } else if (cmpHi > 0) {
                cur.right = delete(cur.right, lo, hi);
            } else {
                // delete and restructure
                if (cur.left == null) {
                    return cur.right;
                } else if (cur.right == null) {
                    return cur.left;
                }

                Node toDelete = cur;
                cur = findMin(toDelete.right);
                cur.right = deleteMin(toDelete.right); // the smallest node in the toDelete right tree need to be the
                                                       // parent
                cur.left = toDelete.left;
            }
        }
        // cur.size = size(cur.left) + size(cur.right) + 1;
        if (cur.right != null && cur.left != null) {
            if (cur.left.max.compareTo(cur.right.max) >= 0) {
                cur.max = maxOf(cur.max, cur.left.max);

            } else if (cur.left.max.compareTo(cur.right.max) < 0) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        } else {
            if (cur.right == null && cur.left == null) {
                cur.max = cur.hi;
            } else if (cur.right == null) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left == null) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        }
        return cur;
    }

    private Node findMin(Node cur) {
        if (cur == null) {
            return null;
        }

        while (cur.left != null) {
            cur = cur.left;
        }
        return cur;
    }

    public void deleteMin() {
        root = deleteMin(root);
        // System.out.println("after deletion");
        // printRecursive(root);
    }

    private Node deleteMin(Node cur) {
        // delete the min node in the subtree and
        // replace the original position with the right child of the deleted node
        if (cur.left == null) {
            return cur.right;
        }
        cur.left = deleteMin(cur.left);
        // cur.size = 1 + size(cur.left) + size(cur.right);
        if (cur.right != null && cur.left != null) {
            if (cur.left.max.compareTo(cur.right.max) >= 0) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left.max.compareTo(cur.right.max) < 0) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        } else {
            if (cur.right == null && cur.left == null) {
                cur.max = cur.hi;
            } else if (cur.right == null) {
                cur.max = maxOf(cur.max, cur.left.max);
            } else if (cur.left == null) {
                cur.max = maxOf(cur.max, cur.right.max);
            }
        }
        return cur;
    }

    public List<Value> intersects(Key lo, Key hi) {
        List<Value> result = new ArrayList<>();
        searchAll(lo, hi, result);
        return result;
    }

    private void searchAll(Key lo, Key hi, List<Value> result) {
        if (root == null) {
            return;
        }

        Queue<Node> nodes = new ArrayDeque<>();
        nodes.offer(root);

        while (!nodes.isEmpty()) {
            Node cur = nodes.poll();

            if (cur.max.compareTo(lo) < 0) {
                continue;
            }

            if (cur.lo.compareTo(hi) <= 0 && cur.hi.compareTo(lo) >= 0) {
                result.add(cur.val);
            }

            if (cur.left != null && cur.left.max.compareTo(lo) >= 0) {
                nodes.offer(cur.left);
            }

            if (cur.right != null && cur.lo.compareTo(hi) <= 0) {
                nodes.offer(cur.right);
            }
        }
    }

    public static void main(String[] args) {
        // Example
        IntervalST<Integer, String> IST = new IntervalST<>();
        IST.put(2, 5, "badminton");
        IST.put(1, 5, "PDSA HW7");
        IST.put(3, 5, "Lunch");
        IST.put(3, 6, "Workout");
        IST.put(3, 7, "Do nothing");
        IST.delete(2, 5); // delete "badminton"
        System.out.println(IST.intersects(1, 2));

        IST.put(8, 8, "Dinner");
        System.out.println(IST.intersects(6, 10));

        IST.put(3, 7, "Do something"); // If an interval is identical to an existing node, then the value of that node
                                       // is updated accordingly
        System.out.println(IST.intersects(7, 7));

        IST.delete(3, 7); // delete "Do something"
        System.out.println(IST.intersects(7, 7));
    }
}