begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

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
name|collections
operator|.
name|CollectionConfigurationManager
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
name|IndexInfo
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
name|DefaultDocumentSet
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
name|DocumentSet
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
name|MutableDocumentSet
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
name|txn
operator|.
name|TransactionManager
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
name|test
operator|.
name|ExistEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|TestConstants
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
name|*
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
name|exist
operator|.
name|xupdate
operator|.
name|Modification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|XUpdateProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|util
operator|.
name|Optional
import|;
end_import

begin_class
specifier|public
class|class
name|RangeIndexUpdateTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<create qname=\"item\" type=\"xs:string\"/>"
operator|+
literal|"<create path=\"//item/@attr\" type=\"xs:string\"/>"
operator|+
literal|"<create path=\"/section/para\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"<test>"
operator|+
literal|"<item id='1' attr='attribute'><description>Chair</description></item>"
operator|+
literal|"<item id='2'><description>Table</description><price>892.25</price></item>"
operator|+
literal|"<item id='3'><description>Cabinet</description><price>1525.00</price></item>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XML2
init|=
literal|"<section>"
operator|+
literal|"<para>01234</para>"
operator|+
literal|"<para>56789</para>"
operator|+
literal|"</section>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XUPDATE_START
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|XUPDATE_END
init|=
literal|"</xu:modifications>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|ITEM_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"item"
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|MutableDocumentSet
name|docs
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|updates
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|ParserConfigurationException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|;
init|)
block|{
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Chair"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Table892.25"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Cabinet1525.00"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"//item[. = 'Chair']"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|seq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|XUpdateProcessor
name|proc
init|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|String
name|xupdate
init|=
name|XUPDATE_START
operator|+
literal|"<xu:update select=\"//item[@id = '1']/description\">Wardrobe</xu:update>"
operator|+
name|XUPDATE_END
decl_stmt|;
name|Modification
index|[]
name|modifications
init|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|long
name|mods
init|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
decl_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mods
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Chair"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Wardrobe"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|xupdate
operator|=
name|XUPDATE_START
operator|+
literal|"<xu:update select=\"//item[@id = '1']/description/text()\">Wheelchair</xu:update>"
operator|+
name|XUPDATE_END
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|mods
operator|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mods
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Wardrobe"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|ITEM_QNAME
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"Wheelchair"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|xupdate
operator|=
name|XUPDATE_START
operator|+
literal|"<xu:update select=\"//item[@id = '1']/@attr\">abc</xu:update>"
operator|+
name|XUPDATE_END
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|mods
operator|=
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mods
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"abc"
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkIndex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentSet
name|docs
parameter_list|,
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|StringValue
name|term
parameter_list|,
specifier|final
name|int
name|expectedCount
parameter_list|)
block|{
specifier|final
name|ValueOccurrences
index|[]
name|occurrences
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
condition|)
block|{
name|occurrences
operator|=
name|broker
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|occurrences
operator|=
name|broker
operator|.
name|getValueIndex
argument_list|()
operator|.
name|scanIndexKeys
argument_list|(
name|docs
argument_list|,
literal|null
argument_list|,
operator|new
name|QName
index|[]
block|{
name|qname
block|}
argument_list|,
name|term
argument_list|)
expr_stmt|;
block|}
name|int
name|found
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|ValueOccurrences
name|occurrence
range|:
name|occurrences
control|)
block|{
if|if
condition|(
name|occurrence
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|term
argument_list|)
operator|==
literal|0
condition|)
block|{
name|found
operator|++
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|expectedCount
argument_list|,
name|found
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|CollectionConfigurationException
throws|,
name|LockException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|;
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
init|)
block|{
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
specifier|final
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|docs
operator|=
operator|new
name|DefaultDocumentSet
argument_list|()
expr_stmt|;
name|IndexInfo
name|info
init|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test_string.xml"
argument_list|)
argument_list|,
name|XML
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test_string2.xml"
argument_list|)
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

