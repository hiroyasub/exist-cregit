begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|protocolhandler
operator|.
name|embedded
operator|.
name|EmbeddedInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmlrpc
operator|.
name|XmlrpcInputStream
import|;
end_import

begin_comment
comment|/**  *  Resolve a resource specified by xs:anyURI. First time the  * resource is resolved by the URL as specified in the constructor,   * the second the URL of the ExpandedSystemId is used.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|AnyUriResolver
implements|implements
name|XMLEntityResolver
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AnyUriResolver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"exist.xml-entity-resolver"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|docPath
decl_stmt|;
specifier|private
specifier|final
name|String
name|parentURI
decl_stmt|;
specifier|private
name|boolean
name|firstTime
init|=
literal|true
decl_stmt|;
comment|/**      * Creates a new instance of AnyUriResolver.      *      * @param path Original path of resource.      */
specifier|public
name|AnyUriResolver
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|docPath
operator|=
name|path
expr_stmt|;
if|if
condition|(
name|docPath
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|docPath
operator|=
literal|"xmldb:exist://"
operator|+
name|docPath
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Specified path="
operator|+
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|parentURI
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"parentURI="
operator|+
name|parentURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parentURI
operator|=
literal|""
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
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
block|}
name|String
name|resourcePath
init|=
literal|null
decl_stmt|;
name|String
name|baseSystemId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|firstTime
condition|)
block|{
comment|// First time use constructor supplied path
name|resourcePath
operator|=
name|docPath
expr_stmt|;
name|baseSystemId
operator|=
name|parentURI
expr_stmt|;
name|xri
operator|.
name|setExpandedSystemId
argument_list|(
name|docPath
argument_list|)
expr_stmt|;
name|firstTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|resourcePath
operator|=
name|xri
operator|.
name|getExpandedSystemId
argument_list|()
expr_stmt|;
block|}
name|xri
operator|.
name|setBaseSystemId
argument_list|(
name|docPath
argument_list|)
expr_stmt|;
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
comment|// prevent NPE
if|if
condition|(
name|resourcePath
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"xmldb:"
argument_list|)
condition|)
block|{
specifier|final
name|XmldbURL
name|xmldbURL
init|=
operator|new
name|XmldbURL
argument_list|(
name|resourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmldbURL
operator|.
name|isEmbedded
argument_list|()
condition|)
block|{
name|is
operator|=
operator|new
name|EmbeddedInputStream
argument_list|(
name|xmldbURL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
operator|new
name|XmlrpcInputStream
argument_list|(
name|threadGroup
argument_list|,
name|xmldbURL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|is
operator|=
operator|new
name|URL
argument_list|(
name|resourcePath
argument_list|)
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
specifier|final
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
name|resourcePath
argument_list|,
name|baseSystemId
argument_list|,
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
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
block|}
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
return|return
literal|"PublicId='"
operator|+
name|xrid
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"BaseSystemId='"
operator|+
name|xrid
operator|.
name|getBaseSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"ExpandedSystemId='"
operator|+
name|xrid
operator|.
name|getExpandedSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"LiteralSystemId='"
operator|+
name|xrid
operator|.
name|getLiteralSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"Namespace='"
operator|+
name|xrid
operator|.
name|getNamespace
argument_list|()
operator|+
literal|"' "
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
return|return
literal|"PublicId='"
operator|+
name|xis
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"SystemId='"
operator|+
name|xis
operator|.
name|getSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"BaseSystemId='"
operator|+
name|xis
operator|.
name|getBaseSystemId
argument_list|()
operator|+
literal|"' "
operator|+
literal|"Encoding='"
operator|+
name|xis
operator|.
name|getEncoding
argument_list|()
operator|+
literal|"' "
return|;
block|}
block|}
end_class

end_unit

