begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Apache FOP Transformation Extension  *  Copyright (C) 2007 Craig Goodyer at the University of the West of England  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xslfo
package|;
end_package

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
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:craiggoodyer@gmail.com">Craig Goodyer</a>  * @author<a href="mailto:adam.retter@devon.gov.uk">Adam Retter</a>  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|XSLFOModule
extends|extends
name|AbstractInternalModule
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XSLFOModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/xslfo"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"xslfo"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2007-10-04"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|RenderFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|RenderFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RenderFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|RenderFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XSLFOModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for performing XSL-FO transformations"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
specifier|private
name|ProcessorAdapter
name|adapter
init|=
literal|null
decl_stmt|;
specifier|public
specifier|synchronized
name|ProcessorAdapter
name|getProcessorAdapter
parameter_list|()
block|{
if|if
condition|(
name|adapter
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|processorAdapterParamList
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|getParameter
argument_list|(
literal|"processorAdapter"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|processorAdapterParamList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|processorAdapter
init|=
name|processorAdapterParamList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|ProcessorAdapter
argument_list|>
name|clazzAdapter
init|=
operator|(
name|Class
argument_list|<
name|ProcessorAdapter
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|processorAdapter
argument_list|)
decl_stmt|;
name|adapter
operator|=
name|clazzAdapter
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to instantiate FO Processor Adapter:"
operator|+
name|cnfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|cnfe
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ie
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to instantiate FO Processor Adapter:"
operator|+
name|ie
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Unable to instantiate FO Processor Adapter:"
operator|+
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|adapter
return|;
block|}
block|}
end_class

end_unit

