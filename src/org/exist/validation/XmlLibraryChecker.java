begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_comment
comment|/**  *  Class for checking dependencies with XML libraries.  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|XmlLibraryChecker
block|{
comment|/** 	 * Possible XML Parsers, at least one must be valid 	 */
specifier|private
specifier|final
specifier|static
name|ClassVersion
index|[]
name|validParsers
init|=
block|{
operator|new
name|ClassVersion
argument_list|(
literal|"Xerces"
argument_list|,
literal|"Xerces-J 2.9.1"
argument_list|,
literal|"org.apache.xerces.impl.Version.getVersion()"
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * Possible XML Transformers, at least one must be valid 	 */
specifier|private
specifier|final
specifier|static
name|ClassVersion
index|[]
name|validTransformers
init|=
block|{
operator|new
name|ClassVersion
argument_list|(
literal|"Saxon"
argument_list|,
literal|"8.9.0"
argument_list|,
literal|"net.sf.saxon.Version.getProductVersion()"
argument_list|)
block|,
operator|new
name|ClassVersion
argument_list|(
literal|"Xalan"
argument_list|,
literal|"Xalan Java 2.7.1"
argument_list|,
literal|"org.apache.xalan.Version.getVersion()"
argument_list|)
block|, 	}
decl_stmt|;
comment|/** 	 * Possible XML resolvers, at least one must be valid 	 */
specifier|private
specifier|final
specifier|static
name|ClassVersion
index|[]
name|validResolvers
init|=
block|{
operator|new
name|ClassVersion
argument_list|(
literal|"Resolver"
argument_list|,
literal|"XmlResolver 1.2"
argument_list|,
literal|"org.apache.xml.resolver.Version.getVersion()"
argument_list|)
block|, 	}
decl_stmt|;
specifier|public
specifier|static
name|void
name|check
parameter_list|()
block|{
name|StringBuffer
name|message
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasValidClassVersion
argument_list|(
literal|"Parser"
argument_list|,
name|validParsers
argument_list|,
name|message
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasValidClassVersion
argument_list|(
literal|"Transformers"
argument_list|,
name|validTransformers
argument_list|,
name|message
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasValidClassVersion
argument_list|(
literal|"Resolver"
argument_list|,
name|validResolvers
argument_list|,
name|message
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|hasValidClassVersion
parameter_list|(
name|String
name|type
parameter_list|,
name|ClassVersion
index|[]
name|validClasses
parameter_list|,
name|StringBuffer
name|message
parameter_list|)
block|{
name|String
name|sep
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"Looking for a valid "
operator|+
name|type
operator|+
literal|"..."
operator|+
name|sep
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|validClasses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|actualVersion
init|=
name|validClasses
index|[
name|i
index|]
operator|.
name|getActualVersion
argument_list|()
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"Checking for "
operator|+
name|validClasses
index|[
name|i
index|]
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualVersion
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
literal|", found version "
operator|+
name|actualVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualVersion
operator|.
name|compareToIgnoreCase
argument_list|(
name|validClasses
index|[
name|i
index|]
operator|.
name|getRequiredVersion
argument_list|()
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|message
operator|.
name|append
argument_list|(
name|sep
operator|+
literal|"OK!"
operator|+
name|sep
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|message
operator|.
name|append
argument_list|(
literal|" needed version "
operator|+
name|validClasses
index|[
name|i
index|]
operator|.
name|getRequiredVersion
argument_list|()
operator|+
name|sep
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|message
operator|.
name|append
argument_list|(
literal|", not found!"
operator|+
name|sep
argument_list|)
expr_stmt|;
block|}
block|}
name|message
operator|.
name|append
argument_list|(
literal|"Warning: Failed find a valid "
operator|+
name|type
operator|+
literal|"!"
operator|+
name|sep
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|sep
operator|+
literal|"Please add an appropriate "
operator|+
name|type
operator|+
literal|" to the "
operator|+
literal|"class-path, e.g. in the 'endorsed' folder of "
operator|+
literal|"the servlet container or in the 'endorsed' folder "
operator|+
literal|"of the JRE."
operator|+
name|sep
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/** 	 * Checks to see if a valid XML Parser exists 	 *  	 * @return boolean true indicates a valid Parser was found, false otherwise 	 */
specifier|public
specifier|static
name|boolean
name|hasValidParser
parameter_list|()
block|{
return|return
name|hasValidParser
argument_list|(
operator|new
name|StringBuffer
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Checks to see if a valid XML Parser exists 	 *  	 * @param message	Messages about the status of available Parser's will be appended to this buffer 	 *  	 * @return boolean true indicates a valid Parser was found, false otherwise 	 */
specifier|public
specifier|static
name|boolean
name|hasValidParser
parameter_list|(
name|StringBuffer
name|message
parameter_list|)
block|{
return|return
name|hasValidClassVersion
argument_list|(
literal|"Parser"
argument_list|,
name|validParsers
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/** 	 * Checks to see if a valid XML Transformer exists 	 *  	 * @return boolean true indicates a valid Transformer was found, false otherwise 	 */
specifier|public
specifier|static
name|boolean
name|hasValidTransformer
parameter_list|()
block|{
return|return
name|hasValidTransformer
argument_list|(
operator|new
name|StringBuffer
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Checks to see if a valid XML Transformer exists 	 *  	 * @param message	Messages about the status of available Transformer's will be appended to this buffer 	 *  	 * @return boolean true indicates a valid Transformer was found, false otherwise 	 */
specifier|public
specifier|static
name|boolean
name|hasValidTransformer
parameter_list|(
name|StringBuffer
name|message
parameter_list|)
block|{
return|return
name|hasValidClassVersion
argument_list|(
literal|"Transformer"
argument_list|,
name|validTransformers
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/** 	 * Simple class to describe a class, its required version and how to obtain the actual version  	 */
specifier|private
specifier|static
class|class
name|ClassVersion
block|{
specifier|private
name|String
name|simpleName
decl_stmt|;
specifier|private
name|String
name|requiredVersion
decl_stmt|;
specifier|private
name|String
name|versionFunction
decl_stmt|;
comment|/**     	 * Default Constructor     	 *      	 * @param simpleName		The simple name for the class (just a descriptor really)     	 * @param requiredVersion	The required version of the class     	 * @param versionFunction	The function to be invoked to obtain the actual version of the class, must be fully qualified (i.e. includes the package name)     	 */
name|ClassVersion
parameter_list|(
name|String
name|simpleName
parameter_list|,
name|String
name|requiredVersion
parameter_list|,
name|String
name|versionFunction
parameter_list|)
block|{
name|this
operator|.
name|simpleName
operator|=
name|simpleName
expr_stmt|;
name|this
operator|.
name|requiredVersion
operator|=
name|requiredVersion
expr_stmt|;
name|this
operator|.
name|versionFunction
operator|=
name|versionFunction
expr_stmt|;
block|}
comment|/**     	 *  @return the simple name of the class     	 */
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
name|simpleName
return|;
block|}
comment|/**     	 *  @return the required version of the class     	 */
specifier|public
name|String
name|getRequiredVersion
parameter_list|()
block|{
return|return
name|requiredVersion
return|;
block|}
comment|/**     	 * Invokes the specified versionFunction using reflection to get the actual version of the class     	 *      	 *  @return the actual version of the class     	 */
specifier|public
name|String
name|getActualVersion
parameter_list|()
block|{
name|String
name|actualVersion
init|=
literal|null
decl_stmt|;
comment|//get the class name from the specifiec version function string
name|String
name|versionClassName
init|=
name|versionFunction
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|versionFunction
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
comment|//get the function name from the specifiec version function string
name|String
name|versionFunctionName
init|=
name|versionFunction
operator|.
name|substring
argument_list|(
name|versionFunction
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|,
name|versionFunction
operator|.
name|lastIndexOf
argument_list|(
literal|'('
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|//get the class
name|Class
name|versionClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|versionClassName
argument_list|)
decl_stmt|;
comment|//get the method
name|Method
name|getVersionMethod
init|=
name|versionClass
operator|.
name|getMethod
argument_list|(
name|versionFunctionName
argument_list|,
operator|(
name|Class
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
comment|//invoke the method on the class
name|actualVersion
operator|=
operator|(
name|String
operator|)
name|getVersionMethod
operator|.
name|invoke
argument_list|(
name|versionClass
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cfe
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsme
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ite
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iae
parameter_list|)
block|{
block|}
comment|//return the actual version
return|return
name|actualVersion
return|;
block|}
block|}
block|}
end_class

end_unit

