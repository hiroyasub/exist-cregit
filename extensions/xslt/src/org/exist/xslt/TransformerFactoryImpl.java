begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|ErrorListener
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
name|Source
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
name|Templates
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
name|TransformerConfigurationException
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
name|URIResolver
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TemplatesHandler
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
name|TransformerHandler
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
name|stream
operator|.
name|StreamSource
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
name|EXistException
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|XMLFilter
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|TransformerFactoryImpl
extends|extends
name|SAXTransformerFactory
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
name|TransformerFactoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
name|URIResolver
name|resolver
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ErrorListener
name|errorListener
init|=
literal|null
decl_stmt|;
specifier|public
name|TransformerFactoryImpl
parameter_list|()
block|{
block|}
specifier|public
name|void
name|setBrokerPool
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
throws|throws
name|EXistException
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
return|return
name|pool
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"that shouldn't happend. internal error."
argument_list|)
throw|;
block|}
specifier|public
name|void
name|releaseBroker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Database wan't set properly."
argument_list|)
throw|;
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#getAssociatedStylesheet(javax.xml.transform.Source, java.lang.String, java.lang.String, java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Source
name|getAssociatedStylesheet
parameter_list|(
name|Source
name|source
parameter_list|,
name|String
name|media
parameter_list|,
name|String
name|title
parameter_list|,
name|String
name|charset
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.getAssociatedStylesheet"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#getAttribute(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#getErrorListener() 	 */
annotation|@
name|Override
specifier|public
name|ErrorListener
name|getErrorListener
parameter_list|()
block|{
return|return
name|errorListener
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#getFeature(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|getFeature
parameter_list|(
name|String
name|name
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.getFeature"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#getURIResolver() 	 */
annotation|@
name|Override
specifier|public
name|URIResolver
name|getURIResolver
parameter_list|()
block|{
return|return
name|resolver
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#newTemplates(javax.xml.transform.Source) 	 */
annotation|@
name|Override
specifier|public
name|Templates
name|newTemplates
parameter_list|(
name|Source
name|source
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
comment|//XXX: handle buffered input stream
if|if
condition|(
name|source
operator|instanceof
name|SourceImpl
condition|)
block|{
try|try
block|{
return|return
name|XSL
operator|.
name|compile
argument_list|(
operator|(
operator|(
name|Document
operator|)
operator|(
operator|(
name|SourceImpl
operator|)
name|source
operator|)
operator|.
name|source
operator|)
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|source
operator|instanceof
name|Element
condition|)
block|{
try|try
block|{
return|return
name|XSL
operator|.
name|compile
argument_list|(
operator|(
name|Element
operator|)
name|source
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|source
operator|instanceof
name|InputStream
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getBroker
argument_list|()
expr_stmt|;
return|return
name|XSL
operator|.
name|compile
argument_list|(
operator|(
name|InputStream
operator|)
name|source
argument_list|,
name|broker
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
if|else if
condition|(
name|source
operator|instanceof
name|StreamSource
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getBroker
argument_list|()
expr_stmt|;
return|return
name|XSL
operator|.
name|compile
argument_list|(
operator|(
operator|(
name|StreamSource
operator|)
name|source
operator|)
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|broker
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Compilation error."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Not supported source "
operator|+
name|source
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#newTransformer() 	 */
annotation|@
name|Override
specifier|public
name|Transformer
name|newTransformer
parameter_list|()
throws|throws
name|TransformerConfigurationException
block|{
comment|//TODO: setURIresolver ???
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|TransformerImpl
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#newTransformer(javax.xml.transform.Source) 	 */
annotation|@
name|Override
specifier|public
name|Transformer
name|newTransformer
parameter_list|(
name|Source
name|source
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.newTransformer"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#setAttribute(java.lang.String, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_BROKER_POOL
argument_list|)
condition|)
name|pool
operator|=
operator|(
name|BrokerPool
operator|)
name|value
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#setErrorListener(javax.xml.transform.ErrorListener) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setErrorListener
parameter_list|(
name|ErrorListener
name|listener
parameter_list|)
block|{
name|errorListener
operator|=
name|listener
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#setFeature(java.lang.String, boolean) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setFeature
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.setFeature"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.TransformerFactory#setURIResolver(javax.xml.transform.URIResolver) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setURIResolver
parameter_list|(
name|URIResolver
name|resolver
parameter_list|)
block|{
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TemplatesHandler
name|newTemplatesHandler
parameter_list|()
throws|throws
name|TransformerConfigurationException
block|{
return|return
operator|new
name|TemplatesHandlerImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TransformerHandler
name|newTransformerHandler
parameter_list|()
throws|throws
name|TransformerConfigurationException
block|{
return|return
operator|new
name|TransformerHandlerImpl
argument_list|(
operator|new
name|XSLContext
argument_list|(
name|pool
argument_list|)
argument_list|,
name|newTransformer
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TransformerHandler
name|newTransformerHandler
parameter_list|(
name|Source
name|src
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.newTransformerHandler"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|TransformerHandler
name|newTransformerHandler
parameter_list|(
name|Templates
name|templates
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
if|if
condition|(
name|templates
operator|==
literal|null
condition|)
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Templates object can not be null."
argument_list|)
throw|;
if|if
condition|(
operator|!
operator|(
name|templates
operator|instanceof
name|XSLStylesheet
operator|)
condition|)
throw|throw
operator|new
name|TransformerConfigurationException
argument_list|(
literal|"Templates object was not created by exist xslt ("
operator|+
name|templates
operator|.
name|getClass
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
return|return
operator|new
name|TransformerHandlerImpl
argument_list|(
operator|new
name|XSLContext
argument_list|(
name|pool
argument_list|)
argument_list|,
name|templates
operator|.
name|newTransformer
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|XMLFilter
name|newXMLFilter
parameter_list|(
name|Source
name|src
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.newXMLFilter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|XMLFilter
name|newXMLFilter
parameter_list|(
name|Templates
name|templates
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not implemented: TransformerFactory.newXMLFilter"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

