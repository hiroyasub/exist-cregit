begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *   *  Copyright (C) 2000, Wolfgang M. Meier (meier@ifs. tu- darmstadt. de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|ArraySet
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
name|NodeSet
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
name|BrokerPool
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
name|storage
operator|.
name|IndexPaths
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
name|analysis
operator|.
name|SimpleTokenizer
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
name|analysis
operator|.
name|TextToken
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
name|analysis
operator|.
name|Token
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
name|Configuration
import|;
end_import

begin_comment
comment|/**  *  compare two operands by =,<,> etc..  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    31. August 2002  */
end_comment

begin_class
specifier|public
class|class
name|OpEquals
extends|extends
name|BinaryOp
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|OpEquals
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|relation
init|=
name|Constants
operator|.
name|EQ
decl_stmt|;
specifier|protected
name|NodeSet
name|temp
init|=
literal|null
decl_stmt|;
comment|// in some cases, we use a fulltext expression to preselect nodes
specifier|protected
name|FunContains
name|containsExpr
init|=
literal|null
decl_stmt|;
comment|/** 	 *  Constructor for the OpEquals object 	 * 	 *@param  relation  Description of the Parameter 	 */
specifier|public
name|OpEquals
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
block|}
comment|/** 	 *  Constructor for the OpEquals object 	 * 	 *@param  left      Description of the Parameter 	 *@param  right     Description of the Parameter 	 *@param  relation  Description of the Parameter 	 */
specifier|public
name|OpEquals
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
if|if
condition|(
name|left
operator|instanceof
name|PathExpr
operator|&&
operator|(
operator|(
name|PathExpr
operator|)
name|left
operator|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|add
argument_list|(
operator|(
operator|(
name|PathExpr
operator|)
name|left
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
if|if
condition|(
name|right
operator|instanceof
name|PathExpr
operator|&&
operator|(
operator|(
name|PathExpr
operator|)
name|right
operator|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|add
argument_list|(
operator|(
operator|(
name|PathExpr
operator|)
name|right
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Left argument is boolean: Convert right argument to a bool. 	 * 	 *@param  left     Description of the Parameter 	 *@param  right    Description of the Parameter 	 *@param  docs     Description of the Parameter 	 *@param  context  Description of the Parameter 	 *@param  node     Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|protected
name|Value
name|booleanCompare
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|NodeProxy
name|n
decl_stmt|;
name|boolean
name|lvalue
decl_stmt|;
name|boolean
name|rvalue
decl_stmt|;
name|ArraySet
name|set
decl_stmt|;
name|DocumentSet
name|dset
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|set
operator|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|dset
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
name|dset
operator|.
name|add
argument_list|(
name|n
operator|.
name|doc
argument_list|)
expr_stmt|;
name|rvalue
operator|=
name|left
operator|.
name|eval
argument_list|(
name|dset
argument_list|,
name|set
argument_list|,
name|n
argument_list|)
operator|.
name|getBooleanValue
argument_list|()
expr_stmt|;
name|lvalue
operator|=
name|right
operator|.
name|eval
argument_list|(
name|dset
argument_list|,
name|set
argument_list|,
name|n
argument_list|)
operator|.
name|getBooleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|lvalue
operator|==
name|rvalue
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  left   Description of the Parameter 	 *@param  right  Description of the Parameter 	 *@return        Description of the Return Value 	 */
specifier|protected
name|boolean
name|cmpBooleans
parameter_list|(
name|boolean
name|left
parameter_list|,
name|boolean
name|right
parameter_list|)
block|{
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
operator|(
name|left
operator|==
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
operator|(
name|left
operator|!=
name|right
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  left   Description of the Parameter 	 *@param  right  Description of the Parameter 	 *@return        Description of the Return Value 	 */
specifier|protected
name|boolean
name|cmpNumbers
parameter_list|(
name|double
name|left
parameter_list|,
name|double
name|right
parameter_list|)
block|{
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
operator|(
name|left
operator|==
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
operator|(
name|left
operator|!=
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
operator|(
name|left
operator|>
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
operator|(
name|left
operator|<
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
operator|(
name|left
operator|>=
name|right
operator|)
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
operator|(
name|left
operator|<=
name|right
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  left   Description of the Parameter 	 *@param  right  Description of the Parameter 	 *@return        Description of the Return Value 	 */
specifier|protected
name|boolean
name|compareStrings
parameter_list|(
name|String
name|left
parameter_list|,
name|String
name|right
parameter_list|)
block|{
name|int
name|cmp
init|=
name|left
operator|.
name|compareTo
argument_list|(
name|right
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
operator|(
name|cmp
operator|==
literal|0
operator|)
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
operator|(
name|cmp
operator|!=
literal|0
operator|)
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
operator|(
name|cmp
operator|>
literal|0
operator|)
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
operator|(
name|cmp
operator|<
literal|0
operator|)
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
operator|(
name|cmp
operator|>=
literal|0
operator|)
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
operator|(
name|cmp
operator|<=
literal|0
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 *  Compare left and right statement. Comparison is done like described in 	 *  the spec. If one argument returns a node set, we handle that first. 	 *  Otherwise if one argument is a number, process that. Third follows 	 *  string, boolean is last. If necessary move right to left and left to 	 *  right. 	 * 	 *@param  docs     Description of the Parameter 	 *@param  context  Description of the Parameter 	 *@param  node     Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|public
name|Value
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
if|if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
return|return
name|nodeSetCompare
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|getRight
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
if|else if
condition|(
name|getRight
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
block|{
name|switchOperands
argument_list|()
expr_stmt|;
return|return
name|nodeSetCompare
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|getLeft
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
block|}
if|else if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NUM
condition|)
return|return
name|numberCompare
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|getRight
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
if|else if
condition|(
name|getRight
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NUM
condition|)
return|return
name|numberCompare
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|getLeft
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
if|else if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_STRING
condition|)
return|return
name|stringCompare
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|getRight
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
if|else if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_BOOL
condition|)
return|return
name|booleanCompare
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|getRight
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
if|else if
condition|(
name|getRight
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_BOOL
condition|)
return|return
name|booleanCompare
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|getLeft
argument_list|()
argument_list|,
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
return|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"syntax error"
argument_list|)
throw|;
block|}
comment|/** 	 *  Left argument is a node set. If right arg is a string-literal, call 	 *  broker.getNodesEqualTo - which is fast. If it is a number, convert it. 	 *  If it is a boolean, get the part of context which matches the left 	 *  expression, get the right value for every node of context and compare it 	 *  with the left-part. 	 * 	 *@param  left     Description of the Parameter 	 *@param  right    Description of the Parameter 	 *@param  docs     Description of the Parameter 	 *@param  context  Description of the Parameter 	 *@param  node     Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|protected
name|Value
name|nodeSetCompare
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|NodeSet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|right
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_STRING
operator|||
name|right
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
block|{
comment|// evaluate left expression
name|NodeSet
name|nodes
init|=
operator|(
name|NodeSet
operator|)
name|left
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
name|String
name|cmp
init|=
name|right
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NODELIST
operator|&&
name|relation
operator|==
name|Constants
operator|.
name|EQ
operator|&&
name|nodes
operator|.
name|hasIndex
argument_list|()
operator|&&
name|cmp
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// try to use a fulltext search expression to reduce the number
comment|// of potential nodes to scan through
name|SimpleTokenizer
name|tokenizer
init|=
operator|new
name|SimpleTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setText
argument_list|(
name|cmp
argument_list|)
expr_stmt|;
name|TextToken
name|token
decl_stmt|;
name|String
name|term
decl_stmt|;
name|boolean
name|foundNumeric
init|=
literal|false
decl_stmt|;
name|cmp
operator|=
name|cmp
operator|.
name|replace
argument_list|(
literal|'%'
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
comment|// setup up an&= expression using the fulltext index
name|containsExpr
operator|=
operator|new
name|FunContains
argument_list|(
name|pool
argument_list|,
name|Constants
operator|.
name|FULLTEXT_AND
argument_list|)
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
literal|5
operator|&&
operator|(
name|token
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|true
argument_list|)
operator|)
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
comment|// remember if we find an alphanumeric token
if|if
condition|(
name|token
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|ALPHANUM
condition|)
name|foundNumeric
operator|=
literal|true
expr_stmt|;
name|containsExpr
operator|.
name|addTerm
argument_list|(
name|token
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check if all elements are indexed. If not, we can't use the
comment|// fulltext index.
if|if
condition|(
name|foundNumeric
condition|)
name|foundNumeric
operator|=
name|checkArgumentTypes
argument_list|(
name|docs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|foundNumeric
condition|)
block|{
comment|// all elements are indexed: use the fulltext index
name|Value
name|temp
init|=
name|containsExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|nodes
operator|=
operator|(
name|NodeSet
operator|)
name|temp
operator|.
name|getNodeList
argument_list|()
expr_stmt|;
block|}
name|cmp
operator|=
name|cmp
operator|.
name|replace
argument_list|(
literal|'*'
argument_list|,
literal|'%'
argument_list|)
expr_stmt|;
block|}
comment|// now compare the input node set to the search expression
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|result
operator|=
name|broker
operator|.
name|getNodesEqualTo
argument_list|(
name|nodes
argument_list|,
name|docs
argument_list|,
name|relation
argument_list|,
name|cmp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|right
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NUM
condition|)
block|{
name|double
name|rvalue
decl_stmt|;
name|double
name|lvalue
decl_stmt|;
name|NodeProxy
name|ln
decl_stmt|;
name|NodeSet
name|temp
decl_stmt|;
name|NodeSet
name|lset
init|=
operator|(
name|NodeSet
operator|)
name|left
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|lset
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
name|ln
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|lvalue
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|ln
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
continue|continue;
block|}
name|temp
operator|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|ln
argument_list|)
expr_stmt|;
name|rvalue
operator|=
name|right
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|temp
argument_list|,
name|ln
argument_list|)
operator|.
name|getNumericValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmpNumbers
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|ln
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|right
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_BOOL
condition|)
block|{
name|NodeProxy
name|n
decl_stmt|;
name|NodeProxy
name|parent
decl_stmt|;
name|boolean
name|rvalue
decl_stmt|;
name|boolean
name|lvalue
decl_stmt|;
name|long
name|pid
decl_stmt|;
name|ArraySet
name|leftNodeSet
decl_stmt|;
name|ArraySet
name|temp
decl_stmt|;
comment|// get left arguments node set
name|leftNodeSet
operator|=
operator|(
name|ArraySet
operator|)
name|left
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
expr_stmt|;
name|temp
operator|=
operator|new
name|ArraySet
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// get that part of context for which left argument's node set would
comment|// be> 0
for|for
control|(
name|Iterator
name|i
init|=
name|leftNodeSet
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|parent
operator|=
name|context
operator|.
name|parentWithChild
argument_list|(
name|n
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
name|temp
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|// now compare every node of context with the temporary set
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|lvalue
operator|=
name|temp
operator|.
name|contains
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|rvalue
operator|=
name|right
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
name|n
argument_list|)
operator|.
name|getBooleanValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmpBooleans
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkArgumentTypes
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|Configuration
name|config
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Map
name|idxPathMap
init|=
operator|(
name|Map
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"indexer.map"
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
name|IndexPaths
name|idx
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
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
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|idx
operator|=
operator|(
name|IndexPaths
operator|)
name|idxPathMap
operator|.
name|get
argument_list|(
name|doc
operator|.
name|getDoctype
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|!=
literal|null
operator|&&
name|idx
operator|.
name|isSelective
argument_list|()
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|idx
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|idx
operator|.
name|getIncludeAlphaNum
argument_list|()
operator|)
condition|)
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while processing expression"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 *  Left argument is a number: Convert right argument to a number for every 	 *  node in context. 	 * 	 *@param  left     Description of the Parameter 	 *@param  right    Description of the Parameter 	 *@param  docs     Description of the Parameter 	 *@param  context  Description of the Parameter 	 *@param  node     Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|protected
name|Value
name|numberCompare
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|ArraySet
name|currentSet
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|;
name|double
name|rvalue
decl_stmt|;
name|double
name|lvalue
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|currentSet
operator|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|currentSet
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|rvalue
operator|=
name|right
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|currentSet
argument_list|,
name|current
argument_list|)
operator|.
name|getNumericValue
argument_list|()
expr_stmt|;
name|lvalue
operator|=
name|left
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|currentSet
argument_list|,
name|current
argument_list|)
operator|.
name|getNumericValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmpNumbers
argument_list|(
name|lvalue
argument_list|,
name|rvalue
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|current
operator|.
name|addContextNode
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getLeft
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|OPS
index|[
name|relation
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getRight
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 *  check relevant documents. Does nothing here. 	 * 	 *@param  in_docs  Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
block|{
return|return
name|in_docs
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_NODELIST
return|;
block|}
specifier|protected
name|Value
name|stringCompare
parameter_list|(
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|NodeProxy
name|n
decl_stmt|;
name|String
name|lvalue
decl_stmt|;
name|String
name|rvalue
decl_stmt|;
name|int
name|cmp
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|context
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|rvalue
operator|=
name|left
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
name|n
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|lvalue
operator|=
name|right
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
name|n
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|compareStrings
argument_list|(
name|rvalue
argument_list|,
name|lvalue
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|protected
name|void
name|switchOperands
parameter_list|()
block|{
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|GT
case|:
name|relation
operator|=
name|Constants
operator|.
name|LT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|LT
case|:
name|relation
operator|=
name|Constants
operator|.
name|GT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|LTEQ
case|:
name|relation
operator|=
name|Constants
operator|.
name|GTEQ
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|GTEQ
case|:
name|relation
operator|=
name|Constants
operator|.
name|LTEQ
expr_stmt|;
break|break;
block|}
block|}
block|}
end_class

end_unit

