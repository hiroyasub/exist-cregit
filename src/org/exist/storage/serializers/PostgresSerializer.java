begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
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
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|*
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
name|*
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
name|*
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
name|security
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
name|storage
operator|.
name|DBConnectionPool
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
name|w3c
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
name|xml
operator|.
name|sax
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  Specialized implementation of Serializer for PostgreSQL. Since loading all  *  the document's nodes is quite slow with the default serializer, this  *  implementation just calls a stored procedure to do the job.  *  *@author     wolf  *@created    3. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|PostgresSerializer
extends|extends
name|Serializer
block|{
specifier|protected
specifier|final
specifier|static
name|String
name|sql
init|=
literal|"select exist_node_to_str(?, ?, ?)"
decl_stmt|;
specifier|protected
name|SAXParserFactory
name|saxFactory
init|=
literal|null
decl_stmt|;
specifier|protected
name|PreparedStatement
name|stmt
decl_stmt|;
comment|/**      *  Constructor for the PostgresSerializer object      *      *@param  broker  Description of the Parameter      *@param  pool    Description of the Parameter      */
specifier|public
name|PostgresSerializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|DBConnectionPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|pool
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|stmt
operator|=
name|con
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|sqe
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|release
argument_list|(
name|con
argument_list|)
expr_stmt|;
name|saxFactory
operator|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  set               Description of the Parameter      *@param  start             Description of the Parameter      *@param  howmany           Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
throws|throws
name|SAXException
block|{
name|toSAX
argument_list|(
name|set
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  set               Description of the Parameter      *@param  start             Description of the Parameter      *@param  howmany           Description of the Parameter      *@param  queryTime         Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|long
name|queryTime
parameter_list|)
throws|throws
name|SAXException
block|{
name|SAXParser
name|sax
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sax
operator|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
try|try
block|{
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://xml.org/sax/properties/lexical-handler"
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
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
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
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
block|}
name|String
name|xml
init|=
name|serializeToString
argument_list|(
name|set
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
decl_stmt|;
try|try
block|{
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  doc               Description of the Parameter      *@param  docEvent          Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Document
name|doc
parameter_list|,
name|boolean
name|docEvent
parameter_list|)
throws|throws
name|SAXException
block|{
name|serializeToSAX
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  doc               Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|SAXException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|doc
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|SAXParser
name|sax
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sax
operator|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://xml.org/sax/properties/lexical-handler"
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
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
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
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
block|}
name|String
name|xml
init|=
name|serializeToString
argument_list|(
name|doc
argument_list|)
decl_stmt|;
comment|//LOG.debug( xml );
try|try
block|{
name|java
operator|.
name|io
operator|.
name|FileWriter
name|fw
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|FileWriter
argument_list|(
literal|"testoutput.xml"
argument_list|)
decl_stmt|;
name|fw
operator|.
name|write
argument_list|(
name|xml
argument_list|)
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error: "
operator|+
name|exc
argument_list|)
expr_stmt|;
name|exc
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"serializing document took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  n                 Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|SAXException
block|{
name|SAXParser
name|sax
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sax
operator|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
try|try
block|{
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://xml.org/sax/properties/lexical-handler"
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
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
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
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
block|}
name|String
name|xml
init|=
name|serializeToString
argument_list|(
name|n
argument_list|)
decl_stmt|;
try|try
block|{
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
comment|/**      *  Description of the Method      *      *@param  set               Description of the Parameter      *@param  start             Description of the Parameter      *@param  howmany           Description of the Parameter      *@return                   Description of the Return Value      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|String
name|serializeToString
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
throws|throws
name|SAXException
block|{
return|return
name|serializeToString
argument_list|(
name|set
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  set               Description of the Parameter      *@param  start             Description of the Parameter      *@param  howmany           Description of the Parameter      *@param  queryTime         Description of the Parameter      *@return                   Description of the Return Value      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|String
name|serializeToString
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|long
name|queryTime
parameter_list|)
throws|throws
name|SAXException
block|{
name|Iterator
name|iter
init|=
name|set
operator|.
name|iterator
argument_list|()
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
name|start
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|sql
decl_stmt|;
name|ResultSet
name|r
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|howmany
operator|&&
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|p
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|p
operator|.
name|gid
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBoolean
argument_list|(
literal|3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"STATEMENT IS: "
operator|+
name|stmt
argument_list|)
expr_stmt|;
name|r
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|()
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
name|result
operator|.
name|append
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|sqe
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|header
init|=
literal|"<?xml version=\"1.0\" encoding=\""
operator|+
name|encoding
operator|+
literal|"\"?>"
operator|+
literal|"<exist:result hitCount=\""
operator|+
name|set
operator|.
name|getLength
argument_list|()
operator|+
literal|"\" "
decl_stmt|;
if|if
condition|(
name|queryTime
operator|>=
literal|0
condition|)
name|header
operator|+=
literal|"queryTime=\""
operator|+
name|queryTime
operator|+
literal|"\" "
expr_stmt|;
name|header
operator|+=
literal|"retrieveTime=\""
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
operator|+
literal|"\" xmlns:exist=\"http://exist.sourceforge.net/NS/exist\">"
expr_stmt|;
name|result
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|header
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</exist:result>"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  doc               Description of the Parameter      *@return                   Description of the Return Value      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|String
name|serializeToString
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|SAXException
block|{
name|NodeList
name|children
init|=
name|doc
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\""
operator|+
name|encoding
operator|+
literal|"\"?>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|serializeToString
argument_list|(
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  n                 Description of the Parameter      *@return                   Description of the Return Value      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|String
name|serializeToString
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|SAXException
block|{
return|return
name|serializeToString
argument_list|(
name|n
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  n                 Description of the Parameter      *@param  addInfo           Description of the Parameter      *@return                   Description of the Return Value      *@exception  SAXException  Description of the Exception      */
specifier|protected
name|String
name|serializeToString
parameter_list|(
name|Node
name|n
parameter_list|,
name|boolean
name|addInfo
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
operator|(
name|n
operator|instanceof
name|NodeImpl
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong implementation"
argument_list|)
throw|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
operator|)
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|NodeImpl
operator|)
name|n
operator|)
operator|.
name|getGID
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setBoolean
argument_list|(
literal|3
argument_list|,
name|addInfo
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"STATEMENT IS: "
operator|+
name|stmt
argument_list|)
expr_stmt|;
name|ResultSet
name|r
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqe
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|sqe
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"database error"
argument_list|)
throw|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Sets the stylesheet attribute of the PostgresSerializer object      *      *@param  stylesheet                             The new stylesheet value      *@exception  SAXException                       Description of the      *      Exception      *@exception  TransformerConfigurationException  Description of the      *      Exception      *@exception  PermissionDeniedException          Description of the      *      Exception      */
specifier|public
name|void
name|setStylesheet
parameter_list|(
name|String
name|stylesheet
parameter_list|)
block|{
if|if
condition|(
name|stylesheet
operator|==
literal|null
condition|)
block|{
name|templates
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
comment|// does stylesheet point to an external resource?
if|if
condition|(
name|stylesheet
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|StreamSource
name|source
init|=
operator|new
name|StreamSource
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
name|templates
operator|=
name|factory
operator|.
name|newTemplates
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|// load stylesheet from eXist
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getDocument
argument_list|(
name|stylesheet
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"document not found!"
argument_list|)
throw|;
name|String
name|xml
init|=
name|serializeToString
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|StreamSource
name|source
init|=
operator|new
name|StreamSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
decl_stmt|;
name|templates
operator|=
name|factory
operator|.
name|newTemplates
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"permission denied"
argument_list|)
throw|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"compiling stylesheet took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|xslHandler
operator|=
operator|(
operator|(
name|SAXTransformerFactory
operator|)
name|factory
operator|)
operator|.
name|newTransformerHandler
argument_list|(
name|templates
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error compiling stylesheet"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error compiling stylesheet"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

