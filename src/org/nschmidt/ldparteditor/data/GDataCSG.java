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

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import org.nschmidt.csg.CSG;
import org.nschmidt.csg.CSGCircle;
import org.nschmidt.csg.CSGCone;
import org.nschmidt.csg.CSGCube;
import org.nschmidt.csg.CSGCylinder;
import org.nschmidt.csg.CSGQuad;
import org.nschmidt.csg.CSGSphere;
import org.nschmidt.csg.Plane;
import org.nschmidt.ldparteditor.composites.Composite3D;
import org.nschmidt.ldparteditor.enums.MyLanguage;
import org.nschmidt.ldparteditor.helpers.math.MathHelper;
import org.nschmidt.ldparteditor.helpers.math.Vector3d;
import org.nschmidt.ldparteditor.helpers.math.Vector3dd;
import org.nschmidt.ldparteditor.i18n.I18n;
import org.nschmidt.ldparteditor.logger.NLogger;
import org.nschmidt.ldparteditor.text.DatParser;

/**
 * @author nils
 *
 */
public final class GDataCSG extends GData {

    final byte type;

    private final static HashMap<String, CSG> linkedCSG = new HashMap<String, CSG>();
    private static boolean deleteAndRecompile = true;

    private final static HashSet<GDataCSG> registeredData = new HashSet<GDataCSG>();
    private final static HashSet<GDataCSG> parsedData = new HashSet<GDataCSG>();

    private static int quality = 16;
    private int global_quality = 16;
    private double global_epsilon = 1e-6;

    private final String ref1;
    private final String ref2;
    private final String ref3;

    private CSG compiledCSG = null;
    private final GColour colour;
    final Matrix4f matrix;

    public static void forceRecompile() {
        registeredData.add(null);
        Plane.EPSILON = 1e-3;
    }

    public static void fullReset() {
        quality = 16;
        registeredData.clear();
        linkedCSG.clear();
        parsedData.clear();
        Plane.EPSILON = 1e-3;
    }

    public static void resetCSG() {
        quality = 16;
        HashSet<GDataCSG> ref = new HashSet<GDataCSG>(registeredData);
        ref.removeAll(parsedData);
        deleteAndRecompile = !ref.isEmpty();
        if (deleteAndRecompile) {
            registeredData.clear();
            registeredData.add(null);
            linkedCSG.clear();
        }
        parsedData.clear();
    }

    public byte getCSGtype() {
        return type;
    }

    // CASE 1 0 !LPE [CSG TAG] [ID] [COLOUR] [MATRIX] 17
    // CASE 2 0 !LPE [CSG TAG] [ID] [ID2] [ID3] 6
    // CASE 3 0 !LPE [CSG TAG] [ID] 4
    public GDataCSG(byte type, String csgLine, GData1 parent) {
        registeredData.add(this);
        String[] data_segments = csgLine.trim().split("\\s+"); //$NON-NLS-1$
        this.type = type;
        this.text = csgLine;
        switch (type) {
        case CSG.COMPILE:
            if (data_segments.length == 4) {
                ref1 = data_segments[3] + "#>" + parent.shortName; //$NON-NLS-1$
            } else {
                ref1 = null;
            }
            ref2 = null;
            ref3 = null;
            colour = null;
            matrix = null;
            break;
        case CSG.QUAD:
        case CSG.CIRCLE:
        case CSG.ELLIPSOID:
        case CSG.CUBOID:
        case CSG.CYLINDER:
        case CSG.CONE:
            if (data_segments.length == 17) {
                ref1 = data_segments[3] + "#>" + parent.shortName; //$NON-NLS-1$
                GColour c = DatParser.validateColour(data_segments[4], .5f, .5f, .5f, 1f);
                if (c != null) {
                    colour = c.clone();
                } else {
                    colour = null;
                }
                matrix = MathHelper.matrixFromStrings(data_segments[5], data_segments[6], data_segments[7], data_segments[8], data_segments[11], data_segments[14], data_segments[9],
                        data_segments[12], data_segments[15], data_segments[10], data_segments[13], data_segments[16]);
            } else {
                colour = null;
                matrix = null;
                ref1 = null;
            }
            ref2 = null;
            ref3 = null;
            break;
        case CSG.DIFFERENCE:
        case CSG.INTERSECTION:
        case CSG.UNION:
            if (data_segments.length == 6) {
                ref1 = data_segments[3] + "#>" + parent.shortName; //$NON-NLS-1$
                ref2 = data_segments[4] + "#>" + parent.shortName; //$NON-NLS-1$
                ref3 = data_segments[5] + "#>" + parent.shortName; //$NON-NLS-1$
            } else {
                ref1 = null;
                ref2 = null;
                ref3 = null;
            }
            colour = null;
            matrix = null;
            break;
        case CSG.QUALITY:
            if (data_segments.length == 4) {
                try {
                    int q = Integer.parseInt(data_segments[3]);
                    if (q > 0 && q < 49) {
                        global_quality = q;
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                }
                ref1 = data_segments[3] + "#>" + parent.shortName; //$NON-NLS-1$
            } else {
                ref1 = null;
            }
            ref2 = null;
            ref3 = null;
            colour = null;
            matrix = null;
            break;
        case CSG.EPSILON:
            if (data_segments.length == 4) {
                try {
                    double q = Double.parseDouble(data_segments[3]);
                    if (q > 0d) {
                        global_epsilon = q;
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                }
                ref1 = data_segments[3] + "#>" + parent.shortName; //$NON-NLS-1$
            } else {
                ref1 = null;
            }
            ref2 = null;
            ref3 = null;
            colour = null;
            matrix = null;
            break;
        default:
            ref1 = null;
            ref2 = null;
            ref3 = null;
            colour = null;
            matrix = null;
            break;
        }
    }

    private void drawAndParse(Composite3D c3d) {
        parsedData.add(this);
        if (deleteAndRecompile) {
            registeredData.remove(null);
            try {
                compiledCSG = null;
                registeredData.add(this);
                if (ref1 != null) {
                    switch (type) {
                    case CSG.QUAD:
                    case CSG.CIRCLE:
                    case CSG.ELLIPSOID:
                    case CSG.CUBOID:
                    case CSG.CYLINDER:
                    case CSG.CONE:
                        if (matrix != null) {
                            switch (type) {
                            case CSG.QUAD:
                                CSGQuad quad = new CSGQuad();
                                CSG csgQuad = quad.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgQuad);
                                break;
                            case CSG.CIRCLE:
                                CSGCircle circle = new CSGCircle(quality);
                                CSG csgCircle = circle.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgCircle);
                                break;
                            case CSG.ELLIPSOID:
                                CSGSphere sphere = new CSGSphere(quality, quality / 2);
                                CSG csgSphere = sphere.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgSphere);
                                break;
                            case CSG.CUBOID:
                                CSGCube cube = new CSGCube();
                                CSG csgCube = cube.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgCube);
                                break;
                            case CSG.CYLINDER:
                                CSGCylinder cylinder = new CSGCylinder(quality);
                                CSG csgCylinder = cylinder.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgCylinder);
                                break;
                            case CSG.CONE:
                                CSGCone cone = new CSGCone(quality);
                                CSG csgCone = cone.toCSG(colour).transformed(matrix);
                                linkedCSG.put(ref1, csgCone);
                                break;
                            default:
                                break;
                            }
                        }
                        break;
                    case CSG.COMPILE:
                        if (linkedCSG.containsKey(ref1)) {
                            compiledCSG = linkedCSG.get(ref1);
                            compiledCSG.compile();
                        } else {
                            compiledCSG = null;
                        }
                        break;
                    case CSG.DIFFERENCE:
                        if (linkedCSG.containsKey(ref1) && linkedCSG.containsKey(ref2)) {
                            linkedCSG.put(ref3, linkedCSG.get(ref1).difference(linkedCSG.get(ref2)));
                        }
                        break;
                    case CSG.INTERSECTION:
                        if (linkedCSG.containsKey(ref1) && linkedCSG.containsKey(ref2)) {
                            linkedCSG.put(ref3, linkedCSG.get(ref1).intersect(linkedCSG.get(ref2)));
                        }
                        break;
                    case CSG.UNION:
                        if (linkedCSG.containsKey(ref1) && linkedCSG.containsKey(ref2)) {
                            linkedCSG.put(ref3, linkedCSG.get(ref1).union(linkedCSG.get(ref2)));
                        }
                        break;
                    case CSG.QUALITY:
                        quality = global_quality;
                        break;
                    case CSG.EPSILON:
                        Plane.EPSILON = global_epsilon;
                        break;
                    default:
                        break;
                    }
                }
            } catch (StackOverflowError e) {
                registeredData.clear();
                linkedCSG.clear();
                parsedData.clear();
                deleteAndRecompile = false;
                registeredData.add(null);
                Plane.EPSILON = Plane.EPSILON * 10d;
            } catch (Exception e) {

            }
        }
        if (compiledCSG != null) {
            if (c3d.getRenderMode() != 5) {
                compiledCSG.draw(c3d);
            } else {
                compiledCSG.draw_textured(c3d);
            }
        }
    }

    @Override
    public void draw(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawRandomColours(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawBFC(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawBFCuncertified(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawBFC_backOnly(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawBFC_Colour(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawBFC_Textured(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public void drawWhileAddCondlines(Composite3D c3d) {
        drawAndParse(c3d);
    }

    @Override
    public int type() {
        return 8;
    }

    @Override
    String getNiceString() {
        return text;
    }

    @Override
    public String inlinedString(byte bfc, GColour colour) {
        switch (type) {
        case CSG.COMPILE:
            if (compiledCSG != null) {
                StringBuilder sb = new StringBuilder();

                Object[] messageArguments = {getNiceString()};
                MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$
                formatter.setLocale(MyLanguage.LOCALE);
                formatter.applyPattern(I18n.DATFILE_Inlined);

                sb.append(formatter.format(messageArguments) + "<br>"); //$NON-NLS-1$

                // FIXME Needs T-Junction Elimination!

                HashMap<Integer, ArrayList<GData3>> surfaces = new HashMap<Integer, ArrayList<GData3>>();
                TreeSet<Vector3dd> verts = new TreeSet<Vector3dd>();
                HashMap<GData3, Integer[]> result = compiledCSG.getResult();
                HashMap<Integer, ArrayList<Vector3dd[]>> edges = new HashMap<Integer, ArrayList<Vector3dd[]>>();

                // 1. Create surfaces, vertices and edges
                NLogger.debug(getClass(), "1. Create surfaces, vertices and edges"); //$NON-NLS-1$
                for (GData3 g3 : result.keySet()) {
                    Integer key = result.get(g3)[0];
                    if (!surfaces.containsKey(key)) {
                        surfaces.put(key, new ArrayList<GData3>());
                        edges.put(key, new ArrayList<Vector3dd[]>());
                    }
                    ArrayList<GData3> elements = surfaces.get(key);
                    ArrayList<Vector3dd[]> edges2 = edges.get(key);
                    elements.add(g3);
                    Vector3dd a = new Vector3dd(new Vector3d(g3.X1, g3.Y1, g3.Z1));
                    Vector3dd b = new Vector3dd(new Vector3d(g3.X2, g3.Y2, g3.Z2));
                    Vector3dd c = new Vector3dd(new Vector3d(g3.X3, g3.Y3, g3.Z3));
                    verts.add(a);
                    verts.add(b);
                    verts.add(c);
                    edges2.add(new Vector3dd[]{a, b});
                    edges2.add(new Vector3dd[]{b, c});
                    edges2.add(new Vector3dd[]{c, a});
                }

                // 1.5 Unify near vertices (Threshold 0.001)

                // 2. Detect outer edges
                NLogger.debug(getClass(), "2. Detect outer edges"); //$NON-NLS-1$
                {
                    TreeSet<Integer> keys = new TreeSet<Integer>();
                    for (GData3 g3 : result.keySet()) {
                        Integer key = result.get(g3)[0];
                        keys.add(key);
                    }
                    for (Integer key : keys) {
                        ArrayList<Vector3dd[]> outerEdges = new ArrayList<Vector3dd[]>();
                        ArrayList<Vector3dd[]> edges2 = edges.get(key);
                        for (Vector3dd[] e1 : edges2) {
                            boolean isOuter = true;
                            for (Vector3dd[] e2 : edges2) {
                                if (e1 != e2) {
                                    if (
                                            e1[0].equals(e2[0]) && e1[1].equals(e2[1])
                                            || e1[1].equals(e2[0]) && e1[0].equals(e2[1])) {
                                        isOuter = false;
                                        break;
                                    }
                                }
                            }
                            if (isOuter) {
                                outerEdges.add(new Vector3dd[]{e1[0], e1[1]});
                            }
                        }
                        edges2.clear();
                        edges2.addAll(outerEdges);
                    }


                    // 3. Remove collinear points
                    NLogger.debug(getClass(), "3. Remove collinear points"); //$NON-NLS-1$
                    {
                        final BigDecimal fourMinusEpsilon = new BigDecimal("3.9"); //$NON-NLS-1$
                        for (Integer key : keys) {
                            boolean foundCollinearity = true;
                            while (foundCollinearity) {
                                foundCollinearity = false;
                                ArrayList<Vector3dd[]> edges2 = edges.get(key);
                                Vector3dd[] match = new Vector3dd[2];
                                for (Vector3dd[] e1 : edges2) {
                                    if (foundCollinearity) {
                                        break;
                                    }
                                    for (Vector3dd[] e2 : edges2) {
                                        if (e1 != e2) {

                                            int[] index = new int[]{0,0,0,0};
                                            if (e1[0].equals(e2[0])) {
                                                index[0] = 0;
                                                index[1] = 1;
                                                index[2] = 0;
                                                index[3] = 1;
                                            } else if (e1[1].equals(e2[1])) {
                                                index[0] = 1;
                                                index[1] = 0;
                                                index[2] = 1;
                                                index[3] = 0;
                                            } else if (e1[1].equals(e2[0])) {
                                                index[0] = 1;
                                                index[1] = 0;
                                                index[2] = 0;
                                                index[3] = 1;
                                            } else if (e1[0].equals(e2[1])) {
                                                index[0] = 0;
                                                index[1] = 1;
                                                index[2] = 1;
                                                index[3] = 0;
                                            }

                                            // Check "angle"
                                            Vector3d v1 = Vector3d.sub(e1[index[1]], e1[index[0]]);
                                            if (v1.length().compareTo(BigDecimal.ZERO) == 0) {
                                                continue;
                                            }
                                            Vector3d v2 = Vector3d.sub(e2[index[1]], e2[index[0]]);
                                            if (v2.length().compareTo(BigDecimal.ZERO) == 0) {
                                                continue;
                                            }
                                            v1.normalise(v1);
                                            v2.normalise(v2);
                                            BigDecimal d = Vector3d.distSquare(v1, v2);
                                            if (d.compareTo(fourMinusEpsilon) > 0) {
                                                NLogger.debug(getClass(), "3. Found collinear points"); //$NON-NLS-1$
                                                foundCollinearity = true;
                                                match[0] = e1[index[0]];
                                                match[1] = e1[index[1]];
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (foundCollinearity) {
                                    for (Iterator<Vector3dd[]> it = edges2.iterator(); it.hasNext();) {
                                        Vector3dd[] v = it.next();
                                        if (v[0].equals(match[0]) && v[1].equals(match[1])) {
                                            it.remove();
                                        } else if (v[0].equals(match[1]) && v[1].equals(match[0])) {
                                            it.remove();
                                        } else if (v[0].equals(match[0])) {
                                            v[0] = match[1];
                                        } else if (v[1].equals(match[0])) {
                                            v[1] = match[1];
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Re-Triangulate
                    NLogger.debug(getClass(), "4. Re-Triangulate"); //$NON-NLS-1$
                    {
                        for (Integer key : keys) {
                            ArrayList<Vector3dd[]> edges2 = edges.get(key);
                            for (Vector3dd[] e1 : edges2) {

                            }
                        }
                    }
                }

                for (GData3 g3 : result.keySet()) {
                    StringBuilder lineBuilder3 = new StringBuilder();
                    lineBuilder3.append(3);
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    if (g3.colourNumber == -1) {
                        lineBuilder3.append("0x2"); //$NON-NLS-1$
                        lineBuilder3.append(MathHelper.toHex((int) (255f * g3.r)).toUpperCase());
                        lineBuilder3.append(MathHelper.toHex((int) (255f * g3.g)).toUpperCase());
                        lineBuilder3.append(MathHelper.toHex((int) (255f * g3.b)).toUpperCase());
                    } else {
                        lineBuilder3.append(g3.colourNumber);
                    }
                    Vector4f g3_v1 = new Vector4f(g3.x1, g3.y1, g3.z1, 1f);
                    Vector4f g3_v2 = new Vector4f(g3.x2, g3.y2, g3.z2, 1f);
                    Vector4f g3_v3 = new Vector4f(g3.x3, g3.y3, g3.z3, 1f);
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v1.x / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v1.y / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v1.z / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v2.x / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v2.y / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v2.z / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v3.x / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v3.y / 1000f));
                    lineBuilder3.append(" "); //$NON-NLS-1$
                    lineBuilder3.append(floatToString(g3_v3.z / 1000f));
                    sb.append(lineBuilder3.toString() + "<br>"); //$NON-NLS-1$
                }
                return sb.toString();
            } else {
                return getNiceString();
            }
        default:
            return getNiceString();
        }
    }

    private String floatToString(float flt) {
        String result;
        if (flt == (int) flt) {
            result = String.format("%d", (int) flt); //$NON-NLS-1$
        } else {
            result = String.format("%s", flt); //$NON-NLS-1$
            if (result.equals("0.0"))result = "0"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (result.startsWith("-0."))return "-" + result.substring(2); //$NON-NLS-1$ //$NON-NLS-2$
        if (result.startsWith("0."))return result.substring(1); //$NON-NLS-1$
        return result;
    }

    @Override
    public String transformAndColourReplace(String colour2, Matrix matrix) {
        boolean notChoosen = true;
        String t = null;
        switch (type) {
        case CSG.QUAD:
            if (notChoosen) {
                t = " CSG_QUAD "; //$NON-NLS-1$
                notChoosen = false;
            }
        case CSG.CIRCLE:
            if (notChoosen) {
                t = " CSG_CIRCLE "; //$NON-NLS-1$
                notChoosen = false;
            }
        case CSG.ELLIPSOID:
            if (notChoosen) {
                t = " CSG_ELLIPSOID "; //$NON-NLS-1$
                notChoosen = false;
            }
        case CSG.CUBOID:
            if (notChoosen) {
                t = " CSG_CUBOID "; //$NON-NLS-1$
                notChoosen = false;
            }
        case CSG.CYLINDER:
            if (notChoosen) {
                t = " CSG_CYLINDER "; //$NON-NLS-1$
                notChoosen = false;
            }
        case CSG.CONE:
            if (notChoosen) {
                t = " CSG_CONE "; //$NON-NLS-1$
                notChoosen = false;
            }
            StringBuilder colourBuilder = new StringBuilder();
            if (colour == null) {
                colourBuilder.append(16);
            } else if (colour.getColourNumber() == -1) {
                colourBuilder.append("0x2"); //$NON-NLS-1$
                colourBuilder.append(MathHelper.toHex((int) (255f * colour.getR())).toUpperCase());
                colourBuilder.append(MathHelper.toHex((int) (255f * colour.getG())).toUpperCase());
                colourBuilder.append(MathHelper.toHex((int) (255f * colour.getB())).toUpperCase());
            } else {
                colourBuilder.append(colour.getColourNumber());
            }
            Matrix4f newMatrix = new Matrix4f(this.matrix);
            Matrix4f newMatrix2 = new Matrix4f(matrix.getMatrix4f());
            Matrix4f.transpose(newMatrix, newMatrix);
            newMatrix.m30 = newMatrix.m03;
            newMatrix.m31 = newMatrix.m13;
            newMatrix.m32 = newMatrix.m23;
            newMatrix.m03 = 0f;
            newMatrix.m13 = 0f;
            newMatrix.m23 = 0f;
            Matrix4f.mul(newMatrix2, newMatrix, newMatrix);
            String col = colourBuilder.toString();
            if (col.equals(colour2))
                col = "16"; //$NON-NLS-1$
            String tag = ref1.substring(0, ref1.lastIndexOf("#>")); //$NON-NLS-1$
            return "0 !LPE" + t + tag + " " + col + " " + MathHelper.matrixToString(newMatrix); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        default:
            return text;
        }
    }

    @Override
    public void getBFCorientationMap(HashMap<GData, Byte> map) {}
    @Override
    public void getBFCorientationMapNOCERTIFY(HashMap<GData, Byte> map) {}
    @Override
    public void getBFCorientationMapNOCLIP(HashMap<GData, Byte> map) {}
    @Override
    public void getVertexNormalMap(TreeMap<Vertex, float[]> vertexLinkedToNormalCACHE, HashMap<GData, float[]> dataLinkedToNormalCACHE, VertexManager vm) {}
    @Override
    public void getVertexNormalMapNOCERTIFY(TreeMap<Vertex, float[]> vertexLinkedToNormalCACHE, HashMap<GData, float[]> dataLinkedToNormalCACHE, VertexManager vm) {}
    @Override
    public void getVertexNormalMapNOCLIP(TreeMap<Vertex, float[]> vertexLinkedToNormalCACHE, HashMap<GData, float[]> dataLinkedToNormalCACHE, VertexManager vm) {}

}
