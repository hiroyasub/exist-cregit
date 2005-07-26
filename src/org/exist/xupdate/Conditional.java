begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|DocumentSet
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
name|PermissionDeniedException
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
name|Source
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|LockException
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
name|Sequence
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Conditional
extends|extends
name|Modification
block|{
specifier|private
name|List
name|modifications
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|/** 	 * @param broker 	 * @param docs 	 * @param selectStmt 	 * @param namespaces 	 */
specifier|public
name|Conditional
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|,
name|Map
name|namespaces
parameter_list|,
name|Map
name|variables
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|selectStmt
argument_list|,
name|namespaces
argument_list|,
name|variables
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addModification
parameter_list|(
name|Modification
name|mod
parameter_list|)
block|{
name|modifications
operator|.
name|add
argument_list|(
name|mod
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#process() 	 */
specifier|public
name|long
name|process
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing xupdate:if ..."
argument_list|)
expr_stmt|;
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
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|selectStmt
argument_list|)
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
name|source
argument_list|)
decl_stmt|;
name|XQueryContext
name|context
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
argument_list|()
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
name|setBackwardsCompatibility
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|declareNamespaces
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|declareVariables
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
try|try
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"An exception occurred while compiling the query: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|Sequence
name|seq
init|=
literal|null
decl_stmt|;
try|try
block|{
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
block|{
name|long
name|mods
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|modifications
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|mods
operator|+=
operator|(
operator|(
name|Modification
operator|)
name|modifications
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|mods
operator|+
literal|" modifications processed."
argument_list|)
expr_stmt|;
return|return
name|mods
return|;
block|}
else|else
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"if"
return|;
block|}
block|}
end_class

end_unit

