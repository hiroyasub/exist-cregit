begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|validation
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|parsers
operator|.
name|XMLGrammarPreparser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|grammars
operator|.
name|Grammar
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|grammars
operator|.
name|XMLGrammarDescription
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLInputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|QName
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
name|DocumentImpl
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
name|MemTreeBuilder
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
name|NodeImpl
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
name|io
operator|.
name|ExistIOException
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
name|Configuration
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
name|XMLReaderObjectFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|GrammarPool
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|SequenceType
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  *   xQuery function for validation of XML instance documents  * using grammars like XSDs and DTDs.  *  * TODO: please use named constants  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|GrammarTooling
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_DTD
init|=
name|Namespaces
operator|.
name|DTD_NS
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_XSD
init|=
name|Namespaces
operator|.
name|SCHEMA_NS
decl_stmt|;
specifier|private
specifier|final
name|Configuration
name|config
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|cacheReport
init|=
literal|"<report>\n"
operator|+
literal|"\t<grammar type=\"...\">\n"
operator|+
literal|"\t\t<Namespace>....\n"
operator|+
literal|"\t\t<BaseSystemId>...\n"
operator|+
literal|"\t\t<LiteralSystemId>...\n"
operator|+
literal|"\t\t<ExpandedSystemId>....\n"
operator|+
literal|"\t</grammar>\n"
operator|+
literal|"\t...\n"
operator|+
literal|"\t...\n"
operator|+
literal|"</report>\n"
decl_stmt|;
comment|// Setup function signature
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"clear-grammar-cache"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Remove all cached grammers."
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"show-grammar-cache"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Show all cached grammars."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"XML document formatted as\n"
operator|+
name|cacheReport
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"pre-parse-grammar"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Pre parse grammars and add to grammar cache. Only XML schemas (.xsd)"
operator|+
literal|" are supported."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"grammar"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Reference to grammar."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Sequence of namespaces of preparsed grammars."
argument_list|)
argument_list|)
block|,                               }
decl_stmt|;
comment|/** Creates a new instance */
specifier|public
name|GrammarTooling
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|BrokerPool
name|brokerPool
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|config
operator|=
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
block|}
comment|/**       * @see org.exist.xquery.BasicFunction#eval(Sequence[], Sequence)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|GrammarPool
name|grammarpool
init|=
operator|(
name|GrammarPool
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|GRAMMER_POOL
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"clear-grammar-cache"
argument_list|)
condition|)
block|{
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|int
name|before
init|=
name|countTotalNumberOfGrammar
argument_list|(
name|grammarpool
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Clearing "
operator|+
name|before
operator|+
literal|" grammars"
argument_list|)
expr_stmt|;
name|clearGrammarPool
argument_list|(
name|grammarpool
argument_list|)
expr_stmt|;
name|int
name|after
init|=
name|countTotalNumberOfGrammar
argument_list|(
name|grammarpool
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Remained "
operator|+
name|after
operator|+
literal|" grammars"
argument_list|)
expr_stmt|;
name|int
name|delta
init|=
name|before
operator|-
name|after
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|delta
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"show-grammar-cache"
argument_list|)
condition|)
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|NodeImpl
name|result
init|=
name|writeReport
argument_list|(
name|grammarpool
argument_list|,
name|builder
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"pre-parse-grammar"
argument_list|)
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
comment|// Setup for XML schema support only
name|XMLGrammarPreparser
name|parser
init|=
operator|new
name|XMLGrammarPreparser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|registerPreparser
argument_list|(
name|TYPE_XSD
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Grammar
argument_list|>
name|allGrammars
init|=
operator|new
name|ArrayList
argument_list|<
name|Grammar
argument_list|>
argument_list|()
decl_stmt|;
comment|// iterate through the argument sequence and parse url
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
name|String
name|url
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// Fix database urls
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|=
literal|"xmldb:exist://"
operator|+
name|url
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing "
operator|+
name|url
argument_list|)
expr_stmt|;
comment|// parse XSD grammar
try|try
block|{
if|if
condition|(
name|url
operator|.
name|endsWith
argument_list|(
literal|".xsd"
argument_list|)
condition|)
block|{
name|InputStream
name|is
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|XMLInputSource
name|xis
init|=
operator|new
name|XMLInputSource
argument_list|(
literal|null
argument_list|,
name|url
argument_list|,
name|url
argument_list|,
name|is
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Grammar
name|schema
init|=
name|parser
operator|.
name|preparseGrammar
argument_list|(
name|TYPE_XSD
argument_list|,
name|xis
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|allGrammars
operator|.
name|add
argument_list|(
name|schema
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Only XMLSchemas can be preparsed."
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ExistIOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Successfully parsed "
operator|+
name|allGrammars
operator|.
name|size
argument_list|()
operator|+
literal|" grammars."
argument_list|)
expr_stmt|;
comment|// Send all XSD grammars to grammarpool
name|Grammar
name|grammars
index|[]
init|=
operator|new
name|Grammar
index|[
name|allGrammars
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|grammars
operator|=
name|allGrammars
operator|.
name|toArray
argument_list|(
name|grammars
argument_list|)
expr_stmt|;
name|grammarpool
operator|.
name|cacheGrammars
argument_list|(
name|TYPE_XSD
argument_list|,
name|grammars
argument_list|)
expr_stmt|;
comment|// Construct result to end user
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|Grammar
name|one
range|:
name|grammars
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|one
operator|.
name|getGrammarDescription
argument_list|()
operator|.
name|getNamespace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
block|{
comment|// oh oh
name|LOG
operator|.
name|error
argument_list|(
literal|"function not found error"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"function not found"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|int
name|countTotalNumberOfGrammar
parameter_list|(
name|GrammarPool
name|grammarpool
parameter_list|)
block|{
return|return
operator|(
name|grammarpool
operator|.
name|retrieveInitialGrammarSet
argument_list|(
name|TYPE_XSD
argument_list|)
operator|.
name|length
operator|+
name|grammarpool
operator|.
name|retrieveInitialGrammarSet
argument_list|(
name|TYPE_DTD
argument_list|)
operator|.
name|length
operator|)
return|;
block|}
specifier|private
name|void
name|clearGrammarPool
parameter_list|(
name|GrammarPool
name|grammarpool
parameter_list|)
block|{
name|grammarpool
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|NodeImpl
name|writeReport
parameter_list|(
name|GrammarPool
name|grammarpool
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"report"
argument_list|,
literal|"report"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Grammar
name|xsds
index|[]
init|=
name|grammarpool
operator|.
name|retrieveInitialGrammarSet
argument_list|(
name|TYPE_XSD
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|xsds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeGrammar
argument_list|(
name|xsds
index|[
name|i
index|]
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|Grammar
name|dtds
index|[]
init|=
name|grammarpool
operator|.
name|retrieveInitialGrammarSet
argument_list|(
name|TYPE_DTD
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dtds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeGrammar
argument_list|(
name|dtds
index|[
name|i
index|]
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
specifier|private
name|void
name|writeGrammar
parameter_list|(
name|Grammar
name|grammar
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|XMLGrammarDescription
name|xgd
init|=
name|grammar
operator|.
name|getGrammarDescription
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
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
name|xgd
operator|.
name|getGrammarType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"grammar"
argument_list|,
literal|"grammar"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|String
name|namespace
init|=
name|xgd
operator|.
name|getNamespace
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"Namespace"
argument_list|,
literal|"Namespace"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|String
name|publicId
init|=
name|xgd
operator|.
name|getPublicId
argument_list|()
decl_stmt|;
if|if
condition|(
name|publicId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"PublicId"
argument_list|,
literal|"PublicId"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|publicId
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|String
name|baseSystemId
init|=
name|xgd
operator|.
name|getBaseSystemId
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseSystemId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"BaseSystemId"
argument_list|,
literal|"BaseSystemId"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|baseSystemId
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|String
name|literalSystemId
init|=
name|xgd
operator|.
name|getLiteralSystemId
argument_list|()
decl_stmt|;
if|if
condition|(
name|literalSystemId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"LiteralSystemId"
argument_list|,
literal|"LiteralSystemId"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|literalSystemId
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|String
name|expandedSystemId
init|=
name|xgd
operator|.
name|getExpandedSystemId
argument_list|()
decl_stmt|;
if|if
condition|(
name|expandedSystemId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"ExpandedSystemId"
argument_list|,
literal|"ExpandedSystemId"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|expandedSystemId
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
name|attribs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

