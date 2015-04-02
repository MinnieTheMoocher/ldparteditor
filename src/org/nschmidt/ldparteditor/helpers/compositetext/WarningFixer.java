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
package org.nschmidt.ldparteditor.helpers.compositetext;

import java.math.BigDecimal;

import org.nschmidt.ldparteditor.data.DatFile;
import org.nschmidt.ldparteditor.enums.Threshold;
import org.nschmidt.ldparteditor.helpers.math.Vector3d;

/**
 * @author nils
 *
 */
final class WarningFixer {

    public static String fix(int lineNumber, String sort, String line, String text, DatFile datFile) {
        // TODO Needs implementation!
        int s = Integer.parseInt(sort, 16);
        switch (s) {
        case 36: // Coplanar Quad
        {
            String[] data_segments = line.trim().split("\\s+"); //$NON-NLS-1$

            final Vector3d vertexA = new Vector3d();
            final Vector3d vertexB = new Vector3d();
            final Vector3d vertexC = new Vector3d();
            final Vector3d vertexD = new Vector3d();

            // 1st vertex
            vertexA.setX(new BigDecimal(data_segments[2], Threshold.mc));
            vertexA.setY(new BigDecimal(data_segments[3], Threshold.mc));
            vertexA.setZ(new BigDecimal(data_segments[4], Threshold.mc));
            // 2nd vertex
            vertexB.setX(new BigDecimal(data_segments[5], Threshold.mc));
            vertexB.setY(new BigDecimal(data_segments[6], Threshold.mc));
            vertexB.setZ(new BigDecimal(data_segments[7], Threshold.mc));
            // 3rd vertex
            vertexC.setX(new BigDecimal(data_segments[8], Threshold.mc));
            vertexC.setY(new BigDecimal(data_segments[9], Threshold.mc));
            vertexC.setZ(new BigDecimal(data_segments[10], Threshold.mc));
            // 4th vertex
            vertexD.setX(new BigDecimal(data_segments[11], Threshold.mc));
            vertexD.setY(new BigDecimal(data_segments[12], Threshold.mc));
            vertexD.setZ(new BigDecimal(data_segments[13], Threshold.mc));

            Vector3d[] normals = new Vector3d[4];
            Vector3d[] lineVectors = new Vector3d[4];
            lineVectors[0] = Vector3d.sub(vertexB, vertexA);
            lineVectors[1] = Vector3d.sub(vertexC, vertexB);
            lineVectors[2] = Vector3d.sub(vertexD, vertexC);
            lineVectors[3] = Vector3d.sub(vertexA, vertexD);
            normals[0] = Vector3d.cross(lineVectors[0], lineVectors[1]);
            normals[1] = Vector3d.cross(lineVectors[1], lineVectors[2]);
            normals[2] = Vector3d.cross(lineVectors[2], lineVectors[3]);
            normals[3] = Vector3d.cross(lineVectors[3], lineVectors[0]);

            double angle = Vector3d.angle(normals[0], normals[2]);
            angle = Math.min(angle, Math.abs(180.0 - angle));
            if (angle > Threshold.coplanarity_angle_error) {
                text = QuickFixer.setLine(lineNumber + 1,
                        "3 " + data_segments[1] + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                                data_segments[2] + " " + data_segments[3] + " " + data_segments[4] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[5] + " " + data_segments[6] + " " + data_segments[7] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[8] + " " + data_segments[9] + " " + data_segments[10] +  //$NON-NLS-1$ //$NON-NLS-2$
                                "<br>3 " + data_segments[1] + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                                data_segments[8] + " " + data_segments[9] + " " + data_segments[10] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[11] + " " + data_segments[12] + " " + data_segments[13] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[2] + " " + data_segments[3] + " " + data_segments[4] + //$NON-NLS-1$ //$NON-NLS-2$
                                "<br>5 24 " +  //$NON-NLS-1$
                                data_segments[8] + " " + data_segments[9] + " " + data_segments[10] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[2] + " " + data_segments[3] + " " + data_segments[4] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[5] + " " + data_segments[6] + " " + data_segments[7] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[11] + " " + data_segments[12] + " " + data_segments[13], text);  //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                text = QuickFixer.setLine(lineNumber + 1,
                        "3 " + data_segments[1] + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                                data_segments[5] + " " + data_segments[6] + " " + data_segments[7] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[8] + " " + data_segments[9] + " " + data_segments[10] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[11] + " " + data_segments[12] + " " + data_segments[13] + //$NON-NLS-1$ //$NON-NLS-2$
                                "<br>3 " + data_segments[1] + " " +  //$NON-NLS-1$ //$NON-NLS-2$
                                data_segments[11] + " " + data_segments[12] + " " + data_segments[13] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[2] + " " + data_segments[3] + " " + data_segments[4] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[5] + " " + data_segments[6] + " " + data_segments[7] + //$NON-NLS-1$ //$NON-NLS-2$
                                "<br>5 24 " +  //$NON-NLS-1$
                                data_segments[11] + " " + data_segments[12] + " " + data_segments[13] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[5] + " " + data_segments[6] + " " + data_segments[7] + " " +  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[2] + " " + data_segments[3] + " " + data_segments[4] + " " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                data_segments[8] + " " + data_segments[9] + " " + data_segments[10], text);  //$NON-NLS-1$ //$NON-NLS-2$
            }

        }
        break;
        case 2: // Flat subfile scaled on X
        {
            String[] data_segments = line.trim().split(" "); //$NON-NLS-1$
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String seg : data_segments) {
                if (!seg.trim().equals("")) {  //$NON-NLS-1$
                    i++;
                    switch (i) {
                    case 6:
                        sb.append("1"); //$NON-NLS-1$
                        break;
                    case 9:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    case 12:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    default:
                        sb.append(seg);
                        break;
                    }
                }
                sb.append(" "); //$NON-NLS-1$
            }
            text = QuickFixer.setLine(lineNumber + 1, sb.toString().trim(), text);
        }
        break;
        case 3: // Flat subfile scaled on Y
        {
            String[] data_segments = line.trim().split(" "); //$NON-NLS-1$
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String seg : data_segments) {
                if (!seg.trim().equals("")) {  //$NON-NLS-1$
                    i++;
                    switch (i) {
                    case 7:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    case 10:
                        sb.append("1"); //$NON-NLS-1$
                        break;
                    case 13:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    default:
                        sb.append(seg);
                        break;
                    }
                }
                sb.append(" "); //$NON-NLS-1$
            }
            text = QuickFixer.setLine(lineNumber + 1, sb.toString().trim(), text);
        }
        break;
        case 4: // Flat subfile scaled on Z
        {
            String[] data_segments = line.trim().split(" "); //$NON-NLS-1$
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (String seg : data_segments) {
                if (!seg.trim().equals("")) {  //$NON-NLS-1$
                    i++;
                    switch (i) {
                    case 8:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    case 11:
                        sb.append("0"); //$NON-NLS-1$
                        break;
                    case 14:
                        sb.append("1"); //$NON-NLS-1$
                        break;
                    default:
                        sb.append(seg);
                        break;
                    }
                }
                sb.append(" "); //$NON-NLS-1$
            }
            text = QuickFixer.setLine(lineNumber + 1, sb.toString().trim(), text);
        }
        break;
        default:
            break;
        }
        return text;
    }

}