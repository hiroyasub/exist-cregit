begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|process
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
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
name|memtree
operator|.
name|ElementImpl
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
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|ExtendedXMLStreamReader
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
name|FileUtils
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
name|*
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
name|*
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|Execute
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"execute"
argument_list|,
name|ProcessModule
operator|.
name|NAMESPACE
argument_list|,
name|ProcessModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|""
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"args"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"a list of strings which signifies the external program file to be invoked and its arguments, if any"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"an XML fragment defining optional parameters like working directory or the lines to send to "
operator|+
literal|"the process via stdin. Format:<options><workingDir>workingDir</workingDir>"
operator|+
literal|"<environment><env name=\"name\" value=\"value\"/></environment><stdin><line>line</line></stdin></options>"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the sequence of code points"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|RESULT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"execution"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|COMMAND_LINE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"commandline"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|STDOUT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"stdout"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|LINE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"line"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|public
name|Execute
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"process:execute is only available to users with dba role"
argument_list|)
throw|;
comment|// create list of parameters to pass to shell
name|List
argument_list|<
name|String
argument_list|>
name|cmdArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
index|]
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|cmdArgs
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// parse options
name|List
argument_list|<
name|String
argument_list|>
name|stdin
init|=
literal|null
decl_stmt|;
name|Path
name|workingDir
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
specifier|final
name|NodeValue
name|options
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|thisLevel
init|=
name|options
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
specifier|final
name|XMLStreamReader
name|reader
init|=
name|context
operator|.
name|getXMLStreamReader
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
name|String
name|name
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"workingDir"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|workingDir
operator|=
name|getWorkingDir
argument_list|(
name|reader
operator|.
name|getElementText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"line"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|stdin
operator|==
literal|null
condition|)
name|stdin
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|21
argument_list|)
expr_stmt|;
name|stdin
operator|.
name|add
argument_list|(
name|reader
operator|.
name|getElementText
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"env"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|environment
operator|==
literal|null
condition|)
name|environment
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|String
name|key
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
name|value
operator|!=
literal|null
condition|)
name|environment
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
specifier|final
name|NodeId
name|otherId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|otherLevel
init|=
name|otherId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherLevel
operator|==
name|thisLevel
condition|)
block|{
comment|// finished `optRoot` element...
break|break;
comment|// exit-while
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Invalid XML fragment for options: "
operator|+
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating process "
operator|+
name|cmdArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmdArgs
argument_list|)
decl_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|workingDir
operator|!=
literal|null
condition|)
name|pb
operator|.
name|directory
argument_list|(
name|workingDir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|environment
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|pb
operator|.
name|environment
argument_list|()
decl_stmt|;
name|env
operator|.
name|putAll
argument_list|(
name|environment
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Process
name|process
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
if|if
condition|(
name|stdin
operator|!=
literal|null
condition|)
block|{
try|try
init|(
specifier|final
name|Writer
name|writer
init|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|process
operator|.
name|getOutputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
specifier|final
name|String
name|line
range|:
name|stdin
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|String
argument_list|>
name|output
init|=
name|readOutput
argument_list|(
name|process
argument_list|)
decl_stmt|;
name|int
name|exitValue
init|=
name|process
operator|.
name|waitFor
argument_list|()
decl_stmt|;
return|return
name|createReport
argument_list|(
name|exitValue
argument_list|,
name|output
argument_list|,
name|cmdArgs
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An IO error occurred while executing the process "
operator|+
name|cmdArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"process:execute was interrupted while waiting for process "
operator|+
name|cmdArgs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Path
name|getWorkingDir
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
specifier|final
name|Path
name|file
init|=
name|Paths
operator|.
name|get
argument_list|(
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|file
return|;
block|}
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
return|return
name|FileUtils
operator|.
name|resolve
argument_list|(
name|home
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|private
name|ElementImpl
name|createReport
parameter_list|(
name|int
name|exitValue
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|output
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|cmdArgs
parameter_list|)
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"exitCode"
argument_list|,
literal|"exitCode"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|exitValue
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|RESULT_QNAME
argument_list|,
name|attribs
argument_list|)
decl_stmt|;
comment|// print command line
name|StringBuilder
name|cmdLine
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|param
range|:
name|cmdArgs
control|)
block|{
name|cmdLine
operator|.
name|append
argument_list|(
name|param
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|COMMAND_LINE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|cmdLine
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// print received output to<stdout>
name|builder
operator|.
name|startElement
argument_list|(
name|STDOUT_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|line
range|:
name|output
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|LINE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|ElementImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readOutput
parameter_list|(
name|Process
name|process
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
init|(
specifier|final
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|process
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|output
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|31
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
return|return
name|output
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An IO error occurred while reading output from the process: "
operator|+
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
end_class

end_unit
