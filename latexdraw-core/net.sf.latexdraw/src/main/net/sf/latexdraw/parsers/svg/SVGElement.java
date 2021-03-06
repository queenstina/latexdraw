package net.sf.latexdraw.parsers.svg;

import net.sf.latexdraw.models.interfaces.shape.Color;
import java.text.ParseException;
import java.util.Objects;

import net.sf.latexdraw.badaboom.BadaboomCollector;
import net.sf.latexdraw.parsers.svg.parsers.CSSStyleParser;
import net.sf.latexdraw.parsers.svg.parsers.SVGLengthParser;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Defines an SVG element.<br>
 *<br>
 * This file is part of LaTeXDraw.<br>
 * Copyright (c) 2005-2016 Arnaud BLOUIN<br>
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
 * 09/11/07<br>
 * @author Arnaud BLOUIN
 * @version 3.0
 */
public abstract class SVGElement implements Element, Cloneable {
	/** The attributes of the element. @since 0.1 */
	protected final SVGNamedNodeMap attributes;

	/** The children of this element. @since 0.1 */
	protected final SVGNodeList children;

	/** The parent SVGElement of this element. @since 0.1 */
	protected SVGElement parent;

	/** The name of the tag. @since 0.1 */
	protected String name;

	/** The list of transformations which concern the element. @since 0.1 */
	protected SVGTransformList transform;

	/** The list of the CSS styles of the SVG attribute style. @since 0.1 */
	protected CSSStyleList stylesCSS;

	/** The document containing the element. @since 0.1 */
	protected SVGDocument ownerDocument;



	/**
	 * The constructor by default.
	 * @since 0.1
	 */
    protected SVGElement() {
		super();
		children 	= new SVGNodeList();
		attributes 	= new SVGNamedNodeMap();
		transform	= null;
		stylesCSS	= null;
		parent 		= null;
		name 		= null;
		ownerDocument = null;
	}



	/**
	 * Creates an simple SVGElement with the owner document.
	 * @param owner The owner document.
	 * @since 0.1
	 */
    protected SVGElement(final SVGDocument owner) {
		this();
		ownerDocument = Objects.requireNonNull(owner);
	}



	/**
	 * The constructor using a node in order to be initialised.
	 * @param n The node.
	 * @throws MalformedSVGDocument If the element is not well formed.
	 * @since 0.1
	 */
    protected SVGElement(final Node n) throws MalformedSVGDocument {
		this(n, null);
	}



	/**
	 * The constructor using a node to create the SVG element and an SVG element to be its parent.
	 * @param n The node.
	 * @param p The parent SVG element.
	 * @throws MalformedSVGDocument If the element is not well formed.
	 * @since 0.1
	 */
    protected SVGElement(final Node n, final SVGElement p) throws MalformedSVGDocument {
		this();

		if(n==null)
			throw new IllegalArgumentException();

		if(p!=null) {
			ownerDocument = p.getOwnerDocument();
			setParent(p);
		}

		setAttributes(n);
		setNodeValue(n.getNodeValue());
		setNodeName(n.getNodeName());

		String v = getAttribute(getUsablePrefix()+SVGAttributes.SVG_TRANSFORM);

		if(v!=null) {
			transform = new SVGTransformList();
			transform.addTransformations(getAttribute(getUsablePrefix()+SVGAttributes.SVG_TRANSFORM));
		}

		v = getAttribute(getUsablePrefix()+SVGAttributes.SVG_STYLE);

		if(v!=null) {
			stylesCSS = new CSSStyleList();
			try { new CSSStyleParser(v, stylesCSS).parse(); }
			catch(final ParseException e) { BadaboomCollector.INSTANCE.add(e); }
		}

		if(!checkAttributes())
			throw new MalformedSVGDocument();

		final NodeList nl = n.getChildNodes();
		int i;
        final int size = nl.getLength();

        if(size==1 && ("#text".equals(nl.item(0).getNodeName()) || "#cdata-section".equals(nl.item(0).getNodeName())))//$NON-NLS-1$//$NON-NLS-2$
			setTextContent(n.getTextContent());

		for(i=0; i<size; i++)
			SVGElementsFactory.INSTANCE.createSVGElement(nl.item(i), this);
	}



	/**
	 * Copies the attributes of the given node.
	 * @param n The node to copy.
	 */
	protected void setAttributes(final Node n) {
		if(n==null || n.getAttributes()==null)
			return;

		final NamedNodeMap nnm = n.getAttributes();

		for(int i=0, size=nnm.getLength(); i<size; i++)
			attributes.getAttributes().add(new SVGAttr(nnm.item(i).getNodeName(), nnm.item(i).getNodeValue(), this));
	}



	/**
	 * Allows to get the root of the SVG document.
	 * @return The root.
	 * @since 0.1
	 */
	public SVGElement getRootElement() {
		if(parent==null)
			return this;

		return parent.getRootElement();
	}



	/**
	 * @return the parent.
	 * @since 0.1
	 */
	public SVGElement getParent() {
		return parent;
	}



	/**
	 * @param parent the parent to set.
	 * @since 0.1
	 */
	public void setParent(final SVGElement parent) {
		if(this.parent!=parent) {
			if(this.parent!=null)
				this.parent.children.getNodes().remove(this);

			this.parent = parent;

			if(this.parent!=null && !this.parent.children.getNodes().contains(this))
				this.parent.children.getNodes().add(this);
		}
	}



	@Override
	public NamedNodeMap getAttributes() {
		return attributes;
	}



	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder().append('[');
		int i;
        final int size = children.getLength();

        str.append("name=").append(name).append(',');//$NON-NLS-1$

		if(!hasChildNodes())
			str.append("textContent=").append(getTextContent()).append(',');//$NON-NLS-1$

		str.append("attributes=");//$NON-NLS-1$

		if(attributes!=null)
			str.append(attributes);

		str.append(", children={");//$NON-NLS-1$

		for(i=0; i<size-1; i++)
			str.append(children.item(i)).append(',');

		if(size>0)
			str.append(children.getNodes().get(children.getNodes().size()-1));

		str.append('}');

		return str.append(']').toString();
	}



	@Override
	public String getNodeName() {
		return name;
	}



	/**
	 * Sets the name of the SVG element.
	 * @param name Its new name.
	 */
	public void setNodeName(final String name) {
		this.name = name;
	}



	@Override
	public String getAttribute(final String nameAttr) {
		if(attributes==null || nameAttr==null)
			return null;

		final Node n = attributes.getNamedItem(nameAttr);

		return n==null ? null : n.getNodeValue();
	}



	@Override
	public Attr getAttributeNode(final String nameAttr) {
		if(attributes==null)
			return null;

		return (Attr)attributes.getNamedItem(nameAttr);
	}


	@Override
	public String getTagName() {
		return name;
	}



	@Override
	public Node appendChild(final Node newChild) {
		if(!(newChild instanceof SVGElement))
			throw new DOMException(DOMException.TYPE_MISMATCH_ERR, "SVGElement excepted here.");//$NON-NLS-1$

		if(children.getNodes().contains(newChild))
			children.getNodes().remove(newChild);

		children.getNodes().add((SVGElement)newChild);
		((SVGElement)newChild).setParent(this);

		return newChild;
	}



	@Override
	public NodeList getChildNodes() {
		return children;
	}



	@Override
	public Node getFirstChild() {
		return children.getNodes()==null || children.getNodes().isEmpty() ? null : children.getNodes().get(0);
	}



	@Override
	public Node getLastChild() {
		return children.getNodes()==null || children.getNodes().isEmpty() ? null : children.getNodes().get(children.getNodes().size()-1);
	}



	@Override
	public short getNodeType() {
		return Node.ELEMENT_NODE;
	}



	@Override
	public SVGDocument getOwnerDocument() {
		return ownerDocument;
	}



	@Override
	public Node getParentNode() {
		return parent;
	}



	@Override
	public boolean hasAttributes() {
		return attributes!=null && attributes.getAttributes()!=null && !attributes.getAttributes().isEmpty();
	}



	@Override
	public boolean hasChildNodes() {
		return children.getNodes()!=null && !children.getNodes().isEmpty();
	}



	@Override
	public Node insertBefore(final Node newChild, final Node refChild) {
		boolean ok = false;

		if(newChild!=null && refChild!=null) {
			final int pos = children.getNodes().indexOf(refChild);

			if(pos!=-1 && newChild instanceof SVGElement) {
				children.getNodes().add(pos, (SVGElement)newChild);
				ok = true;
			}
		}

		return ok ? newChild : null;
	}




	@Override
	public boolean isEqualNode(final Node node) {
		if(!(node instanceof SVGElement))
			return false;

		final SVGElement elt = (SVGElement)node;
		final String uri = lookupNamespaceURI(null);
		final String val = getNodeValue();
		final boolean valEq = val==null ? elt.getNodeValue()==null : val.equals(elt.getNodeValue());
		final boolean uriEq = uri==null ? elt.lookupNamespaceURI(null)==null : uri.equals(elt.lookupNamespaceURI(null));
		final boolean attrEq = attributes==null ? elt.attributes==null : attributes.equals(elt.attributes);

		return name.equals(elt.name) && getUsablePrefix().equals(elt.getUsablePrefix()) && uriEq && valEq && attrEq;
	}



	@Override
	public boolean isSameNode(final Node other) {
		return other!=null && other==this;
	}



	@Override
	public Node removeChild(final Node oldChild) {
		boolean ok = false;

		if(oldChild!=null)
			ok = children.getNodes().remove(oldChild);

		return ok ? oldChild : null;
	}



	@Override
	public void setTextContent(final String textContent) {
		if(textContent==null)
			throw new DOMException(DOMException.DOMSTRING_SIZE_ERR, "textContent is null.");//$NON-NLS-1$

		appendChild(new SVGText(textContent, getOwnerDocument()));
	}


	@Override
	public String lookupPrefix(final String namespaceURI) {
		if(namespaceURI == null)
			return null;

		String pref = null;
        final String xmlns = "xmlns"; //$NON-NLS-1$

        if(attributes!=null) {
			int i=0;
            final int size = attributes.getLength();
            boolean again = true;

			while(i<size && again) {
				final SVGAttr attr = attributes.getAttributes().get(i);
				final String attrName = attr.getName();

				if(attrName!=null && attrName.startsWith(xmlns) && namespaceURI.equals(attr.getValue())) {
					final int index = attrName.indexOf(':');

					pref = index==-1 ? "" : attrName.substring(index+1); //$NON-NLS-1$
					again = false;
				}
				else
					i++;
			}
		}

		if(pref!=null)
			return pref;

		if(getParentNode()==null)
			return null;

		return getParentNode().lookupPrefix(namespaceURI);
	}


	@Override
	public boolean hasAttribute(final String nameAttr) {
		return attributes!=null && attributes.getLength()>0;
	}




	@Override
	public String getNamespaceURI() {
		return lookupNamespaceURI(getPrefix());
	}




	@Override
	public String lookupNamespaceURI(final String pref) {
		String uri = null;

		if(attributes!=null)
			if(pref==null) {
				int i=0;
				final int size = attributes.getLength();
				boolean again = true;
				final String xmlns = "xmlns";//$NON-NLS-1$

				while(i<size && again) {
					final SVGAttr attr = attributes.getAttributes().get(i);
					final String attrName = attr.getName();

					if(xmlns.equals(attrName)) {
						uri = attr.getNodeValue();
						again = false;
					}
					else
						i++;
				}
			}
			else {
				int i=0;
                final int size = attributes.getLength();
                boolean again = true;
				final String xmlns = "xmlns:";//$NON-NLS-1$

				while(i<size && again) {
					final SVGAttr attr = attributes.getAttributes().get(i);
					final String attrName = attr.getName();

					if(attrName!=null && attrName.startsWith(xmlns) && pref.equals(attrName.substring(xmlns.length()))) {
						uri = attr.getNodeValue();
						again = false;
					}
					else
						i++;
				}
			}

		if(uri!=null)
			return uri;

		if(getParentNode()==null)
			return null;

		return parent.lookupNamespaceURI(pref);
	}



	@Override
	public String getPrefix() {
		if(getNodeName()==null)
			return null;

		final int index = getNodeName().indexOf(':');

		if(index!=-1)
			return getNodeName().substring(0, index);

		return null;
	}



	/**
	 * @return The prefix followed by Character ':'. This method aims at simplifying the use of the prefix.
	 */
	public String getUsablePrefix() {
		final String prefix = getPrefix();

		return prefix==null || prefix.isEmpty() ? "" : prefix + ':'; //$NON-NLS-1$
	}



	/**
	 * Sets the transformation of the elements. Removes previous transformations.
	 * Should not be called directly; call setAttribute instead.
	 * @param transformation The transformation to set.
	 * @since 3.0
	 */
	private void setTransformation(final String transformation) {
		if(transform==null)
			transform = new SVGTransformList();
		else
			transform.clear();

		transform.addTransformations(getAttribute(getUsablePrefix()+SVGAttributes.SVG_TRANSFORM));
	}



	@Override
	public void setAttribute(final String name, final String value) {
		if(value==null || name==null)
			throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Invalid name or/and value");//$NON-NLS-1$

		attributes.setNamedItem(new SVGAttr(name, value, this));

		if(SVGAttributes.SVG_TRANSFORM.equals(name))
			setTransformation(value);
	}


	@Override
	public Node getNextSibling() {
		if(parent==null)
			return null;

		final SVGNodeList nl = (SVGNodeList)parent.getChildNodes();
		final int index = nl.getNodes().indexOf(this);

		if(index==-1)
			return null;

		return index+1>=nl.getLength() ? null : nl.getNodes().get(index+1);

	}



	@Override
	public Node getPreviousSibling() {
		if(parent==null)
			return null;

		final SVGNodeList nl = (SVGNodeList)parent.getChildNodes();
		final int index = nl.getNodes().indexOf(this);

		if(index==-1)
			return null;

		return index-1<0 ? null : nl.getNodes().get(index+1);
	}




	@Override
	public NodeList getElementsByTagName(final String nameElt) {
		if("*".equals(nameElt)) //$NON-NLS-1$
			return getChildNodes();

		final SVGNodeList nl = new SVGNodeList();

		if(nameElt!=null) {
			final NodeList nl2 = getChildNodes();
			int i;
            final int size = nl2.getLength();
            Node n;

			for(i=0; i<size; i++) {
				n = nl2.item(i);

				if(n instanceof SVGElement && nameElt.equals(n.getNodeName()))
					nl.getNodes().add((SVGElement)n);
			}
		}

		return nl;
	}




	@Override
	public NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
		final String all = "*"; //$NON-NLS-1$

		if(all.equals(namespaceURI))
			return getElementsByTagName(localName);

		final SVGNodeList nl = new SVGNodeList();

		if(namespaceURI!=null && localName!=null) {
			final boolean getAll = all.equals(localName);
			final NodeList nl2 = getChildNodes();
			int i;
            final int size = nl2.getLength();
            Node n;

			for(i=0; i<size; i++) {
				n = nl2.item(i);

				if(n instanceof SVGElement && namespaceURI.equals(n.getNamespaceURI()) && (getAll || n.getNodeName().endsWith(localName)))
					nl.getNodes().add((SVGElement)n);
			}
		}

		return nl;
	}



	@Override
	public String getTextContent() {
		final NodeList nl = getElementsByTagName(SVGText.TEXT_NODE_NAME);
		final StringBuilder buf = new StringBuilder();

		for(int i=0, size=nl.getLength(); i<size; i++)
			buf.append(((SVGText)nl.item(i)).getData());

		return buf.toString();
	}


	@Override
	public String getLocalName() {
		return name.replaceAll(getUsablePrefix(), ""); //$NON-NLS-1$
	}


	@Override
	public void removeAttribute(final String nameAttr) {
		try {
			if(nameAttr!=null && attributes!=null)
				attributes.removeNamedItem(nameAttr);
		}
		catch(final DOMException e) { /* Nothing to do. */ }
	}


	@Override
	public void removeAttributeNS(final String namespaceURI, final String localName)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Attr removeAttributeNode(final Attr oldAttr)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Attr setAttributeNode(final Attr newAttr)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Attr setAttributeNodeNS(final Attr newAttr)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void setIdAttribute(final String name, final boolean isId)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void setIdAttributeNode(final Attr idAttr, final boolean isId)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Node cloneNode(final boolean deep)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public short compareDocumentPosition(final Node other)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public String getBaseURI()
	{ return ""; } //$NON-NLS-1$

	@Override
	public TypeInfo getSchemaTypeInfo()
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Object getFeature(final String feature, final String version)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Object getUserData(final String key)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public boolean isDefaultNamespace(final String namespaceURI)
	{ return SVGDocument.SVG_NAMESPACE.equals(namespaceURI); }

	@Override
	public boolean isSupported(final String feature, final String version)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void normalize()
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Node replaceChild(final Node newChild, final Node oldChild)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public Object setUserData(final String key, final Object data, final UserDataHandler handler)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }

	@Override
	public void setPrefix(final String prefix)
	{ throw new DOMException(DOMException.INVALID_ACCESS_ERR, SVGDocument.ACTION_NOT_IMPLEMENTED); }


	@Override
	public String getNodeValue()
	{ return null; }

	@Override
	public void setNodeValue(final String nodeValue)
	{ /* No value. */ }


	/**
	 * Returns the prefix of the given namespace URI with the ':' character after or an empty string
	 * if no prefix is found.
	 * @param namespaceURI The URI to look for.
	 * @return the prefix followed by ':' or an empty string.
	 * @since 0.1
	 */
	public String lookupPrefixUsable(final String namespaceURI) {
		String pref = lookupPrefix(namespaceURI);

		if(pref==null)
			pref = "";//$NON-NLS-1$
		else
            pref += ":";//$NON-NLS-1$

		return pref;
	}


	/**
	 * Check if the SVG element is valid according to the SVG specification.
	 * @return True if valid.
	 * @since 0.1
	 */
	public abstract boolean checkAttributes();


	/**
	 * According to the SVG specification, some attributes may lead to disables rendering of the element (e.g. width=0,
	 * height=0,...). This method checks these conditions depending of the SVG element.
	 * @return True if the element can be rendered.
	 * @since 0.1
	 */
	public abstract boolean enableRendering();



	@Override
	public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
		if(localName==null)
			return null;

		return getAttributeNode(lookupPrefixUsable(namespaceURI) + localName);
	}


	@Override
	public String getAttributeNS(final String namespaceURI, final String localName) {
		if(localName==null)
			return null;

		return getAttribute(lookupPrefixUsable(namespaceURI) + localName);
	}



	@Override
	public boolean hasAttributeNS(final String namespaceURI, final String localName) {
		return  getAttributeNodeNS(namespaceURI, localName)!=null;
	}


	/**
	 * @return The root element of the SVG document: an SVGSVGElement.
	 * @since 0.1
	 */
	public SVGSVGElement getSVGRoot() {
		final SVGElement e = getRootElement();

		if(e instanceof SVGSVGElement)
			return (SVGSVGElement)e;

		return null;
	}



	/**
	 * Allows to get a definition (a tag in the 'def' part) with the identifier 'id'.
	 * @param id The identifier of the wanted definition.
	 * @return The definition or null.
	 * @since 0.1
	 */
	public SVGElement getDef(final String id) {
		final SVGSVGElement root = getSVGRoot();

		if(root==null)
			return null;

		return root.getDefs().getDef(id);
	}



	/**
	 * @return The identifier of the SVGElement.
	 * @since 0.1
	 */
	public String getId() {
		return getAttribute(getUsablePrefix()+SVGAttributes.SVG_ID);
	}



	/**
	 * Allow to get a set of children having the name 'nodeName'
	 * @param nodeName The name of the wanted nodes.
	 * @return The set of nodes (may be empty but not null).
	 * @since 0.1
	 */
	public SVGNodeList getChildren(final String nodeName) {
		final SVGNodeList nl 	= new SVGNodeList();
		final NodeList nl2 	=  getChildNodes();

		for(int i=0, size = nl2.getLength(); i<size; i++)
			if(nl2.item(i).getNodeName().equals(nodeName))
				nl.getNodes().add((SVGElement)nl2.item(i));

		return nl;
	}



	/**
	 * Sets the stroke width of the SVG shape.
	 * @param strokeW The new stroke width (must be greater than 0).
	 * @since 0.1
	 */
	public void setStrokeWidth(final double strokeW) {
		if(strokeW>0)
			setAttribute(getUsablePrefix()+SVGAttributes.SVG_STROKE_WIDTH, String.valueOf(strokeW));
	}


	/**
	 * Sets the line cap of the stroke of the SVG shape.
	 * @param svgLineCap The line cap to set. Must be SVG_LINECAP_VALUE_BUTT or SVG_LINECAP_VALUE_ROUND
	 * or SVG_LINECAP_VALUE_SQUARE.
	 * @since 0.2
	 */
	public void setStrokeLineCap(final String svgLineCap) {
		if(SVGAttributes.SVG_LINECAP_VALUE_BUTT.equals(svgLineCap) || SVGAttributes.SVG_LINECAP_VALUE_ROUND.equals(svgLineCap) ||
		   SVGAttributes.SVG_LINECAP_VALUE_SQUARE.equals(svgLineCap))
			setAttribute(getUsablePrefix()+SVGAttributes.SVG_STROKE_LINECAP, svgLineCap);
	}


	/**
	 * Sets the miter level of the stroke.
	 * @param miterLevel The miter level to set. Must be greater than or equal to 1.
	 * @since 0.2
	 */
	public void setStrokeMiterLevel(final double miterLevel) {
		if(miterLevel>=1)
			setAttribute(SVGAttributes.SVG_STROKE_MITERLIMIT, String.valueOf(miterLevel));
	}


	/**
	 * Sets the dash offset of the stroke.
	 * @param dashOffset The dash offset to set.
	 * @since 0.2
	 */
	public void setStrokeDashOffset(final double dashOffset) {
		setAttribute(SVGAttributes.SVG_STROKE_DASHOFFSET, String.valueOf(dashOffset));
	}



	/**
	 * Sets the dash array of the stroke.
	 * @param dashArray The dash array to set. Must not be null.
	 * @since 0.2
	 */
	public void setStrokeDashArray(final String dashArray) {
		if(dashArray!=null)
			setAttribute(SVGAttributes.SVG_STROKE_DASHARRAY, dashArray);
	}



	/**
	 * Sets the line join of the stroke of the SVG shape.
	 * @param svgLineJoin The line join to set. Must be SVG_LINEJOIN_VALUE_BEVEL or SVG_LINEJOIN_VALUE_MITER
	 * or SVG_LINEJOIN_VALUE_ROUND.
	 * @since 0.2
	 */
	public void setStrokeLineJoin(final String svgLineJoin) {
		if(SVGAttributes.SVG_LINEJOIN_VALUE_BEVEL.equals(svgLineJoin) || SVGAttributes.SVG_LINEJOIN_VALUE_MITER.equals(svgLineJoin) ||
		   SVGAttributes.SVG_LINEJOIN_VALUE_ROUND.equals(svgLineJoin))
				setAttribute(getUsablePrefix()+SVGAttributes.SVG_STROKE_LINEJOIN, svgLineJoin);
	}



	/**
	 * @return The stroke width of the element (if it is possible) or 1.
	 * @since 0.1
	 */
	public double getStrokeWidth() {
		final String swStr = getSVGAttribute(SVGAttributes.SVG_STROKE_WIDTH, getUsablePrefix());
		double sw;

		try {
			sw = swStr==null ? parent==null ? 1 : parent.getStrokeWidth() :
				new SVGLengthParser(swStr).parseLength().getValue();
		} catch(final ParseException e){ sw = 1; }

		return sw;
	}



	/**
	 * @return The dash array of the element (if it is possible) or null.
	 * @since 0.1
	 */
	public String getStrokeDasharray() {
		final String da = getSVGAttribute(SVGAttributes.SVG_STROKE_DASHARRAY, getUsablePrefix());

		return da==null ? parent==null ? SVGAttributes.SVG_VALUE_NONE : parent.getStrokeDasharray() : da;
	}



	/**
	 * @return The line join of the element or its default value.
	 * @since 3.0
	 */
	public String getStrokeLinejoin() {
		final String lj = getSVGAttribute(SVGAttributes.SVG_STROKE_LINEJOIN, getUsablePrefix());

		return lj==null ? parent==null ? SVGAttributes.SVG_LINEJOIN_VALUE_MITER : parent.getStrokeLinejoin() : lj;
	}



	/**
	 * @return The line cap of the element or its default value.
	 * @since 0.1
	 */
	public String getStrokeLinecap() {
		final String linecap = getSVGAttribute(SVGAttributes.SVG_STROKE_LINECAP, getUsablePrefix());

		return linecap==null ? parent==null ? SVGAttributes.SVG_LINECAP_VALUE_BUTT : parent.getStrokeLinecap() : linecap;
	}



	/**
	 * @return The miter limit of the element or its default value.
	 * @since 0.1
	 */
	public double getStrokeMiterlimit() {
		final String linecap = getSVGAttribute(SVGAttributes.SVG_STROKE_MITERLIMIT, getUsablePrefix());
		double lc;

		if(linecap!=null)
			try { lc = Double.parseDouble(linecap); }
			catch(final Exception e) { lc = 4.; }
		else
			lc = parent==null ? 4. : parent.getStrokeMiterlimit();

		return lc;
	}


	/**
	 * @return The font-size value in point of the element, or from one of its parents.
	 */
	public float getFontSize() {
		final String fs = getSVGAttribute(SVGAttributes.SVG_FONT_SIZE, getUsablePrefix());

		return fs==null ? parent==null ? SVGLengthParser.FontSize.MEDIUM.getPointValue() :
					parent.getFontSize() : SVGLengthParser.fontSizetoPoint(fs);
	}


	/**
	 * @return The defined or inherited font family. Otherwise, an empty string.
	 */
	public String getFontFamily() {
		final String fam = getSVGAttribute(SVGAttributes.SVG_FONT_FAMILY, getUsablePrefix());

		return fam==null ? parent==null ? "" : parent.getFontFamily() : fam; //$NON-NLS-1$
	}


	/**
	 * @return The defined or inherited font style. Otherwise, the default value "normal" is returned.
	 */
	public String getFontStyle() {
		final String style = getSVGAttribute(SVGAttributes.SVG_FONT_STYLE, getUsablePrefix());

		return style==null ? parent==null ? SVGAttributes.SVG_FONT_STYLE_NORMAL : parent.getFontStyle() : style;
	}


	/**
	 * @return The defined or inherited font weight. Otherwise, the default value "normal" is returned.
	 */
	public String getFontWeight() {
		final String weight = getSVGAttribute(SVGAttributes.SVG_FONT_WEIGHT, getUsablePrefix());

		return weight==null ? parent==null ? SVGAttributes.SVG_FONT_WEIGHT_NORMAL : parent.getFontWeight() : weight;
	}




	/**
	 * Sets the colour of the filling.
	 * @param c The new filling colour.
	 * @since 0.1
	 */
	public void setFill(final Color c) {
		if(c!=null)
			setAttribute(getUsablePrefix()+SVGAttributes.SVG_FILL, CSSColors.INSTANCE.getColorName(c, true));
	}



	/**
	 * @return The fill content of the element or its default value.
	 * @since 0.1
	 */
	public String getFill() {
		final String fill = getSVGAttribute(SVGAttributes.SVG_FILL, getUsablePrefix());

		return fill==null ? parent==null ? CSSColors.CSS_BLACK_NAME : parent.getFill() : fill;
	}



	/**
	 * Sets The colour of the stroke.
	 * @param c The new colour of the stroke (must not be null).
	 * @since 0.1
	 */
	public void setStroke(final Color c) {
		if(c!=null)
			setAttribute(getUsablePrefix()+SVGAttributes.SVG_STROKE, CSSColors.INSTANCE.getColorName(c, true));
	}



	/**
	 * @return The fill content of the element (if it is possible) or null.
	 * @since 0.1
	 */
	public Color getStroke() {
		final String stroke = getSVGAttribute(SVGAttributes.SVG_STROKE, getUsablePrefix());

		return stroke==null ? parent==null ? null : parent.getStroke() : CSSColors.INSTANCE.getRGBColour(stroke);
	}



	/**
	 * @param uri The URI that will be used parsed to extract a prefix.
	 * @return The prefix followed by ':' if there is a prefix. An empty string is returned in the other case.
	 * @since 0.1
	 */
	public String getUsablePrefix(final String uri) {
		final String pref = lookupPrefix(uri);

		return pref==null || pref.isEmpty() ? "" : pref+':';//$NON-NLS-1$
	}



	/**
	 * @return The list of transformations of the current SVG element (may be null).
	 * @since 0.1
	 */
	public SVGTransformList getTransform() {
		return transform;
	}




	/**
	 * @return The list of all the transformations of the node's parents followed by the node's transformations.
	 * The first transformations will be the transformations of the oldest parent and the last ones, the
	 * transformations of the current node. If no transformation are defined, an empty list is returned.
	 * @since 0.1
	 */
	public SVGTransformList getWholeTransform() {
		final SVGTransformList tl = new SVGTransformList(); 	// The list that will be returned.
		SVGElement p = getParent();						// A parent element.

		if(getTransform()!=null)
			tl.addAll(getTransform());

		while(p!=null) {
			if(p.getTransform()!=null)
				tl.addAll(0, p.getTransform());

			p = p.getParent();
		}

		return tl;
	}



	/**
	 * Sets the owner document of the node.
	 * @param doc The document to set.
	 * @since 2.0.0
	 */
	public void setOwnerDocument(final SVGDocument doc) {
		if(doc!=null) {
			ownerDocument = doc;

			for(int i=0, size=children.getLength(); i<size; i++)
				children.item(i).setOwnerDocument(doc);
		}
	}



	/**
	 * @param deep If deep equals to 1, only direct children will be considered, and so on.
	 * @return The number of children of the node.
	 */
	public int getNbChildren(final int deep) {
		if(deep<1)
			return 0;

		int cpt = children.getLength();

		for(final SVGElement e : children.getNodes())
			cpt += e.getNbChildren(deep-1);

		return cpt;
	}


	/**
	 * @return the stylesCSS
	 * @since 0.1
	 */
	public CSSStyleList getStylesCSS() {
		return stylesCSS;
	}




	/**
	 * An SVG attribute can be defined in: its corresponding attribute (e.g. fill="...");
	 * the attribute style (e.g. style="fill:...;..."); a CSS stylesheet. This function
	 * returns the value of an SVG attribute by testing: 1. Its corresponding attribute;
	 * 2. The attribute style is 1. fails.
	 * @param attrName The name of the researched attribute.
	 * @param prefix The usable prefix.
	 * @return The found value or null.
	 * @since 2.0.6
	 */
	public String getSVGAttribute(final String attrName, final String prefix) {
		if(attrName==null) return null;

		String value = getAttribute((prefix==null?"":prefix) + attrName); //$NON-NLS-1$

		if(value==null && stylesCSS!=null)
			value = stylesCSS.get(attrName);

		return value;
	}
}
