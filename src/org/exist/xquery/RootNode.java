begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|Type
import|;
end_import

begin_comment
comment|/**  *  Represents the document-root node in an expression.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    02 August 2002  */
end_comment

begin_class
specifier|public
class|class
name|RootNode
extends|extends
name|Step
block|{
comment|/**  Constructor for the RootNode object */
specifier|public
name|RootNode
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|)
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
block|{
name|DocumentSet
name|ds
init|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
decl_stmt|;
if|if
condition|(
name|ds
operator|==
literal|null
operator|||
name|ds
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
comment|//NodeSet result = new ExtArrayNodeSet(ds.getLength(), 1);
name|NodeSet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
name|ds
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|ds
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
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|"ROOT"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Step#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Step#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

