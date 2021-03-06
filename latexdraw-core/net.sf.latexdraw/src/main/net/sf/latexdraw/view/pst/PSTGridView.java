/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2015 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.view.pst;

import net.sf.latexdraw.models.GLibUtilities;
import net.sf.latexdraw.models.interfaces.shape.Color;
import net.sf.latexdraw.models.interfaces.shape.IGrid;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.util.LNumber;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Defines a PSTricks view of the LGrid model.
 */
class PSTGridView extends PSTShapeView<IGrid> {
	/**
	 * Creates and initialises a LGrid PSTricks view.
	 * @param model The model to view.
	 * @throws IllegalArgumentException If the given model is not valid.
	 * @since 3.0
	 */
	protected PSTGridView(final @NonNull IGrid model) {
		super(model);
	}


	/**
	 * Returns the PST code of the parameters of the grid.
	 */
	private StringBuilder getParamsCode(final float ppc, final double unit) {
		final Color gridLabelsColor = shape.getGridLabelsColour();
		final Color subGridColor = shape.getSubGridColour();
		final Color linesColor = shape.getLineColour();
		final StringBuilder params = new StringBuilder();

		params.append("gridwidth=").append(LNumber.getCutNumberFloat(shape.getGridWidth() / ppc)); //$NON-NLS-1$
		params.append(", subgridwidth=").append(LNumber.getCutNumberFloat(shape.getSubGridWidth() / ppc)); //$NON-NLS-1$
		params.append(", gridlabels=").append(LNumber.getCutNumber(shape.getLabelsSize() * 0.6f)).append("pt"); //$NON-NLS-1$ //$NON-NLS-2$

		if(shape.getSubGridDiv() != PSTricksConstants.DEFAULT_SUBGRIDDIV)
			params.append(", subgriddiv=").append(shape.getSubGridDiv()); //$NON-NLS-1$

		if(shape.getGridDots() != PSTricksConstants.DEFAULT_GRIDDOTS)
			params.append(", griddots=").append(shape.getGridDots()); //$NON-NLS-1$

		if(shape.getSubGridDots() != PSTricksConstants.DEFAULT_SUBGRIDDOTS)
			params.append(", subgriddots=").append(shape.getSubGridDots()); //$NON-NLS-1$

		if(!gridLabelsColor.equals(PSTricksConstants.DEFAULT_LABELGRIDCOLOR))
			params.append(", gridlabelcolor=").append(getColourName(gridLabelsColor)); //$NON-NLS-1$

		if(!LNumber.equalsDouble(unit, PSTricksConstants.DEFAULT_UNIT))
			params.append(", unit=").append(LNumber.getCutNumberFloat(unit)).append(PSTricksConstants.TOKEN_CM); //$NON-NLS-1$

		if(!linesColor.equals(PSTricksConstants.DEFAULT_GRIDCOLOR))
			params.append(", gridcolor=").append(getColourName(linesColor)); //$NON-NLS-1$

		params.append(", subgridcolor=").append(getColourName(subGridColor)); //$NON-NLS-1$

		return params;
	}


	@Override
	public String getCode(final IPoint pt, final float ppc) {
		if(!GLibUtilities.isValidPoint(pt) || ppc < 1) return "";

		final int startX;
		final int startY;
		final int endX;
		final int endY;
		final boolean isXLabelSouth = shape.isXLabelSouth();
		final boolean isYLabelWest = shape.isYLabelWest();
		final IPoint position = shape.getPosition();
		final StringBuilder start = new StringBuilder();
		final StringBuilder end = new StringBuilder();
		final StringBuilder rot = getRotationHeaderCode(ppc, position);
		final StringBuilder coord = new StringBuilder();
		final double unit = shape.getUnit();
		final double gridStartx = shape.getGridStartX();
		final double gridStarty = shape.getGridStartY();
		final double gridEndx = shape.getGridEndX();
		final double gridEndy = shape.getGridEndY();
		final StringBuilder code = new StringBuilder();

		if(isXLabelSouth) {
			startY = (int) gridStarty;
			endY = (int) gridEndy;
		}else {
			startY = (int) gridEndy;
			endY = (int) gridStarty;
		}

		if(isYLabelWest) {
			startX = (int) gridStartx;
			endX = (int) gridEndx;
		}else {
			startX = (int) gridEndx;
			endX = (int) gridStartx;
		}

		coord.append('(').append((int) shape.getOriginX()).append(',').append((int) shape.getOriginY()).append(')');
		coord.append('(').append(startX).append(',').append(startY).append(')');
		coord.append('(').append(endX).append(',').append(endY).append(')');

		if(!LNumber.equalsDouble(unit, PSTricksConstants.DEFAULT_UNIT))
			end.append("\n\\psset{unit=").append(PSTricksConstants.DEFAULT_UNIT).append(PSTricksConstants.TOKEN_CM).append('}');//$NON-NLS-1$

		if(!LNumber.equalsDouble(position.getX(), 0.0) || !LNumber.equalsDouble(position.getY(), 0.0)) {
			final float posX = LNumber.getCutNumberFloat((position.getX() - pt.getX()) / ppc);
			final float posY = LNumber.getCutNumberFloat((pt.getY() - position.getY()) / ppc);

			end.append('}');
			start.append("\\rput(").append(posX).append(',').append(posY).append(')').append('{');//$NON-NLS-1$
		}

		if(rot != null) {
			start.append(rot);
			end.insert(0, '}');
		}

		code.append(start);
		code.append("\\psgrid[");//$NON-NLS-1$
		code.append(getParamsCode(ppc, unit));
		code.append(']');
		code.append(coord);
		code.append(end);

		return code.toString();
	}
}
