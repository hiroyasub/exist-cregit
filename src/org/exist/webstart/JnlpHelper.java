begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webstart
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  *  Helper class for webstart.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|JnlpHelper
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|JnlpHelper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|File
name|existHome
init|=
name|Configuration
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
specifier|private
name|File
name|coreJarsFolder
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|existJarFolder
init|=
literal|null
decl_stmt|;
specifier|private
name|File
name|webappFolder
init|=
literal|null
decl_stmt|;
comment|/** Creates a new instance of JnlpHelper */
specifier|public
name|JnlpHelper
parameter_list|()
block|{
comment|// Setup path based on installation (in jetty, container)
if|if
condition|(
name|Configuration
operator|.
name|isInWarFile
argument_list|()
condition|)
block|{
comment|// all files mixed in existHome/lib/
name|logger
operator|.
name|debug
argument_list|(
literal|"eXist is running in container (.war)."
argument_list|)
expr_stmt|;
name|coreJarsFolder
operator|=
operator|new
name|File
argument_list|(
name|existHome
argument_list|,
literal|"lib/"
argument_list|)
expr_stmt|;
name|existJarFolder
operator|=
operator|new
name|File
argument_list|(
name|existHome
argument_list|,
literal|"lib/"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// all files located in existHome/lib/core/
name|logger
operator|.
name|debug
argument_list|(
literal|"eXist is running private jetty server."
argument_list|)
expr_stmt|;
name|coreJarsFolder
operator|=
operator|new
name|File
argument_list|(
name|existHome
argument_list|,
literal|"lib/core"
argument_list|)
expr_stmt|;
name|existJarFolder
operator|=
name|existHome
expr_stmt|;
block|}
name|webappFolder
operator|=
name|Configuration
operator|.
name|getWebappHome
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"CORE jars location="
operator|+
name|coreJarsFolder
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"EXIST jars location="
operator|+
name|existJarFolder
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"WEBAPP location="
operator|+
name|webappFolder
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//    /**
comment|//     *  Check wether exist runs in Servlet container (as war file).
comment|//     * @return TRUE if exist runs in servlet container.
comment|//     */
comment|//    public boolean isInWarFile(){
comment|//
comment|//        boolean retVal =true;
comment|//        if( new File(existHome, "lib/core").isDirectory() ) {
comment|//            retVal=false;
comment|//        }
comment|//        return retVal;
comment|//    }
specifier|public
name|File
name|getWebappFolder
parameter_list|()
block|{
return|return
name|webappFolder
return|;
block|}
specifier|public
name|File
name|getCoreJarsFolder
parameter_list|()
block|{
return|return
name|coreJarsFolder
return|;
block|}
specifier|public
name|File
name|getExistJarFolder
parameter_list|()
block|{
return|return
name|existJarFolder
return|;
block|}
block|}
end_class

end_unit

