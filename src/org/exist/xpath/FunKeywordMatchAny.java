begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    7. Oktober 2002  */
end_comment

begin_class
specifier|public
class|class
name|FunKeywordMatchAny
extends|extends
name|FunKeywordMatchAll
block|{
comment|/**  Constructor for the FunKeywordMatchAny object */
specifier|public
name|FunKeywordMatchAny
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"match-any"
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the operatorType attribute of the FunKeywordMatchAny object      *      *@return    The operatorType value      */
specifier|protected
name|int
name|getOperatorType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|FULLTEXT_OR
return|;
block|}
block|}
end_class

end_unit

