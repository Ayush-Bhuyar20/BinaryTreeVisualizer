import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;

class TreeNode {
    int val;
    TreeNode left, right;

    TreeNode(int val) {
        this.val = val;
    }
}

public class TreeVisualizerApp {
    static TreeNode root = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Binary Tree Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        // NORTH panel for input
        JLabel label = new JLabel("Enter Tree Nodes (level-order, comma separated):");
        JTextField inputField = new JTextField();
        JButton buildButton = new JButton("Build Tree");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));
        inputPanel.add(label);
        inputPanel.add(inputField);
        inputPanel.add(buildButton);

        // CENTER panel for tree drawing
        TreePanel treePanel = new TreePanel();

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(treePanel, BorderLayout.CENTER);

        buildButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = inputField.getText();
                root = buildTreeFromInput(input);
                treePanel.setTreeRoot(root); // update the tree in drawing panel
                treePanel.repaint();
            }
        });

        frame.setVisible(true);
    }

    public static TreeNode buildTreeFromInput(String data) {
        if (data == null || data.isEmpty())
            return null;

        // Trim all parts to avoid space issues
        String[] parts = Arrays.stream(data.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        if (parts[0].equalsIgnoreCase("null"))
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
                TreeNode left = new TreeNode(Integer.parseInt(parts[i]));
                current.left = left;
                queue.offer(left);
            }
            i++;

            // Right child
            if (i < parts.length && !parts[i].equalsIgnoreCase("null")) {
                TreeNode right = new TreeNode(Integer.parseInt(parts[i]));
                current.right = right;
                queue.offer(right);
            }
            i++;
        }

        return root;
    }

}

// Panel to draw the tree
class TreePanel extends JPanel {
    private TreeNode treeRoot;

    public void setTreeRoot(TreeNode root) {
        this.treeRoot = root;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (treeRoot != null) {
            drawTree(g, treeRoot, getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void drawTree(Graphics g, TreeNode node, int x, int y, int xOffset) {
        Graphics2D g2 = (Graphics2D) g; // cast to Graphics2D for better control

        // Draw connecting lines first (black)
        g2.setColor(Color.BLACK);
        if (node.left != null) {
            g2.drawLine(x, y, x - xOffset, y + 70);
        }
        if (node.right != null) {
            g2.drawLine(x, y, x + xOffset, y + 70);
        }

        // Draw circle (white fill with black border)
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 20, y - 20, 40, 40);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2)); // thicker border
        g2.drawOval(x - 20, y - 20, 40, 40);

        // Draw number (black, bold)
        Font originalFont = g2.getFont();
        g2.setFont(originalFont.deriveFont(Font.BOLD, 16f));

        // Center the string roughly
        FontMetrics fm = g2.getFontMetrics();
        String valStr = Integer.toString(node.val);
        int strWidth = fm.stringWidth(valStr);
        int strHeight = fm.getAscent();

        g2.drawString(valStr, x - strWidth / 2, y + strHeight / 4);

        // Recurse left and right
        if (node.left != null) {
            drawTree(g2, node.left, x - xOffset, y + 70, xOffset / 2);
        }
        if (node.right != null) {
            drawTree(g2, node.right, x + xOffset, y + 70, xOffset / 2);
        }
    }

}
