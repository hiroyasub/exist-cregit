begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Templates
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AnalyzeContextInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|CompiledXQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NamespaceNodeAtExist
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|Validation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|AttributeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|Declaration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|Param
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|Template
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|XSLExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|i
operator|.
name|Parameted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  *<(xsl:stylesheet|xsl:transform)  *   id? = id  *   extension-element-prefixes? = tokens  *   exclude-result-prefixes? = tokens  *   version = number  *   xpath-default-namespace? = uri  *   default-validation? = "preserve" | "strip"  *   default-collation? = uri-list  *   input-type-annotations? = "preserve" | "strip" | "unspecified">  *<!-- Content: (xsl:import*, other-declarations) -->  *</(xsl:stylesheet|xsl:transform)>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLStylesheet
extends|extends
name|Declaration
implements|implements
name|CompiledXQuery
implements|,
name|Templates
implements|,
name|XSLExpression
implements|,
name|Parameted
block|{
specifier|private
name|Transformer
name|transformer
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
name|double
name|version
init|=
literal|2.0
decl_stmt|;
specifier|private
name|String
name|id
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|extension_element_prefixes
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|exclude_result_prefixes
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|xpath_default_namespace
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|default_validation
init|=
name|Validation
operator|.
name|STRIP
decl_stmt|;
specifier|private
name|String
name|default_collation
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|input_type_annotations
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|simplified
init|=
literal|false
decl_stmt|;
specifier|private
name|Template
name|rootTemplate
init|=
literal|null
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|Template
argument_list|>
name|templates
init|=
operator|new
name|TreeSet
argument_list|<
name|Template
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|Template
argument_list|>
name|namedTemplates
init|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|Template
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AttributeSet
argument_list|>
name|attributeSets
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AttributeSet
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
specifier|public
name|XSLStylesheet
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|//UNDERSTAND: may be better to set at eval???
name|context
operator|.
name|setXSLStylesheet
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStripWhitespace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XSLStylesheet
parameter_list|(
name|XSLContext
name|context
parameter_list|,
name|boolean
name|embedded
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|simplified
operator|=
name|embedded
expr_stmt|;
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|id
operator|=
literal|null
expr_stmt|;
name|extension_element_prefixes
operator|=
literal|null
expr_stmt|;
name|exclude_result_prefixes
operator|=
literal|null
expr_stmt|;
name|xpath_default_namespace
operator|=
literal|null
expr_stmt|;
name|default_validation
operator|=
name|Validation
operator|.
name|STRIP
expr_stmt|;
name|default_collation
operator|=
literal|null
expr_stmt|;
name|input_type_annotations
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr
operator|instanceof
name|NamespaceNodeAtExist
condition|)
block|{
name|NamespaceNodeAtExist
name|namespace
init|=
operator|(
name|NamespaceNodeAtExist
operator|)
name|attr
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|context
operator|.
name|setDefaultElementNamespace
argument_list|(
name|namespace
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|attr_name
argument_list|,
name|namespace
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|ID
argument_list|)
condition|)
block|{
name|id
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|EXTENSION_ELEMENT_PREFIXES
argument_list|)
condition|)
block|{
name|extension_element_prefixes
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|EXCLUDE_RESULT_PREFIXES
argument_list|)
condition|)
block|{
name|exclude_result_prefixes
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|VERSION
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Double
operator|.
name|valueOf
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|version
argument_list|)
condition|)
block|{
block|}
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|XPATH_DEFAULT_NAMESPACE
argument_list|)
condition|)
block|{
name|xpath_default_namespace
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DEFAULT_VALIDATION
argument_list|)
condition|)
block|{
comment|//XXX: fix -> default_validation = attr.getValue();
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DEFAULT_COLLATION
argument_list|)
condition|)
block|{
name|default_collation
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|INPUT_TYPE_ANNOTATIONS
argument_list|)
condition|)
block|{
name|input_type_annotations
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Templates#getOutputProperties() 	 */
specifier|public
name|Properties
name|getOutputProperties
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: getOutputProperties() at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Templates#newTransformer() 	 */
specifier|public
name|Transformer
name|newTransformer
parameter_list|()
throws|throws
name|TransformerConfigurationException
block|{
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|Transformer
name|transformer
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|Transformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setPreparedStylesheet
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|transformer
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: dump(Writer writer) at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|XACMLSource
name|getSource
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: getSource() at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: isValid() at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: reset() at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|void
name|setSource
parameter_list|(
name|XACMLSource
name|source
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: setSource(XACMLSource source) at "
operator|+
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
for|for
control|(
name|Expression
name|expr
range|:
name|steps
control|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|Template
condition|)
block|{
name|Template
name|template
init|=
operator|(
name|Template
operator|)
name|expr
decl_stmt|;
if|if
condition|(
name|template
operator|.
name|isRootMatch
argument_list|()
condition|)
block|{
if|if
condition|(
name|rootTemplate
operator|!=
literal|null
condition|)
name|compileError
argument_list|(
literal|"double root match"
argument_list|)
expr_stmt|;
comment|//XXX: put error code
name|rootTemplate
operator|=
name|template
expr_stmt|;
if|if
condition|(
name|template
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
name|namedTemplates
operator|.
name|put
argument_list|(
name|template
operator|.
name|getName
argument_list|()
argument_list|,
name|template
argument_list|)
expr_stmt|;
comment|//UNDERSTAND: check doubles?
block|}
if|else if
condition|(
name|template
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|templates
operator|.
name|add
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|namedTemplates
operator|.
name|put
argument_list|(
name|template
operator|.
name|getName
argument_list|()
argument_list|,
name|template
argument_list|)
expr_stmt|;
comment|//UNDERSTAND: check doubles?
block|}
block|}
if|else if
condition|(
name|expr
operator|instanceof
name|AttributeSet
condition|)
block|{
name|AttributeSet
name|attributeSet
init|=
operator|(
name|AttributeSet
operator|)
name|expr
decl_stmt|;
name|attributeSets
operator|.
name|put
argument_list|(
name|attributeSet
operator|.
name|getName
argument_list|()
argument_list|,
name|attributeSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|simplified
condition|)
name|result
operator|=
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
else|else
name|result
operator|=
name|templates
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|Sequence
name|attributeSet
parameter_list|(
name|String
name|name
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|String
index|[]
name|names
init|=
name|name
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
name|n
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|=
name|names
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|attributeSets
operator|.
name|containsKey
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|attributeSets
operator|.
name|get
argument_list|(
name|n
argument_list|)
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//UNDERSTAND: error???
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Sequence
name|templates
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Sequence
name|currentSequence
init|=
name|contextSequence
decl_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|currentSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|int
name|pos
init|=
name|context
operator|.
name|getContextPosition
argument_list|()
decl_stmt|;
comment|//		for (Item item : currentSequence) {
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|currentSequence
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//UNDERSTAND: work around
if|if
condition|(
name|item
operator|instanceof
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
condition|)
block|{
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|document
init|=
operator|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
operator|)
name|item
decl_stmt|;
name|item
operator|=
operator|(
name|Item
operator|)
name|document
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setContextPosition
argument_list|(
name|pos
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|contextItem
operator|==
literal|null
operator|)
operator|&&
operator|(
name|rootTemplate
operator|!=
literal|null
operator|)
condition|)
block|{
name|context
operator|.
name|setContextPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Sequence
name|res
init|=
name|rootTemplate
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|matched
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|Template
name|template
range|:
name|templates
control|)
block|{
if|if
condition|(
name|template
operator|.
name|matched
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
condition|)
block|{
comment|//contextSequence
name|matched
operator|=
literal|true
expr_stmt|;
name|Sequence
name|res
init|=
name|template
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|res
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
break|break;
block|}
block|}
comment|//XXX: performance !?! how to get subelements sequence?? fast...
comment|//			if (!matched) {
comment|//				if (item instanceof ElementAtExist) {
comment|//					ElementAtExist element = (ElementAtExist) item;
comment|//
comment|//					NodeList children = element.getChildNodes();
comment|//					for (int i=0; i<children.getLength(); i++) {
comment|//						NodeAtExist child = (NodeAtExist)children.item(i);
comment|//
comment|//						if (child instanceof Text) {
comment|//		                    MemTreeBuilder builder = context.getDocumentBuilder();
comment|//		            		builder.characters(item.getStringValue());
comment|//		            		result.add(item);
comment|//						} else {
comment|//							Sequence res = templates((Sequence)element, (Item)child);
comment|//							if (res != null) {
comment|//								result.addAll(res);
comment|//								matched = true;
comment|//							}
comment|//						}
comment|//					}
comment|//				}
comment|//			}
name|pos
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|matched
condition|)
return|return
name|result
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|Sequence
name|template
parameter_list|(
name|QName
name|name
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|namedTemplates
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"no template with given name = "
operator|+
name|name
argument_list|)
throw|;
comment|//TODO: error?
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Sequence
name|currentSequence
init|=
name|contextSequence
decl_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|currentSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|int
name|pos
init|=
name|context
operator|.
name|getContextPosition
argument_list|()
decl_stmt|;
name|Template
name|template
init|=
name|namedTemplates
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|currentSequence
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//UNDERSTAND: work around
if|if
condition|(
name|item
operator|instanceof
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
condition|)
block|{
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|document
init|=
operator|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
operator|)
name|item
decl_stmt|;
name|item
operator|=
operator|(
name|Item
operator|)
name|document
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|setContextPosition
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|Sequence
name|res
init|=
name|template
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|res
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
break|break;
name|pos
operator|++
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|getXSLParams
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|params
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xslt.expression.i.Parameted#addXSLParam(org.exist.xslt.expression.Param) 	 */
specifier|public
name|void
name|addXSLParam
parameter_list|(
name|Param
name|param
parameter_list|)
throws|throws
name|XPathException
block|{
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|params
init|=
name|getXSLParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
name|compileError
argument_list|(
name|XSLExceptions
operator|.
name|ERR_XTSE0580
argument_list|)
expr_stmt|;
name|Variable
name|variable
init|=
name|context
operator|.
name|declareVariable
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|,
name|param
argument_list|)
decl_stmt|;
comment|//UNDERSTAND: global
name|params
operator|.
name|put
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|,
name|variable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTransformer
parameter_list|(
name|Transformer
name|transformer
parameter_list|)
block|{
name|this
operator|.
name|transformer
operator|=
name|transformer
expr_stmt|;
block|}
specifier|public
name|Transformer
name|getTransformer
parameter_list|()
block|{
return|return
name|transformer
return|;
block|}
block|}
end_class

end_unit

