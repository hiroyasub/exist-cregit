begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|Dependency
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
name|Function
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

begin_comment
comment|/**  * Built-in function fn:last().  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunLast
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
literal|"last"
argument_list|,
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunLast
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|+
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
specifier|final
name|int
name|len
init|=
name|contextSequence
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|len
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|len
argument_list|)
return|;
block|}
comment|/*public Sequence eval(StaticContext context, DocumentSet docs, Sequence contextSequence, 		Item contextItem) throws XPathException { 			if(!Type.subTypeOf(contextItem.getType(), Type.NODE)) 				throw new XPathException("last() can only be applied to nodes"); 			NodeProxy contextNode = (NodeProxy)contextItem; 	   DocumentImpl doc = contextNode.getDoc(); 	   int level = doc.getTreeLevel(contextNode.getGID()); 	   long pid = (contextNode.getGID() - doc.getLevelStartPoint(level)) / 	     doc.getTreeLevelOrder(level) 	     + doc.getLevelStartPoint(level - 1); 	   long f_gid = (pid - doc.getLevelStartPoint(level -1)) * 	     doc.getTreeLevelOrder(level) + 	     doc.getLevelStartPoint(level); 	   long e_gid = f_gid + doc.getTreeLevelOrder(level); 	   NodeSet set = ((NodeSet)contextSequence).getRange(doc, f_gid, e_gid); 	   int len = set.getLength(); 	   return new IntegerValue(len); 	}*/
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|"last()"
return|;
block|}
block|}
end_class

end_unit

