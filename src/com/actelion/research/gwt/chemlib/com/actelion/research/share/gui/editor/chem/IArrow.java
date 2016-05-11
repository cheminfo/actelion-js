/*
 * Project: DD_jfx
 * @(#)IArrow.java
 *
 * Copyright (c) 1997- 2015
 * Actelion Pharmaceuticals Ltd.
 * Gewerbestrasse 16
 * CH-4123 Allschwil, Switzerland
 *
 * All Rights Reserved.
 *
 * This software is the proprietary information of Actelion Pharmaceuticals, Ltd.
 * Use is subject to license terms.
 *
 * Author: Christian Rufener
 */

package com.actelion.research.share.gui.editor.chem;

/**
 * Project:
 * User: rufenec
 * Date: 11/24/2014
 * Time: 3:28 PM
 */
public interface IArrow extends IDrawingObject
{
    int getLength();

    void setCoordinates(float v, float v1, float v2, float v3);

    boolean isOnProductSide(float x, float y);
}
