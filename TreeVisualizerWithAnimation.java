import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class TreeVisualizerWithAnimation {

    static TreeNode root = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Binary Tree Visualizer with Traversal Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        JTextField inputField = new JTextField();
        JButton buildButton = new JButton("Build Tree");
        JButton preorderBtn = new JButton("Preorder");
        JButton inorderBtn = new JButton("Inorder");
        JButton postorderBtn = new JButton("Postorder");

        JComboBox<String> speedCombo = new JComboBox<>(new String[] { "Slow", "Medium", "Fast" });
        JLabel resultLabel = new JLabel("Traversal result will appear here");

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Enter tree nodes (level order, comma separated):"), BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buildButton, BorderLayout.EAST);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(preorderBtn);
        buttonsPanel.add(inorderBtn);
        buttonsPanel.add(postorderBtn);
        buttonsPanel.add(new JLabel("Speed:"));
        buttonsPanel.add(speedCombo);

        topPanel.add(inputPanel);
        topPanel.add(buttonsPanel);
        topPanel.add(resultLabel);

        TreePanel treePanel = new TreePanel();

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(treePanel, BorderLayout.CENTER);

        // Build tree from input
        buildButton.addActionListener(e -> {
            String input = inputField.getText();
            root = buildTreeFromInput(input);
            treePanel.setTreeRoot(root);
            treePanel.setHighlightedNode(null);
            treePanel.setTraversalList(null);
            treePanel.repaint();
            resultLabel.setText("Traversal result will appear here");
        });

        // Timer for animation
        final javax.swing.Timer[] timer = new javax.swing.Timer[1];

        final List<TreeNode>[] traversalList = new List[1];
        final int[] currentIndex = { 0 };

        ActionListener startTraversal = e -> {
            if (root == null) {
                resultLabel.setText("Build the tree first!");
                return;
            }

            if (timer[0] != null && timer[0].isRunning()) {
                timer[0].stop();
            }

            JButton btn = (JButton) e.getSource();
            traversalList[0] = new ArrayList<>();

            // Select traversal based on button clicked
            switch (btn.getText()) {
                case "Preorder":
                    preorder(root, traversalList[0]);
                    break;
                case "Inorder":
                    inorder(root, traversalList[0]);
                    break;
                case "Postorder":
                    postorder(root, traversalList[0]);
                    break;
            }

            if (traversalList[0].isEmpty()) {
                resultLabel.setText("Tree is empty!");
                return;
            }

            currentIndex[0] = 0;
            treePanel.setTraversalList(traversalList[0]);
            treePanel.setHighlightedNode(traversalList[0].get(0));
            treePanel.repaint();

            int delay;
            switch ((String) speedCombo.getSelectedItem()) {
                case "Slow":
                    delay = 1000;
                    break;
                case "Medium":
                    delay = 500;
                    break;
                case "Fast":
                    delay = 200;
                    break;
                default:
                    delay = 500;
            }

            timer[0] = new javax.swing.Timer(delay, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e1) {
                    currentIndex[0]++;
                    if (currentIndex[0] >= traversalList[0].size()) {
                        timer[0].stop();
                        treePanel.setHighlightedNode(null);
                        treePanel.repaint();
                        resultLabel.setText(btn.getText() + " traversal: " + getValues(traversalList[0]));
                        return;
                    }
                    treePanel.setHighlightedNode(traversalList[0].get(currentIndex[0]));
                    treePanel.repaint();
                }
            });
            timer[0].start();
        };

        preorderBtn.addActionListener(startTraversal);
        inorderBtn.addActionListener(startTraversal);
        postorderBtn.addActionListener(startTraversal);

        frame.setVisible(true);
    }

    // Utility method to extract integer values from TreeNode list
    private static List<Integer> getValues(List<TreeNode> nodes) {
        List<Integer> vals = new ArrayList<>();
        for (TreeNode node : nodes) {
            vals.add(node.val);
        }
        return vals;
    }

    // Preorder traversal: root, left, right
    static void preorder(TreeNode node, List<TreeNode> list) {
        if (node == null)
            return;
        list.add(node);
        preorder(node.left, list);
        preorder(node.right, list);
    }

    // Inorder traversal: left, root, right
    static void inorder(TreeNode node, List<TreeNode> list) {
        if (node == null)
            return;
        inorder(node.left, list);
        list.add(node);
        inorder(node.right, list);
    }

    // Postorder traversal: left, right, root
    static void postorder(TreeNode node, List<TreeNode> list) {
        if (node == null)
            return;
        postorder(node.left, list);
        postorder(node.right, list);
        list.add(node);
    }

    // Build tree from comma separated string (level order)
    public static TreeNode buildTreeFromInput(String data) {
        if (data == null || data.trim().isEmpty())
            return null;

        String[] parts = Arrays.stream(data.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        if (parts.length == 0 || parts[0].equalsIgnoreCase("null"))
            return null;

        TreeNode root = new TreeNode(Integer.parseInt(parts[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (i < parts.length) {
            TreeNode current = queue.poll();
            if (current == null)
                continue;

            // Left child
            if (i < parts.length && !parts[i].equalsIgnoreCase("null")) {
                current.left = new TreeNode(Integer.parseInt(parts[i]));
                queue.offer(current.left);
            }
            i++;

            // Right child
            if (i < parts.length && !parts[i].equalsIgnoreCase("null")) {
                current.right = new TreeNode(Integer.parseInt(parts[i]));
                queue.offer(current.right);
            }
            i++;
        }
        return root;
    }
}

// TreeNode definition
class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int x) {
        val = x;
    }
}

// Panel to draw the tree and highlight traversal
class TreePanel extends JPanel {
    private TreeNode treeRoot;
    private TreeNode highlightedNode;
    private List<TreeNode> traversalList;

    public void setTreeRoot(TreeNode root) {
        this.treeRoot = root;
        this.traversalList = null;
        this.highlightedNode = null;
    }

    public void setHighlightedNode(TreeNode node) {
        this.highlightedNode = node;
    }

    public void setTraversalList(List<TreeNode> list) {
        this.traversalList = list;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (treeRoot != null) {
            drawTree(g, treeRoot, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int xOffset) {
        Graphics2D g2 = (Graphics2D) g;

        // Draw lines connecting nodes
        g2.setColor(Color.BLACK);
        if (node.left != null) {
            g2.drawLine(x, y, x - xOffset, y + 70);
        }
        if (node.right != null) {
            g2.drawLine(x, y, x + xOffset, y + 70);
        }

        // Draw node circle and highlight if this node is current in traversal
        if (node == highlightedNode) {
            g2.setColor(Color.ORANGE);
            g2.fillOval(x - 22, y - 22, 44, 44);
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(x - 22, y - 22, 44, 44);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x - 20, y - 20, 40, 40);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x - 20, y - 20, 40, 40);
        }

        // Draw node value (bold black)
        g2.setColor(Color.BLACK);
        Font originalFont = g2.getFont();
        g2.setFont(originalFont.deriveFont(Font.BOLD, 16f));
        FontMetrics fm = g2.getFontMetrics();
        String valStr = Integer.toString(node.val);
        int strWidth = fm.stringWidth(valStr);
        int strHeight = fm.getAscent();
        g2.drawString(valStr, x - strWidth / 2, y + strHeight / 4);

        // Recursive calls for left and right children
        if (node.left != null) {
            drawTree(g2, node.left, x - xOffset, y + 70, xOffset / 2);
        }
        if (node.right != null) {
            drawTree(g2, node.right, x + xOffset, y + 70, xOffset / 2);
        }
    }
}
