/*

Copyright (c) 2015-2016, cheminfo

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of {{ project }} nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.actelion.research.share.gui.editor.actions;

import com.actelion.research.chem.CoordinateInventor;
import com.actelion.research.chem.Molecule;
import com.actelion.research.chem.NamedSubstituents;
import com.actelion.research.chem.StereoMolecule;
import com.actelion.research.share.gui.DialogResult;
import com.actelion.research.share.gui.editor.Model;
import com.actelion.research.share.gui.editor.dialogs.IAtomQueryFeaturesDialog;
import com.actelion.research.share.gui.editor.geom.IDrawContext;
import com.actelion.research.share.gui.editor.io.IKeyEvent;
import com.actelion.research.share.gui.editor.io.IMouseEvent;

import java.awt.geom.Point2D;


/**
 * Project:
 * User: rufenec
 * Date: 2/1/13
 * Time: 4:13 PM
 */
public abstract class AtomHighlightAction extends DrawAction
{

    AtomHighlightAction(Model model)
    {
        super(model);
    }

    protected java.awt.geom.Point2D lastHightlightPoint = null;

    boolean trackHighLight(java.awt.geom.Point2D pt)
    {

        StereoMolecule mol = model.getSelectedMolecule();// .getFragment(pt, false);
        int currentAtom = model.getSelectedAtom();
        int atom = findAtom(mol,pt) ;//getAtomAt(pt);
        // Update at least when current selected atom and new selected atom differ
        boolean ok = atom != -1;//currentAtom;
        lastHightlightPoint = pt;
        setHighlightAtom(mol, -1);
        String keyStrokes = model.getKeyStrokeBuffer().toString();
        if (currentAtom != -1 && atom != currentAtom) {
            if (keyStrokes.length() > 0) {
                StereoMolecule currentMol = model.getSelectedMolecule();
                expandAtomKeyStrokes(currentMol, currentAtom, keyStrokes);
                model.getKeyStrokeBuffer().setLength(0);
            }
        }
        if (mol != null) {
            setHighlightAtom(mol, atom);
        } else {
            if (model.getSelectedAtom() != -1) {
                model.getKeyStrokeBuffer().setLength(0);
                ok = true;
            }
        }
        return ok;
    }

    void setHighlightAtom(StereoMolecule mol, int atom)
    {
        if (mol != null) {
            model.setSelectedMolecule(mol);
        }
        model.setSelectedAtom(atom);
    }


    @Override
    public boolean onMouseDown(IMouseEvent evt)
    {
        return false;
    }


    @Override
    public boolean onMouseMove(IMouseEvent evt, boolean drag)
    {
        if (!drag) {
            java.awt.geom.Point2D pt = new Point2D.Double(evt.getX(), evt.getY());
            return trackHighLight(pt);
        } else {
            return false;
        }

    }

    @Override
    public boolean paint(IDrawContext _ctx)
    {
        boolean ok = false;
        int theAtom = model.getSelectedAtom();
        StereoMolecule mol = model.getSelectedMolecule();
        if (mol != null) {
            if (theAtom != -1) {
                drawAtomHighlight(_ctx, mol, theAtom);
                ok = true;
            }
        }
        return ok;
    }

    @Override
    public boolean onKeyPressed(IKeyEvent evt)
    {
        if (evt.getCode().equals(builder.getDeleteKey())) {
            int theAtom = model.getSelectedAtom();
            StereoMolecule mol = model.getSelectedMolecule();
//            System.out.println("Delete Atom " + theAtom);
            if (theAtom != -1) {
                mol.deleteAtom(theAtom);
                setHighlightAtom(mol, -1);
                return true;
            } else {
                boolean update = false;
                StereoMolecule m = model.getMolecule();
                //for (StereoMolecule m : model.getMols())
                {
                    if (m.deleteSelectedAtoms()) {
                        update = true;
                    }
                }
                return update;
            }
        } else if (handleCharacter(evt)) {
            return true;
        }
        return false;
    }

    private boolean handleCharacter(IKeyEvent evt)
    {
        int theAtom = model.getSelectedAtom();
        StereoMolecule mol = model.getSelectedMolecule();
        StringBuilder keyStrokeBuffer = model.getKeyStrokeBuffer();
        if (mol == null) {
            return false;
        }
        int newRadical;
        String code = evt.getText();
        if (theAtom != -1 /*&& !evt.isShiftDown()*/) {
            char c = code != null && code.length() > 0 ? code.charAt(0) : 0;
            boolean isFirst = (keyStrokeBuffer.length() == 0);
//                System.out.printf("KeyStroke is first: %s\n", isFirst);
            if (isFirst) {
                switch (c) {
                    case '+':
                    case '-':
                        return mol.changeAtomCharge(theAtom, c == '+');

                    case '.':
                        newRadical = (mol.getAtomRadical(theAtom) == Molecule.cAtomRadicalStateD) ? 0 : Molecule.cAtomRadicalStateD;
                        mol.setAtomRadical(theAtom, newRadical);
                        return true;
                    case ':':
                        newRadical = (mol.getAtomRadical(theAtom) == Molecule.cAtomRadicalStateT) ? Molecule.cAtomRadicalStateS
                            : (mol.getAtomRadical(theAtom) == Molecule.cAtomRadicalStateS) ? 0 : Molecule.cAtomRadicalStateT;
                        mol.setAtomRadical(theAtom, newRadical);
                        return true;

                    case 'q':
                        return mol.isFragment() ? showAtomQFDialog(theAtom) : false;
                    case '?':
                        return mol.changeAtom(theAtom, 0, 0, -1, 0);

                    default:
                        break;
                }
            }
            return handleCharacter(mol, theAtom, evt);
        } else {
            return handleCharsNonSelected(code);
        }
    }

    private boolean handleCharsNonSelected(String code)
    {
        if ("h".equals(code)) {
            model.flip(true);
            return true;
        } else if ("v".equals(code)) {
            model.flip(false);
            return true;
        }
        return false;
    }

    private void expandAtomKeyStrokes(StereoMolecule mol, int highliteAtom, String keyStrokes)
    {

        int atomicNo = Molecule.getAtomicNoFromLabel(keyStrokes);
        if (atomicNo != 0) {
            if (mol.changeAtom(highliteAtom, atomicNo, 0, -1, 0)) {
                return;
            }
        }

        StereoMolecule substituent = NamedSubstituents.getSubstituent(keyStrokes);
        if (substituent != null) {

            // Copy the the fragment containing the attachment point into a new molecule.
            // Then attach the substituent, create new atom coordinates for the substituent,
            // while retaining coordinates of the fragment.
            StereoMolecule fragment = new StereoMolecule();
            fragment.addFragment(mol, highliteAtom, null);
            float sourceAVBL = fragment.getAverageBondLength();
            int firstAtomInFragment = fragment.getAllAtoms();
            for (int atom = 0; atom < fragment.getAllAtoms(); atom++) {
                fragment.setAtomMarker(atom, true);
            }
            fragment.addSubstituent(substituent, 0);
            new CoordinateInventor(CoordinateInventor.MODE_KEEP_MARKED_ATOM_COORDS).invent(fragment);

            float dx = mol.getAtomX(highliteAtom) - sourceAVBL * fragment.getAtomX(0);
            float dy = mol.getAtomY(highliteAtom) - sourceAVBL * fragment.getAtomY(0);

            // Attach the substituent to the complete molecule and take coodinates from the
            // previously created fragment-substituent species.
            int firstAtomInMol = mol.getAllAtoms();
            mol.addSubstituent(substituent, highliteAtom);
            int substituentAtoms = mol.getAllAtoms() - firstAtomInMol;
            for (int i = 0; i < substituentAtoms; i++) {
                mol.setAtomX(firstAtomInMol + i, sourceAVBL * fragment.getAtomX(firstAtomInFragment + i) + dx);
                mol.setAtomY(firstAtomInMol + i, sourceAVBL * fragment.getAtomY(firstAtomInFragment + i) + dy);
            }
            mol.setStereoBondsFromParity();
        }
    }

    private boolean handleCharacter(StereoMolecule mol, int theAtom, IKeyEvent evt)
    {
        StringBuilder keyStrokeBuffer = model.getKeyStrokeBuffer();
        boolean isFirst = (keyStrokeBuffer.length() == 0);
        String code = evt.getText();
        char c = code != null && code.length() > 0 ? code.charAt(0) : 0;
        if (evt.getCode().equals(builder.getDeleteKey())) {
            if (theAtom != -1) {
                mol.deleteAtom(theAtom);
                setHighlightAtom(mol, -1);
                return true;
            } else {
                if (mol.deleteSelectedAtoms()) {
                    return true;
                }
            }
        } else if (!isFirst && evt.getCode().equals(builder.getEscapeKey())) {
            keyStrokeBuffer.setLength(0);
            return true;
        } else if (!isFirst && evt.getCode().equals(builder.getBackSpaceKey())) {
//            System.out.println("BackSpace!");
            keyStrokeBuffer.setLength(keyStrokeBuffer.length() - 1);
            return true;
        } else if (evt.getCode().equals(builder.getEnterKey())) {
            expandAtomKeyStrokes(mol, theAtom, keyStrokeBuffer.toString());
            keyStrokeBuffer.setLength(0);
            return true;
        } else if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c >= 48 && c <= 57) || (c == '-')) {
            keyStrokeBuffer.append(c);
//            System.out.printf("KeyStroke buffer is first: %s\n", keyStrokeBuffer);
            return true;
        }
        return false;
    }


    private boolean showAtomQFDialog(int atom)
    {
        StereoMolecule mol = model.getSelectedMolecule();
        if (mol != null) {
            IAtomQueryFeaturesDialog dlg = builder.createAtomQueryFeatureDialog(/*new AtomQueryFeaturesDialog*/mol, atom);
            return dlg.doModalAt(lastHightlightPoint.getX(),lastHightlightPoint.getY()) == DialogResult.IDOK;
        }
        return false;
    }

    public int findAtom(StereoMolecule mol,Point2D pt) {
        int foundAtom = -1;
        float pickx = (float)pt.getX();
        float picky = (float)pt.getY();
        float avbl = mol.getAverageBondLength();
        float foundDistanceSquare = Float.MAX_VALUE;
        float maxDistanceSquare = avbl * avbl / (avbl/3) ;
        int mAllAtoms = mol.getAllAtoms();
        for (int atom=0; atom<mAllAtoms; atom++) {
            float x = mol.getAtomX(atom);
            float y = mol.getAtomY(atom);
            float distanceSquare = (pickx-x) * (pickx-x) + (picky-y) * (picky-y);
            if (distanceSquare < maxDistanceSquare && distanceSquare < foundDistanceSquare) {
                foundDistanceSquare = distanceSquare;
                foundAtom = atom;
            }
        }
//        System.out.printf("Atom %d\n",foundAtom);
        return foundAtom;
    }


}
