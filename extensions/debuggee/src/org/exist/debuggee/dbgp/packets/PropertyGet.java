begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSession
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
name|*
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|PropertyGet
extends|extends
name|Command
block|{
comment|/** 	 * -d stack depth (optional, debugger engine should assume zero if not provided) 	 */
specifier|private
name|int
name|stackDepth
init|=
literal|0
decl_stmt|;
comment|/** 	 * -c context id (optional, retrieved by context-names, debugger engine should assume zero if not provided) 	 */
specifier|private
name|int
name|contextID
init|=
literal|0
decl_stmt|;
comment|/** 	 * -n property long name (required) 	 */
specifier|private
name|String
name|nameLong
decl_stmt|;
comment|/** 	 * -m max data size to retrieve (optional) 	 */
specifier|private
name|Integer
name|maxDataSize
init|=
literal|null
decl_stmt|;
comment|/** 	 * -p data page (property_get, property_value: optional for arrays, hashes, objects, etc.; property_set: not required; debugger engine should assume zero if not provided) 	 */
specifier|private
name|String
name|dataPage
init|=
literal|null
decl_stmt|;
comment|/** 	 * -k property key as retrieved in a property element, optional, used for property_get of children and property_value, required if it was provided by the debugger engine. 	 */
specifier|private
name|String
name|propertyKey
init|=
literal|null
decl_stmt|;
comment|/** 	 * -a property address as retrieved in a property element, optional, used for property_set/value 	 */
specifier|private
name|String
name|propertyAddress
init|=
literal|null
decl_stmt|;
specifier|private
name|Variable
name|variable
decl_stmt|;
specifier|public
name|PropertyGet
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setArgument
parameter_list|(
name|String
name|arg
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"d"
argument_list|)
condition|)
name|stackDepth
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"c"
argument_list|)
condition|)
name|contextID
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"n"
argument_list|)
condition|)
name|nameLong
operator|=
name|val
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"m"
argument_list|)
condition|)
name|maxDataSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
condition|)
name|dataPage
operator|=
name|val
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"k"
argument_list|)
condition|)
name|propertyKey
operator|=
name|val
expr_stmt|;
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"a"
argument_list|)
condition|)
name|propertyAddress
operator|=
name|val
expr_stmt|;
else|else
name|super
operator|.
name|setArgument
argument_list|(
name|arg
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
name|variable
operator|=
name|getJoint
argument_list|()
operator|.
name|getVariable
argument_list|(
name|nameLong
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
if|if
condition|(
name|variable
operator|==
literal|null
condition|)
return|return
name|errorBytes
argument_list|(
literal|"property_get"
argument_list|)
return|;
name|StringBuilder
name|responce
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|responce
operator|.
name|append
argument_list|(
name|xml_declaration
argument_list|)
expr_stmt|;
name|responce
operator|.
name|append
argument_list|(
literal|"<response "
operator|+
name|namespaces
operator|+
literal|"command=\"property_get\" transaction_id=\""
argument_list|)
expr_stmt|;
name|responce
operator|.
name|append
argument_list|(
name|transactionID
argument_list|)
expr_stmt|;
name|responce
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|responce
operator|.
name|append
argument_list|(
name|getPropertyString
argument_list|(
name|variable
argument_list|,
name|getJoint
argument_list|()
operator|.
name|getContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|responce
operator|.
name|append
argument_list|(
literal|"</response>"
argument_list|)
expr_stmt|;
return|return
name|responce
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|StringBuilder
name|getPropertyString
parameter_list|(
name|Variable
name|variable
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
block|{
name|Sequence
name|value
init|=
name|variable
operator|.
name|getValue
argument_list|()
decl_stmt|;
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
try|try
block|{
name|StringBuilder
name|property
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|String
name|strVal
init|=
name|getPropertyValue
argument_list|(
name|value
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|serializer
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|?
literal|"node"
else|:
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|)
decl_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"<property name=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|variable
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" fullname=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|variable
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" type=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" size=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|strVal
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" encoding=\"none\">"
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|strVal
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|property
operator|.
name|append
argument_list|(
literal|"<property name=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|variable
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" fullname=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|variable
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" type=\"array\" children=\"true\" numchildren=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|value
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|si
init|=
name|value
operator|.
name|iterate
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|Item
name|item
init|=
name|si
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|String
name|strVal
init|=
name|getPropertyValue
argument_list|(
name|item
argument_list|,
name|serializer
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|?
literal|"xs:string"
else|:
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|)
decl_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"<property name=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" type=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" size=\""
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|strVal
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"\" encoding=\"none\">"
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
name|strVal
argument_list|)
expr_stmt|;
name|property
operator|.
name|append
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
name|property
operator|.
name|append
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
block|}
return|return
name|property
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
specifier|static
name|String
name|getTypeString
parameter_list|(
name|Variable
name|variable
parameter_list|)
block|{
if|if
condition|(
operator|!
name|variable
operator|.
name|isInitialized
argument_list|()
condition|)
return|return
literal|"uninitialized"
return|;
return|return
name|Type
operator|.
name|getTypeName
argument_list|(
name|variable
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|String
name|getPropertyValue
parameter_list|(
name|Item
name|item
parameter_list|,
name|Serializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
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
return|return
literal|"<![CDATA["
operator|+
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|item
argument_list|)
operator|+
literal|"]]>"
return|;
block|}
else|else
block|{
return|return
name|item
operator|.
name|getStringValue
argument_list|()
return|;
block|}
block|}
specifier|public
name|byte
index|[]
name|commandBytes
parameter_list|()
block|{
name|String
name|command
init|=
literal|"property_get"
operator|+
literal|" -i "
operator|+
name|transactionID
operator|+
literal|" -n "
operator|+
name|nameLong
operator|+
literal|" -d "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|stackDepth
argument_list|)
operator|+
literal|" -c "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|contextID
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDataSize
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -m "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|maxDataSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataPage
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -p "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|dataPage
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyKey
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -k "
operator|+
name|propertyKey
expr_stmt|;
if|if
condition|(
name|propertyAddress
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -a "
operator|+
name|propertyAddress
expr_stmt|;
return|return
name|command
operator|.
name|getBytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

