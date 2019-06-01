begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|persistent
operator|.
name|AVLTreeNodeSet
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
name|persistent
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
name|persistent
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
name|dom
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
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|DBBroker
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|serializer
operator|.
name|DOMStreamer
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
name|serializer
operator|.
name|SerializerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|LocalXMLResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|RemoteXMLResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|*
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
name|Document
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_class
specifier|public
class|class
name|XPathUtil
block|{
comment|/**      * Convert Java object to an XQuery sequence. Objects of type Sequence are      * directly returned, other objects are converted into the corresponding      * internal types.      *      * @param obj The java object      * @param context XQuery context      * @return XQuery sequence      * @throws XPathException      */
specifier|public
specifier|final
specifier|static
name|Sequence
name|javaObjectToXPath
parameter_list|(
name|Object
name|obj
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|javaObjectToXPath
argument_list|(
name|obj
argument_list|,
name|context
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|Sequence
name|javaObjectToXPath
parameter_list|(
name|Object
name|obj
parameter_list|,
name|XQueryContext
name|context
parameter_list|,
name|boolean
name|expandChars
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
comment|//return Sequence.EMPTY_SEQUENCE;
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Sequence
condition|)
block|{
return|return
operator|(
name|Sequence
operator|)
name|obj
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|String
condition|)
block|{
specifier|final
name|StringValue
name|v
init|=
operator|new
name|StringValue
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|)
decl_stmt|;
return|return
operator|(
name|expandChars
condition|?
name|v
operator|.
name|expand
argument_list|()
else|:
name|v
operator|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Boolean
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|obj
operator|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Float
condition|)
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
operator|(
name|Float
operator|)
name|obj
operator|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Double
condition|)
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
operator|(
operator|(
name|Double
operator|)
name|obj
operator|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Short
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|Short
operator|)
name|obj
operator|)
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Integer
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|obj
operator|)
argument_list|,
name|Type
operator|.
name|INT
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Long
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|Long
operator|)
name|obj
operator|)
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|byte
index|[]
condition|)
block|{
return|return
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|new
name|FastByteArrayInputStream
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|obj
argument_list|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|ResourceSet
condition|)
block|{
specifier|final
name|Sequence
name|seq
init|=
operator|new
name|AVLTreeNodeSet
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ResourceIterator
name|it
init|=
operator|(
operator|(
name|ResourceSet
operator|)
name|obj
operator|)
operator|.
name|getIterator
argument_list|()
init|;
name|it
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
name|getNode
argument_list|(
name|broker
argument_list|,
operator|(
name|XMLResource
operator|)
name|it
operator|.
name|nextResource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to convert ResourceSet to node: "
operator|+
name|xe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|seq
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|XMLResource
condition|)
block|{
return|return
name|getNode
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
operator|(
name|XMLResource
operator|)
name|obj
argument_list|)
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Node
condition|)
block|{
specifier|final
name|DOMStreamer
name|streamer
init|=
operator|(
name|DOMStreamer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|DOMStreamer
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|serialize
argument_list|(
operator|(
name|Node
operator|)
name|obj
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Document
condition|)
block|{
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to transform node into internal model: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|streamer
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|List
argument_list|<
name|?
argument_list|>
condition|)
block|{
name|boolean
name|createNodeSequence
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Object
name|next
range|:
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|obj
operator|)
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|next
operator|instanceof
name|NodeProxy
operator|)
condition|)
block|{
name|createNodeSequence
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|Sequence
name|seq
init|=
name|createNodeSequence
condition|?
operator|new
name|AVLTreeNodeSet
argument_list|()
else|:
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|obj
operator|)
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
operator|(
name|Item
operator|)
name|javaObjectToXPath
argument_list|(
name|o
argument_list|,
name|context
argument_list|,
name|expandChars
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|NodeList
condition|)
block|{
specifier|final
name|DOMStreamer
name|streamer
init|=
operator|(
name|DOMStreamer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|DOMStreamer
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
specifier|final
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|nl
init|=
operator|(
name|NodeList
operator|)
name|obj
decl_stmt|;
name|int
name|last
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|n
init|=
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|streamer
operator|.
name|serialize
argument_list|(
name|n
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|NodeImpl
name|created
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|last
operator|+
literal|1
argument_list|)
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|created
argument_list|)
expr_stmt|;
name|last
operator|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getLastNode
argument_list|()
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to transform node into internal model: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|streamer
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|boolean
name|createNodeSequence
init|=
literal|true
decl_stmt|;
specifier|final
name|Object
index|[]
name|array
init|=
operator|(
name|Object
index|[]
operator|)
name|obj
decl_stmt|;
for|for
control|(
name|Object
name|arrayItem
range|:
name|array
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|arrayItem
operator|instanceof
name|NodeProxy
operator|)
condition|)
block|{
name|createNodeSequence
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|Sequence
name|seq
init|=
name|createNodeSequence
condition|?
operator|new
name|AVLTreeNodeSet
argument_list|()
else|:
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|arrayItem
range|:
name|array
control|)
block|{
name|seq
operator|.
name|add
argument_list|(
operator|(
name|Item
operator|)
name|javaObjectToXPath
argument_list|(
name|arrayItem
argument_list|,
name|context
argument_list|,
name|expandChars
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
else|else
block|{
return|return
operator|new
name|JavaObjectValue
argument_list|(
name|obj
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|int
name|javaClassToXPath
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
if|else if
condition|(
name|clazz
operator|==
name|Boolean
operator|.
name|class
operator|||
name|clazz
operator|==
name|boolean
operator|.
name|class
condition|)
block|{
return|return
name|Type
operator|.
name|BOOLEAN
return|;
block|}
if|else if
condition|(
name|clazz
operator|==
name|Integer
operator|.
name|class
operator|||
name|clazz
operator|==
name|int
operator|.
name|class
operator|||
name|clazz
operator|==
name|Long
operator|.
name|class
operator|||
name|clazz
operator|==
name|long
operator|.
name|class
operator|||
name|clazz
operator|==
name|Short
operator|.
name|class
operator|||
name|clazz
operator|==
name|short
operator|.
name|class
operator|||
name|clazz
operator|==
name|Byte
operator|.
name|class
operator|||
name|clazz
operator|==
name|byte
operator|.
name|class
condition|)
block|{
return|return
name|Type
operator|.
name|INTEGER
return|;
block|}
if|else if
condition|(
name|clazz
operator|==
name|Double
operator|.
name|class
operator|||
name|clazz
operator|==
name|double
operator|.
name|class
condition|)
block|{
return|return
name|Type
operator|.
name|DOUBLE
return|;
block|}
if|else if
condition|(
name|clazz
operator|==
name|Float
operator|.
name|class
operator|||
name|clazz
operator|==
name|float
operator|.
name|class
condition|)
block|{
return|return
name|Type
operator|.
name|FLOAT
return|;
block|}
if|else if
condition|(
name|clazz
operator|.
name|isAssignableFrom
argument_list|(
name|Node
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|JAVA_OBJECT
return|;
block|}
block|}
comment|/**      * Converts an XMLResource into a NodeProxy.      *      * @param broker The DBBroker to use to access the database      * @param xres The XMLResource to convert      * @return A NodeProxy for accessing the content represented by xres      * @throws XPathException if an XMLDBException is encountered      */
specifier|public
specifier|static
specifier|final
name|NodeProxy
name|getNode
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XMLResource
name|xres
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|xres
operator|instanceof
name|LocalXMLResource
condition|)
block|{
specifier|final
name|LocalXMLResource
name|lres
init|=
operator|(
name|LocalXMLResource
operator|)
name|xres
decl_stmt|;
try|try
block|{
return|return
name|lres
operator|.
name|getNode
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to convert LocalXMLResource to node: "
operator|+
name|xe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|DocumentImpl
name|document
decl_stmt|;
try|try
block|{
name|document
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|xres
operator|.
name|getParentCollection
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|xres
operator|.
name|getDocumentId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|xe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to get document for RemoteXMLResource: "
operator|+
name|xe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Failed to get document: "
operator|+
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|NodeId
name|nodeId
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromString
argument_list|(
operator|(
operator|(
name|RemoteXMLResource
operator|)
name|xres
operator|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
name|nodeId
argument_list|)
return|;
block|}
block|}
end_class

end_unit
