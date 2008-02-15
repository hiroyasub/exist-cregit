begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|*
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
name|value
operator|.
name|Sequence
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
comment|/**  * An XML document from the database.  *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|XMLDocument
extends|extends
name|Document
block|{
specifier|private
specifier|final
name|NodeProxy
name|proxy
decl_stmt|;
comment|/** 	 * Create a new XML document wrapper for the given document, and start tracking 	 * the node. 	 * 	 * @param dimpl the document implementation to wrap 	 * @param namespaceBindings the namespace bindings to use 	 * @param db the database the document is part of 	 */
name|XMLDocument
parameter_list|(
name|DocumentImpl
name|dimpl
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|Database
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|dimpl
argument_list|,
name|namespaceBindings
argument_list|,
name|db
argument_list|)
expr_stmt|;
if|if
condition|(
name|dimpl
operator|instanceof
name|BinaryDocument
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"binary document impl passed to XML document constructor"
argument_list|)
throw|;
name|proxy
operator|=
operator|new
name|NodeProxy
argument_list|(
name|dimpl
argument_list|)
expr_stmt|;
comment|// no need to track a DocumentImpl proxy, since its gid cannot change
block|}
annotation|@
name|Override
name|Sequence
name|convertToSequence
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
comment|/** 	 * Return this XML document. 	 *  	 * @return this document 	 */
annotation|@
name|Override
specifier|public
name|XMLDocument
name|xml
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/** 	 * Return the root element node of this document. 	 * 	 * @return the root element node of this document 	 */
specifier|public
name|org
operator|.
name|exist
operator|.
name|fluent
operator|.
name|Node
name|root
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
return|return
name|query
argument_list|()
operator|.
name|single
argument_list|(
literal|"*"
argument_list|)
operator|.
name|node
argument_list|()
return|;
block|}
comment|/** 	 * Return a query service that executes queries in the context of this document. 	 *  	 * @return a query service over this document 	 */
annotation|@
name|Override
specifier|public
name|QueryService
name|query
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|query
argument_list|()
return|;
block|}
annotation|@
name|Override
name|QueryService
name|createQueryService
parameter_list|()
block|{
comment|// must explicitly return null here to avoid getting stuck with a NULL from superclass
return|return
literal|null
return|;
block|}
comment|/** 	 * Return a string representation of the reference to this document.  The representation will 	 * list the document's path, but will not include its contents. 	 *  	 * @return a string representation of this XML document 	 */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"XML "
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|XMLDocument
name|copy
parameter_list|(
name|Folder
name|destination
parameter_list|,
name|Name
name|name
parameter_list|)
block|{
return|return
operator|(
name|XMLDocument
operator|)
name|super
operator|.
name|copy
argument_list|(
name|destination
argument_list|,
name|name
argument_list|)
return|;
block|}
comment|/** 	 * Return the serialized contents of this XML document. 	 * 	 * @return the serialized contents of this XML document 	 */
specifier|public
name|String
name|contentsAsString
parameter_list|()
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|write
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * Serialize this document to the given output stream using the default encoding specified 	 * for the database.  If you wish to control the encoding at a finer granularity, use 	 * {@link #write(Writer)}. 	 *  	 * @see Database#setDefaultCharacterEncoding(String) 	 * @param stream the output stream to write to 	 * @throws IOException in case of problems with the encoding 	 * @throws DatabaseException in case of I/O problems 	 */
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|stream
argument_list|,
name|db
operator|.
name|defaultCharacterEncoding
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Serialize this document to the given writer. 	 * 	 * @param writer destination writer 	 */
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|db
operator|.
name|acquireBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|getSerializer
argument_list|()
operator|.
name|serialize
argument_list|(
name|doc
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

