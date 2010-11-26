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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|Result
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
name|TransformerException
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
name|stax
operator|.
name|StAXResult
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
name|stax
operator|.
name|StAXSource
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceIterator
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
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
operator|.
name|ApplyTemplates
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
name|Node
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|TransformerImpl
extends|extends
name|Transformer
block|{
specifier|public
specifier|final
name|double
name|version
init|=
literal|2.0
decl_stmt|;
specifier|private
name|XSLStylesheet
name|compiled
init|=
literal|null
decl_stmt|;
specifier|private
name|URIResolver
name|resolver
decl_stmt|;
specifier|private
name|Properties
name|outputPropertys
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parameters
init|=
literal|null
decl_stmt|;
specifier|private
name|ErrorListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getParameters
parameter_list|()
block|{
if|if
condition|(
name|parameters
operator|==
literal|null
condition|)
name|parameters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|parameters
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#clearParameters() 	 */
annotation|@
name|Override
specifier|public
name|void
name|clearParameters
parameter_list|()
block|{
name|getParameters
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#getErrorListener() 	 */
annotation|@
name|Override
specifier|public
name|ErrorListener
name|getErrorListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#getOutputProperties() 	 */
annotation|@
name|Override
specifier|public
name|Properties
name|getOutputProperties
parameter_list|()
block|{
if|if
condition|(
name|outputPropertys
operator|==
literal|null
condition|)
name|outputPropertys
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
return|return
name|outputPropertys
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#getOutputProperty(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|String
name|getOutputProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
name|getOutputProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#getParameter(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#getURIResolver() 	 */
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
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#setErrorListener(javax.xml.transform.ErrorListener) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setErrorListener
parameter_list|(
name|ErrorListener
name|listener
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#setOutputProperties(java.util.Properties) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setOutputProperties
parameter_list|(
name|Properties
name|oformat
parameter_list|)
block|{
name|outputPropertys
operator|=
name|oformat
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#setOutputProperty(java.lang.String, java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setOutputProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|getOutputProperties
argument_list|()
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#setParameter(java.lang.String, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|getParameters
argument_list|()
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#setURIResolver(javax.xml.transform.URIResolver) 	 */
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
comment|/* (non-Javadoc) 	 * @see javax.xml.transform.Transformer#transform(javax.xml.transform.Source, javax.xml.transform.Result) 	 */
annotation|@
name|Override
specifier|public
name|void
name|transform
parameter_list|(
name|Source
name|xmlSource
parameter_list|,
name|Result
name|outputTarget
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Stylesheet has not been prepared."
argument_list|)
throw|;
if|if
condition|(
name|xmlSource
operator|instanceof
name|StAXSource
operator|&&
name|outputTarget
operator|instanceof
name|StAXResult
condition|)
block|{
name|StAXSource
name|in
init|=
operator|(
name|StAXSource
operator|)
name|xmlSource
decl_stmt|;
name|StAXResult
name|out
init|=
operator|(
name|StAXResult
operator|)
name|outputTarget
decl_stmt|;
name|transformStream
argument_list|(
name|in
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"The source type "
operator|+
name|xmlSource
operator|.
name|getClass
argument_list|()
operator|+
literal|" do not supported."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|transformStream
parameter_list|(
name|StAXSource
name|source
parameter_list|,
name|StAXResult
name|out
parameter_list|)
throws|throws
name|TransformerException
block|{
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|StAXSequenceIterator
name|sequenceIterator
init|=
operator|new
name|StAXSequenceIterator
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|XSLContext
name|context
init|=
operator|new
name|XSLContext
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|context
operator|.
name|setOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|context
operator|.
name|getResultWriter
argument_list|()
operator|.
name|writeStartDocument
argument_list|(
literal|"UTF-8"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiled
operator|.
name|rootTemplate
operator|!=
literal|null
condition|)
name|compiled
operator|.
name|rootTemplate
operator|.
name|process
argument_list|(
name|context
argument_list|,
name|sequenceIterator
argument_list|)
expr_stmt|;
else|else
name|ApplyTemplates
operator|.
name|searchAndProcess
argument_list|(
name|sequenceIterator
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|getResultWriter
argument_list|()
operator|.
name|writeEndDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
comment|//		try {
comment|//			for (int event = staxXmlReader.next(); event != XMLStreamConstants.END_DOCUMENT; event = staxXmlReader.next()) {
comment|//				switch (event) {
comment|//				  case XMLStreamConstants.START_DOCUMENT:
comment|//				    System.out.println("Start document " + staxXmlReader.getLocalName());
comment|//				    break;
comment|//				  case XMLStreamConstants.START_ELEMENT:
comment|//				    System.out.println("Start element " + staxXmlReader.getLocalName());
comment|////				 	System.out.println("Element text " + staxXmlReader.getElementText());
comment|//				    break;
comment|//				  case XMLStreamConstants.END_ELEMENT:
comment|//				    System.out.println("End element " + staxXmlReader.getLocalName());
comment|//				    break;
comment|//				  default:
comment|//				    break;
comment|//				  }
comment|//				}
comment|//		} catch (Exception e) {
comment|//			e.printStackTrace();
comment|//		}
block|}
specifier|public
name|Sequence
name|transform
parameter_list|(
name|Item
name|xmlSource
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Stylesheet has not been prepared."
argument_list|)
throw|;
comment|//work around for top xpath child::
name|Sequence
name|source
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Item
name|xslItem
init|=
name|xmlSource
decl_stmt|;
if|if
condition|(
name|xmlSource
operator|instanceof
name|Node
condition|)
block|{
name|xslItem
operator|=
operator|(
name|Item
operator|)
operator|(
operator|(
name|Node
operator|)
name|xmlSource
operator|)
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
name|source
operator|.
name|add
argument_list|(
name|xslItem
argument_list|)
expr_stmt|;
return|return
name|compiled
operator|.
name|eval
argument_list|(
name|source
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|transform
parameter_list|(
name|Sequence
name|xmlSource
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Stylesheet has not been prepared."
argument_list|)
throw|;
comment|//work around for top xpath child::
name|Sequence
name|source
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
comment|//		for (Item item : xmlSource) {
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|xmlSource
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Item
name|xslItem
init|=
name|item
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|Node
condition|)
block|{
name|xslItem
operator|=
operator|(
name|Item
operator|)
operator|(
operator|(
name|Node
operator|)
name|item
operator|)
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
block|}
name|source
operator|.
name|add
argument_list|(
name|xslItem
argument_list|)
expr_stmt|;
block|}
return|return
name|compiled
operator|.
name|eval
argument_list|(
name|source
argument_list|)
return|;
block|}
specifier|public
name|void
name|setPreparedStylesheet
parameter_list|(
name|XSLStylesheet
name|compiled
parameter_list|)
block|{
name|this
operator|.
name|compiled
operator|=
name|compiled
expr_stmt|;
block|}
block|}
end_class

end_unit

