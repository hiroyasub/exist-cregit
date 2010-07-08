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
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
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
name|ExistSAXParserFactory
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
comment|/**  *  Class for checking dependencies with XML libraries.  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|XmlLibraryChecker
block|{
comment|/**      * Possible XML Parsers, at least one must be valid      */
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
literal|"Xerces-J 2.10.0"
argument_list|,
literal|"org.apache.xerces.impl.Version.getVersion()"
argument_list|)
block|}
decl_stmt|;
comment|/**      * Possible XML Transformers, at least one must be valid      */
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
block|,     }
decl_stmt|;
comment|/**      * Possible XML resolvers, at least one must be valid      */
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
block|,     }
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XmlLibraryChecker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      *  Remove "@" from string.      */
specifier|private
specifier|static
name|String
name|getClassName
parameter_list|(
name|String
name|classid
parameter_list|)
block|{
name|String
name|className
decl_stmt|;
name|int
name|lastChar
init|=
name|classid
operator|.
name|lastIndexOf
argument_list|(
literal|"@"
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastChar
operator|==
operator|-
literal|1
condition|)
block|{
name|className
operator|=
name|classid
expr_stmt|;
block|}
else|else
block|{
name|className
operator|=
name|classid
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastChar
argument_list|)
expr_stmt|;
block|}
return|return
name|className
return|;
block|}
comment|/**      *  Determine the class that is actually used as XML parser.       *       * @return Full classname of parser.      */
specifier|private
specifier|static
name|String
name|determineActualParserClass
parameter_list|()
block|{
name|String
name|parserClass
init|=
literal|"Unable to determine parser class"
decl_stmt|;
try|try
block|{
name|SAXParserFactory
name|factory
init|=
name|ExistSAXParserFactory
operator|.
name|getSAXParserFactory
argument_list|()
decl_stmt|;
name|XMLReader
name|xmlReader
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|String
name|classId
init|=
name|xmlReader
operator|.
name|toString
argument_list|()
decl_stmt|;
name|parserClass
operator|=
name|getClassName
argument_list|(
name|classId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|parserClass
return|;
block|}
comment|/**      *  Determine the class that is actually used as XML transformer.       *       * @return Full classname of transformer.      */
specifier|private
specifier|static
name|String
name|determineActualTransformerClass
parameter_list|()
block|{
name|String
name|transformerClass
init|=
literal|"Unable to determine transformer class"
decl_stmt|;
try|try
block|{
name|TransformerFactory
name|factory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Transformer
name|transformer
init|=
name|factory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|String
name|classId
init|=
name|transformer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|transformerClass
operator|=
name|getClassName
argument_list|(
name|classId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|transformerClass
return|;
block|}
comment|/**      *  Perform checks on parsers, transformers and resolvers.      */
specifier|public
specifier|static
name|void
name|check
parameter_list|()
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/*          * Parser          */
name|message
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|ServiceLoader
argument_list|<
name|SAXParserFactory
argument_list|>
name|allSax
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|SAXParserFactory
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|SAXParserFactory
name|sax
range|:
name|allSax
control|)
block|{
name|message
operator|.
name|append
argument_list|(
name|getClassName
argument_list|(
name|sax
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Detected SAXParserFactory classes: "
operator|+
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|boolean
name|invalidVersionFound
init|=
literal|false
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
name|logger
operator|.
name|info
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
name|logger
operator|.
name|warn
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|invalidVersionFound
operator|=
literal|true
expr_stmt|;
block|}
comment|/*          * Transformer          */
name|message
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|ServiceLoader
argument_list|<
name|TransformerFactory
argument_list|>
name|allXsl
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|TransformerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|TransformerFactory
name|xsl
range|:
name|allXsl
control|)
block|{
name|message
operator|.
name|append
argument_list|(
name|getClassName
argument_list|(
name|xsl
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Detected TransformerFactory classes: "
operator|+
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasValidClassVersion
argument_list|(
literal|"Transformer"
argument_list|,
name|validTransformers
argument_list|,
name|message
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
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
name|logger
operator|.
name|warn
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|invalidVersionFound
operator|=
literal|true
expr_stmt|;
block|}
comment|/*          * Resolver          */
name|message
operator|=
operator|new
name|StringBuilder
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
name|logger
operator|.
name|info
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
name|logger
operator|.
name|warn
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|invalidVersionFound
operator|=
literal|true
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Using parser "
operator|+
name|determineActualParserClass
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Using transformer "
operator|+
name|determineActualTransformerClass
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|invalidVersionFound
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Using parser "
operator|+
name|determineActualParserClass
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Using transformer "
operator|+
name|determineActualTransformerClass
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *  Check if for the specified service object one of the required      * classes is available.      *       * @param type  Parser, Transformer or Resolver, used for reporting only.      * @param validClasses Array of valid classes.       * @param message  Output message of detecting classes.      * @return TRUE if valid class has been found, otherwise FALSE.      */
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
name|StringBuilder
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
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"..."
argument_list|)
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
for|for
control|(
name|ClassVersion
name|validClass
range|:
name|validClasses
control|)
block|{
name|String
name|actualVersion
init|=
name|validClass
operator|.
name|getActualVersion
argument_list|()
decl_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"Checking for "
argument_list|)
operator|.
name|append
argument_list|(
name|validClass
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
argument_list|)
operator|.
name|append
argument_list|(
name|actualVersion
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualVersion
operator|.
name|compareToIgnoreCase
argument_list|(
name|validClass
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
argument_list|)
operator|.
name|append
argument_list|(
literal|"OK!"
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|append
argument_list|(
name|validClass
operator|.
name|getRequiredVersion
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|append
argument_list|(
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
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"!"
argument_list|)
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
name|sep
argument_list|)
operator|.
name|append
argument_list|(
literal|"Please add an appropriate "
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|" to the "
operator|+
literal|"class-path, e.g. in the 'endorsed' folder of "
operator|+
literal|"the servlet container or in the 'endorsed' folder of the JRE."
argument_list|)
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|/**      * Checks to see if a valid XML Parser exists      *       * @return boolean true indicates a valid Parser was found, false otherwise      */
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
name|StringBuilder
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Checks to see if a valid XML Parser exists      *       * @param message	Messages about the status of available Parser's will       *                  be appended to this buffer      *       * @return boolean true indicates a valid Parser was found, false otherwise      */
specifier|public
specifier|static
name|boolean
name|hasValidParser
parameter_list|(
name|StringBuilder
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
comment|/**      * Checks to see if a valid XML Transformer exists      *       * @return boolean true indicates a valid Transformer was found,       *         false otherwise      */
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
name|StringBuilder
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Checks to see if a valid XML Transformer exists      *       * @param message	Messages about the status of available Transformer's       *                  will be appended to this buffer      *       * @return boolean true indicates a valid Transformer was found,       *         false otherwise      */
specifier|public
specifier|static
name|boolean
name|hasValidTransformer
parameter_list|(
name|StringBuilder
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
comment|/**      * Simple class to describe a class, its required version and how to       * obtain the actual version       */
specifier|public
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
comment|/**          * Default Constructor          *           * @param simpleName		The simple name for the class (just a           *                          descriptor really)          * @param requiredVersion	The required version of the class          * @param versionFunction	The function to be invoked to obtain the           *                          actual version of the class, must be fully           *                          qualified (i.e. includes the package name)          */
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
comment|/**          *  @return the simple name of the class          */
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
name|simpleName
return|;
block|}
comment|/**          *  @return the required version of the class          */
specifier|public
name|String
name|getRequiredVersion
parameter_list|()
block|{
return|return
name|requiredVersion
return|;
block|}
comment|/**          * Invokes the specified versionFunction using reflection to get the           * actual version of the class          *           *  @return the actual version of the class          */
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
argument_list|<
name|?
argument_list|>
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
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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

