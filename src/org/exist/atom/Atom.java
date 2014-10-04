begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2006-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|persistent
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_interface
specifier|public
interface|interface
name|Atom
block|{
name|String
name|MIME_TYPE
init|=
literal|"application/atom+xml"
decl_stmt|;
name|URI
name|NAMESPACE
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"http://www.w3.org/2005/Atom"
argument_list|)
decl_stmt|;
name|String
name|NAMESPACE_STRING
init|=
name|NAMESPACE
operator|.
name|toString
argument_list|()
decl_stmt|;
name|QName
name|FEED
init|=
operator|new
name|QName
argument_list|(
literal|"feed"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
name|QName
name|ENTRY
init|=
operator|new
name|QName
argument_list|(
literal|"entry"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
name|QName
name|TITLE
init|=
operator|new
name|QName
argument_list|(
literal|"title"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
name|QName
name|UPDATED
init|=
operator|new
name|QName
argument_list|(
literal|"updated"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
name|QName
name|PUBLISHED
init|=
operator|new
name|QName
argument_list|(
literal|"published"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
name|QName
name|SUMMARY
init|=
operator|new
name|QName
argument_list|(
literal|"summary"
argument_list|,
name|NAMESPACE_STRING
argument_list|,
literal|"atom"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

