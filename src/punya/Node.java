package punya;

import java.util.List;

import com.google.common.collect.Lists;

public class Node {
    
    final String name, path;
    final List<Node> children = Lists.newArrayList();

    public Node(String name, Node parent) {
        this.name = name;
        this.path = (parent == null ? "" : parent.path + ", ") + name;
    }
    
    public Node getOrAddPath(String path) {
        Node it = this;
        for (String piece : path.split(", "))
            it = it.getOrAddChild(piece);
        return it;
    }

    public Node getOrAddChild(String name) {
        for (Node child : children)
            if (child.name.equals(name))
                return child;

        Node node = new Node(name, this);
        children.add(node);
        return node;
    }

}
