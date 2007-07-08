begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: TransformerFactoryAllocator.java 0000 2006-08-10 22:39:00 +0000 (Thu, 10 Aug 2006) deliriumsky $  */
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
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * Allows the TransformerFactory that is used for XSLT to be  * chosen through configuration settings in conf.xml  *  * Within eXist this class should be used instead of  * directly calling SAXTransformerFactory.newInstance() directly  *  * @author Adam Retter<adam.retter@devon.gov.uk>  */
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
name|Logger
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
comment|//private constructor
specifier|private
name|TransformerFactoryAllocator
parameter_list|()
block|{
block|}
comment|/**      * Get the TransformerFactory defined in conf.xml      * If the class can't be found or the given class doesn't implement      * the required interface, the default factory is returned.      *      * @param broker A database broker, used for reading the conf.xml configuration      *      * @return  A SAXTransformerFactory, for which newInstance() can then be called      *      *      * Typical usage:      *      * Instead of SAXTransformerFactory.newInstance() use      * TransformerFactoryAllocator.getTransformerFactory(broker).newInstance()      */
specifier|public
specifier|static
name|SAXTransformerFactory
name|getTransformerFactory
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|SAXTransformerFactory
name|factory
decl_stmt|;
comment|//get the transformer class name from conf.xml
name|String
name|transformerFactoryClassName
init|=
operator|(
name|String
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TRANSFORMER_CLASS
argument_list|)
decl_stmt|;
comment|//        LOG.debug("transformerFactoryClassName=" + transformerFactoryClassName);
comment|//        LOG.debug("javax.xml.transform.TransformerFactory="
comment|//                + System.getProperty("javax.xml.transform.TransformerFactory"));
comment|//was a TransformerFactory class specified
if|if
condition|(
name|transformerFactoryClassName
operator|==
literal|null
condition|)
block|{
comment|//no, use the system default
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
comment|//try and load the specified TransformerFactory class
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
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnfe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot find the requested TrAX factory '"
operator|+
name|transformerFactoryClassName
operator|+
literal|"'. Using default TrAX Transformer Factory instead."
argument_list|)
expr_stmt|;
comment|//fallback to system default
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
catch|catch
parameter_list|(
name|ClassCastException
name|cce
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"The indicated class '"
operator|+
name|transformerFactoryClassName
operator|+
literal|"' is not a TrAX Transformer Factory. "
operator|+
literal|"Using default TrAX Transformer Factory instead."
argument_list|)
expr_stmt|;
comment|//fallback to system default
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error found loading the requested TrAX Transformer Factory '"
operator|+
name|transformerFactoryClassName
operator|+
literal|"'. Using default TrAX Transformer Factory instead."
argument_list|)
expr_stmt|;
comment|//fallback to system default
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

