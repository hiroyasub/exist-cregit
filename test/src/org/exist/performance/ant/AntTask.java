begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|TestResultWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_class
specifier|public
class|class
name|AntTask
extends|extends
name|Task
block|{
specifier|private
name|String
name|source
decl_stmt|;
specifier|private
name|String
name|outputFile
decl_stmt|;
specifier|private
name|String
name|group
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
specifier|final
name|Path
name|src
init|=
name|Paths
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|Files
operator|.
name|isReadable
argument_list|(
name|src
argument_list|)
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|src
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"Cannot read input file: "
operator|+
name|source
argument_list|)
throw|;
block|}
specifier|final
name|Path
name|outFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|outputFile
argument_list|)
decl_stmt|;
name|Runner
name|runner
init|=
literal|null
decl_stmt|;
try|try
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|src
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|TestResultWriter
name|writer
init|=
operator|new
name|TestResultWriter
argument_list|(
name|outFile
argument_list|)
init|)
block|{
name|runner
operator|=
operator|new
name|Runner
argument_list|(
name|doc
operator|.
name|getDocumentElement
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|runner
operator|.
name|run
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|runner
operator|!=
literal|null
condition|)
name|runner
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getOutputFile
parameter_list|()
block|{
return|return
name|outputFile
return|;
block|}
specifier|public
name|void
name|setOutputFile
parameter_list|(
name|String
name|outputFile
parameter_list|)
block|{
name|this
operator|.
name|outputFile
operator|=
name|outputFile
expr_stmt|;
block|}
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
specifier|public
name|void
name|setSource
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
block|}
end_class

end_unit

