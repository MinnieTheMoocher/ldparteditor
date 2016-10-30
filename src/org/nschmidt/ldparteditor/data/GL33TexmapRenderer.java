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
import java.util.HashMap;

import org.lwjgl.util.vector.Vector3f;
import org.nschmidt.ldparteditor.data.GL33ModelRenderer.GDataAndWinding;
import org.nschmidt.ldparteditor.opengl.GL33Helper;
import org.nschmidt.ldparteditor.opengl.GLShader;
import org.nschmidt.ldparteditor.opengl.OpenGLRenderer;

public enum GL33TexmapRenderer {
    INSTANCE;

    public static void render(ArrayList<GDataAndWinding> texmapData, GLShader mainShader, OpenGLRenderer renderer,
            HashMap<GData, Vector3f[]> normalMap, HashMap<GData, Vertex[]> vertexMap, boolean smoothShading) {
        GTexture lastTexture = null;
        float[] uv;
        float[] triVertices = new float[36];
        float[] quadVertices = new float[48];

        int[] triIndices = new int[]{0, 1, 2};
        int[] quadIndices = new int[]{0, 1, 2, 2, 3, 0};
        for (GDataAndWinding gw : texmapData) {
            final GData gd = gw.data;
            switch (gd.type()) {
            case 3:
                GData3 gd3 = (GData3) gd;
                Vertex[] v = vertexMap.get(gd);
                Vector3f[] n = normalMap.get(gd);
                if (lastTexture != null && v != null && n != null) {
                    lastTexture.calcUVcoords1(gd3.x1, gd3.y1, gd3.z1, gd3.parent, gd);
                    lastTexture.calcUVcoords2(gd3.x2, gd3.y2, gd3.z2, gd3.parent);
                    lastTexture.calcUVcoords3(gd3.x3, gd3.y3, gd3.z3, gd3.parent);
                    uv = lastTexture.getUVcoords(true, gd);
                    switch (gw.winding) {
                    case BFC.CW:
                        colourise(3, gd3.r, gd3.g, gd3.b, gd3.a, triVertices);
                        if (smoothShading) {
                            if (gw.invertNext ^ gw.negativeDeterminant) {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[2].x, n[2].y, n[2].z, triVertices);
                                normal(2, n[1].x, n[1].y, n[1].z, triVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, triVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, triVertices);
                            }
                        } else {
                            if (gw.invertNext) {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, triVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, triVertices);
                            } else {
                                normal(0, -n[0].x, -n[0].y, -n[0].z, triVertices);
                                normal(1, -n[1].x, -n[1].y, -n[1].z, triVertices);
                                normal(2, -n[2].x, -n[2].y, -n[2].z, triVertices);
                            }
                        }
                        if (gw.negativeDeterminant ^ gw.invertNext) {
                            pointAt(0, v[0].x, v[0].y, v[0].z, triVertices);
                            pointAt(1, v[2].x, v[2].y, v[2].z, triVertices);
                            pointAt(2, v[1].x, v[1].y, v[1].z, triVertices);
                            uv(0, uv[0], uv[1], triVertices);
                            uv(1, uv[4], uv[5], triVertices);
                            uv(2, uv[2], uv[3], triVertices);
                        } else {
                            pointAt(0, v[0].x, v[0].y, v[0].z, triVertices);
                            pointAt(1, v[1].x, v[1].y, v[1].z, triVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, triVertices);
                            uv(0, uv[0], uv[1], triVertices);
                            uv(1, uv[2], uv[3], triVertices);
                            uv(2, uv[4], uv[5], triVertices);
                        }
                        break;
                    case BFC.CCW:
                        colourise(3, gd3.r, gd3.g, gd3.b, gd3.a, triVertices);
                        if (smoothShading) {
                            if (gw.invertNext ^ gw.negativeDeterminant) {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, triVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, triVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[2].x, n[2].y, n[2].z, triVertices);
                                normal(2, n[1].x, n[1].y, n[1].z, triVertices);
                            }
                        } else {
                            if (gw.invertNext) {
                                normal(0, -n[0].x, -n[0].y, -n[0].z, triVertices);
                                normal(1, -n[1].x, -n[1].y, -n[1].z, triVertices);
                                normal(2, -n[2].x, -n[2].y, -n[2].z, triVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, triVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, triVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, triVertices);
                            }
                        }
                        if (gw.negativeDeterminant ^ gw.invertNext) {
                            pointAt(0, v[0].x, v[0].y, v[0].z, triVertices);
                            pointAt(1, v[1].x, v[1].y, v[1].z, triVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, triVertices);
                            uv(0, uv[0], uv[1], triVertices);
                            uv(1, uv[2], uv[3], triVertices);
                            uv(2, uv[4], uv[5], triVertices);
                        } else {
                            pointAt(0, v[0].x, v[0].y, v[0].z, triVertices);
                            pointAt(1, v[2].x, v[2].y, v[2].z, triVertices);
                            pointAt(2, v[1].x, v[1].y, v[1].z, triVertices);
                            uv(0, uv[0], uv[1], triVertices);
                            uv(1, uv[4], uv[5], triVertices);
                            uv(2, uv[2], uv[3], triVertices);
                        }
                        break;
                    case BFC.NOCERTIFY:
                    default:
                        continue;
                    }
                    GL33Helper.drawTrianglesIndexedTextured_GeneralSlow(triVertices, triIndices);
                }
                continue;
            case 4:
                GData4 gd4 = (GData4) gd;
                v = vertexMap.get(gd);
                n = normalMap.get(gd);
                if (lastTexture != null && v != null && n != null) {
                    lastTexture.calcUVcoords1(gd4.x1, gd4.y1, gd4.z1, gd4.parent, gd);
                    lastTexture.calcUVcoords2(gd4.x2, gd4.y2, gd4.z2, gd4.parent);
                    lastTexture.calcUVcoords3(gd4.x3, gd4.y3, gd4.z3, gd4.parent);
                    lastTexture.calcUVcoords4(gd4.x4, gd4.y4, gd4.z4, gd4.parent);
                    uv = lastTexture.getUVcoords(false, gd);
                    switch (gw.winding) {
                    case BFC.CW:
                        colourise(4, gd4.r, gd4.g, gd4.b, gd4.a, quadVertices);
                        if (smoothShading) {
                            if (gw.invertNext ^ gw.negativeDeterminant) {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[3].x, n[3].y, n[3].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[1].x, n[1].y, n[1].z, quadVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[3].x, n[3].y, n[3].z, quadVertices);
                            }
                        } else {
                            if (gw.invertNext) {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[3].x, n[3].y, n[3].z, quadVertices);
                            } else {
                                normal(0, -n[0].x, -n[0].y, -n[0].z, quadVertices);
                                normal(1, -n[1].x, -n[1].y, -n[1].z, quadVertices);
                                normal(2, -n[2].x, -n[2].y, -n[2].z, quadVertices);
                                normal(3, -n[3].x, -n[3].y, -n[3].z, quadVertices);
                            }
                        }
                        if (gw.negativeDeterminant ^ gw.invertNext) {
                            pointAt(0, v[0].x, v[0].y, v[0].z, quadVertices);
                            pointAt(1, v[3].x, v[3].y, v[3].z, quadVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, quadVertices);
                            pointAt(3, v[1].x, v[1].y, v[1].z, quadVertices);
                            uv(0, uv[0], uv[1], quadVertices);
                            uv(1, uv[6], uv[7], quadVertices);
                            uv(2, uv[4], uv[5], quadVertices);
                            uv(3, uv[2], uv[3], quadVertices);
                        } else {
                            pointAt(0, v[0].x, v[0].y, v[0].z, quadVertices);
                            pointAt(1, v[1].x, v[1].y, v[1].z, quadVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, quadVertices);
                            pointAt(3, v[3].x, v[3].y, v[3].z, quadVertices);
                            uv(0, uv[0], uv[1], quadVertices);
                            uv(1, uv[2], uv[3], quadVertices);
                            uv(2, uv[4], uv[5], quadVertices);
                            uv(3, uv[6], uv[7], quadVertices);
                        }
                        break;
                    case BFC.CCW:
                        colourise(4, gd4.r, gd4.g, gd4.b, gd4.a, quadVertices);
                        if (smoothShading) {
                            if (gw.invertNext ^ gw.negativeDeterminant) {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[3].x, n[3].y, n[3].z, quadVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[3].x, n[3].y, n[3].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[1].x, n[1].y, n[1].z, quadVertices);
                            }
                        } else {
                            if (gw.invertNext) {
                                normal(0, -n[0].x, -n[0].y, -n[0].z, quadVertices);
                                normal(1, -n[1].x, -n[1].y, -n[1].z, quadVertices);
                                normal(2, -n[2].x, -n[2].y, -n[2].z, quadVertices);
                                normal(3, -n[3].x, -n[3].y, -n[3].z, quadVertices);
                            } else {
                                normal(0, n[0].x, n[0].y, n[0].z, quadVertices);
                                normal(1, n[1].x, n[1].y, n[1].z, quadVertices);
                                normal(2, n[2].x, n[2].y, n[2].z, quadVertices);
                                normal(3, n[3].x, n[3].y, n[3].z, quadVertices);
                            }
                        }
                        if (gw.negativeDeterminant ^ gw.invertNext) {
                            pointAt(0, v[0].x, v[0].y, v[0].z, quadVertices);
                            pointAt(1, v[1].x, v[1].y, v[1].z, quadVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, quadVertices);
                            pointAt(3, v[3].x, v[3].y, v[3].z, quadVertices);
                            uv(0, uv[0], uv[1], quadVertices);
                            uv(1, uv[2], uv[3], quadVertices);
                            uv(2, uv[4], uv[5], quadVertices);
                            uv(3, uv[6], uv[7], quadVertices);
                        } else {
                            pointAt(0, v[0].x, v[0].y, v[0].z, quadVertices);
                            pointAt(1, v[3].x, v[3].y, v[3].z, quadVertices);
                            pointAt(2, v[2].x, v[2].y, v[2].z, quadVertices);
                            pointAt(3, v[1].x, v[1].y, v[1].z, quadVertices);
                            uv(0, uv[0], uv[1], quadVertices);
                            uv(1, uv[6], uv[7], quadVertices);
                            uv(2, uv[4], uv[5], quadVertices);
                            uv(3, uv[2], uv[3], quadVertices);
                        }
                        break;
                    case BFC.NOCERTIFY:
                    default:
                        continue;
                    }
                    GL33Helper.drawTrianglesIndexedTextured_GeneralSlow(quadVertices, quadIndices);
                }
                continue;
            case 9:
                GDataTEX tex = (GDataTEX) gd;
                if (tex.meta == TexMeta.START || tex.meta == TexMeta.NEXT) {
                    lastTexture = tex.linkedTexture;
                    lastTexture.refreshCache();
                    lastTexture.bindGL33(renderer, mainShader);
                }
                continue;
            default:
            }
        }
    }

    private static void pointAt(int i, float x, float y, float z,
            float[] data) {
        int j = i * 12;
        data[j] = x;
        data[j + 1] = y;
        data[j + 2] = z;
    }

    private static void normal(int i, float x, float y, float z,
            float[] data) {
        int j = i * 12;
        data[j + 3] = x;
        data[j + 4] = y;
        data[j + 5] = z;
    }

    private static void colourise(int i, float r, float g, float b, float a,
            float[] data) {
        for (int j = 0; j < i; j++) {
            int k = j * 12;
            data[k + 6] = r;
            data[k + 7] = g;
            data[k + 8] = b;
            data[k + 9] = a;
        }
    }

    private static void uv(int i, float u, float v, float[] data) {
        int j = i * 12;
        data[j + 10] = u;
        data[j + 11] = v;
    }

}