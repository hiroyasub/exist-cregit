begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  *  Helper class for creating an instance of javax.xml.parsers.SAXParserFactory  *   * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|ExistSAXParserFactory
block|{
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
name|ExistSAXParserFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ORG_EXIST_SAXPARSERFACTORY
init|=
literal|"org.exist.SAXParserFactory"
decl_stmt|;
comment|/**      *  Get SAXParserFactory instance specified by factory class name.      *      * @param className Full class name of factory      *      * @return A Sax parser factory or NULL when not available.      */
specifier|public
specifier|static
name|SAXParserFactory
name|getSAXParserFactory
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|clazz
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
comment|// ClassNotFoundException
comment|// quick escape
name|LOG
operator|.
name|debug
argument_list|(
name|className
operator|+
literal|": "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Get specific method
name|Method
name|method
init|=
literal|null
decl_stmt|;
try|try
block|{
name|method
operator|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"newInstance"
argument_list|,
operator|(
name|Class
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
comment|// SecurityException and NoSuchMethodException
comment|// quick escape
name|LOG
operator|.
name|debug
argument_list|(
literal|"Method "
operator|+
name|className
operator|+
literal|".newInstance not found."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Invoke method
name|Object
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|method
operator|.
name|invoke
argument_list|(
literal|null
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
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
comment|//IllegalAccessException and InvocationTargetException
comment|// quick escape
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not invoke method "
operator|+
name|className
operator|+
literal|".newInstance."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|result
operator|instanceof
name|SAXParserFactory
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not create instance of SAXParserFactory: "
operator|+
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
operator|(
name|SAXParserFactory
operator|)
name|result
return|;
block|}
comment|/**      *  Get instance of a SAXParserFactory. Return factory specified by      * system property org.exist.SAXParserFactory (if available) otherwise      * return system default.      *      * @return A sax parser factory.      */
specifier|public
specifier|static
name|SAXParserFactory
name|getSAXParserFactory
parameter_list|()
block|{
name|SAXParserFactory
name|factory
init|=
literal|null
decl_stmt|;
comment|// Get SAXParser configuratin from system
specifier|final
name|String
name|config
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|ORG_EXIST_SAXPARSERFACTORY
argument_list|)
decl_stmt|;
comment|// Get SAXparser factory specified by system property
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|factory
operator|=
name|getSAXParserFactory
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|// If no factory could be retrieved, create system default property.
if|if
condition|(
name|factory
operator|==
literal|null
condition|)
block|{
name|factory
operator|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Fall back: using default SAXParserFactory '%s'"
argument_list|,
name|factory
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

