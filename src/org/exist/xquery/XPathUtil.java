begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|util
operator|.
name|Iterator
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
name|NodeProxy
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
name|DOMStreamerPool
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
name|BooleanValue
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
name|DoubleValue
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
name|FloatValue
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
name|IntegerValue
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
name|JavaObjectValue
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
name|StringValue
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

begin_class
specifier|public
class|class
name|XPathUtil
block|{
comment|/**      * Convert Java object to an XQuery sequence. Objects of type Sequence      * are directly returned, other objects are converted into the corresponding      * internal types.      *       * @param obj      * @return      * @throws XPathException      */
specifier|public
specifier|final
specifier|static
name|Sequence
name|javaObjectToXPath
parameter_list|(
name|Object
name|obj
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Sequence
condition|)
return|return
operator|(
name|Sequence
operator|)
name|obj
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|String
condition|)
return|return
operator|new
name|StringValue
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Boolean
condition|)
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
operator|.
name|booleanValue
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Float
condition|)
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
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Double
condition|)
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
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Short
condition|)
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
operator|.
name|shortValue
argument_list|()
argument_list|,
name|Type
operator|.
name|SHORT
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Integer
condition|)
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
operator|.
name|intValue
argument_list|()
argument_list|,
name|Type
operator|.
name|INT
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|Long
condition|)
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
operator|.
name|longValue
argument_list|()
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
return|;
if|else if
condition|(
name|obj
operator|instanceof
name|List
condition|)
block|{
name|boolean
name|createNodeSequence
init|=
literal|true
decl_stmt|;
name|Object
name|next
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|List
operator|)
name|obj
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|next
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|next
operator|instanceof
name|NodeProxy
operator|)
condition|)
name|createNodeSequence
operator|=
literal|false
expr_stmt|;
block|}
name|Sequence
name|seq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|createNodeSequence
condition|)
name|seq
operator|=
operator|new
name|AVLTreeNodeSet
argument_list|()
expr_stmt|;
else|else
name|seq
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|List
operator|)
name|obj
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
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
name|i
operator|.
name|next
argument_list|()
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
name|DOMStreamer
name|streamer
init|=
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowDOMStreamer
argument_list|()
decl_stmt|;
try|try
block|{
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
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
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
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnDOMStreamer
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
name|Node
condition|)
block|{
name|DOMStreamer
name|streamer
init|=
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowDOMStreamer
argument_list|()
decl_stmt|;
try|try
block|{
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
name|DOMStreamerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnDOMStreamer
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|array
index|[
name|i
index|]
operator|instanceof
name|NodeProxy
operator|)
condition|)
name|createNodeSequence
operator|=
literal|false
expr_stmt|;
block|}
name|Sequence
name|seq
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|createNodeSequence
condition|)
name|seq
operator|=
operator|new
name|AVLTreeNodeSet
argument_list|()
expr_stmt|;
else|else
name|seq
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|length
condition|;
name|i
operator|++
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
name|array
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
else|else
return|return
operator|new
name|JavaObjectValue
argument_list|(
name|obj
argument_list|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|int
name|javaClassToXPath
parameter_list|(
name|Class
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
return|return
name|Type
operator|.
name|STRING
return|;
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
return|return
name|Type
operator|.
name|BOOLEAN
return|;
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
return|return
name|Type
operator|.
name|INTEGER
return|;
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
return|return
name|Type
operator|.
name|DOUBLE
return|;
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
return|return
name|Type
operator|.
name|FLOAT
return|;
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
return|return
name|Type
operator|.
name|NODE
return|;
else|else
return|return
name|Type
operator|.
name|JAVA_OBJECT
return|;
block|}
block|}
end_class

end_unit

