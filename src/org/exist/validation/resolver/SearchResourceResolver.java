begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|resolver
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|XMLResourceIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|XNIException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLEntityResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLInputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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
name|validation
operator|.
name|internal
operator|.
name|DatabaseResources
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
name|InputSource
import|;
end_import

begin_comment
comment|/**  *  Resolve a resource by searching in database. Schema's are queried  * directly, DTD are searched in catalog files.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|SearchResourceResolver
implements|implements
name|XMLEntityResolver
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SearchResourceResolver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
comment|/** Creates a new instance of StoredResourceResolver */
specifier|public
name|SearchResourceResolver
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
block|{
name|collection
operator|=
name|collectionPath
expr_stmt|;
name|brokerPool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      *      */
specifier|public
name|XMLInputSource
name|resolveEntity
parameter_list|(
name|XMLResourceIdentifier
name|xri
parameter_list|)
throws|throws
name|XNIException
throws|,
name|IOException
block|{
if|if
condition|(
name|xri
operator|.
name|getExpandedSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getLiteralSystemId
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getNamespace
argument_list|()
operator|==
literal|null
operator|&&
name|xri
operator|.
name|getPublicId
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// quick fail
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving XMLResourceIdentifier: "
operator|+
name|getXriDetails
argument_list|(
name|xri
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|documentName
init|=
literal|null
decl_stmt|;
name|String
name|resourcePath
init|=
literal|null
decl_stmt|;
name|DatabaseResources
name|databaseResources
init|=
operator|new
name|DatabaseResources
argument_list|(
name|brokerPool
argument_list|)
decl_stmt|;
name|User
name|user
init|=
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
argument_list|)
decl_stmt|;
if|if
condition|(
name|xri
operator|.
name|getNamespace
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// XML Schema search
name|LOG
operator|.
name|debug
argument_list|(
literal|"Searching namespace '"
operator|+
name|xri
operator|.
name|getNamespace
argument_list|()
operator|+
literal|"' in database from "
operator|+
name|collection
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|resourcePath
operator|=
name|databaseResources
operator|.
name|findXSD
argument_list|(
name|collection
argument_list|,
name|xri
operator|.
name|getNamespace
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
comment|// DIZZZ: set systemid?
block|}
if|else if
condition|(
name|xri
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Catalog search
name|LOG
operator|.
name|debug
argument_list|(
literal|"Searching publicId '"
operator|+
name|xri
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' in catalogs in database from "
operator|+
name|collection
operator|+
literal|"..."
argument_list|)
expr_stmt|;
name|String
name|catalogPath
init|=
name|databaseResources
operator|.
name|findCatalogWithDTD
argument_list|(
name|collection
argument_list|,
name|xri
operator|.
name|getPublicId
argument_list|()
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found publicId in catalog '"
operator|+
name|catalogPath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|catalogPath
operator|!=
literal|null
operator|&&
name|catalogPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|catalogPath
operator|=
literal|"xmldb:exist://"
operator|+
name|catalogPath
expr_stmt|;
block|}
name|eXistXMLCatalogResolver
name|resolver
init|=
operator|new
name|eXistXMLCatalogResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|setCatalogList
argument_list|(
operator|new
name|String
index|[]
block|{
name|catalogPath
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|InputSource
name|source
init|=
name|resolver
operator|.
name|resolveEntity
argument_list|(
name|xri
operator|.
name|getPublicId
argument_list|()
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|resourcePath
operator|=
name|source
operator|.
name|getSystemId
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// set systemid?
block|}
else|else
block|{
comment|// Fast escape; no logging, otherwise validation is slow!
return|return
literal|null
return|;
block|}
comment|// Another escape route
if|if
condition|(
name|resourcePath
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"resourcePath=null"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|resourcePath
operator|!=
literal|null
operator|&&
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|resourcePath
operator|=
literal|"xmldb:exist://"
operator|+
name|resourcePath
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"resourcePath='"
operator|+
name|resourcePath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|URL
argument_list|(
name|resourcePath
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|XMLInputSource
name|xis
init|=
operator|new
name|XMLInputSource
argument_list|(
name|xri
operator|.
name|getPublicId
argument_list|()
argument_list|,
name|xri
operator|.
name|getExpandedSystemId
argument_list|()
argument_list|,
name|xri
operator|.
name|getBaseSystemId
argument_list|()
argument_list|,
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"XMLInputSource: "
operator|+
name|getXisDetails
argument_list|(
name|xis
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|xis
return|;
block|}
specifier|private
name|String
name|getXriDetails
parameter_list|(
name|XMLResourceIdentifier
name|xrid
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"PublicId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xrid
operator|.
name|getPublicId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"BaseSystemId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ExpandedSystemId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xrid
operator|.
name|getExpandedSystemId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"LiteralSystemId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xrid
operator|.
name|getLiteralSystemId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Namespace='"
argument_list|)
operator|.
name|append
argument_list|(
name|xrid
operator|.
name|getNamespace
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|String
name|getXisDetails
parameter_list|(
name|XMLInputSource
name|xis
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"PublicId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xis
operator|.
name|getPublicId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"SystemId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xis
operator|.
name|getSystemId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"BaseSystemId='"
argument_list|)
operator|.
name|append
argument_list|(
name|xis
operator|.
name|getBaseSystemId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Encoding='"
argument_list|)
operator|.
name|append
argument_list|(
name|xis
operator|.
name|getEncoding
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' "
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

