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
package org.nschmidt.ldparteditor.shells.editortext;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.nschmidt.ldparteditor.composites.ToolItem;
import org.nschmidt.ldparteditor.composites.compositetab.CompositeTab;
import org.nschmidt.ldparteditor.composites.compositetab.CompositeTabFolder;
import org.nschmidt.ldparteditor.data.ColourChanger;
import org.nschmidt.ldparteditor.data.DatFile;
import org.nschmidt.ldparteditor.data.GColour;
import org.nschmidt.ldparteditor.dialogs.colour.ColourDialog;
import org.nschmidt.ldparteditor.enums.View;
import org.nschmidt.ldparteditor.helpers.math.MathHelper;
import org.nschmidt.ldparteditor.i18n.I18n;
import org.nschmidt.ldparteditor.logger.NLogger;
import org.nschmidt.ldparteditor.resources.ResourceManager;
import org.nschmidt.ldparteditor.shells.editor3d.Editor3DWindow;
import org.nschmidt.ldparteditor.workbench.WorkbenchManager;

import swing2swt.layout.BorderLayout;

/**
 * The text editor window
 * <p>
 * Note: This class should not be instantiated, it defines the gui layout and no
 * business logic.
 *
 * @author nils
 *
 */
class EditorTextDesign extends ApplicationWindow {

    final MenuManager[] mnu_File = new MenuManager[1];
    final Button[] btn_New = new Button[1];
    final Button[] btn_Open = new Button[1];
    final Button[] btn_Save = new Button[1];

    final Button[] btn_Palette = new Button[1];

    final Button[] btn_Cut = new Button[1];
    final Button[] btn_Copy = new Button[1];
    final Button[] btn_Paste = new Button[1];
    final Button[] btn_Delete = new Button[1];

    final Button[] btn_Undo = new Button[1];
    final Button[] btn_AddHistory = new Button[1];
    final Button[] btn_Redo = new Button[1];

    final Button[] btn_ShowErrors = new Button[1];
    final Button[] btn_FindAndReplace = new Button[1];
    final Button[] btn_Sort = new Button[1];
    final Button[] btn_SplitQuad = new Button[1];
    final Button[] btn_Unrectify = new Button[1];
    final Button[] btn_Beautify = new Button[1];
    final Button[] btn_Inline = new Button[1];
    final Button[] btn_InlineDeep = new Button[1];
    final Button[] btn_InlineLinked = new Button[1];
    final Button[] btn_CompileSubfile = new Button[1];
    final Button[] btn_RoundSelection = new Button[1];
    final Button[] btn_Texmap = new Button[1];
    final Button[] btn_Annotate = new Button[1];

    private Composite toolBar;
    /**
     * The reference to the underlying business logic (only for testing
     * purpose!)
     */
    EditorTextWindow editorTextWindow;
    CompositeTabFolder[] tabFolder = new CompositeTabFolder[1];

    EditorTextDesign() {
        super(null);
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();
    }

    /**
     * Create contents of the application window.
     *
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
        setStatus(I18n.EDITOR3D_ReadyStatus);
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new BorderLayout(0, 0));
        toolBar = new Composite(container, SWT.NONE);
        toolBar.setLayoutData(BorderLayout.NORTH);
        RowLayout rl_toolBar = new RowLayout(SWT.HORIZONTAL);
        rl_toolBar.center = true;
        toolBar.setLayout(rl_toolBar);
        ToolItem toolItem_NewOpenSave = new ToolItem(toolBar, SWT.NONE);
        {
            Button btn_New = new Button(toolItem_NewOpenSave, SWT.NONE);
            this.btn_New[0] = btn_New;
            btn_New.setToolTipText(I18n.EDITOR3D_NewDat);
            btn_New.setImage(ResourceManager.getImage("icon16_document-newdat.png")); //$NON-NLS-1$
        }
        {
            Button btn_Open = new Button(toolItem_NewOpenSave, SWT.NONE);
            this.btn_Open[0] = btn_Open;
            btn_Open.setToolTipText(I18n.EDITOR3D_OpenDat);
            btn_Open.setImage(ResourceManager.getImage("icon16_document-opendat.png")); //$NON-NLS-1$
        }
        {
            Button btn_Save = new Button(toolItem_NewOpenSave, SWT.NONE);
            this.btn_Save[0] = btn_Save;
            btn_Save.setToolTipText(I18n.EDITOR3D_Save);
            btn_Save.setImage(ResourceManager.getImage("icon16_document-save.png")); //$NON-NLS-1$
        }
        ToolItem toolItem_UndoRedo = new ToolItem(toolBar, SWT.NONE);
        {
            Button btn_Undo = new Button(toolItem_UndoRedo, SWT.NONE);
            this.btn_Undo[0] = btn_Undo;
            btn_Undo.setImage(ResourceManager.getImage("icon16_undo.png")); //$NON-NLS-1$
            btn_Undo.setToolTipText(I18n.EDITOR3D_Undo);
        }
        if (NLogger.DEBUG) {
            Button btn_Snapshot = new Button(toolItem_UndoRedo, SWT.NONE);
            this.btn_AddHistory[0] = btn_Snapshot;
            btn_Snapshot.setImage(ResourceManager.getImage("icon16_snapshot.png")); //$NON-NLS-1$
            btn_Snapshot.setToolTipText(I18n.EDITOR3D_Snapshot);
        }
        {
            Button btn_Redo = new Button(toolItem_UndoRedo, SWT.NONE);
            this.btn_Redo[0] = btn_Redo;
            btn_Redo.setImage(ResourceManager.getImage("icon16_redo.png")); //$NON-NLS-1$
            btn_Redo.setToolTipText(I18n.EDITOR3D_Redo);
        }
        ToolItem toolItem_CCPD = new ToolItem(toolBar, SWT.NONE);
        {
            Button btn_Cut = new Button(toolItem_CCPD, SWT.NONE);
            this.btn_Cut[0] = btn_Cut;
            btn_Cut.setImage(ResourceManager.getImage("icon16_edit-cut.png")); //$NON-NLS-1$
            btn_Cut.setToolTipText(I18n.COPYNPASTE_Cut);
        }
        {
            Button btn_Copy = new Button(toolItem_CCPD, SWT.NONE);
            this.btn_Copy[0] = btn_Copy;
            btn_Copy.setImage(ResourceManager.getImage("icon16_edit-copy.png")); //$NON-NLS-1$
            btn_Copy.setToolTipText(I18n.COPYNPASTE_Copy);
        }
        {
            Button btn_Paste = new Button(toolItem_CCPD, SWT.NONE);
            this.btn_Paste[0] = btn_Paste;
            btn_Paste.setImage(ResourceManager.getImage("icon16_edit-paste.png")); //$NON-NLS-1$
            btn_Paste.setToolTipText(I18n.COPYNPASTE_Paste);
        }
        {
            Button btn_Delete = new Button(toolItem_CCPD, SWT.NONE);
            this.btn_Delete[0] = btn_Delete;
            btn_Delete.setImage(ResourceManager.getImage("icon16_delete.png")); //$NON-NLS-1$
            btn_Delete.setToolTipText(I18n.COPYNPASTE_Delete);
        }
        ToolItem toolItem_Debug = new ToolItem(toolBar, SWT.NONE);
        {
            Button btn_ShowErrors = new Button(toolItem_Debug, SWT.NONE);
            this.btn_ShowErrors[0] = btn_ShowErrors;
            btn_ShowErrors.setImage(ResourceManager.getImage("icon16_error.png")); //$NON-NLS-1$
            btn_ShowErrors.setToolTipText(I18n.EDITORTEXT_ShowHideErrorTab);
        }
        {
            Button btn_FindAndReplace = new Button(toolItem_Debug, SWT.NONE);
            this.btn_FindAndReplace[0] = btn_FindAndReplace;
            btn_FindAndReplace.setImage(ResourceManager.getImage("icon16_findReplace.png")); //$NON-NLS-1$
            btn_FindAndReplace.setToolTipText("Find/Replace"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_Sort = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Sort[0] = btn_Sort;
            btn_Sort.setImage(ResourceManager.getImage("icon16_sort.png")); //$NON-NLS-1$
            btn_Sort.setToolTipText("Sort"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_SplitQuad = new Button(toolItem_Debug, SWT.NONE);
            this.btn_SplitQuad[0] = btn_SplitQuad;
            btn_SplitQuad.setImage(ResourceManager.getImage("icon16_quadToTri.png")); //$NON-NLS-1$
            btn_SplitQuad.setToolTipText("Split Quad into Triangles"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_Unrectify = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Unrectify[0] = btn_Unrectify;
            btn_Unrectify.setImage(ResourceManager.getImage("icon16_unrectify.png")); //$NON-NLS-1$
            btn_Unrectify.setToolTipText("'Unrectifier': Split all Quads and rect*.dat-Primitives into Triangles"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_Beautify = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Beautify[0] = btn_Beautify;
            btn_Beautify.setImage(ResourceManager.getImage("icon16_duplicate.png")); //$NON-NLS-1$
            btn_Beautify.setToolTipText("Remove Duplicates + Invisible TYPE 5 Lines"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_Inline = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Inline[0] = btn_Inline;
            btn_Inline.setImage(ResourceManager.getImage("icon16_inline.png")); //$NON-NLS-1$
            btn_Inline.setToolTipText("Inline selection (partial BFC conformity)"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_InlineDeep = new Button(toolItem_Debug, SWT.NONE);
            this.btn_InlineDeep[0] = btn_InlineDeep;
            btn_InlineDeep.setImage(ResourceManager.getImage("icon16_inlinedeep.png")); //$NON-NLS-1$
            btn_InlineDeep.setToolTipText("Inline selection recursively (partial BFC conformity,\nno comments,\nno whitespace.)"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_InlineLinked = new Button(toolItem_Debug, SWT.NONE);
            this.btn_InlineLinked[0] = btn_InlineLinked;
            btn_InlineLinked.setImage(ResourceManager.getImage("icon16_inlinelinked.png")); //$NON-NLS-1$
            btn_InlineLinked.setToolTipText("Inline selection (Linked, no BFC conformity)"); //$NON-NLS-1$ I18N Needs translation!
        }
        {
            Button btn_CompileSubfile = new Button(toolItem_Debug, SWT.NONE);
            this.btn_CompileSubfile[0] = btn_CompileSubfile;
            btn_CompileSubfile.setToolTipText("Compile linked Subfile Data"); //$NON-NLS-1$ I18N
            btn_CompileSubfile.setImage(ResourceManager.getImage("icon16_subcompile.png")); //$NON-NLS-1$
        }
        {
            Button btn_RoundSelection = new Button(toolItem_Debug, SWT.NONE);
            this.btn_RoundSelection[0] = btn_RoundSelection;
            btn_RoundSelection.setToolTipText("Round"); //$NON-NLS-1$ I18N
            btn_RoundSelection.setImage(ResourceManager.getImage("icon16_round.png")); //$NON-NLS-1$
        }

        {
            Button btn_Texmap = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Texmap[0] = btn_Texmap;
            btn_Texmap.setToolTipText("Toggle TEXMAP"); //$NON-NLS-1$ I18N
            btn_Texmap.setImage(ResourceManager.getImage("icon16_texmap.png")); //$NON-NLS-1$
        }

        {
            Button btn_Annotate = new Button(toolItem_Debug, SWT.NONE);
            this.btn_Annotate[0] = btn_Annotate;
            btn_Annotate.setToolTipText("Toggle Comment"); //$NON-NLS-1$ I18N
            btn_Annotate.setImage(ResourceManager.getImage("icon16_annotate.png")); //$NON-NLS-1$
        }

        {
            ToolItem toolItem_Colours = new ToolItem(toolBar, SWT.NONE);
            List<GColour> colours = WorkbenchManager.getUserSettingState().getUserPalette();
            addColorButton(toolItem_Colours, colours.get(0), 0);
            addColorButton(toolItem_Colours, colours.get(1), 1);
            addColorButton(toolItem_Colours, colours.get(2), 2);
            addColorButton(toolItem_Colours, colours.get(3), 3);
            addColorButton(toolItem_Colours, colours.get(4), 4);
            addColorButton(toolItem_Colours, colours.get(5), 5);
            addColorButton(toolItem_Colours, colours.get(6), 6);
            addColorButton(toolItem_Colours, colours.get(7), 7);
            addColorButton(toolItem_Colours, colours.get(8), 8);
            addColorButton(toolItem_Colours, colours.get(9), 9);
            addColorButton(toolItem_Colours, colours.get(10), 10);
            addColorButton(toolItem_Colours, colours.get(11), 11);
            addColorButton(toolItem_Colours, colours.get(12), 12);
            addColorButton(toolItem_Colours, colours.get(13), 13);
            addColorButton(toolItem_Colours, colours.get(14), 14);
            addColorButton(toolItem_Colours, colours.get(15), 15);
            addColorButton(toolItem_Colours, colours.get(16), 16);
            {
                Button btn_Palette = new Button(toolItem_Colours, SWT.NONE);
                this.btn_Palette[0] = btn_Palette;
                btn_Palette.setToolTipText("More…"); //$NON-NLS-1$ I18N Needs translation!
                btn_Palette.setImage(ResourceManager.getImage("icon16_colours.png")); //$NON-NLS-1$
            }
        }

        {
            Composite cmp_text_editor = new Composite(container, SWT.BORDER);
            cmp_text_editor.setLayoutData(BorderLayout.CENTER);
            cmp_text_editor.setLayout(new FillLayout(SWT.HORIZONTAL));
            {
                CompositeTabFolder tabFolder = new CompositeTabFolder(cmp_text_editor, SWT.BORDER);
                this.tabFolder[0] = tabFolder;
                tabFolder.setMRUVisible(true);
                tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
                tabFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            }
        }
        return container;
    }

    /**
     * Create the menu manager.
     *
     * @return the menu manager
     */
    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = new MenuManager("menu"); //$NON-NLS-1$
        menuManager.setVisible(true);
        {
            MenuManager mnu_File = new MenuManager(I18n.EDITOR3D_File);
            mnu_File.setVisible(true);
            menuManager.add(mnu_File);
            this.mnu_File[0] = mnu_File;
        }
        return menuManager;
    }

    /**
     * Create the status line manager.
     *
     * @return the status line manager
     */
    @Override
    protected StatusLineManager createStatusLineManager() {
        StatusLineManager status = new StatusLineManager();
        return status;
    }

    /**
     * Configure the shell.
     *
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(916, 578);
    }

    /**
     * Sets the reference to the business logic component of the text editor.
     *
     * @param editorTextWindow
     *            the window (business logic) of the text editor Note: I need
     *            this only for testing!
     */
    public void setEditorTextWindow(EditorTextWindow editorTextWindow) {
        this.editorTextWindow = editorTextWindow;
    }


    private void addColorButton(ToolItem toolItem_Colours, GColour gColour, final int index) {
        int cn = gColour.getColourNumber();
        if (cn != -1 && View.hasLDConfigColour(cn)) {
            gColour = View.getLDConfigColour(cn);
        }
        final int imgSize;
        switch (Editor3DWindow.getIconsize()) {
        case 0:
            imgSize = 16;
            break;
        case 1:
            imgSize = 24;
            break;
        case 2:
            imgSize = 32;
            break;
        case 3:
            imgSize = 48;
            break;
        case 4:
            imgSize = 64;
            break;
        case 5:
            imgSize = 72;
            break;
        default:
            imgSize = 16;
            break;
        }

        final GColour[] gColour2 = new GColour[] { gColour };
        final Color[] col = new Color[1];
        col[0] = SWTResourceManager.getColor((int) (gColour2[0].getR() * 255f), (int) (gColour2[0].getG() * 255f), (int) (gColour2[0].getB() * 255f));

        final Button btn_Col = new Button(toolItem_Colours, SWT.NONE);
        btn_Col.setData(gColour2);
        int num = gColour2[0].getColourNumber();
        if (!View.hasLDConfigColour(num)) {
            num = -1;
        }
        if (num != -1) {
            btn_Col.setToolTipText("Colour [" + num + "]: " + View.getLDConfigColourName(num)); //$NON-NLS-1$ //$NON-NLS-2$ I18N
        } else {
            StringBuilder colourBuilder = new StringBuilder();
            colourBuilder.append("0x2"); //$NON-NLS-1$
            colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getR())).toUpperCase());
            colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getG())).toUpperCase());
            colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getB())).toUpperCase());
            btn_Col.setToolTipText("Colour [" + colourBuilder.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ I18N
        }

        btn_Col.setImage(ResourceManager.getImage("icon16_transparent.png")); //$NON-NLS-1$

        btn_Col.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if ((e.stateMask & SWT.CTRL) == SWT.CTRL) {
                    // Choose new colour
                    new ColourDialog(getShell(), gColour2).open();
                    WorkbenchManager.getUserSettingState().getUserPalette().set(index, gColour2[0]);
                    col[0] = SWTResourceManager.getColor((int) (gColour2[0].getR() * 255f), (int) (gColour2[0].getG() * 255f), (int) (gColour2[0].getB() * 255f));
                    int num = gColour2[0].getColourNumber();
                    if (View.hasLDConfigColour(num)) {
                        gColour2[0] = View.getLDConfigColour(num);
                    } else {
                        num = -1;
                    }
                    if (num != -1) {
                        btn_Col.setToolTipText("Colour [" + num + "]: " + View.getLDConfigColourName(num)); //$NON-NLS-1$ //$NON-NLS-2$ I18N
                    } else {
                        StringBuilder colourBuilder = new StringBuilder();
                        colourBuilder.append("0x2"); //$NON-NLS-1$
                        colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getR())).toUpperCase());
                        colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getG())).toUpperCase());
                        colourBuilder.append(MathHelper.toHex((int) (255f * gColour2[0].getB())).toUpperCase());
                        btn_Col.setToolTipText("Colour [" + colourBuilder.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ I18N
                    }
                } else {
                    int num = gColour2[0].getColourNumber();
                    if (View.hasLDConfigColour(num)) {
                        gColour2[0] = View.getLDConfigColour(num);
                    } else {
                        num = -1;
                    }

                    CompositeTab selection = (CompositeTab) tabFolder[0].getSelection();
                    if (selection != null) {
                        DatFile df = selection.getState().getFileNameObj();
                        if (!df.isReadOnly()) {
                            NLogger.debug(getClass(), "Change colours..."); //$NON-NLS-1$
                            final StyledText st = selection.getTextComposite();
                            int s1 = st.getSelectionRange().x;
                            int s2 = s1 + st.getSelectionRange().y;
                            int fromLine = s1 > -1 ? st.getLineAtOffset(s1) : s1 * -1;
                            int toLine = s2 > -1 ? st.getLineAtOffset(s2) : s2 * -1;
                            fromLine++;
                            toLine++;
                            NLogger.debug(getClass(), "From line " + fromLine); //$NON-NLS-1$
                            NLogger.debug(getClass(), "To   line " + toLine); //$NON-NLS-1$
                            ColourChanger.changeColour(st, fromLine, toLine, df, num, gColour2[0].getR(), gColour2[0].getG(), gColour2[0].getB());
                            st.forceFocus();
                        }
                    }

                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        final Point size = btn_Col.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final int x = size.x / 4;
        final int y = size.y / 4;
        final int w = size.x / 2;
        final int h = size.y / 2;
        btn_Col.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.setBackground(col[0]);
                e.gc.fillRectangle(x, y, w, h);
                if (gColour2[0].getA() == 1f) {
                    e.gc.drawImage(ResourceManager.getImage("icon16_transparent.png"), 0, 0, imgSize, imgSize, x, y, w, h); //$NON-NLS-1$
                } else {
                    e.gc.drawImage(ResourceManager.getImage("icon16_halftrans.png"), 0, 0, imgSize, imgSize, x, y, w, h); //$NON-NLS-1$
                }
            }
        });
    }


}