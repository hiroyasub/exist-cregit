begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|io
operator|.
name|IOException
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
name|collections
operator|.
name|CollectionConfigurationException
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
name|DocumentImpl
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
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
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|AnyURIValue
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
name|BooleanValue
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
name|StringValue
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
name|ContentHandler
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
comment|/**  * A trigger that executes a user XQuery statement when invoked.  * The XQuery source executed is the value of the context parameter named "query".  * These external variables are accessible to the user XQuery statement :  *<code>xxx:collectionName</code> : the name of the collection from which the event is triggered  *<code>xxx:documentName</code> : the name of the document from wich the event is triggered  *<code>xxx:triggeredEvent</code> : the kind of triggered event  *<code>xxx:document</code> : the document from wich the event is triggered  * @author Pierrick Brihaye<pierrick.brihaye@free.fr> */
end_comment

begin_class
specifier|public
class|class
name|XQueryTrigger
extends|extends
name|FilteringTrigger
block|{
specifier|private
name|SAXAdapter
name|adapter
decl_stmt|;
specifier|private
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|query
init|=
literal|null
decl_stmt|;
comment|/** namespace prefix associated to trigger */
specifier|private
name|String
name|bindingPrefix
init|=
literal|null
decl_stmt|;
specifier|private
name|XQuery
name|service
decl_stmt|;
specifier|private
name|ContentHandler
name|originalOutputHandler
decl_stmt|;
specifier|public
name|XQueryTrigger
parameter_list|()
block|{
name|adapter
operator|=
operator|new
name|SAXAdapter
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @link org.exist.collections.Trigger#configure(org.exist.storage.DBBroker, org.exist.collections.Collection, java.util.Map) 	 */
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
name|parameters
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Configured XQuery trigger for collection : '"
operator|+
name|parent
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|this
operator|.
name|collection
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|query
operator|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"query"
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
return|return;
name|this
operator|.
name|bindingPrefix
operator|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"bindingPrefix"
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|bindingPrefix
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|this
operator|.
name|bindingPrefix
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
name|this
operator|.
name|bindingPrefix
operator|=
name|this
operator|.
name|bindingPrefix
operator|.
name|trim
argument_list|()
operator|+
literal|":"
expr_stmt|;
name|service
operator|=
name|broker
operator|.
name|getXQueryService
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @link org.exist.collections.Trigger#prepare(java.lang.String, org.w3c.dom.Document) 	 */
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|documentName
parameter_list|,
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Preparing "
operator|+
name|eventToString
argument_list|(
name|event
argument_list|)
operator|+
literal|"XQuery trigger for document : '"
operator|+
name|documentName
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
return|return;
comment|// avoid infinite recursion by allowing just one trigger per thread
if|if
condition|(
operator|!
name|TriggerStatePerThread
operator|.
name|verifyUniqueTriggerPerThreadBeforePrepare
argument_list|(
name|this
argument_list|,
name|existingDocument
argument_list|)
condition|)
block|{
return|return;
block|}
name|TriggerStatePerThread
operator|.
name|setTransaction
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|XQueryContext
name|context
init|=
name|service
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TRIGGER
argument_list|)
decl_stmt|;
comment|//TODO : futher initializations ?
name|CompiledXQuery
name|compiledQuery
decl_stmt|;
try|try
block|{
name|compiledQuery
operator|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
comment|/*         	Variable globalVar;         	         	globalVar = new Variable(new QName("collectionName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(collection.getName()));	         	        context.declareGlobalVariable(globalVar);	          	        globalVar = new Variable(new QName("documentName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(documentName)); 	        context.declareGlobalVariable(globalVar);	                  	        globalVar = new Variable(new QName("triggerEvent", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(eventToString(event))); 	        context.declareGlobalVariable(globalVar);  	        globalVar = new Variable(new QName("document", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new NodeProxy((DocumentImpl)existingDocument)); 	        context.declareGlobalVariable(globalVar); 	        */
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"collectionName"
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"documentName"
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
name|documentName
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"triggerEvent"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|eventToString
argument_list|(
name|event
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//if (existingDocument == null)
if|if
condition|(
name|existingDocument
operator|instanceof
name|BinaryDocument
condition|)
comment|//        		TODO : encode in Base64 ?
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
else|else
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
operator|(
name|DocumentImpl
operator|)
name|existingDocument
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
comment|//prevents future use
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Error during trigger prepare"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
comment|//prevents future use
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Error during trigger prepare"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
comment|//TODO : should we provide another contextSet ?
name|NodeSet
name|contextSet
init|=
name|NodeSet
operator|.
name|EMPTY_SET
decl_stmt|;
name|service
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|,
name|contextSet
argument_list|)
expr_stmt|;
comment|//TODO : should we have a special processing ?
name|LOG
operator|.
name|debug
argument_list|(
literal|"done."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
comment|//prevents future use
throw|throw
operator|new
name|TriggerException
argument_list|(
literal|"Error during trigger prepare"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @link org.exist.collections.triggers.DocumentTrigger#finish(int, org.exist.storage.DBBroker, java.lang.String, org.w3c.dom.Document)      */
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
comment|// avoid infinite recursion by allowing just one trigger per thread
if|if
condition|(
operator|!
name|TriggerStatePerThread
operator|.
name|verifyUniqueTriggerPerThreadBeforeFinish
argument_list|(
name|this
argument_list|,
name|document
argument_list|)
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Finishing "
operator|+
name|eventToString
argument_list|(
name|event
argument_list|)
operator|+
literal|"XQuery trigger for document : '"
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
return|return;
name|XQueryContext
name|context
init|=
name|service
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TRIGGER
argument_list|)
decl_stmt|;
name|CompiledXQuery
name|compiledQuery
init|=
literal|null
decl_stmt|;
try|try
block|{
name|compiledQuery
operator|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
comment|/*         	Variable globalVar;          	globalVar = new Variable(new QName("collectionName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(collection.getName()));	     	        context.declareGlobalVariable(globalVar); 	         	        globalVar = new Variable(new QName("documentName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(documentName)); 	        context.declareGlobalVariable(globalVar);  	        globalVar = new Variable(new QName("triggerEvent", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(eventToString(event))); 	        context.declareGlobalVariable(globalVar);	    	        globalVar = new Variable(new QName("document", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new NodeProxy((DocumentImpl)document)); 	        context.declareGlobalVariable(globalVar); 	        */
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"collectionName"
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"documentName"
argument_list|,
operator|new
name|AnyURIValue
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"triggerEvent"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|eventToString
argument_list|(
name|event
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|==
name|REMOVE_DOCUMENT_EVENT
condition|)
comment|//        		Document does not exist any more -> Sequence.EMPTY_SEQUENCE
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
if|else if
condition|(
name|document
operator|instanceof
name|BinaryDocument
condition|)
comment|//        		TODO : encode in Base64 ?
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
else|else
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
operator|(
name|DocumentImpl
operator|)
name|document
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//Should never be reached
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//Should never be reached
block|}
try|try
block|{
comment|//TODO : should we provide another contextSet ?
name|NodeSet
name|contextSet
init|=
name|NodeSet
operator|.
name|EMPTY_SET
decl_stmt|;
name|service
operator|.
name|execute
argument_list|(
name|compiledQuery
argument_list|,
name|contextSet
argument_list|)
expr_stmt|;
comment|//TODO : should we have a special processing ?
name|TriggerStatePerThread
operator|.
name|setTriggerRunningState
argument_list|(
name|TriggerStatePerThread
operator|.
name|NO_TRIGGER_RUNNING
argument_list|,
name|this
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TriggerStatePerThread
operator|.
name|setTransaction
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"trigger done."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|//Should never be reached
name|LOG
operator|.
name|error
argument_list|(
literal|"trigger done with error: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|originalOutputHandler
operator|=
name|getOutputHandler
argument_list|()
expr_stmt|;
comment|//		TODO : uncomment when it works
comment|/* 		if (isValidating())  			setOutputHandler(adapter);	 		*/
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|setOutputHandler
argument_list|(
name|originalOutputHandler
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isValidating
argument_list|()
condition|)
return|return;
name|XQueryContext
name|context
init|=
name|service
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|TRIGGER
argument_list|)
decl_stmt|;
comment|//TODO : futher initializations ?
comment|// CompiledXQuery compiledQuery;
try|try
block|{
comment|// compiledQuery =
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
comment|/*         	Variable globalVar;         	         	globalVar = new Variable(new QName("collectionName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(collection.getName()));	         	        context.declareGlobalVariable(globalVar);	          	        globalVar = new Variable(new QName("documentName", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(documentName)); 	        context.declareGlobalVariable(globalVar);	                  	        globalVar = new Variable(new QName("triggerEvent", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new StringValue(eventToString(event))); 	        context.declareGlobalVariable(globalVar);  	        globalVar = new Variable(new QName("document", XQueryContext.EXIST_NS, "exist")); 	        globalVar.setValue(new NodeProxy((DocumentImpl)existingDocument)); 	        context.declareGlobalVariable(globalVar); 	        */
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"validating"
argument_list|,
operator|new
name|BooleanValue
argument_list|(
name|isValidating
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|adapter
operator|.
name|getDocument
argument_list|()
operator|==
literal|null
condition|)
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
comment|//TODO : find the right method ;-)
comment|/*         	else         		context.declareVariable(bindingPrefix + "document", (DocumentImpl)adapter.getDocument());         	*/
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
comment|//prevents future use
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Error during endDocument"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|query
operator|=
literal|null
expr_stmt|;
comment|//prevents future use
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Error during endDocument"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|//TODO : uncomment when it works
comment|/*         try {         	//TODO : should we provide another contextSet ? 	        NodeSet contextSet = NodeSet.EMPTY_SET;	         			//Sequence result = service.execute(compiledQuery, contextSet); 			//TODO : should we have a special processing ? 			LOG.debug("done."); 			         } catch (XPathException e) {         	query = null; //prevents future use         	throw new SAXException("Error during endDocument", e); 		}	 		*/
comment|//TODO : check that result is a document node
comment|//TODO : Stream result to originalOutputHandler
block|}
specifier|public
specifier|static
name|String
name|eventToString
parameter_list|(
name|int
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
condition|)
block|{
case|case
name|STORE_DOCUMENT_EVENT
case|:
return|return
literal|"STORE"
return|;
case|case
name|UPDATE_DOCUMENT_EVENT
case|:
return|return
literal|"UPDATE"
return|;
case|case
name|REMOVE_DOCUMENT_EVENT
case|:
return|return
literal|"REMOVE"
return|;
case|case
name|CREATE_COLLECTION_EVENT
case|:
return|return
literal|"CREATE"
return|;
case|case
name|RENAME_COLLECTION_EVENT
case|:
return|return
literal|"RENAME"
return|;
case|case
name|DELETE_COLLECTION_EVENT
case|:
return|return
literal|"DELETE"
return|;
default|default :
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"collection="
operator|+
name|collection
operator|+
literal|"\n"
operator|+
literal|"modifiedDocument="
operator|+
name|TriggerStatePerThread
operator|.
name|getModifiedDocument
argument_list|()
operator|+
literal|"\n"
operator|+
operator|(
name|query
operator|!=
literal|null
condition|?
name|query
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|40
argument_list|)
else|:
literal|null
operator|)
return|;
block|}
block|}
end_class

end_unit

