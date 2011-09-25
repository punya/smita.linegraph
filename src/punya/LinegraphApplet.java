package punya;

import java.util.Map;

import javax.inject.Inject;

import processing.core.PApplet;
import processing.core.PFont;

import com.google.common.collect.Maps;
import com.google.common.collect.Table;

@SuppressWarnings("serial")
public class LinegraphApplet extends PApplet {
    private final class TreeRenderer implements Runnable {
        private final PFont serif, sans;

        float x = 0;
        int y = 0;

        private final Map<String, Float> pos;

        private TreeRenderer(PFont serif, PFont sans, Map<String, Float> pos) {
            this.serif = serif;
            this.sans = sans;
            this.pos = pos;
        }

        @Override
        public void run() {
            textAlign(LEFT, TOP);
            visit(root);
        }

        private float ownWidth(Node node) {
            if (node.name.equals("Spleen neutrophils (%)")) {
                return LINE_HEIGHT * 4;
            } else if (node.children.isEmpty()) {
                return LINE_HEIGHT;
            } else {
                return textWidth(" " + node.name + " ");
            }
        }

        private void visit(Node node) {
            float xsave = x;
            if (!node.name.isEmpty()) ++y;
            for (Node child : node.children) {
                visit(child);
            }
            if (!node.name.isEmpty()) --y;
            x = max(x, xsave + ownWidth(node));
            pos.put(node.path, xsave + LINE_HEIGHT / 2);

            if (node.name.isEmpty()) {
                // do nothing
            } else if (node.children.isEmpty()) {
                strokeWeight(3f);
                stroke(0.7f, 0.7f, 0.7f);
                line(xsave + LINE_HEIGHT / 2, -12f, xsave + LINE_HEIGHT / 2, 120f);
                noStroke();

                textFont(serif);

                pushMatrix();
                translate(xsave + LINE_HEIGHT, y * LINE_HEIGHT);
                rotate(PI / 2);

                fill(1f);
                rect(0, 0, textWidth(" " + node.name + " "), LINE_HEIGHT);

                fill(0f);
                textAlign(LEFT, TOP);
                text(" " + node.name, 0, 0);
                popMatrix();
            } else {
                fill(0.2f + 0.1f * y);
                strokeWeight(0.05f);
                stroke(1f);
                rect(xsave, y * LINE_HEIGHT, x - xsave, LINE_HEIGHT);
                noStroke();
                fill(1f);
                textFont(sans);
                textAlign(CENTER, TOP);
                text(" " + node.name + " ", (x + xsave) / 2, (y + 0.1f) * LINE_HEIGHT);
            }
        
        }
    }

    private final Node root;
    private final Map<String, Table<Node, Node, Float>> correls;

    @Inject
    public LinegraphApplet(Node root, Map<String, Table<Node, Node, Float>> correls) {
        this.root = root;
        this.correls = correls;
    }

    @Override
    public void setup() {
        size(1200, 750, PDF, "out.pdf");
        smooth();

        colorMode(RGB, 1.0f);
        background(1.0f);
        noStroke();
        fill(0f);
        final PFont sans = createFont("Calibri-Bold", 11), serif = createFont("Cambria", 11);
        textSize(8f);
        
        translate(0f, 300f);
        Map<String, Float> pos = Maps.newHashMap();
        new TreeRenderer(serif, sans, pos).run();
        
        ellipseMode(CENTER);
        noFill();
        strokeWeight(3f);

        for (Table.Cell<Node, Node, Float> c : correls.get("Infected").cellSet()) {
            float x1 = pos.get(c.getColumnKey().path), x2 = pos.get(c.getRowKey().path);
            float radius = abs((x2 - x1));
            if (c.getValue() > 0) {
                stroke(0.6f, 1f, 0.6f);
            } else {
                stroke(1f, 0.6f, 0.6f);
            }
            arc((x2 + x1) / 2, -12f, radius, radius / 2, PI, TWO_PI);
        }

        for (Table.Cell<Node, Node, Float> c : correls.get("Uninfected").cellSet()) {
            float x1 = pos.get(c.getColumnKey().path), x2 = pos.get(c.getRowKey().path);
            float radius = abs((x2 - x1));
            println(x1 + " " + x2);
            if (c.getValue() > 0) {
                stroke(0.6f, 1f, 0.6f);
            } else {
                stroke(1f, 0.6f, 0.6f);
            }
            arc((x2 + x1) / 2, 120f, radius, radius / 2, 0, PI);
        }

    }

    private static final float LINE_HEIGHT = 18;
    
}
