package net.sf.latexdraw.parsers.pst.parser

import net.sf.latexdraw.models.ShapeFactory
import net.sf.latexdraw.models.interfaces.shape.IDot
import net.sf.latexdraw.models.interfaces.shape.IPoint
import net.sf.latexdraw.models.interfaces.shape.IShape

/**
 * A parser grouping parsers parsing dots.<br>
 *<br>
 * This file is part of LaTeXDraw<br>
 * Copyright (c) 2005-2014 Arnaud BLOUIN<br>
 *<br>
 *  LaTeXDraw is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.<br>
 *<br>
 *  LaTeXDraw is distributed without any warranty; without even the
 *  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the GNU General Public License for more details.<br>
 *<br>
 * 2012-05-02<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 */
trait PSDotParser extends PSTAbstractParser with PSTParamParser with PSTCoordinateParser with PSTValueParser {
	/**
	 * Parses psdot commands.
	 */
	def parsePsdot(ctx : PSTContext) : Parser[List[IShape]] =
		("\\psdot*" | "\\psdot") ~ opt(parseParam(ctx)) ~ opt(parseCoord(ctx)) ^^ { case cmdName ~ _ ~ posRaw =>


		val pos = posRaw match {
			case Some(value) => transformPointTo2DScene(value, ctx)
			case None => transformPointTo2DScene(ctx.origin, ctx)
		}

		checkTextParsed(ctx) ::: List(createDot(pos, cmdName.endsWith("*"), ctx))
	}



	/**
	 * Parses psdots commands.
	 */
	def parsePsdots(ctx : PSTContext) : Parser[List[IShape]] =
		("\\psdots*" | "\\psdots") ~ opt(parseParam(ctx)) ~ rep1(parseCoord(ctx)) ^^ { case cmdName ~ _ ~ ptList =>

		val hasStar = cmdName.endsWith("*")
		checkTextParsed(ctx) ::: ptList.map{pt => createDot(transformPointTo2DScene(pt, ctx), hasStar, ctx)}
	}



	private def createDot(pos : IPoint, hasStar : Boolean, ctx : PSTContext) : IDot = {
		val dot = ShapeFactory.createDot(pos)
		val dotSizeDim = if(ctx.arrowDotSize._1+ctx.arrowDotSize._2<0) scala.math.abs(ctx.arrowDotSize._1) else ctx.arrowDotSize._1
		val dotSizeNum = if(ctx.arrowDotSize._1+ctx.arrowDotSize._2<0) scala.math.abs(ctx.arrowDotSize._2) else ctx.arrowDotSize._2

		dot.setDiametre((dotSizeDim+dotSizeNum*ctx.lineWidth)*IShape.PPC*ctx.dotScale._1)
		setShapeParameters(dot, ctx)
		dot.setRotationAngle(dot.getRotationAngle+scala.math.toRadians(ctx.dotAngle))
		dot.setDotStyle(ctx.dotStyle)
		if(hasStar)
			setShapeForStar(dot)
		dot
	}
}
