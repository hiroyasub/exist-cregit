begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|transform
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|URIResolver
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
name|dom
operator|.
name|DOMSource
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
name|sax
operator|.
name|SAXResult
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
name|sax
operator|.
name|SAXTransformerFactory
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
name|sax
operator|.
name|TemplatesHandler
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
name|sax
operator|.
name|TransformerHandler
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
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|DocumentImpl
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
name|NodeProxy
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
name|memtree
operator|.
name|DocumentBuilderReceiver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|Permission
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
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|Serializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Lock
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|NodeValue
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
name|SequenceType
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
name|Type
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Transform
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"transform"
argument_list|,
name|TransformModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TransformModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Applies an XSL stylesheet to the node tree passed as first argument. The stylesheet "
operator|+
literal|"is specified in the second argument. This should either be an URI or a node. "
operator|+
literal|"Stylesheet parameters "
operator|+
literal|"may be passed in the third argument using an XML fragment with the following structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/>"
operator|+
literal|"</parameters>"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Map
name|cache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|Transform
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|Item
name|inputNode
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Item
name|stylesheetItem
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Node
name|options
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
name|options
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
decl_stmt|;
try|try
block|{
name|Templates
name|templates
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|stylesheetItem
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|NodeValue
name|stylesheetNode
init|=
operator|(
name|NodeValue
operator|)
name|stylesheetItem
decl_stmt|;
name|templates
operator|=
name|getSource
argument_list|(
name|factory
argument_list|,
name|stylesheetNode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|stylesheet
init|=
name|stylesheetItem
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|templates
operator|=
name|getSource
argument_list|(
name|factory
argument_list|,
name|stylesheet
argument_list|)
expr_stmt|;
block|}
name|handler
operator|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|templates
argument_list|)
expr_stmt|;
if|if
condition|(
name|options
operator|!=
literal|null
condition|)
name|parseParameters
argument_list|(
name|options
argument_list|,
name|handler
operator|.
name|getTransformer
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to set up transformer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|SAXResult
name|result
init|=
operator|new
name|SAXResult
argument_list|(
name|receiver
argument_list|)
decl_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|inputNode
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"SAX exception while transforming node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Node
name|next
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|seq
operator|.
name|add
argument_list|(
operator|(
name|NodeValue
operator|)
name|next
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
return|return
name|seq
return|;
block|}
specifier|private
name|void
name|parseParameters
parameter_list|(
name|Node
name|options
parameter_list|,
name|Transformer
name|handler
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|options
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|options
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"parameters"
argument_list|)
condition|)
block|{
name|Node
name|child
init|=
name|options
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"param"
argument_list|)
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|String
name|name
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Name or value attribute missing for stylesheet parameter"
argument_list|)
throw|;
name|handler
operator|.
name|setParameter
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Templates
name|getSource
parameter_list|(
name|SAXTransformerFactory
name|factory
parameter_list|,
name|String
name|stylesheet
parameter_list|)
throws|throws
name|XPathException
throws|,
name|TransformerConfigurationException
block|{
name|String
name|base
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|<
literal|0
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|canRead
argument_list|()
condition|)
name|stylesheet
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
else|else
block|{
name|stylesheet
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|stylesheet
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|stylesheet
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|canRead
argument_list|()
condition|)
name|stylesheet
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
block|}
block|}
name|int
name|p
init|=
name|stylesheet
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
name|base
operator|=
name|stylesheet
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
else|else
name|base
operator|=
name|stylesheet
expr_stmt|;
name|CachedStylesheet
name|cached
init|=
operator|(
name|CachedStylesheet
operator|)
name|cache
operator|.
name|get
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|cached
operator|==
literal|null
condition|)
block|{
name|cached
operator|=
operator|new
name|CachedStylesheet
argument_list|(
name|factory
argument_list|,
name|stylesheet
argument_list|,
name|base
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|stylesheet
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
return|return
name|cached
operator|.
name|getTemplates
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Malformed URL for stylesheet: "
operator|+
name|stylesheet
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"IO error while loading stylesheet: "
operator|+
name|stylesheet
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Templates
name|getSource
parameter_list|(
name|SAXTransformerFactory
name|factory
parameter_list|,
name|NodeValue
name|stylesheetRoot
parameter_list|)
throws|throws
name|XPathException
throws|,
name|TransformerConfigurationException
block|{
if|if
condition|(
name|stylesheetRoot
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|factory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|DatabaseResolver
argument_list|(
operator|(
operator|(
name|NodeProxy
operator|)
name|stylesheetRoot
operator|)
operator|.
name|getDocument
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TemplatesHandler
name|handler
init|=
name|factory
operator|.
name|newTemplatesHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|stylesheetRoot
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|handler
operator|.
name|getTemplates
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"A SAX exception occurred while compiling the stylesheet: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Templates
name|getSource
parameter_list|(
name|SAXTransformerFactory
name|factory
parameter_list|,
name|DocumentImpl
name|stylesheet
parameter_list|)
throws|throws
name|XPathException
throws|,
name|TransformerConfigurationException
block|{
name|factory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|DatabaseResolver
argument_list|(
name|stylesheet
argument_list|)
argument_list|)
expr_stmt|;
name|TemplatesHandler
name|handler
init|=
name|factory
operator|.
name|newTemplatesHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|stylesheet
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|handler
operator|.
name|getTemplates
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"A SAX exception occurred while compiling the stylesheet: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|CachedStylesheet
block|{
name|SAXTransformerFactory
name|factory
decl_stmt|;
name|long
name|lastModified
init|=
operator|-
literal|1
decl_stmt|;
name|Templates
name|templates
init|=
literal|null
decl_stmt|;
name|String
name|uri
decl_stmt|;
specifier|public
name|CachedStylesheet
parameter_list|(
name|SAXTransformerFactory
name|factory
parameter_list|,
name|String
name|uri
parameter_list|,
name|String
name|baseURI
parameter_list|)
throws|throws
name|TransformerConfigurationException
throws|,
name|IOException
throws|,
name|XPathException
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
if|if
condition|(
operator|!
name|baseURI
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://"
argument_list|)
condition|)
name|factory
operator|.
name|setURIResolver
argument_list|(
operator|new
name|ExternalResolver
argument_list|(
name|baseURI
argument_list|)
argument_list|)
expr_stmt|;
name|getTemplates
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Templates
name|getTemplates
parameter_list|()
throws|throws
name|TransformerConfigurationException
throws|,
name|IOException
throws|,
name|XPathException
block|{
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist://"
argument_list|)
condition|)
block|{
name|String
name|docPath
init|=
name|uri
operator|.
name|substring
argument_list|(
literal|"xmldb:exist://"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|openDocument
argument_list|(
name|docPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
operator|&&
operator|(
name|templates
operator|==
literal|null
operator|||
name|doc
operator|.
name|getLastModified
argument_list|()
operator|>
name|lastModified
operator|)
condition|)
name|templates
operator|=
name|getSource
argument_list|(
name|factory
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|lastModified
operator|=
name|doc
operator|.
name|getLastModified
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Permission denied to read stylesheet: "
operator|+
name|uri
argument_list|)
throw|;
block|}
finally|finally
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|long
name|modified
init|=
name|connection
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|templates
operator|==
literal|null
operator|||
name|modified
operator|>
name|lastModified
operator|||
name|modified
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"compiling stylesheet "
operator|+
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|templates
operator|=
name|factory
operator|.
name|newTemplates
argument_list|(
operator|new
name|StreamSource
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lastModified
operator|=
name|modified
expr_stmt|;
block|}
return|return
name|templates
return|;
block|}
block|}
specifier|private
class|class
name|ExternalResolver
implements|implements
name|URIResolver
block|{
specifier|private
name|String
name|baseURI
decl_stmt|;
specifier|public
name|ExternalResolver
parameter_list|(
name|String
name|base
parameter_list|)
block|{
name|this
operator|.
name|baseURI
operator|=
name|base
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String) 		 */
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
name|URL
name|url
decl_stmt|;
try|try
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|baseURI
operator|+
literal|'/'
operator|+
name|href
argument_list|)
expr_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
return|return
operator|new
name|StreamSource
argument_list|(
name|connection
operator|.
name|getInputStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
specifier|private
class|class
name|DatabaseResolver
implements|implements
name|URIResolver
block|{
name|DocumentImpl
name|doc
decl_stmt|;
specifier|public
name|DatabaseResolver
parameter_list|(
name|DocumentImpl
name|myDoc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|myDoc
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String) 		 */
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
name|Collection
name|collection
init|=
name|doc
operator|.
name|getCollection
argument_list|()
decl_stmt|;
name|String
name|path
decl_stmt|;
if|if
condition|(
name|href
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
name|path
operator|=
name|href
expr_stmt|;
else|else
name|path
operator|=
name|collection
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|href
expr_stmt|;
name|DocumentImpl
name|xslDoc
decl_stmt|;
try|try
block|{
name|xslDoc
operator|=
operator|(
name|DocumentImpl
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getDocument
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|xslDoc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Insufficient privileges to read resource "
operator|+
name|path
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|xslDoc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document "
operator|+
name|href
operator|+
literal|" not found in collection "
operator|+
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|xslDoc
argument_list|)
decl_stmt|;
return|return
name|source
return|;
block|}
block|}
block|}
end_class

end_unit

