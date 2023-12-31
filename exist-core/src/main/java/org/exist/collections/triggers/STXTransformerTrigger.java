begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

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
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|BinaryDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|Constants
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * STXTransformerTrigger applies an STX stylesheet to the input SAX stream,  * using<a href="http://joost.sourceforge.net">Joost</a>. The stylesheet location  * is identified by parameter "src". If the src parameter is just a path, the stylesheet  * will be loaded from the database, otherwise, it is interpreted as an URI.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|STXTransformerTrigger
extends|extends
name|SAXTrigger
implements|implements
name|DocumentTrigger
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|TransformerFactory
operator|.
name|newInstance
argument_list|(
literal|"net.sf.joost.trax.TransformerFactoryImpl"
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|TransformerHandler
name|handler
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
name|super
operator|.
name|configure
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|parent
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
specifier|final
name|String
name|stylesheet
init|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"src"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"STXTransformerTrigger requires an attribute 'src'"
argument_list|)
throw|;
block|}
comment|/*         String origProperty = System.getProperty("javax.xml.transform.TransformerFactory");         System.setProperty("javax.xml.transform.TransformerFactory",  "net.sf.joost.trax.TransformerFactoryImpl");         factory = (SAXTransformerFactory)TransformerFactory.newInstance();         // reset property to previous setting         if(origProperty != null) {                 System.setProperty("javax.xml.transform.TransformerFactory", origProperty);         }          */
comment|/*ServiceLoader<TransformerFactory> loader = ServiceLoader.load(TransformerFactory.class);         for(TransformerFactory transformerFactory : loader) {             if(transformerFactory.getClass().getName().equals("net.sf.joost.trax.TransformerFactoryImpl")) {                     factory = transformerFactory.ne             }         }*/
name|XmldbURI
name|stylesheetUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stylesheetUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|stylesheet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
block|}
comment|//TODO: allow full XmldbURIs to be used as well.
if|if
condition|(
name|stylesheetUri
operator|==
literal|null
operator|||
name|stylesheet
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|stylesheetUri
operator|=
name|parent
operator|.
name|getURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|stylesheetUri
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
try|try
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|stylesheetUri
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"stylesheet "
operator|+
name|stylesheetUri
operator|+
literal|" not found in database"
argument_list|)
throw|;
block|}
if|if
condition|(
name|doc
operator|instanceof
name|BinaryDocument
condition|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"stylesheet "
operator|+
name|stylesheetUri
operator|+
literal|" must be stored as an xml document and not a binary document!"
argument_list|)
throw|;
block|}
name|handler
operator|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|STXTemplatesCache
operator|.
name|getInstance
argument_list|()
operator|.
name|getOrUpdateTemplate
argument_list|(
name|broker
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransformerConfigurationException
decl||
name|PermissionDeniedException
decl||
name|SAXException
decl||
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"compiling stylesheet "
operator|+
name|stylesheet
argument_list|)
expr_stmt|;
specifier|final
name|Templates
name|template
init|=
name|factory
operator|.
name|newTemplates
argument_list|(
operator|new
name|StreamSource
argument_list|(
name|stylesheet
argument_list|)
argument_list|)
decl_stmt|;
name|handler
operator|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|prepare
parameter_list|()
block|{
comment|//XXX: refactoring required!!!
comment|//        final SAXResult result = new SAXResult();
comment|//        result.setHandler(getOutputHandler());
comment|//        result.setLexicalHandler(getLexicalOutputHandler());
comment|//        handler.setResult(result);
comment|//        setOutputHandler(handler);
comment|//        setLexicalOutputHandler(handler);
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
name|prepare
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterUpdateDocumentMetadata
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
block|}
end_class

end_unit

