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
name|Iterator
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
name|Source
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
name|SourceFactory
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
name|Base64Binary
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
comment|/**  * A trigger that executes a user XQuery statement when invoked.  *   * The XQuery source executed is the value of the parameter named "query" or the  * query at the URL indicated by the parameter named "url".  *   * Any additional parameters will be declared as external variables with the type xs:string  *   * These external variables for the Trigger are accessible to the user XQuery statement  *<code>xxx:eventType</code> : the type of event for the Trigger. Either "prepare" or "finish"  *<code>xxx:collectionName</code> : the name of the collection from which the event is triggered  *<code>xxx:documentName</code> : the name of the document from which the event is triggered  *<code>xxx:triggerEvent</code> : the kind of triggered event  *<code>xxx:document</code> : the document from which the event is triggered  * xxx is the namespace prefix within the XQuery, can be set by the variable "bindingPrefix"  *   * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  * @author Adam Retter<adam.retter@devon.gov.uk> */
end_comment

begin_class
specifier|public
class|class
name|XQueryTrigger
extends|extends
name|FilteringTrigger
block|{
specifier|private
specifier|final
specifier|static
name|String
name|EVENT_TYPE_PREPARE
init|=
literal|"prepare"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EVENT_TYPE_FINISH
init|=
literal|"finish"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_BINDING_PREFIX
init|=
literal|"local:"
decl_stmt|;
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
name|strQuery
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|urlQuery
init|=
literal|null
decl_stmt|;
specifier|private
name|Properties
name|userDefinedVariables
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/** Namespace prefix associated to trigger */
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
name|this
operator|.
name|collection
operator|=
name|parent
expr_stmt|;
comment|//for an XQuery trigger there must be at least
comment|//one parameter to specify the XQuery
if|if
condition|(
name|parameters
operator|!=
literal|null
condition|)
block|{
name|urlQuery
operator|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"url"
argument_list|)
expr_stmt|;
name|strQuery
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
for|for
control|(
name|Iterator
name|itParamName
init|=
name|parameters
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itParamName
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|paramName
init|=
operator|(
name|String
operator|)
name|itParamName
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//get the binding prefix (if any)
if|if
condition|(
name|paramName
operator|.
name|equals
argument_list|(
literal|"bindingPrefix"
argument_list|)
condition|)
block|{
name|String
name|bindingPrefix
init|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"bindingPrefix"
argument_list|)
decl_stmt|;
if|if
condition|(
name|bindingPrefix
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|bindingPrefix
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|bindingPrefix
operator|=
name|bindingPrefix
operator|.
name|trim
argument_list|()
operator|+
literal|":"
expr_stmt|;
block|}
block|}
comment|//get the URL of the query (if any)
if|else if
condition|(
name|paramName
operator|.
name|equals
argument_list|(
literal|"url"
argument_list|)
condition|)
block|{
name|urlQuery
operator|=
operator|(
name|String
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"url"
argument_list|)
expr_stmt|;
block|}
comment|//get the query (if any)
if|else if
condition|(
name|paramName
operator|.
name|equals
argument_list|(
literal|"query"
argument_list|)
condition|)
block|{
name|strQuery
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
block|}
comment|//make any other parameters available as external variables for the query
else|else
block|{
name|userDefinedVariables
operator|.
name|put
argument_list|(
name|paramName
argument_list|,
name|parameters
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//set a default binding prefix if none was specified
if|if
condition|(
name|this
operator|.
name|bindingPrefix
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|bindingPrefix
operator|=
name|DEFAULT_BINDING_PREFIX
expr_stmt|;
block|}
comment|//old
if|if
condition|(
name|urlQuery
operator|!=
literal|null
operator|||
name|strQuery
operator|!=
literal|null
condition|)
block|{
name|service
operator|=
name|broker
operator|.
name|getXQueryService
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
comment|//no query to execute
name|LOG
operator|.
name|error
argument_list|(
literal|"XQuery Trigger for: '"
operator|+
name|parent
operator|.
name|getURI
argument_list|()
operator|+
literal|"' is missing its XQuery parameter"
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Get's a Source for the Trigger's XQuery 	 *  	 * @param the database broker 	 *  	 * @return the Source for the XQuery  	 */
specifier|private
name|Source
name|getQuerySource
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|Source
name|querySource
init|=
literal|null
decl_stmt|;
comment|//try and get the XQuery from a URL
if|if
condition|(
name|urlQuery
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|querySource
operator|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|,
name|urlQuery
argument_list|,
literal|false
argument_list|)
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
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|//try and get the XQuery from a string
if|else if
condition|(
name|strQuery
operator|!=
literal|null
condition|)
block|{
name|querySource
operator|=
operator|new
name|StringSource
argument_list|(
name|strQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|querySource
return|;
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
name|documentPath
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
literal|"XQuery trigger for document: '"
operator|+
name|documentPath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//get the query
name|Source
name|query
init|=
name|getQuerySource
argument_list|(
name|broker
argument_list|)
decl_stmt|;
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
name|documentPath
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
comment|//TODO : further initialisations ?
name|CompiledXQuery
name|compiledQuery
decl_stmt|;
try|try
block|{
comment|//compile the XQuery
name|compiledQuery
operator|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|query
argument_list|)
expr_stmt|;
comment|//declare external variables
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"eventType"
argument_list|,
name|EVENT_TYPE_PREPARE
argument_list|)
expr_stmt|;
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
name|documentPath
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
comment|//declare user defined parameters as external variables
for|for
control|(
name|Iterator
name|itUserVarName
init|=
name|userDefinedVariables
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itUserVarName
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|varName
init|=
operator|(
name|String
operator|)
name|itUserVarName
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|varValue
init|=
name|userDefinedVariables
operator|.
name|getProperty
argument_list|(
name|varName
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
name|varName
argument_list|,
operator|new
name|StringValue
argument_list|(
name|varValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|existingDocument
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
if|else if
condition|(
name|existingDocument
operator|instanceof
name|BinaryDocument
condition|)
block|{
comment|//binary document
name|BinaryDocument
name|bin
init|=
operator|(
name|BinaryDocument
operator|)
name|existingDocument
decl_stmt|;
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
name|bin
argument_list|)
index|]
decl_stmt|;
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
operator|new
name|Base64Binary
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//XML document
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
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
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
comment|//execute the XQuery
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
literal|"Trigger fired for prepare"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
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
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
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
literal|" XQuery trigger for document : '"
operator|+
name|documentPath
operator|+
literal|"'"
argument_list|)
expr_stmt|;
comment|//get the query
name|Source
name|query
init|=
name|getQuerySource
argument_list|(
name|broker
argument_list|)
decl_stmt|;
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
name|verifyUniqueTriggerPerThreadBeforeFinish
argument_list|(
name|this
argument_list|,
name|documentPath
argument_list|)
condition|)
block|{
return|return;
block|}
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
comment|//compile the XQuery
name|compiledQuery
operator|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|query
argument_list|)
expr_stmt|;
comment|//declare external variables
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"eventType"
argument_list|,
name|EVENT_TYPE_FINISH
argument_list|)
expr_stmt|;
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
name|documentPath
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
comment|//declare user defined parameters as external variables
for|for
control|(
name|Iterator
name|itUserVarName
init|=
name|userDefinedVariables
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itUserVarName
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|varName
init|=
operator|(
name|String
operator|)
name|itUserVarName
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|varValue
init|=
name|userDefinedVariables
operator|.
name|getProperty
argument_list|(
name|varName
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
name|varName
argument_list|,
operator|new
name|StringValue
argument_list|(
name|varValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|==
name|REMOVE_DOCUMENT_EVENT
condition|)
block|{
comment|//Document does not exist any more -> Sequence.EMPTY_SEQUENCE
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
block|}
if|else if
condition|(
name|document
operator|instanceof
name|BinaryDocument
condition|)
block|{
comment|//Binary document
name|BinaryDocument
name|bin
init|=
operator|(
name|BinaryDocument
operator|)
name|document
decl_stmt|;
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
name|bin
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
name|bin
argument_list|)
index|]
decl_stmt|;
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareVariable
argument_list|(
name|bindingPrefix
operator|+
literal|"document"
argument_list|,
operator|new
name|Base64Binary
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//XML document
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
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//Should never be reached
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|//execute the XQuery
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
literal|"Trigger fired for finish"
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
literal|"Error during trigger finish "
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
comment|//TODO : uncomment when it works
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
comment|//if (!isValidating())
comment|//		return;
comment|//XQueryContext context = service.newContext(AccessContext.TRIGGER);
comment|//TODO : futher initializations ?
comment|// CompiledXQuery compiledQuery;
comment|//try {
comment|// compiledQuery =
comment|//service.compile(context, query);
comment|//context.declareVariable(bindingPrefix + "validating", new BooleanValue(isValidating()));
comment|//if (adapter.getDocument() == null)
comment|//context.declareVariable(bindingPrefix + "document", Sequence.EMPTY_SEQUENCE);
comment|//TODO : find the right method ;-)
comment|/*         	else         		context.declareVariable(bindingPrefix + "document", (DocumentImpl)adapter.getDocument());         	*/
comment|//} catch (XPathException e) {
comment|//query = null; //prevents future use
comment|//	throw new SAXException("Error during endDocument", e);
comment|//} catch (IOException e) {
comment|//query = null; //prevents future use
comment|//	throw new SAXException("Error during endDocument", e);
comment|//}
comment|//TODO : uncomment when it works
comment|/*         try {         	//TODO : should we provide another contextSet ? 	        NodeSet contextSet = NodeSet.EMPTY_SET;	         			//Sequence result = service.execute(compiledQuery, contextSet); 			//TODO : should we have a special processing ? 			LOG.debug("done."); 			         } catch (XPathException e) {         	query = null; //prevents future use         	throw new SAXException("Error during endDocument", e); 		}	 		*/
comment|//TODO : check that result is a document node
comment|//TODO : Stream result to originalOutputHandler
block|}
comment|/** 	 * Returns a String representation of the Trigger event 	 *  	 * @param event The Trigger event 	 *  	 * @return The String representation 	 */
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
comment|/*public String toString() { 		return "collection=" + collection + "\n" + 			"modifiedDocument=" + TriggerStatePerThread.getModifiedDocument() + "\n" + 			( query != null ? query.substring(0, 40 ) : null ); 	}*/
block|}
end_class

end_unit

