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
package org.nschmidt.ldparteditor.dialogs.rectifier;

import java.math.BigDecimal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.nschmidt.ldparteditor.widgets.NButton;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nschmidt.ldparteditor.helpers.composite3d.RectifierSettings;
import org.nschmidt.ldparteditor.i18n.I18n;
import org.nschmidt.ldparteditor.widgets.BigDecimalSpinner;

/**
 * The rectifier dialog
 * <p>
 * Note: This class should not be instantiated, it defines the gui layout and no
 * business logic.
 *
 * @author nils
 *
 */
class RectifierDesign extends Dialog {

    final RectifierSettings rs;
    final BigDecimalSpinner[] spn_angle = new BigDecimalSpinner[1];
    final Combo[] cmb_scope = new Combo[1];
    final NButton[] btn_verbose = new NButton[1];

    final Combo[] cmb_colourise = new Combo[1];
    final Combo[] cmb_noQuadConversation = new Combo[1];
    final Combo[] cmb_noRectConversationOnAdjacentCondlines = new Combo[1];
    final Combo[] cmb_noBorderedQuadToRectConversation = new Combo[1];

    // Use final only for subclass/listener references!

    RectifierDesign(Shell parentShell, RectifierSettings rs) {
        super(parentShell);
        this.rs = rs;
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite cmp_container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) cmp_container.getLayout();
        gridLayout.verticalSpacing = 10;
        gridLayout.horizontalSpacing = 10;

        Label lbl_specify = new Label(cmp_container, SWT.NONE);
        lbl_specify.setText(I18n.RECTIFIER_Title);

        Label lbl_separator = new Label(cmp_container, SWT.SEPARATOR | SWT.HORIZONTAL);
        lbl_separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        Label lbl_angle = new Label(cmp_container, SWT.NONE);
        lbl_angle.setText(I18n.RECTIFIER_MaxAngle);

        BigDecimalSpinner spn_angle = new BigDecimalSpinner(cmp_container, SWT.NONE);
        this.spn_angle [0] = spn_angle;
        spn_angle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        spn_angle.setMaximum(new BigDecimal(90));
        spn_angle.setMinimum(new BigDecimal(0));
        spn_angle.setValue(rs.getMaximumAngle());

        {
            Combo cmb_colourise = new Combo(cmp_container, SWT.READ_ONLY);
            this.cmb_colourise[0] = cmb_colourise;
            cmb_colourise.setItems(new String[] {I18n.RECTIFIER_Colour1, I18n.RECTIFIER_Colour2});
            cmb_colourise.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            cmb_colourise.setText(cmb_colourise.getItem(rs.isColourise() ? 1 : 0));
            cmb_colourise.select(rs.isColourise() ? 1 : 0);
        }
        {
            Combo cmb_noQuadConversation = new Combo(cmp_container, SWT.READ_ONLY);
            this.cmb_noQuadConversation[0] = cmb_noQuadConversation;
            cmb_noQuadConversation.setItems(new String[] {I18n.RECTIFIER_TriQuads1, I18n.RECTIFIER_TriQuads2});
            cmb_noQuadConversation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            cmb_noQuadConversation.setText(cmb_noQuadConversation.getItem(rs.isNoQuadConversation() ? 1 : 0));
            cmb_noQuadConversation.select(rs.isNoQuadConversation() ? 1 : 0);
        }
        {
            Combo cmb_noBorderedQuadToRectConversation = new Combo(cmp_container, SWT.READ_ONLY);
            this.cmb_noBorderedQuadToRectConversation[0] = cmb_noBorderedQuadToRectConversation;
            cmb_noBorderedQuadToRectConversation.setItems(new String[] {I18n.RECTIFIER_Rect1, I18n.RECTIFIER_Rect2});
            cmb_noBorderedQuadToRectConversation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            cmb_noBorderedQuadToRectConversation.setText(cmb_noBorderedQuadToRectConversation.getItem(rs.isNoBorderedQuadToRectConversation() ? 1 : 0));
            cmb_noBorderedQuadToRectConversation.select(rs.isNoBorderedQuadToRectConversation() ? 1 : 0);
        }
        {
            Combo cmb_noRectConversationOnAdjacentCondlines = new Combo(cmp_container, SWT.READ_ONLY);
            this.cmb_noRectConversationOnAdjacentCondlines[0] = cmb_noRectConversationOnAdjacentCondlines;
            cmb_noRectConversationOnAdjacentCondlines.setItems(new String[] {I18n.RECTIFIER_Rect3, I18n.RECTIFIER_Rect4});
            cmb_noRectConversationOnAdjacentCondlines.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
            cmb_noRectConversationOnAdjacentCondlines.setText(cmb_noRectConversationOnAdjacentCondlines.getItem(rs.isNoRectConversationOnAdjacentCondlines() ? 1 : 0));
            cmb_noRectConversationOnAdjacentCondlines.select(rs.isNoRectConversationOnAdjacentCondlines() ? 1 : 0);
        }
        Combo cmb_scope = new Combo(cmp_container, SWT.READ_ONLY);
        this.cmb_scope[0] = cmb_scope;
        cmb_scope.setItems(new String[] {I18n.RECTIFIER_ScopeFile, I18n.RECTIFIER_ScopeSelection});
        cmb_scope.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        cmb_scope.setText(cmb_scope.getItem(rs.getScope()));
        cmb_scope.select(rs.getScope());

        NButton btn_verbose = new NButton(cmp_container, SWT.CHECK);
        this.btn_verbose[0] = btn_verbose;
        btn_verbose.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btn_verbose.setText(I18n.RECTIFIER_Verbose);
        btn_verbose.setSelection(rs.isVerbose());

        cmp_container.pack();
        return cmp_container;
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, I18n.DIALOG_OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, I18n.DIALOG_Cancel, false);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return super.getInitialSize();
    }

}
