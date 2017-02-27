/* MIT - License

Copyright (c) 2012 - this year, Nils Schmidt

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. */
package org.nschmidt.ldparteditor.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.nschmidt.ldparteditor.helpers.math.Vector3d;
import org.nschmidt.ldparteditor.helpers.math.Vector3dd;

public class VM26LineIntersector extends VM25Smooth {

    protected VM26LineIntersector(DatFile linkedDatFile) {
        super(linkedDatFile);
    }

    public void intersectionVerticesBetweenLines3D() {

        final Set<GData2> linesToIntersect = new HashSet<>();

        if (selectedLines.isEmpty()) {
            linesToIntersect.addAll(lines.keySet());
        } else {
            linesToIntersect.addAll(selectedLines);
        }

        clearSelection();

        final ArrayList<ArrayList<Vector3dd>> linesToParse = new ArrayList<ArrayList<Vector3dd>>();
        TreeSet<Vertex> m1 = new TreeSet<Vertex>();
        TreeSet<Vertex> m2 = new TreeSet<Vertex>();

        for (GData2 g2 : linesToIntersect) {
            Vertex[] verts = lines.get(g2);
            for (Vertex v : verts) {
                if (m1.contains(v)) {
                    m2.add(v);
                } else {
                    m1.add(v);
                }
            }
            ArrayList<Vector3dd> l = new ArrayList<Vector3dd>();
            l.add(new Vector3dd(verts[0]));
            l.add(new Vector3dd(verts[1]));
            linesToParse.add(l);
        }

        Set<Vertex> intersectionPoints = new TreeSet<Vertex>();

        // Calculate intersection points
        int i = 0;
        int j = 0;
        for (Iterator<ArrayList<Vector3dd>> iterator = linesToParse.iterator(); iterator.hasNext();) {
            ArrayList<Vector3dd> line = iterator.next();
            i++;
            j = 0;
            for (ArrayList<Vector3dd> line2 : linesToParse) {
                j++;
                if (j > i) {
                    TreeSet<Vector3dd> allVertices = new TreeSet<Vector3dd>();
                    for(int l = 0; l < 2; l++) {
                        allVertices.add(line.get(l));
                        allVertices.add(line2.get(l));
                    }
                    if (allVertices.size() == 4) {
                        Vector3d ip = intersectLineLineSegmentUnidirectional2(line.get(0), line.get(1), line2.get(0), line2.get(1));
                        if (ip != null) {
                            intersectionPoints.add(new Vertex(ip));
                        }
                    }
                }
            }
        }

        for (Vertex intersection : intersectionPoints) {
            linkedDatFile.addToTailOrInsertAfterCursor(addVertex(intersection));
        }

        if (!intersectionPoints.isEmpty()) {
            setModified_NoSync();
            syncWithTextEditors(true);
            updateUnsavedStatus();
        }
    }

    public void intersectionVerticesBetweenLines2D() {
        // FIXME Needs implementation!
    }
}
