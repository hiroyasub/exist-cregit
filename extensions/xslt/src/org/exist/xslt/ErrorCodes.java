begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ErrorCodes
extends|extends
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|ErrorCodes
block|{
comment|/* XSL 2.0 http://www.w3.org/TR/xslt20/#error-summary */
comment|/** 	 * A static error is signaled if an XSLT-defined element is used in a context where it is not permitted, if a required attribute is omitted, or if the content of the element does not correspond to the content that is allowed for the element.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0010
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0010"
argument_list|,
literal|"A static error is signaled if an XSLT-defined element is used in a context where it is not permitted, if a required attribute is omitted, or if the content of the element does not correspond to the content that is allowed for the element."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an attribute (other than an attribute written using curly brackets in a position where an attribute value template is permitted) contains a value that is not one of the permitted values for that attribute. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0020
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0020"
argument_list|,
literal|"It is a static error if an attribute (other than an attribute written using curly brackets in a position where an attribute value template is permitted) contains a value that is not one of the permitted values for that attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error to use a reserved namespace in the name of a named template, a mode, an attribute set, a key, a decimal-format, a variable or parameter, a stylesheet function, a named output definition, or a character map.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0080
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0080"
argument_list|,
literal|"It is a static error to use a reserved namespace in the name of a named template, a mode, an attribute set, a key, a decimal-format, a variable or parameter, a stylesheet function, a named output definition, or a character map."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error for an element from the XSLT namespace to have an attribute whose namespace is either null (that is, an attribute with an unprefixed name) or the XSLT namespace, other than attributes defined for the element in this document.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0090
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0090"
argument_list|,
literal|"It is a static error for an element from the XSLT namespace to have an attribute whose namespace is either null (that is, an attribute with an unprefixed name) or the XSLT namespace, other than attributes defined for the element in this document."
argument_list|)
decl_stmt|;
comment|/** 	 * The value of the version attribute must be a number: specifically, it must be a a valid instance of the type xs:decimal as defined in [XML Schema Part 2].  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0110
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0110"
argument_list|,
literal|"The value of the version attribute must be a number: specifically, it must be a a valid instance of the type xs:decimal as defined in [XML Schema Part 2]."
argument_list|)
decl_stmt|;
comment|/** 	 * An xsl:stylesheet element must not have any text node children.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0120
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0120"
argument_list|,
literal|"An xsl:stylesheet element must not have any text node children."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value of an [xsl:]default-collation attribute, after resolving against the base URI, contains no URI that the implementation recognizes as a collation URI.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0125
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0125"
argument_list|,
literal|"It is a static error if the value of an [xsl:]default-collation attribute, after resolving against the base URI, contains no URI that the implementation recognizes as a collation URI."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the xsl:stylesheet element has a child element whose name has a null namespace URI.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0130
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0130"
argument_list|,
literal|"It is a static error if the xsl:stylesheet element has a child element whose name has a null namespace URI."
argument_list|)
decl_stmt|;
comment|/** 	 * A literal result element that is used as the outermost element of a simplified stylesheet module must have an xsl:version attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0150
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0150"
argument_list|,
literal|"A literal result element that is used as the outermost element of a simplified stylesheet module must have an xsl:version attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the processor is not able to retrieve the resource identified by the URI reference [ in the href attribute of xsl:include or xsl:import] , or if the resource that is retrieved does not contain a stylesheet module conforming to this specification.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0165
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0165"
argument_list|,
literal|"It is a static error if the processor is not able to retrieve the resource identified by the URI reference [ in the href attribute of xsl:include or xsl:import] , or if the resource that is retrieved does not contain a stylesheet module conforming to this specification."
argument_list|)
decl_stmt|;
comment|/** 	 * An xsl:include element must be a top-level element.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0170
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0170"
argument_list|,
literal|"An xsl:include element must be a top-level element."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a stylesheet module directly or indirectly includes itself.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0180
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0180"
argument_list|,
literal|"It is a static error if a stylesheet module directly or indirectly includes itself."
argument_list|)
decl_stmt|;
comment|/** 	 * An xsl:import element must be a top-level element.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0190
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0190"
argument_list|,
literal|"An xsl:import element must be a top-level element."
argument_list|)
decl_stmt|;
comment|/** 	 * The xsl:import element children must precede all other element children of an xsl:stylesheet element, including any xsl:include element children and any user-defined data elements.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0200
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0200"
argument_list|,
literal|"The xsl:import element children must precede all other element children of an xsl:stylesheet element, including any xsl:include element children and any user-defined data elements."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a stylesheet module directly or indirectly imports itself.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0210
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0210"
argument_list|,
literal|"It is a static error if a stylesheet module directly or indirectly imports itself."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:import-schema element that contains an xs:schema element has a schema-location attribute, or if it has a namespace attribute that conflicts with the target namespace of the contained schema.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0215
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0215"
argument_list|,
literal|"It is a static error if an xsl:import-schema element that contains an xs:schema element has a schema-location attribute, or if it has a namespace attribute that conflicts with the target namespace of the contained schema."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the synthetic schema document does not satisfy the constraints described in [XML Schema Part 1] (section 5.1, Errors in Schema Construction and Structure). This includes, without loss of generality, conflicts such as multiple definitions of the same name. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0220
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0220"
argument_list|,
literal|"It is a static error if the synthetic schema document does not satisfy the constraints described in [XML Schema Part 1] (section 5.1, Errors in Schema Construction and Structure). This includes, without loss of generality, conflicts such as multiple definitions of the same name."
argument_list|)
decl_stmt|;
comment|/** 	 * Within an XSLT element that is required to be empty, any content other than comments or processing instructions, including any whitespace text node preserved using the xml:space="preserve" attribute, is a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0260
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0260"
argument_list|,
literal|"Within an XSLT element that is required to be empty, any content other than comments or processing instructions, including any whitespace text node preserved using the xml:space=\"preserve\" attribute, is a static error."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if there is a stylesheet module in the stylesheet that specifies input-type-annotations="strip" and another stylesheet module that specifies input-type-annotations="preserve".  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0265
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0265"
argument_list|,
literal|"It is a static error if there is a stylesheet module in the stylesheet that specifies input-type-annotations=\"strip\" and another stylesheet module that specifies input-type-annotations=\"preserve\"."
argument_list|)
decl_stmt|;
comment|/** 	 * In the case of a prefixed QName used as the value of an attribute in the stylesheet, or appearing within an XPath expression in the stylesheet, it is a static error if the defining element has no namespace node whose name matches the prefix of the QName.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0280
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0280"
argument_list|,
literal|"In the case of a prefixed QName used as the value of an attribute in the stylesheet, or appearing within an XPath expression in the stylesheet, it is a static error if the defining element has no namespace node whose name matches the prefix of the QName."
argument_list|)
decl_stmt|;
comment|/** 	 * Where an attribute is defined to contain a pattern, it is a static error if the pattern does not match the production Pattern.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0340
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0340"
argument_list|,
literal|"Where an attribute is defined to contain a pattern, it is a static error if the pattern does not match the production Pattern."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an unescaped left curly bracket appears in a fixed part of an attribute value template without a matching right curly bracket.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0350
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0350"
argument_list|,
literal|"It is a static error if an unescaped left curly bracket appears in a fixed part of an attribute value template without a matching right curly bracket."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an unescaped right curly bracket occurs in a fixed part of an attribute value template.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0370
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0370"
argument_list|,
literal|"It is a static error if an unescaped right curly bracket occurs in a fixed part of an attribute value template."
argument_list|)
decl_stmt|;
comment|/** 	 * An xsl:template element must have either a match attribute or a name attribute, or both. An xsl:template element that has no match attribute must have no mode attribute and no priority attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0500
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0500"
argument_list|,
literal|"An xsl:template element must have either a match attribute or a name attribute, or both. An xsl:template element that has no match attribute must have no mode attribute and no priority attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * The value of this attribute [the priority attribute of the xsl:template element] must conform to the rules for the xs:decimal type defined in [XML Schema Part 2]. Negative values are permitted..  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0530
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0530"
argument_list|,
literal|"The value of this attribute [the priority attribute of the xsl:template element] must conform to the rules for the xs:decimal type defined in [XML Schema Part 2]. Negative values are permitted.."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the list [of modes in the mode attribute of xsl:template] is empty, if the same token is included more than once in the list, if the list contains an invalid token, or if the token #all appears together with any other value.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0550
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0550"
argument_list|,
literal|"It is a static error if the list [of modes in the mode attribute of xsl:template] is empty, if the same token is included more than once in the list, if the list contains an invalid token, or if the token #all appears together with any other value."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if two parameters of a template or of a stylesheet function have the same name.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0580
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0580"
argument_list|,
literal|"It is a static error if two parameters of a template or of a stylesheet function have the same name."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a variable-binding element has a select attribute and has non-empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0620
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0620"
argument_list|,
literal|"It is a static error if a variable-binding element has a select attribute and has non-empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a stylesheet contains more than one binding of a global variable with the same name and same import precedence, unless it also contains another binding with the same name and higher import precedence.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0630
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0630"
argument_list|,
literal|"It is a static error if a stylesheet contains more than one binding of a global variable with the same name and same import precedence, unless it also contains another binding with the same name and higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a stylesheet contains an xsl:call-template instruction whose name attribute does not match the name attribute of any xsl:template in the stylesheet. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0650
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0650"
argument_list|,
literal|"It is a static error if a stylesheet contains an xsl:call-template instruction whose name attribute does not match the name attribute of any xsl:template in the stylesheet."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a stylesheet contains more than one template with the same name and the same import precedence, unless it also contains a template with the same name and higher import precedence.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0660
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0660"
argument_list|,
literal|"It is a static error if a stylesheet contains more than one template with the same name and the same import precedence, unless it also contains a template with the same name and higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a single xsl:call-template, xsl:apply-templates, xsl:apply-imports, or xsl:next-match element contains two or more xsl:with-param elements with matching name attributes.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0670
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0670"
argument_list|,
literal|"It is a static error if a single xsl:call-template, xsl:apply-templates, xsl:apply-imports, or xsl:next-match element contains two or more xsl:with-param elements with matching name attributes."
argument_list|)
decl_stmt|;
comment|/** 	 * In the case of xsl:call-template, it is a static error to pass a non-tunnel parameter named x to a template that does not have a template parameter named x, unless backwards compatible behavior is enabled for the xsl:call-template instruction.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0680
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0680"
argument_list|,
literal|"In the case of xsl:call-template, it is a static error to pass a non-tunnel parameter named x to a template that does not have a template parameter named x, unless backwards compatible behavior is enabled for the xsl:call-template instruction."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a template that is invoked using xsl:call-template declares a template parameter specifying required="yes" and not specifying tunnel="yes", if no value for this parameter is supplied by the calling instruction. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0690
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0690"
argument_list|,
literal|"It is a static error if a template that is invoked using xsl:call-template declares a template parameter specifying required=\"yes\" and not specifying tunnel=\"yes\", if no value for this parameter is supplied by the calling instruction."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value of the use-attribute-sets attribute of an xsl:copy, xsl:element, or xsl:attribute-set element, or the xsl:use-attribute-sets attribute of a literal result element, is not a whitespace-separated sequence of QNames, or if it contains a QName that does not match the name attribute of any xsl:attribute-set declaration in the stylesheet.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0710
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0710"
argument_list|,
literal|"It is a static error if the value of the use-attribute-sets attribute of an xsl:copy, xsl:element, or xsl:attribute-set element, or the xsl:use-attribute-sets attribute of a literal result element, is not a whitespace-separated sequence of QNames, or if it contains a QName that does not match the name attribute of any xsl:attribute-set declaration in the stylesheet."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:attribute-set element directly or indirectly references itself via the names contained in the use-attribute-sets attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0720
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0720"
argument_list|,
literal|"It is a static error if an xsl:attribute-set element directly or indirectly references itself via the names contained in the use-attribute-sets attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * A stylesheet function must have a prefixed name, to remove any risk of a clash with a function in the default function namespace. It is a static error if the name has no prefix.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0740
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0740"
argument_list|,
literal|"A stylesheet function must have a prefixed name, to remove any risk of a clash with a function in the default function namespace. It is a static error if the name has no prefix."
argument_list|)
decl_stmt|;
comment|/** 	 * Because arguments to a stylesheet function call must all be specified, the xsl:param elements within an xsl:function element must not specify a default value: this means they must be empty, and must not have a select attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0760
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0760"
argument_list|,
literal|"Because arguments to a stylesheet function call must all be specified, the xsl:param elements within an xsl:function element must not specify a default value: this means they must be empty, and must not have a select attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error for a stylesheet to contain two or more functions with the same expanded-QName, the same arity, and the same import precedence, unless there is another function with the same expanded-QName and arity, and a higher import precedence.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0770
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0770"
argument_list|,
literal|"It is a static error for a stylesheet to contain two or more functions with the same expanded-QName, the same arity, and the same import precedence, unless there is another function with the same expanded-QName and arity, and a higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an attribute on a literal result element is in the XSLT namespace, unless it is one of the attributes explicitly defined in this specification.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0805
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0805"
argument_list|,
literal|"It is a static error if an attribute on a literal result element is in the XSLT namespace, unless it is one of the attributes explicitly defined in this specification."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a namespace prefix is used within the [xsl:]exclude-result-prefixes attribute and there is no namespace binding in scope for that prefix.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0808
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0808"
argument_list|,
literal|"It is a static error if a namespace prefix is used within the [xsl:]exclude-result-prefixes attribute and there is no namespace binding in scope for that prefix."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value #default is used within the [xsl:]exclude-result-prefixes attribute and the parent element of the [xsl:]exclude-result-prefixes attribute has no default namespace.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0809
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0809"
argument_list|,
literal|"It is a static error if the value #default is used within the [xsl:]exclude-result-prefixes attribute and the parent element of the [xsl:]exclude-result-prefixes attribute has no default namespace."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if there is more than one such declaration [more than one xsl:namespace-alias declaration] with the same literal namespace URI and the same import precedence and different values for the target namespace URI, unless there is also an xsl:namespace-alias declaration with the same literal namespace URI and a higher import precedence.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0810
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0810"
argument_list|,
literal|"It is a static error if there is more than one such declaration [more than one xsl:namespace-alias declaration] with the same literal namespace URI and the same import precedence and different values for the target namespace URI, unless there is also an xsl:namespace-alias declaration with the same literal namespace URI and a higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a value other than #default is specified for either the stylesheet-prefix or the result-prefix attributes of the xsl:namespace-alias element when there is no in-scope binding for that namespace prefix.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0812
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0812"
argument_list|,
literal|"It is a static error if a value other than #default is specified for either the stylesheet-prefix or the result-prefix attributes of the xsl:namespace-alias element when there is no in-scope binding for that namespace prefix."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the select attribute of the xsl:attribute element is present unless the element has empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0840
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0840"
argument_list|,
literal|"It is a static error if the select attribute of the xsl:attribute element is present unless the element has empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the select attribute of the xsl:value-of element is present when the content of the element is non-empty, or if the select attribute is absent when the content is empty.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0870
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0870"
argument_list|,
literal|"It is a static error if the select attribute of the xsl:value-of element is present when the content of the element is non-empty, or if the select attribute is absent when the content is empty."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the select attribute of the xsl:processing-instruction element is present unless the element has empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0880
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0880"
argument_list|,
literal|"It is a static error if the select attribute of the xsl:processing-instruction element is present unless the element has empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the select attribute of the xsl:namespace element is present when the element has content other than one or more xsl:fallback instructions, or if the select attribute is absent when the element has empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0910
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0910"
argument_list|,
literal|"It is a static error if the select attribute of the xsl:namespace element is present when the element has content other than one or more xsl:fallback instructions, or if the select attribute is absent when the element has empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the select attribute of the xsl:comment element is present unless the element has empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0940
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0940"
argument_list|,
literal|"It is a static error if the select attribute of the xsl:comment element is present unless the element has empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error to use the xsl:copy or xsl:copy-of instruction to copy a node that has namespace-sensitive content if the copy-namespaces attribute has the value no and its explicit or implicit validation attribute has the value preserve. It is also a type error if either of these instructions (with validation="preserve") is used to copy an attribute having namespace-sensitive content, unless the parent element is also copied. A node has namespace-sensitive content if its typed value contains an item of type xs:QName or xs:NOTATION or a type derived therefrom. The reason this is an error is because the validity of the content depends on the namespace context being preserved.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0950
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0950"
argument_list|,
literal|"It is a type error to use the xsl:copy or xsl:copy-of instruction to copy a node that has namespace-sensitive content if the copy-namespaces attribute has the value no and its explicit or implicit validation attribute has the value preserve. It is also a type error if either of these instructions (with validation=\"preserve\") is used to copy an attribute having namespace-sensitive content, unless the parent element is also copied. A node has namespace-sensitive content if its typed value contains an item of type xs:QName or xs:NOTATION or a type derived therefrom. The reason this is an error is because the validity of the content depends on the namespace context being preserved."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value attribute of xsl:number is present unless the select, level, count, and from attributes are all absent.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE0975
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE0975"
argument_list|,
literal|"It is a static error if the value attribute of xsl:number is present unless the select, level, count, and from attributes are all absent."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:sort element with a select attribute has non-empty content.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1015
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1015"
argument_list|,
literal|"It is a static error if an xsl:sort element with a select attribute has non-empty content."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:sort element other than the first in a sequence of sibling xsl:sort elements has a stable attribute. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1017
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1017"
argument_list|,
literal|"It is a static error if an xsl:sort element other than the first in a sequence of sibling xsl:sort elements has a stable attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:perform-sort instruction with a select attribute has any content other than xsl:sort and xsl:fallback instructions.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1040
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1040"
argument_list|,
literal|"It is a static error if an xsl:perform-sort instruction with a select attribute has any content other than xsl:sort and xsl:fallback instructions."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the current-group function is used within a pattern.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1060
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1060"
argument_list|,
literal|"It is a static error if the current-group function is used within a pattern."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the current-grouping-key function is used within a pattern.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1070
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1070"
argument_list|,
literal|"It is a static error if the current-grouping-key function is used within a pattern."
argument_list|)
decl_stmt|;
comment|/** 	 * These four attributes [the group-by, group-adjacent, group-starting-with, and group-ending-with attributes of xsl:for-each-group ] are mutually exclusive: it is a static error if none of these four attributes is present, or if more than one of them is present.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1080
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1080"
argument_list|,
literal|"These four attributes [the group-by, group-adjacent, group-starting-with, and group-ending-with attributes of xsl:for-each-group ] are mutually exclusive: it is a static error if none of these four attributes is present, or if more than one of them is present."
argument_list|)
decl_stmt|;
comment|/** 	 * It is an error to specify the collation attribute if neither the group-by attribute nor group-adjacent attribute is specified.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1090
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1090"
argument_list|,
literal|"It is an error to specify the collation attribute if neither the group-by attribute nor group-adjacent attribute is specified."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the xsl:analyze-ErrorCode instruction contains neither an xsl:matching-subErrorCode nor an xsl:non-matching-subErrorCode element.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1130
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1130"
argument_list|,
literal|"It is a static error if the xsl:analyze-ErrorCode instruction contains neither an xsl:matching-subErrorCode nor an xsl:non-matching-subErrorCode element."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if an xsl:key declaration has a use attribute and has non-empty content, or if it has empty content and no use attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1205
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1205"
argument_list|,
literal|"It is a static error if an xsl:key declaration has a use attribute and has non-empty content, or if it has empty content and no use attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the xsl:key declaration has a collation attribute whose value (after resolving against the base URI) is not a URI recognized by the implementation as referring to a collation.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1210
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1210"
argument_list|,
literal|"It is a static error if the xsl:key declaration has a collation attribute whose value (after resolving against the base URI) is not a URI recognized by the implementation as referring to a collation."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if there are several xsl:key declarations in the stylesheet with the same key name and different effective collations. Two collations are the same if their URIs are equal under the rules for comparing xs:anyURI values, or if the implementation can determine that they are different URIs referring to the same collation.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1220
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1220"
argument_list|,
literal|"It is a static error if there are several xsl:key declarations in the stylesheet with the same key name and different effective collations. Two collations are the same if their URIs are equal under the rules for comparing xs:anyURI values, or if the implementation can determine that they are different URIs referring to the same collation."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a named or unnamed decimal format contains two conflicting values for the same attribute in different xsl:decimal-format declarations having the same import precedence, unless there is another definition of the same attribute with higher import precedence.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1290
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1290"
argument_list|,
literal|"It is a static error if a named or unnamed decimal format contains two conflicting values for the same attribute in different xsl:decimal-format declarations having the same import precedence, unless there is another definition of the same attribute with higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the character specified in the zero-digit attribute is not a digit or is a digit that does not have the numeric value zero.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1295
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1295"
argument_list|,
literal|"It is a static error if the character specified in the zero-digit attribute is not a digit or is a digit that does not have the numeric value zero."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if, for any named or unnamed decimal format, the variables representing characters used in a picture ErrorCode do not each have distinct values. These variables are decimal-separator-sign, grouping-sign, percent-sign, per-mille-sign, digit-zero-sign, digit-sign, and pattern-separator-sign.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1300
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1300"
argument_list|,
literal|"It is a static error if, for any named or unnamed decimal format, the variables representing characters used in a picture ErrorCode do not each have distinct values. These variables are decimal-separator-sign, grouping-sign, percent-sign, per-mille-sign, digit-zero-sign, digit-sign, and pattern-separator-sign."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if there is no namespace bound to the prefix on the element bearing the [xsl:]extension-element-prefixes attribute or, when #default is specified, if there is no default namespace.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1430
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1430"
argument_list|,
literal|"It is a static error if there is no namespace bound to the prefix on the element bearing the [xsl:]extension-element-prefixes attribute or, when #default is specified, if there is no default namespace."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if both the [xsl:]type and [xsl:]validation attributes are present on the xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, xsl:document, or xsl:result-document instructions, or on a literal result element. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1505
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1505"
argument_list|,
literal|"It is a static error if both the [xsl:]type and [xsl:]validation attributes are present on the xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, xsl:document, or xsl:result-document instructions, or on a literal result element."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value of the type attribute of an xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, xsl:document, or xsl:result-document instruction, or the xsl:type attribute of a literal result element, is not a valid QName, or if it uses a prefix that is not defined in an in-scope namespace declaration, or if the QName is not the name of a type definition included in the in-scope schema components for the stylesheet. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1520
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1520"
argument_list|,
literal|"It is a static error if the value of the type attribute of an xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, xsl:document, or xsl:result-document instruction, or the xsl:type attribute of a literal result element, is not a valid QName, or if it uses a prefix that is not defined in an in-scope namespace declaration, or if the QName is not the name of a type definition included in the in-scope schema components for the stylesheet."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the value of the type attribute of an xsl:attribute instruction refers to a complex type definition 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1530
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1530"
argument_list|,
literal|"It is a static error if the value of the type attribute of an xsl:attribute instruction refers to a complex type definition"
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if two xsl:output declarations within an output definition specify explicit values for the same attribute (other than cdata-section-elements and use-character-maps), with the values of the attributes being not equal, unless there is another xsl:output declaration within the same output definition that has higher import precedence and that specifies an explicit value for the same attribute. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1560
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1560"
argument_list|,
literal|"It is a static error if two xsl:output declarations within an output definition specify explicit values for the same attribute (other than cdata-section-elements and use-character-maps), with the values of the attributes being not equal, unless there is another xsl:output declaration within the same output definition that has higher import precedence and that specifies an explicit value for the same attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * The value [of the method attribute on xsl:output ] must (if present) be a valid QName. If the QName does not have a prefix, then it identifies a method specified in [XSLT and XQuery Serialization] and must be one of xml, html, xhtml, or text. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1570
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1570"
argument_list|,
literal|"The value [of the method attribute on xsl:output ] must (if present) be a valid QName. If the QName does not have a prefix, then it identifies a method specified in [XSLT and XQuery Serialization] and must be one of xml, html, xhtml, or text."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if the stylesheet contains two or more character maps with the same name and the same import precedence, unless it also contains another character map with the same name and higher import precedence. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1580
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1580"
argument_list|,
literal|"It is a static error if the stylesheet contains two or more character maps with the same name and the same import precedence, unless it also contains another character map with the same name and higher import precedence."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a name in the use-character-maps attribute of the xsl:output or xsl:character-map elements does not match the name attribute of any xsl:character-map in the stylesheet. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1590
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1590"
argument_list|,
literal|"It is a static error if a name in the use-character-maps attribute of the xsl:output or xsl:character-map elements does not match the name attribute of any xsl:character-map in the stylesheet."
argument_list|)
decl_stmt|;
comment|/** 	 * It is a static error if a character map references itself, directly or indirectly, via a name in the use-character-maps attribute. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1600
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1600"
argument_list|,
literal|"It is a static error if a character map references itself, directly or indirectly, via a name in the use-character-maps attribute."
argument_list|)
decl_stmt|;
comment|/** 	 * A basic XSLT processor must signal a static error if the stylesheet includes an xsl:import-schema declaration. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1650
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1650"
argument_list|,
literal|"A basic XSLT processor must signal a static error if the stylesheet includes an xsl:import-schema declaration."
argument_list|)
decl_stmt|;
comment|/** 	 * A basic XSLT processor must signal a static error if the stylesheet includes an [xsl:]type attribute, or an [xsl:]validation or default-validation attribute with a value other than strip. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTSE1660
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTSE1660"
argument_list|,
literal|"A basic XSLT processor must signal a static error if the stylesheet includes an [xsl:]type attribute, or an [xsl:]validation or default-validation attribute with a value other than strip."
argument_list|)
decl_stmt|;
comment|//Type errors
comment|/** 	 * It is a type error if the result of evaluating the sequence constructor cannot be converted to the required type. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0505
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0505"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if an xsl:apply-templates instruction with no select attribute is evaluated when the context item is not a node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0510
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0510"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the sequence returned by the select expression [of xsl:apply-templates] contains an item that is not a node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0520
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0520"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the supplied value of a variable cannot be converted to the required type. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0570
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0570"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the conversion of the supplied value of a parameter to its required type fails. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0590
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0590"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If a default value is given explicitly, that is, if there is either a select attribute or a non-empty sequence constructor, then it is a type error if the default value cannot be converted to the required type, using the function conversion rules. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0600
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0600"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the as attribute [of xsl:function ] is specified, then the result evaluated by the sequence constructor (see 5.7 Sequence Constructors) is converted to the required type, using the function conversion rules. It is a type error if this conversion fails. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0780
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0780"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the value of a parameter to a stylesheet function cannot be converted to the required type, a type error is signaled. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0790
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0790"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the xsl:number instruction is evaluated, with no value or select attribute, when the context item is not a node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE0990
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE0990"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the result of evaluating the select attribute of the xsl:number instruction is anything other than a single node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1000
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1000"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If any sort key value, after atomization and any type conversion required by the data-type attribute, is a sequence containing more than one item, then the effect depends on whether the xsl:sort element is evaluated with backwards compatible behavior. With backwards compatible behavior, the effective sort key value is the first item in the sequence. In other cases, this is a type error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1020
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1020"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if the grouping key evaluated using the group-adjacent attribute is an empty sequence, or a sequence containing more than one item. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1100
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1100"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * When the group-starting-with or group-ending-with attribute [of the xsl:for-each-group instruction] is used, it is a type error if the result of evaluating the select expression contains an item that is not a node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1120
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1120"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the validation attribute of an xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, or xsl:result-document instruction, or the xsl:validation attribute of a literal result element, has the effective value strict, and schema validity assessment concludes that the validity of the element or attribute is invalid or unknown, a type error occurs. As with other type errors, the error may be signaled statically if it can be detected statically. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1510
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1510"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the validation attribute of an xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, or xsl:result-document instruction, or the xsl:validation attribute of a literal result element, has the effective value strict, and there is no matching top-level declaration in the schema, then a type error occurs. As with other type errors, the error may be signaled statically if it can be detected statically. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1512
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1512"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the validation attribute of an xsl:element, xsl:attribute, xsl:copy, xsl:copy-of, or xsl:result-document instruction, or the xsl:validation attribute of a literal result element, has the effective value lax, and schema validity assessment concludes that the element or attribute is invalid, a type error occurs. As with other type errors, the error may be signaled statically if it can be detected statically. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1515
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1515"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if an [xsl:]type attribute is defined for a constructed element or attribute, and the outcome of schema validity assessment against that type is that the validity property of that element or attribute information item is other than valid. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1540
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1540"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * A type error occurs if a type or validation attribute is defined (explicitly or implicitly) for an instruction that constructs a new attribute node, if the effect of this is to cause the attribute value to be validated against a type that is derived from, or constructed by list or union from, the primitive types xs:QName or xs:NOTATION. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1545
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1545"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * A type error occurs [when a document node is validated] unless the children of the document node comprise exactly one element node, no text nodes, and zero or more comment and processing instruction nodes, in any order. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1550
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1550"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a type error if, when validating a document node, document-level constraints are not satisfied. These constraints include identity constraints (xs:unique, xs:key, and xs:keyref) and ID/IDREF constraints. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTTE1555
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTTE1555"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|//Dynamic errors
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of an attribute written using curly brackets, in a position where an attribute value template is permitted, is a value that is not one of the permitted values for that attribute. If the processor is able to detect the error statically (for example, when any XPath expressions within the curly brackets can be evaluated statically), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0030
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0030"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the invocation of the stylesheet specifies a template name that does not match the expanded-QName of a named template defined in the stylesheet. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0040
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0040"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the invocation of the stylesheet specifies an initial mode (other than the default mode) that does not match the expanded-QName in the mode attribute of any template defined in the stylesheet. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0045
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0045"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the invocation of the stylesheet specifies both an initial mode and an initial template. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0047
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0047"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the stylesheet that is invoked declares a visible stylesheet parameter with required="yes" and no value for this parameter is supplied during the invocation of the stylesheet. A stylesheet parameter is visible if it is not masked by another global variable or parameter with the same name and higher import precedence. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0050
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0050"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the initial template defines a template parameter that specifies required="yes". 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0060
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0060"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If an implementation does not support backwards-compatible behavior, then it is a non-recoverable dynamic error if any element is evaluated that enables backwards-compatible behavior. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0160
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0160"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a recoverable dynamic error if this [the process of finding an xsl:strip-space or xsl:preserve-space declaration to match an element in the source document] leaves more than one match, unless all the matched declarations are equivalent (that is, they are all xsl:strip-space or they are all xsl:preserve-space). 	 * 	    Action: The optional recovery action is to select, from the matches that are left, the one that occurs last in declaration order. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE0270
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE0270"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * Where the result of evaluating an XPath expression (or an attribute value template) is required to be a lexical QName, then unless otherwise specified it is a non-recoverable dynamic error if the defining element has no namespace node whose name matches the prefix of the lexical QName. This error may be signaled as a static error if the value of the expression can be determined statically. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0290
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0290"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the result sequence used to construct the content of an element node contains a namespace node or attribute node that is preceded in the sequence by a node that is neither a namespace node nor an attribute node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0410
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0410"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the result sequence used to construct the content of a document node contains a namespace node or attribute node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0420
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0420"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the result sequence contains two or more namespace nodes having the same name but different ErrorCode values (that is, namespace nodes that map the same prefix to different namespace URIs). 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0430
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0430"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the result sequence contains a namespace node with no name and the element node being constructed has a null namespace URI (that is, it is an error to define a default namespace when the element is in no namespace). 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0440
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0440"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if namespace fixup is performed on an element that contains among the typed values of the element and its attributes two values of type xs:QName or xs:NOTATION containing conflicting namespace prefixes, that is, two values that use the same prefix to refer to different namespace URIs. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0485
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0485"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a recoverable dynamic error if the conflict resolution algorithm for template rules leaves more than one matching template rule. 	 * 	    Action: The optional recovery action is to select, from the matching template rules that are left, the one that occurs last in declaration order. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE0540
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE0540"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if xsl:apply-imports or xsl:next-match is evaluated when the current template rule is null. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0560
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0560"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If an optional parameter has no select attribute and has an empty sequence constructor, and if there is an as attribute, then the default value of the parameter is an empty sequence. If the empty sequence is not a valid instance of the required type defined in the as attribute, then the parameter is treated as a required parameter, which means that it is a non-recoverable dynamic error if the caller supplies no value for the parameter. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0610
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0610"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * In general, a circularity in a stylesheet is a non-recoverable dynamic error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0640
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0640"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * In other cases, [with xsl:apply-templates, xsl:apply-imports, and xsl:next-match, or xsl:call-template with tunnel parameters] it is a non-recoverable dynamic error if the template that is invoked declares a template parameter with required="yes" and no value for this parameter is supplied by the calling instruction. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0700
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0700"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a recoverable dynamic error if the name of a constructed attribute is xml:space and the value is not either default or preserve. 	 * 	    Action: The optional recovery action is to construct the attribute with the value as requested. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE0795
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE0795"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the name attribute [of the xsl:element instruction] is not a lexical QName. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0820
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0820"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * In the case of an xsl:element instruction with no namespace attribute, it is a non-recoverable dynamic error if the effective value of the name attribute is a QName whose prefix is not declared in an in-scope namespace declaration for the xsl:element instruction. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0830
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0830"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the namespace attribute [of the xsl:element instruction] is not in the lexical space of the xs:anyURI data type. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0835
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0835"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the name attribute [of an xsl:attribute instruction] is not a lexical QName. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0850
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0850"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * In the case of an xsl:attribute instruction with no namespace attribute, it is a non-recoverable dynamic error if the effective value of the name attribute is the ErrorCode xmlns. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0855
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0855"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * In the case of an xsl:attribute instruction with no namespace attribute, it is a non-recoverable dynamic error if the effective value of the name attribute is a lexical QName whose prefix is not declared in an in-scope namespace declaration for the xsl:attribute instruction. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0860
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0860"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the namespace attribute [of the xsl:attribute instruction] is not in the lexical space of the xs:anyURI data type. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0865
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0865"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the name attribute [of the xsl:processing-instruction instruction] is not both an NCName Names and a PITarget XML. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0890
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0890"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the ErrorCode value of the new namespace node [created using xsl:namespace] is not valid in the lexical space of the data type xs:anyURI. [see XTDE0835]  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0905
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0905"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the name attribute [of the xsl:namespace instruction] is neither a zero-length ErrorCode nor an NCName Names, or if it is xmlns. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0920
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0920"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the xsl:namespace instruction generates a namespace node whose name is xml and whose ErrorCode value is not http://www.w3.org/XML/1998/namespace, or a namespace node whose ErrorCode value is http://www.w3.org/XML/1998/namespace and whose name is not xml. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0925
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0925"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if evaluating the select attribute or the contained sequence constructor of an xsl:namespace instruction results in a zero-length ErrorCode. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0930
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0930"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if any undiscarded item in the atomized sequence supplied as the value of the value attribute of xsl:number cannot be converted to an integer, or if the resulting integer is less than 0 (zero). 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE0980
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE0980"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if, for any sort key component, the set of sort key values evaluated for all the items in the initial sequence, after any type conversion requested, contains a pair of ordinary values for which the result of the XPath lt operator is an error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1030
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1030"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the collation attribute of xsl:sort (after resolving against the base URI) is not a URI that is recognized by the implementation as referring to a collation. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1035
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1035"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the collation URI specified to xsl:for-each-group (after resolving against the base URI) is a collation that is not recognized by the implementation. (For notes, [see XTDE1035].) 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1110
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1110"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the regex attribute [of the xsl:analyze-ErrorCode instruction] does not conform to the required syntax for regular expressions, as specified in [Functions and Operators]. If the regular expression is known statically (for example, if the attribute does not contain any expressions enclosed in curly brackets) then the processor may signal the error as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1140
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1140"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the flags attribute [of the xsl:analyze-ErrorCode instruction] has a value other than the values defined in [Functions and Operators]. If the value of the attribute is known statically (for example, if the attribute does not contain any expressions enclosed in curly brackets) then the processor may signal the error as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1145
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1145"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the regex attribute [of the xsl:analyze-ErrorCode instruction] is a regular expression that matches a zero-length ErrorCode: or more specifically, if the regular expression $r and flags $f are such that matches("", $r, $f) returns true. If the regular expression is known statically (for example, if the attribute does not contain any expressions enclosed in curly brackets) then the processor may signal the error as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1150
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1150"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * When a URI reference [supplied to the document function] contains a fragment identifier, it is a recoverable dynamic error if the media type is not one that is recognized by the processor, or if the fragment identifier does not conform to the rules for fragment identifiers for that media type, or if the fragment identifier selects something other than a sequence of nodes (for example, if it selects a range of characters within a text node). 	 * 	    Action: The optional recovery action is to ignore the fragment identifier and return the document node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE1160
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE1160"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if a URI [supplied in the first argument to the unparsed-text function] contains a fragment identifier, or if it cannot be used to retrieve a resource containing text. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1170
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1170"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if a resource [retrieved using the unparsed-text function] contains octets that cannot be decoded into Unicode characters using the specified encoding, or if the resulting characters are not permitted XML characters. This includes the case where the processor does not support the requested encoding. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1190
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1190"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the second argument of the unparsed-text function is omitted and the processor cannot infer the encoding using external information and the encoding is not UTF-8. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1200
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1200"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the value [of the first argument to the key function] is not a valid QName, or if there is no namespace declaration in scope for the prefix of the QName, or if the name obtained by expanding the QName is not the same as the expanded name of any xsl:key declaration in the stylesheet. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1260
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1260"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error to call the key function with two arguments if there is no context node, or if the root of the tree containing the context node is not a document node; or to call the function with three arguments if the root of the tree containing the node supplied in the third argument is not a document node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1270
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1270"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the name specified as the $decimal-format-name argument [ to the format-number function] is not a valid QName, or if its prefix has not been declared in an in-scope namespace declaration, or if the stylesheet does not contain a declaration of a decimal-format with a matching expanded-QName. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1280
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1280"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * The picture ErrorCode [supplied to the format-number function] must conform to the following rules. [ See full specification.] It is a non-recoverable dynamic error if the picture ErrorCode does not satisfy these rules. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1310
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1310"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the syntax of the picture [used for date/time formatting] is incorrect. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1340
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1340"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if a component specifier within the picture [used for date/time formatting] refers to components that are not available in the given type of $value, for example if the picture supplied to the format-time refers to the year, month, or day component. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1350
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1350"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * If the current function is evaluated within an expression that is evaluated when the context item is undefined, a non-recoverable dynamic error occurs. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1360
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1360"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the unparsed-entity-uri function is called when there is no context node, or when the root of the tree containing the context node is not a document node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1370
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1370"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the unparsed-entity-public-id function is called when there is no context node, or when the root of the tree containing the context node is not a document node. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1380
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1380"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the value [supplied as the $property-name argument to the system-property function] is not a valid QName, or if there is no namespace declaration in scope for the prefix of the QName. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1390
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1390"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * When a transformation is terminated by use of xsl:message terminate="yes", the effect is the same as when a non-recoverable dynamic error occurs during the transformation. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTMM9000
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTMM9000"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the argument [passed to the function-available function] does not evaluate to a ErrorCode that is a valid QName, or if there is no namespace declaration in scope for the prefix of the QName. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1400
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1400"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the arguments supplied to a call on an extension function do not satisfy the rules defined for that particular extension function, or if the extension function reports an error, or if the result of the extension function cannot be converted to an XPath value. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1420
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1420"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * When backwards compatible behavior is enabled, it is a non-recoverable dynamic error to evaluate an extension function call if no implementation of the extension function is available. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1425
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1425"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the argument [passed to the type-available function] does not evaluate to a ErrorCode that is a valid QName, or if there is no namespace declaration in scope for the prefix of the QName. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1428
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1428"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the argument [passed to the element-available function] does not evaluate to a ErrorCode that is a valid QName, or if there is no namespace declaration in scope for the prefix of the QName. If the processor is able to detect the error statically (for example, when the argument is supplied as a ErrorCode literal), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1440
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1440"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * When a processor performs fallback for an extension instruction that is not recognized, if the instruction element has one or more xsl:fallback children, then the content of each of the xsl:fallback children must be evaluated; it is a non-recoverable dynamic error if it has no xsl:fallback children. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1450
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1450"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a non-recoverable dynamic error if the effective value of the format attribute [of an xsl:result-document element] is not a valid lexical QName, or if it does not match the expanded-QName of an output definition in the stylesheet. If the processor is able to detect the error statically (for example, when the format attribute contains no curly brackets), then the processor may optionally signal this as a static error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1460
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1460"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * 	It is a non-recoverable dynamic error to evaluate the xsl:result-document instruction in temporary output state. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1480
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1480"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * 	It is a non-recoverable dynamic error for a transformation to generate two or more final result trees with the same URI. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1490
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1490"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * 	It is a recoverable dynamic error for a transformation to generate two or more final result trees with URIs that identify the same physical resource. The optional recovery action is implementation-dependent, since it may be impossible for the processor to detect the error. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE1495
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE1495"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * 	It is a recoverable dynamic error for a stylesheet to write to an external resource and read from the same resource during a single transformation, whether or not the same URI is used to access the resource in both cases. 	 * 	    Action: The optional recovery action is implementation-dependent: implementations are not required to detect the error condition. Note that if the error is not detected, it is undefined whether the document that is read from the resource reflects its state before or after the result tree is written. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE1500
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE1500"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * 	It is a recoverable dynamic error if an xsl:value-of or xsl:text instruction specifies that output escaping is to be disabled and the implementation does not support this. 	 * 	    Action: The optional recovery action is to ignore the disable-output-escaping attribute. 	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE1620
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE1620"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * It is a recoverable dynamic error if an xsl:value-of or xsl:text instruction specifies that output escaping is to be disabled when writing to a final result tree that is not being serialized. 	 * 	    Action: The optional recovery action is to ignore the disable-output-escaping attribute.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTRE1630
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTRE1630"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/** 	 * A basic XSLT processor must raise a non-recoverable dynamic error if the input to the processor includes a node with a type annotation other than xs:untyped or xs:untypedAtomic, or an atomic value of a type other than those which a basic XSLT processor supports.  	 */
specifier|public
specifier|static
specifier|final
name|ErrorCode
name|XTDE1665
init|=
operator|new
name|ErrorCode
argument_list|(
literal|"XTDE1665"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

