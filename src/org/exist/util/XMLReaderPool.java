begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|PoolableObjectFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|pool
operator|.
name|impl
operator|.
name|StackObjectPool
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
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * Maintains a pool of XMLReader objects. The pool is available through  * {@link BrokerPool#getParserPool()}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLReaderPool
extends|extends
name|StackObjectPool
block|{
comment|/** 	 * @param arg0 	 * @param arg1 	 * @param arg2 	 */
specifier|public
name|XMLReaderPool
parameter_list|(
name|PoolableObjectFactory
name|factory
parameter_list|,
name|int
name|maxIdle
parameter_list|,
name|int
name|initIdleCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|maxIdle
argument_list|,
name|initIdleCapacity
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|XMLReader
name|borrowXMLReader
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|XMLReader
operator|)
name|borrowObject
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"error while returning XMLReader: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|returnXMLReader
parameter_list|(
name|XMLReader
name|reader
parameter_list|)
block|{
try|try
block|{
name|returnObject
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"error while returning XMLReader: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

