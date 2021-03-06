/*
 * This file is part of LaTeXDraw<br>
 * Copyright (c) 2005-2015 Arnaud BLOUIN<br>
 * <br>
 * LaTeXDraw is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.<br>
 * <br>
 * LaTeXDraw is distributed without any warranty; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.<br>
 */
package net.sf.latexdraw.view.latex;

/**
 * The different vertical positions.
 */
public enum VerticalPosition {
	/** The LaTeX t position. */
	TOP {
		@Override
		public String getToken() {
			return "t"; //$NON-NLS-1$
		}
	},
	/** The LaTeX b position. */
	BOTTOM {
		@Override
		public String getToken() {
			return "b"; //$NON-NLS-1$
		}
	},
	/** The LaTeX f position. */
	FLOATS_PAGE {
		@Override
		public String getToken() {
			return "p"; //$NON-NLS-1$
		}
	},
	/** The LaTeX h position. */
	HERE {
		@Override
		public String getToken() {
			return "h"; //$NON-NLS-1$
		}
	},
	/** The LaTeX H position. */
	HERE_HERE {
		@Override
		public String getToken() {
			return "H"; //$NON-NLS-1$
		}
	},
	/** No position specified. */
	NONE {
		@Override
		public String getToken() {
			return ""; //$NON-NLS-1$
		}
	};

	@Override
	public String toString() {
		return getToken();
	}

	/**
	 * @return The token corresponding to the placement.
	 * @since 3.0
	 */
	public abstract String getToken();

	/**
	 * @param pos The position token to check.
	 * @return The corresponding vertical position.
	 * @since 3.0
	 */
	public static VerticalPosition getPosition(final String pos) {
		if(pos == null)
			return null;

		if(pos.equals(TOP.getToken()))
			return TOP;

		if(pos.equals(BOTTOM.getToken()))
			return BOTTOM;

		if(pos.equals(FLOATS_PAGE.getToken()))
			return FLOATS_PAGE;

		if(pos.equals(HERE.getToken()))
			return HERE;

		if(pos.equals(HERE_HERE.getToken()))
			return HERE_HERE;

		if(pos.equals(NONE.getToken()))
			return NONE;

		return null;
	}
}
