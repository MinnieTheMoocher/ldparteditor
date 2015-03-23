package org.nschmidt.ldparteditor.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.nschmidt.ldparteditor.logger.NLogger;
import org.nschmidt.ldparteditor.shells.editor3d.Editor3DWindow;
import org.nschmidt.ldparteditor.text.StringHelper;

// FIXME MOCKUP!!
public class HistoryManager {

    private boolean hasNoThread = true;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final AtomicInteger action = new AtomicInteger(0);

    private Queue<Object[]> workQueue = new ConcurrentLinkedQueue<Object[]>();

    public void pushHistory(String text, int selectionStart, int selectionEnd, GData[] data, boolean[] selectedData, Vertex[] selectedVertices) {
        if (hasNoThread) {
            hasNoThread = false;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    int pointer = 0;
                    int pointerMax = 0;

                    final ArrayList<Integer> historySelectionStart = new ArrayList<Integer>();
                    final ArrayList<Integer> historySelectionEnd = new ArrayList<Integer>();
                    final ArrayList<int[]> historyText = new ArrayList<int[]>();
                    final ArrayList<boolean[]> historySelectedData = new ArrayList<boolean[]>();
                    final ArrayList<Vertex[]> historySelectedVertices = new ArrayList<Vertex[]>();

                    while (isRunning.get() && Editor3DWindow.getAlive().get()) {
                        try {
                            Object[] newEntry = workQueue.poll();
                            if (newEntry != null) {
                                final int[] result;
                                String text = (String) newEntry[0];
                                GData[] data = (GData[]) newEntry[3];
                                if (text != null) {
                                    result = StringHelper.compress(text);
                                } else if (data != null) {
                                    StringBuilder sb = new StringBuilder();
                                    int size = data.length - 1;
                                    if (size > 0) {
                                        final String ld = StringHelper.getLineDelimiter();
                                        for (int i = 0; i < size; i++) {
                                            sb.append(data[i].toString());
                                            sb.append(ld);
                                        }
                                        sb.append(data[size].toString());
                                    }
                                    result = StringHelper.compress(sb.toString());
                                } else {
                                    throw new AssertionError("There must be data to backup!"); //$NON-NLS-1$
                                }
                                if (pointer != pointerMax) {
                                    // Delete old entries
                                    removeFromListAboveOrEqualIndex(historySelectionStart, pointer);
                                    removeFromListAboveOrEqualIndex(historySelectionEnd, pointer);
                                    removeFromListAboveOrEqualIndex(historySelectedData, pointer);
                                    removeFromListAboveOrEqualIndex(historySelectedVertices, pointer);
                                    removeFromListAboveOrEqualIndex(historyText, pointer);
                                    pointerMax = pointer;
                                }
                                // Dont store more than hundred undo/redo entries
                                if (pointerMax > 100) {
                                    int delta = pointerMax - 100;
                                    removeFromListLessIndex(historySelectionStart, delta);
                                    removeFromListLessIndex(historySelectionEnd, delta);
                                    removeFromListLessIndex(historySelectedData, delta);
                                    removeFromListLessIndex(historySelectedVertices, delta);
                                    removeFromListLessIndex(historyText, delta);
                                    pointerMax = pointerMax - delta;
                                    if (pointer > 100) {
                                        pointer = pointer - delta;
                                    }
                                }

                                historySelectionStart.add((Integer) newEntry[1]);
                                historySelectionEnd.add((Integer) newEntry[2]);
                                historySelectedData.add((boolean[]) newEntry[4]);
                                historySelectedVertices.add((Vertex[]) newEntry[5]);
                                historyText.add(result);

                                // Cleanup duplicated text entries
                                if (pointer > 0) {
                                    int[] previous;
                                    int k = 1;
                                    while ((previous = historyText.get(pointer - k)) == null) {
                                        if (k == pointer) break;
                                        k++;
                                    }
                                    if (previous != null) {
                                        boolean match = true;
                                        for (int i = 0; i < previous.length; i++) {
                                            int v1 = previous[i];
                                            int v2 = result[i];
                                            if (v1 != v2) {
                                                match = false;
                                                break;
                                            }
                                        }
                                        if (match) {
                                            historyText.remove(pointer);
                                            historyText.add(null);
                                        }
                                    }
                                }

                                pointerMax++;
                                pointer++;
                                NLogger.debug(getClass(), "Added undo/redo data"); //$NON-NLS-1$
                            } else {
                                final int action2 = action.get();
                                if (action2 > 0) {
                                    switch (action2) {
                                    case 1:
                                        // Undo
                                        NLogger.debug(getClass(), "Requested undo."); //$NON-NLS-1$
                                        if (pointer > 0) {
                                            pointer--;

                                        }
                                        break;
                                    case 2:
                                        // Redo
                                        NLogger.debug(getClass(), "Requested redo."); //$NON-NLS-1$
                                        if (pointer < pointerMax) {
                                            pointer++;

                                        }
                                        break;
                                    default:
                                        break;
                                    }
                                    action.set(0);
                                }
                            }
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                    }
                    // TODO Cleanup the data here
                }
            }).start();
        }

        while (!workQueue.offer(new Object[]{text, selectionStart, selectionEnd, data, selectedData, selectedVertices})) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }

    }

    public void deleteHistory() {
        isRunning.set(false);
    }

    public void undo() {
        action.set(1);
        while (action.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    public void redo() {
        action.set(2);
        while (action.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
    }

    private void removeFromListAboveOrEqualIndex(List<?> l, int i) {
        i--;
        for (int j = l.size() - 1; j > i; j--) {
            l.remove(j);
        }
    }

    private void removeFromListLessIndex(List<?> l, int i) {
        i--;
        for (int j = i - 1; j > -1; j--) {
            l.remove(j);
        }
    }
}
