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
name|versioning
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|XQueryPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|CompiledXQuery
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
name|XQuery
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
name|Sequence
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|VersioningHelper
block|{
specifier|private
specifier|final
specifier|static
name|String
name|GET_CURRENT_REV
init|=
literal|"declare namespace v=\"http://exist-db.org/versioning\";\n"
operator|+
literal|"declare variable $collection external;\n"
operator|+
literal|"declare variable $document external;\n"
operator|+
literal|"max("
operator|+
literal|"   for $r in collection($collection)//v:properties[v:document = $document]/v:revision\n"
operator|+
literal|"   return xs:long($r)"
operator|+
literal|")"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|StringSource
name|GET_CURRENT_REV_SOURCE
init|=
operator|new
name|StringSource
argument_list|(
name|GET_CURRENT_REV
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|GET_CONFLICTING_REV
init|=
literal|"declare namespace v=\"http://exist-db.org/versioning\";\n"
operator|+
literal|"declare variable $collection external;\n"
operator|+
literal|"declare variable $document external;\n"
operator|+
literal|"declare variable $base external;\n"
operator|+
literal|"declare variable $key external;\n"
operator|+
literal|"collection($collection)//v:properties[v:document = $document]"
operator|+
literal|"   [v:revision> $base][v:key != $key]"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|StringSource
name|GET_CONFLICTING_REV_SOURCE
init|=
operator|new
name|StringSource
argument_list|(
name|GET_CONFLICTING_REV
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|GET_BASE_REV_FOR_KEY
init|=
literal|"declare namespace v=\"http://exist-db.org/versioning\";\n"
operator|+
literal|"declare variable $collection external;\n"
operator|+
literal|"declare variable $document external;\n"
operator|+
literal|"declare variable $base external;\n"
operator|+
literal|"declare variable $key external;\n"
operator|+
literal|"let $p := collection($collection)//v:properties[v:document = $document]\n"
operator|+
literal|"let $withKey := for $r in $p[v:revision> $base][v:key = $key] "
operator|+
literal|"                   order by $r/v:revision descending return $r\n"
operator|+
literal|"return\n"
operator|+
literal|"   if ($withKey) then\n"
operator|+
literal|"       xs:long($withKey[1]/v:revision)\n"
operator|+
literal|"   else\n"
operator|+
literal|"       xs:long($p[v:revision = $base]/v:revision)"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|StringSource
name|GET_BASE_REV_FOR_KEY_SOURCE
init|=
operator|new
name|StringSource
argument_list|(
name|GET_BASE_REV_FOR_KEY
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|long
name|getCurrentRevision
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|docPath
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
block|{
name|String
name|docName
init|=
name|docPath
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|XmldbURI
name|collectionPath
init|=
name|docPath
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|path
init|=
name|VersioningTrigger
operator|.
name|VERSIONS_COLLECTION
operator|.
name|append
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|GET_CURRENT_REV_SOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|VALIDATION_INTERNAL
argument_list|)
expr_stmt|;
else|else
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"document"
argument_list|,
name|docName
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|GET_CURRENT_REV_SOURCE
argument_list|)
expr_stmt|;
try|try
block|{
name|Sequence
name|s
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|0
return|;
name|IntegerValue
name|iv
init|=
operator|(
name|IntegerValue
operator|)
name|s
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|iv
operator|.
name|getLong
argument_list|()
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|GET_CURRENT_REV_SOURCE
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|newerRevisionExists
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|docPath
parameter_list|,
name|long
name|baseRev
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
block|{
name|String
name|docName
init|=
name|docPath
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|XmldbURI
name|collectionPath
init|=
name|docPath
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|path
init|=
name|VersioningTrigger
operator|.
name|VERSIONS_COLLECTION
operator|.
name|append
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|GET_CONFLICTING_REV_SOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|VALIDATION_INTERNAL
argument_list|)
expr_stmt|;
else|else
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"document"
argument_list|,
name|docName
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"base"
argument_list|,
operator|new
name|IntegerValue
argument_list|(
name|baseRev
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"key"
argument_list|,
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|GET_CONFLICTING_REV_SOURCE
argument_list|)
expr_stmt|;
try|try
block|{
name|Sequence
name|s
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
return|return
operator|!
name|s
operator|.
name|isEmpty
argument_list|()
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|GET_CONFLICTING_REV_SOURCE
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|long
name|getBaseRevision
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XmldbURI
name|docPath
parameter_list|,
name|long
name|baseRev
parameter_list|,
name|String
name|sessionKey
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
block|{
name|String
name|docName
init|=
name|docPath
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|XmldbURI
name|collectionPath
init|=
name|docPath
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
name|XmldbURI
name|path
init|=
name|VersioningTrigger
operator|.
name|VERSIONS_COLLECTION
operator|.
name|append
argument_list|(
name|collectionPath
argument_list|)
decl_stmt|;
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|pool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|pool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|GET_BASE_REV_FOR_KEY_SOURCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|VALIDATION_INTERNAL
argument_list|)
expr_stmt|;
else|else
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"collection"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"document"
argument_list|,
name|docName
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"base"
argument_list|,
operator|new
name|IntegerValue
argument_list|(
name|baseRev
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
literal|"key"
argument_list|,
name|sessionKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|GET_BASE_REV_FOR_KEY_SOURCE
argument_list|)
expr_stmt|;
try|try
block|{
name|Sequence
name|s
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|0
return|;
name|IntegerValue
name|iv
init|=
operator|(
name|IntegerValue
operator|)
name|s
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|iv
operator|.
name|getLong
argument_list|()
return|;
block|}
finally|finally
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|GET_BASE_REV_FOR_KEY_SOURCE
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

