/*
  * This file is part of LaTeXDraw.
  * Copyright (c) 2005-2014 Arnaud BLOUIN
  * LaTeXDraw is free software; you can redistribute it and/or modify it under
  * the terms of the GNU General Public License as published by the Free Software
  * Foundation; either version 2 of the License, or (at your option) any later version.
  * LaTeXDraw is distributed without any warranty; without even the implied
  * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * General Public License for more details.
 */
package net.sf.latexdraw.actions.shape;

import net.sf.latexdraw.actions.DrawingAction;
import net.sf.latexdraw.actions.Modifying;
import net.sf.latexdraw.actions.ShapeActionImpl;
import net.sf.latexdraw.models.GLibUtilities;
import net.sf.latexdraw.models.interfaces.shape.IDrawing;
import net.sf.latexdraw.models.interfaces.shape.IGroup;
import net.sf.latexdraw.util.LNumber;
import net.sf.latexdraw.util.LangTool;
import org.malai.undo.Undoable;

import java.util.Optional;

/**
 * This action translates shapes.
 */
public class TranslateShapes extends ShapeActionImpl<IGroup> implements DrawingAction, Undoable, Modifying {
	/** The x vector translation. */
	double tx;

	/** The y vector translation. */
	double ty;

	/**
	 * The x vector translation that has been already performed. This attribute is needed since
	 * this action can be executed several times.
	 */
	double performedTx;

	/**
	 * The y vector translation that has been already performed. This attribute is needed since
	 * this action can be executed several times.
	 */
	double performedTy;

	/** The drawing that will be handled by the action. */
	protected Optional<IDrawing> drawing;


	public TranslateShapes() {
		super();
		drawing = Optional.empty();
	}

	@Override
	public boolean isRegisterable() {
		return hadEffect();
	}

	@Override
	public boolean hadEffect() {
		return !LNumber.equalsDouble(performedTx, 0.0) || !LNumber.equalsDouble(performedTy, 0.0);
	}

	@Override
	protected void doActionBody() {
		shape.ifPresent(sh -> drawing.ifPresent(dr -> {
			if(!LNumber.equalsDouble(tx - performedTx, 0.0) || !LNumber.equalsDouble(ty - performedTy, 0.0)) {
				sh.translate(tx - performedTx, ty - performedTy);
				sh.setModified(true);
				dr.setModified(true);
				performedTx = tx;
				performedTy = ty;
			}
		}));
	}

	@Override
	public boolean canDo() {
		return super.canDo() && drawing.isPresent() && !shape.get().isEmpty() && GLibUtilities.isValidPoint(tx, ty);
	}

	@Override
	public void undo() {
		shape.ifPresent(sh -> drawing.ifPresent(dr -> {
			sh.translate(-tx, -ty);
			sh.setModified(true);
			dr.setModified(true);
		}));
	}

	@Override
	public void redo() {
		shape.ifPresent(sh -> drawing.ifPresent(dr -> {
			sh.translate(tx, ty);
			sh.setModified(true);
			dr.setModified(true);
		}));
	}

	@Override
	public String getUndoName() {
		return LangTool.INSTANCE.getBundle().getString("Actions.32");
	}

	/**
	 * @param theTx The x vector translation.
	 */
	public void setTx(final double theTx) {
		tx = theTx;
	}

	/**
	 * @param theTy The y vector translation.
	 */
	public void setTy(final double theTy) {
		ty = theTy;
	}

	@Override
	public void setDrawing(final IDrawing dr) {
		drawing = Optional.ofNullable(dr);
	}

	@Override
	public Optional<IDrawing> getDrawing() {
		return drawing;
	}
}
