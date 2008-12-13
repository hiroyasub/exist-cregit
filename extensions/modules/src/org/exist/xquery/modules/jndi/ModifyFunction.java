begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension GetConnectionFunction  *  Copyright (C) 2008 Adam Retter<adam@exist-db.org>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: GetConnectionFunction.java 4126 2006-09-18 21:20:17 +0000 (Mon, 18 Sep 2006) deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jndi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|BasicAttribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|ModificationItem
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * eXist JNDI Module Extension ModifyFunction  *   * Modify a JNDI Directory entry  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 2008-12-02  * @version 1.0  *   * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext,  *      org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|ModifyFunction
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DSML_NAMESPACE
init|=
literal|"http://www.dsml.org/DSML"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DSML_PREFIX
init|=
literal|"dsml"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"modify"
argument_list|,
name|JNDIModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|JNDIModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Modify a JNDI Directory entry. $a is the directory context handle from a jndi:get-dir-context() call. $b is the DN. Expects "
operator|+
literal|" entry attributes to be set in $c in the"
operator|+
literal|" form<attributes><attribute name=\"\" value=\"\" operation=\"add | replace | remove\"/></attributes>. "
operator|+
literal|" You can also optionally specify ordered=\"true\" for an attribute."
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
name|INTEGER
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
name|ELEMENT
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
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * ModifyFunction Constructor 	 *  	 * @param context 	The Context of the calling XQuery 	 */
specifier|public
name|ModifyFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
comment|/** 	 * evaluate the call to the xquery modify() function, it is really 	 * the main entry point of this class 	 *  	 * @param args				arguments from the get-connection() function call 	 * @param contextSequence 	the Context Sequence to operate on (not used here internally!) 	 * @return 					A xs:long representing a handle to the connection 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
comment|// Was context handle or DN specified?
if|if
condition|(
operator|!
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|!
operator|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|String
name|dn
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|long
name|ctxID
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|DirContext
name|ctx
init|=
operator|(
name|DirContext
operator|)
name|JNDIModule
operator|.
name|retrieveJNDIContext
argument_list|(
name|context
argument_list|,
name|ctxID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ctx
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"jndi:modify() - Invalid JNDI context handle provided: "
operator|+
name|ctxID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ModificationItem
index|[]
name|items
init|=
name|parseAttributes
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|items
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|ctx
operator|.
name|modifyAttributes
argument_list|(
name|dn
argument_list|,
name|items
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ne
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"jndi:modify() Modify failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
operator|+
name|ne
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"jndi:modify() Modify failed for dn ["
operator|+
name|dn
operator|+
literal|"]: "
operator|+
name|ne
argument_list|)
operator|)
throw|;
block|}
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
comment|/** 	 * Parses attributes into a JNDI ModificationItem array 	 *  	 * @param arg				The attributes as a sequence of nodes 	 * @return 					The array of ModificationItems 	 */
specifier|private
name|ModificationItem
index|[]
name|parseAttributes
parameter_list|(
name|Sequence
name|arg
parameter_list|)
throws|throws
name|XPathException
block|{
name|ArrayList
argument_list|<
name|ModificationItem
argument_list|>
name|items
init|=
operator|new
name|ArrayList
argument_list|<
name|ModificationItem
argument_list|>
argument_list|()
decl_stmt|;
name|ModificationItem
index|[]
name|mi
init|=
operator|new
name|ModificationItem
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|arg
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
name|Node
name|container
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
operator|&&
name|container
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|NodeList
name|attrs
init|=
operator|(
operator|(
name|Element
operator|)
name|container
operator|)
operator|.
name|getElementsByTagName
argument_list|(
literal|"attribute"
argument_list|)
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|attr
init|=
operator|(
operator|(
name|Element
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
decl_stmt|;
name|String
name|name
init|=
name|attr
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|attr
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|String
name|op
init|=
name|attr
operator|.
name|getAttribute
argument_list|(
literal|"operation"
argument_list|)
decl_stmt|;
name|String
name|ordered
init|=
name|attr
operator|.
name|getAttribute
argument_list|(
literal|"ordered"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|value
operator|!=
literal|null
operator|&&
name|op
operator|!=
literal|null
condition|)
block|{
name|int
name|opCode
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"add"
argument_list|)
condition|)
block|{
name|opCode
operator|=
literal|1
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"replace"
argument_list|)
condition|)
block|{
name|opCode
operator|=
literal|2
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"remove"
argument_list|)
condition|)
block|{
name|opCode
operator|=
literal|3
expr_stmt|;
block|}
if|if
condition|(
name|opCode
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"jndi:modify() - Invalid operation code: ["
operator|+
name|op
operator|+
literal|"]"
argument_list|)
expr_stmt|;
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"jndi:modify() - Invalid operation code: ["
operator|+
name|op
operator|+
literal|"]"
argument_list|)
operator|)
throw|;
block|}
name|Attribute
name|existingAttr
init|=
literal|null
decl_stmt|;
comment|// Scan the existing list of ModificationItems backwards for one that matches the name we're trying to add.
comment|// If the last such entry matches the opCode, then just add the value to the existing attribute (ModItem),
comment|// Otherwise create a new ModificationItem.
comment|//
comment|// This basically collapses nearby identically named attributes that have the same opCode into one, except for removes
for|for
control|(
name|int
name|j
init|=
name|items
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|j
operator|>=
literal|0
condition|;
name|j
operator|--
control|)
block|{
name|ModificationItem
name|item
init|=
operator|(
name|ModificationItem
operator|)
name|items
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|item
operator|.
name|getAttribute
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|item
operator|.
name|getModificationOp
argument_list|()
operator|==
name|opCode
operator|&&
name|opCode
operator|!=
literal|3
condition|)
block|{
name|existingAttr
operator|=
name|item
operator|.
name|getAttribute
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|existingAttr
operator|!=
literal|null
condition|)
block|{
name|existingAttr
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|items
operator|.
name|add
argument_list|(
operator|new
name|ModificationItem
argument_list|(
name|opCode
argument_list|,
operator|new
name|BasicAttribute
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|ordered
operator|!=
literal|null
operator|&&
name|ordered
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Name, value or operation attribute missing for attribute"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|(
name|items
operator|.
name|toArray
argument_list|(
name|mi
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

