/* * Project: DD_jfx * @(#)DownBondAction.java * * Copyright (c) 1997- 2015 * Actelion Pharmaceuticals Ltd. * Gewerbestrasse 16 * CH-4123 Allschwil, Switzerland * * All Rights Reserved. * * This software is the proprietary information of Actelion Pharmaceuticals, Ltd. * Use is subject to license terms. * * Author: Christian Rufener */package com.actelion.research.share.gui.editor.actions;import com.actelion.research.chem.Molecule;import com.actelion.research.chem.StereoMolecule;import com.actelion.research.share.gui.editor.Model;/** * Project: * User: rufenec * Date: 1/28/13 * Time: 1:51 PM */public class DownBondAction extends NewBondAction{    public DownBondAction(Model model)    {        super(model);    }    public int getBondType()    {        return Molecule.cBondTypeDown;    }    public void onChangeBond(int bond)     {       StereoMolecule mol = model.getMolecule();//.getSelectedMolecule();       if (mol != null) {           mol.changeBond(bond,Molecule.cBondTypeDown);           mol.ensureHelperArrays(Molecule.cHelperNeighbours);       }     }}