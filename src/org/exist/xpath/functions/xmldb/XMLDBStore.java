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
name|xpath
operator|.
name|functions
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
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
name|xpath
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
name|xpath
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|StaticContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|Collection
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBStore
extends|extends
name|Function
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
literal|"store"
argument_list|,
name|XMLDB_FUNCTION_NS
argument_list|,
literal|"xmldb"
argument_list|)
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
name|JAVA_OBJECT
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
name|STRING
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
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|XMLDBStore
parameter_list|(
name|StaticContext
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
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
name|JavaObjectValue
name|obj
init|=
operator|(
name|JavaObjectValue
operator|)
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|docName
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Item
name|item
init|=
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|.
name|getObject
argument_list|()
operator|instanceof
name|Collection
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Argument 3 should be an instance of org.xmldb.api.base.Collection"
argument_list|)
throw|;
name|Collection
name|collection
init|=
operator|(
name|Collection
operator|)
name|obj
operator|.
name|getObject
argument_list|()
decl_stmt|;
try|try
block|{
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|docName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|resource
operator|.
name|setContentAsSAX
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|item
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
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XMLDB reported an exception while storing document"
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"SAX reported an exception while storing document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

