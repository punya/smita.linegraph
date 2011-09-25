package punya;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.io.Closeables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;

public class Linegraph extends AbstractModule {
    @Override
    protected void configure() {
        bind(PApplet.class).to(LinegraphApplet.class);
    }

    @Provides
    List<String[]> data() throws IOException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream("data.csv"),
                    Charsets.UTF_8);
            return new CSVReader(reader).readAll();
        } finally {
            Closeables.close(reader, false);
        }
    }

    @Provides
    Node root(List<String[]> data) {
        Node root = new Node("", null);
        for (String[] path : data) {
            root.getOrAddPath(path[1]);
            root.getOrAddPath(path[2]);
        }
        return root;
    }

    @Provides
    Map<String, Table<Node, Node, Float>> correls(Node root, List<String[]> data) {
        Map<String, Table<Node, Node, Float>> out = Maps.newHashMap();
        for (String[] row : data) {
            final Table<Node, Node, Float> tbl;
            if (out.containsKey(row[0])) {
                tbl = out.get(row[0]);
            } else {
                out.put(row[0], tbl = HashBasedTable.create());
            }
            tbl.put(root.getOrAddPath(row[1]), root.getOrAddPath(row[2]),
                    Float.valueOf(row[3]));
        }
        return out;
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[] { Linegraph.class.getName() }, Guice
                .createInjector(new Linegraph()).getInstance(PApplet.class));
    }
}
