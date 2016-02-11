begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

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
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
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
name|Type
import|;
end_import

begin_comment
comment|/**  * Used to specify a range index on a node path.  * The range index indexes the value of nodes according  * to a predefined type.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|GeneralRangeIndexSpec
extends|extends
name|RangeIndexSpec
block|{
specifier|private
name|NodePath
name|path
decl_stmt|;
specifier|public
name|GeneralRangeIndexSpec
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|String
name|pathStr
parameter_list|,
name|String
name|typeStr
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|pathStr
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"The path attribute is required in index.create"
argument_list|)
throw|;
block|}
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|namespaces
argument_list|,
name|pathStr
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|type
operator|=
name|getSuperType
argument_list|(
name|Type
operator|.
name|getType
argument_list|(
name|typeStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Unknown type: "
operator|+
name|typeStr
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns the path corresponding to this index.      */
specifier|public
name|NodePath
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * Check if the path argument matches the path      * of this index spec.      *       * @param otherPath      * @return Whether or not the 2 paths match      */
specifier|protected
name|boolean
name|matches
parameter_list|(
name|NodePath
name|otherPath
parameter_list|)
block|{
return|return
name|path
operator|.
name|match
argument_list|(
name|otherPath
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"General range index\n"
operator|+
literal|"\ttype : "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|type
argument_list|)
operator|+
literal|'\n'
operator|+
literal|"\tpath : "
operator|+
name|path
operator|.
name|toString
argument_list|()
operator|+
literal|'\n'
operator|+
literal|"\thas Qname index : "
operator|+
name|hasQNameIndex
argument_list|(
name|this
operator|.
name|type
argument_list|)
operator|+
literal|'\n'
operator|+
literal|"\thas Qname or value index : "
operator|+
name|hasQNameOrValueIndex
argument_list|(
name|this
operator|.
name|type
argument_list|)
operator|+
literal|'\n'
operator|+
literal|"\thas range index : "
operator|+
name|hasRangeIndex
argument_list|(
name|this
operator|.
name|type
argument_list|)
operator|+
literal|'\n'
return|;
block|}
block|}
end_class

end_unit

