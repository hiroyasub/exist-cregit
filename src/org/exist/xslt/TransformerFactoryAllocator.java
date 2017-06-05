begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXTransformerFactory
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
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  * Allows the TransformerFactory that is used for XSLT to be  * chosen through configuration settings in conf.xml  *  * Within eXist this class should be used instead of  * directly calling SAXTransformerFactory.newInstance() directly  *  * @author Adam Retter<adam.retter@googlemail.com>  * @author Andrzej Taramina<andrzej@chaeron.com>  */
end_comment

begin_class
specifier|public
class|class
name|TransformerFactoryAllocator
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"transformer"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TRANSFORMER_CLASS_ATTRIBUTE
init|=
literal|"class"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_TRANSFORMER_CLASS
init|=
literal|"transformer.class"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONFIGURATION_TRANSFORMER_ATTRIBUTE_ELEMENT_NAME
init|=
literal|"attribute"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_TRANSFORMER_ATTRIBUTES
init|=
literal|"transformer.attributes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TRANSFORMER_CACHING_ATTRIBUTE
init|=
literal|"caching"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_CACHING_ATTRIBUTE
init|=
literal|"transformer.caching"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_BROKER_POOL
init|=
literal|"transformer.brokerPool"
decl_stmt|;
comment|//private constructor
specifier|private
name|TransformerFactoryAllocator
parameter_list|()
block|{
block|}
comment|/**      * Get the TransformerFactory defined in conf.xml      * If the class can't be found or the given class doesn't implement      * the required interface, the default factory is returned.      *      * @param pool A database broker pool, used for reading the conf.xml configuration      * @return A SAXTransformerFactory, for which newInstance() can then be called      *      * Typical usage:      *      * Instead of SAXTransformerFactory.newInstance() use      * TransformerFactoryAllocator.getTransformerFactory(broker).newInstance()      */
specifier|public
specifier|static
name|SAXTransformerFactory
name|getTransformerFactory
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|)
block|{
comment|//Get the transformer class name from conf.xml
specifier|final
name|String
name|transformerFactoryClassName
init|=
operator|(
name|String
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TRANSFORMER_CLASS
argument_list|)
decl_stmt|;
comment|//Was a TransformerFactory class specified?
name|SAXTransformerFactory
name|factory
decl_stmt|;
if|if
condition|(
name|transformerFactoryClassName
operator|==
literal|null
condition|)
block|{
comment|//No, use the system default
name|factory
operator|=
operator|(
name|SAXTransformerFactory
operator|)
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//Try and load the specified TransformerFactory class
try|try
block|{
name|factory
operator|=
operator|(
name|SAXTransformerFactory
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|transformerFactoryClassName
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set transformer factory: "
operator|+
name|transformerFactoryClassName
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|(
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TRANSFORMER_ATTRIBUTES
argument_list|)
decl_stmt|;
specifier|final
name|Enumeration
argument_list|<
name|String
argument_list|>
name|attrNames
init|=
name|attributes
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|attrNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|attrNames
operator|.
name|nextElement
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|value
init|=
name|attributes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|factory
operator|.
name|setAttribute
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set transformer attribute: "
operator|+
literal|", "
operator|+
literal|"name: "
operator|+
name|name
operator|+
literal|", value: "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to set attribute for TransformerFactory: '"
operator|+
name|transformerFactoryClassName
operator|+
literal|"', name: "
operator|+
name|name
operator|+
literal|", value: "
operator|+
name|value
operator|+
literal|", exception: "
operator|+
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|factory
operator|.
name|setAttribute
argument_list|(
name|PROPERTY_BROKER_POOL
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|//some transformers do not support "setAttribute"
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error found loading the requested TrAX Transformer Factory '"
operator|+
name|transformerFactoryClassName
operator|+
literal|"'. Using default TrAX Transformer Factory instead: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
comment|//Fallback to system default
name|factory
operator|=
operator|(
name|SAXTransformerFactory
operator|)
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

