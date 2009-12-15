begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|pragmas
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|*
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
name|exist
operator|.
name|Namespaces
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
name|storage
operator|.
name|txn
operator|.
name|TransactionException
import|;
end_import

begin_class
specifier|public
class|class
name|BatchTransactionPragma
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|BATCH_TRANSACTION_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"batch-transaction"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|BatchTransactionPragma
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|BatchTransactionPragma
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|qname
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|context
operator|.
name|finishBatchTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|te
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|te
operator|.
name|getMessage
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|context
operator|.
name|startBatchTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|te
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|te
operator|.
name|getMessage
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

