package net.sf.latexdraw.models.interfaces.prop;

import net.sf.latexdraw.models.interfaces.shape.AxesStyle;
import net.sf.latexdraw.models.interfaces.shape.IPoint;
import net.sf.latexdraw.models.interfaces.shape.PlottingStyle;
import net.sf.latexdraw.models.interfaces.shape.TicksStyle;

/**
 * Groups axes' properties.
 * <br>
 * This file is part of LaTeXDraw<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 *<br>
 *  LaTeXDraw is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.<br>
 *<br>
 *  LaTeXDraw is distributed without any warranty; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.<br>
 *<br>
 */
public interface IAxesProp extends IStdGridProp {
	/**
	 * @return The X increment of the axes.
	 * @since 3.0
	 */
	double getIncrementX();

	/**
	 * @return The Y increment of the axes.
	 * @since 3.0
	 */
	double getIncrementY();

	/**
	 * @return The increments of the axes.
	 * @since 3.0
	 */
	IPoint getIncrement();

	/**
	 * @param increment the X increment to set.
	 */
	void setIncrementX(final double increment);

	/**
	 * @param increment the Y increment to set.
	 */
	void setIncrementY(final double increment);

	/**
	 * @param increment The axes' increment to set.
	 */
	void setIncrement(final IPoint increment);

	/**
	 * @return The distance between the labels of the axes.
	 */
	IPoint getDistLabels();

	/**
	 * @return the distLabels.x.
	 */
	double getDistLabelsX();

	/**
	 * @return the distLabels.y.
	 */
	double getDistLabelsY();

	/**
	 * @param distLabels The distance between the labels of the axes.
	 */
	void setDistLabels(final IPoint distLabels);

	/**
	 * @param distLabelsX the distLabels.x to set.
	 */
	void setDistLabelsX(final double distLabelsX);

	/**
	 * @param distLabelsY the distLabels.y to set.
	 */
	void setDistLabelsY(final double distLabelsY);

	/**
	 * @return the labelsDisplayed.
	 */
	PlottingStyle getLabelsDisplayed();

	/**
	 * @param labelsDisplayed the labelsDisplayed to set.
	 */
	void setLabelsDisplayed(final PlottingStyle labelsDisplayed);

	/**
	 * @return the showOrigin.
	 */
	boolean isShowOrigin();

	/**
	 * @param showOrigin the showOrigin to set.
	 */
	void setShowOrigin(final boolean showOrigin);

	/**
	 * @return the ticksDisplayed.
	 */
	PlottingStyle getTicksDisplayed();

	/**
	 * @param ticksDisplayed the ticksDisplayed to set.
	 */
	void setTicksDisplayed(final PlottingStyle ticksDisplayed);

	/**
	 * @return the ticksStyle.
	 */
	TicksStyle getTicksStyle();

	/**
	 * @param ticksStyle the ticksStyle to set.
	 */
	void setTicksStyle(final TicksStyle ticksStyle);

	/**
	 * @return the ticksSize.
	 */
	double getTicksSize();

	/**
	 * @param ticksSize the ticksSize to set.
	 */
	void setTicksSize(final double ticksSize);

	/**
	 * @return the axesStyle.
	 */
	AxesStyle getAxesStyle();

	/**
	 * @param axesStyle the axesStyle to set.
	 */
	void setAxesStyle(final AxesStyle axesStyle);
}
