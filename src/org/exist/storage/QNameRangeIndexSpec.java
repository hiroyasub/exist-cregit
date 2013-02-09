begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_class
specifier|public
class|class
name|QNameRangeIndexSpec
extends|extends
name|RangeIndexSpec
block|{
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|public
name|QNameRangeIndexSpec
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
name|name
parameter_list|,
name|String
name|typeStr
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
try|try
block|{
name|this
operator|.
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
name|boolean
name|isAttribute
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|isAttribute
operator|=
literal|true
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|prefix
init|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|String
name|localName
init|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No namespace defined for prefix: "
operator|+
name|prefix
operator|+
literal|" in index definition"
argument_list|)
throw|;
block|}
block|}
name|qname
operator|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|isAttribute
condition|)
block|{
name|qname
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
block|}
end_class

end_unit

