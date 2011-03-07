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
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|EXistException
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
name|DocumentAtExist
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
name|ElementAtExist
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
name|AnalyzeContextInfo
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
name|Expression
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
name|Optimizer
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
name|util
operator|.
name|ExpressionDumper
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
name|compiler
operator|.
name|Factory
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
name|compiler
operator|.
name|XSLElement
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
name|InputSource
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_comment
comment|/**  *<xsl:stylesheet version="1.0"  *         xmlns:xsl="http://www.w3.org/1999/XSL/Transform">  *   *<xsl:import href="..."/>  *<xsl:include href="..."/>  *<xsl:strip-space elements="..."/>  *<xsl:preserve-space elements="..."/>  *<xsl:output method="..."/>  *<xsl:key name="..." match="..." use="..."/>  *<xsl:decimal-format name="..."/>  *<xsl:namespace-alias stylesheet-prefix="..." result-prefix="..."/>  *<xsl:attribute-set name="...">...</xsl:attribute-set>  *<xsl:variable name="...">...</xsl:variable>  *<xsl:param name="...">...</xsl:param>  *<xsl:template match="...">...</xsl:template>  *<xsl:template name="...">...</xsl:template>  *</xsl:stylesheet>	  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSL
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
name|XSL
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|XSL
parameter_list|()
block|{
block|}
specifier|protected
specifier|static
name|XSLStylesheet
name|compile
parameter_list|(
name|ElementAtExist
name|source
parameter_list|)
throws|throws
name|XPathException
block|{
name|BrokerPool
name|pool
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
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|compile
argument_list|(
name|source
argument_list|,
name|broker
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|XSLStylesheet
name|compile
parameter_list|(
name|ElementAtExist
name|source
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|XPathException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|XSLElement
name|stylesheet
init|=
operator|new
name|XSLElement
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
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|setDefaultFunctionNamespace
argument_list|(
name|Factory
operator|.
name|namespaceURI
argument_list|)
expr_stmt|;
name|XSLStylesheet
name|expr
init|=
operator|(
name|XSLStylesheet
operator|)
name|stylesheet
operator|.
name|compile
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|AnalyzeContextInfo
name|info
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
operator|(
name|XQueryContext
operator|)
name|context
argument_list|)
decl_stmt|;
name|info
operator|.
name|setFlags
argument_list|(
name|Expression
operator|.
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|expr
operator|.
name|analyze
argument_list|(
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|optimizationsEnabled
argument_list|()
condition|)
block|{
name|Optimizer
name|optimizer
init|=
operator|new
name|Optimizer
argument_list|(
operator|(
name|XQueryContext
operator|)
name|context
argument_list|)
decl_stmt|;
name|expr
operator|.
name|accept
argument_list|(
name|optimizer
argument_list|)
expr_stmt|;
if|if
condition|(
name|optimizer
operator|.
name|hasOptimized
argument_list|()
condition|)
block|{
name|context
operator|.
name|reset
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expr
operator|.
name|resetState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expr
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
comment|// Log the query if it is not too large, but avoid
comment|// dumping huge queries to the log
if|if
condition|(
name|context
operator|.
name|getExpressionCount
argument_list|()
operator|<
literal|150
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XSL diagnostics:\n"
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"XSL diagnostics:\n"
operator|+
literal|"[skipped: more than 150 expressions]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Compilation took "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
return|return
name|expr
return|;
block|}
specifier|protected
specifier|static
name|XSLStylesheet
name|compile
parameter_list|(
name|InputStream
name|source
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|DocumentAtExist
name|document
init|=
operator|(
name|DocumentAtExist
operator|)
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
comment|//			document.setContext(new XSLContext(broker));
comment|//return receiver.getDocument();
return|return
name|compile
argument_list|(
operator|(
name|ElementAtExist
operator|)
name|document
operator|.
name|getDocumentElement
argument_list|()
argument_list|,
name|broker
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
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
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
name|XPathException
argument_list|(
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
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

