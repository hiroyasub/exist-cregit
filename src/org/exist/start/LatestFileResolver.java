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
name|start
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
name|java
operator|.
name|io
operator|.
name|FilenameFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * This class uses regex pattern matching to find the latest version of a  * particular jar file.   *   * @see LatestFileResolver#getResolvedFileName(String)  *   * @author Ben Schmaus (exist@benschmaus.com)  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LatestFileResolver
block|{
comment|// Pattern that can be used to indicate that the
comment|// latest version of a particular file should be added to the classpath.
comment|// E.g., commons-fileupload-%latest%.jar would resolve to something like
comment|// commons-fileupload-1.1.jar.
specifier|private
specifier|final
specifier|static
name|Pattern
name|latestVersionPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(%latest%)"
argument_list|)
decl_stmt|;
comment|// Set debug mode for each file resolver instance based on whether or
comment|// not the system was started with debugging turned on.
specifier|private
specifier|static
name|boolean
name|_debug
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"exist.start.debug"
argument_list|)
decl_stmt|;
comment|/**      * If the passed file name contains a %latest% token,      * find the latest version of that file. Otherwise, return      * the passed file name unmodified.      *       * @param filename Path relative to exist home dir of      * a jar file that should be added to the classpath.      */
specifier|public
name|String
name|getResolvedFileName
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|Matcher
name|matches
init|=
name|latestVersionPattern
operator|.
name|matcher
argument_list|(
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matches
operator|.
name|find
argument_list|()
condition|)
block|{
return|return
name|filename
return|;
block|}
name|String
index|[]
name|fileinfo
init|=
name|filename
operator|.
name|split
argument_list|(
literal|"%latest%"
argument_list|)
decl_stmt|;
comment|// Path of file up to the beginning of the %latest% token.
name|String
name|uptoToken
init|=
name|fileinfo
index|[
literal|0
index|]
decl_stmt|;
comment|// Dir that should contain our jar.
name|String
name|containerDirName
init|=
name|uptoToken
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|uptoToken
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|containerDir
init|=
operator|new
name|File
argument_list|(
name|containerDirName
argument_list|)
decl_stmt|;
comment|// 0-9 . - and _ are valid chars that can occur where the %latest% token
comment|// was (maybe allow letters too?).
name|String
name|patternString
init|=
name|uptoToken
operator|.
name|substring
argument_list|(
name|uptoToken
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
operator|+
literal|1
argument_list|)
operator|+
literal|"([\\d\\.\\-_]+)"
operator|+
name|fileinfo
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|patternString
argument_list|)
decl_stmt|;
name|File
index|[]
name|jars
init|=
name|containerDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Matcher
name|matches
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|matches
operator|.
name|find
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|jars
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|actualFileName
init|=
name|jars
index|[
literal|0
index|]
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
if|if
condition|(
name|_debug
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Found match: "
operator|+
name|actualFileName
operator|+
literal|" for jar file pattern: "
operator|+
name|filename
argument_list|)
expr_stmt|;
block|}
return|return
name|actualFileName
return|;
block|}
else|else
block|{
if|if
condition|(
name|_debug
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARN: No latest version found for JAR file: '"
operator|+
name|filename
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|filename
return|;
block|}
block|}
end_class

end_unit

