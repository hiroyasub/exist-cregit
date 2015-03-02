begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|IndexInfo
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
name|storage
operator|.
name|lock
operator|.
name|Lock
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
name|Configuration
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
name|TestDataGenerator
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|RemoveCollectionTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|generateXQ
init|=
literal|"<book id=\"{$filename}\" n=\"{$count}\">"
operator|+
literal|"<chapter>"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"       {"
operator|+
literal|"           for $section in 1 to 8 return"
operator|+
literal|"<section id=\"sect{$section}\">"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"                   {"
operator|+
literal|"                       for $para in 1 to 10 return"
operator|+
literal|"<para>{pt:random-text(40)}</para>"
operator|+
literal|"                   }"
operator|+
literal|"</section>"
operator|+
literal|"       }"
operator|+
literal|"</chapter>"
operator|+
literal|"</book>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<lucene>"
operator|+
literal|"<text match=\"/*\"/>"
operator|+
literal|"</lucene>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|COUNT
init|=
literal|300
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|runTests
parameter_list|()
block|{
name|removeCollection
argument_list|()
expr_stmt|;
name|recover
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|removeResources
argument_list|()
expr_stmt|;
name|recover
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replaceResources
argument_list|()
expr_stmt|;
name|recover
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeCollection
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Collection
name|test
init|=
name|storeDocs
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Removing collection ..."
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction interrupted ..."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopDB
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeResources
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Collection
name|test
init|=
name|storeDocs
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Removing documents one by one ..."
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|test
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|broker
operator|.
name|removeXMLResource
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction committed ..."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopDB
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|replaceResources
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|startDB
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|Collection
name|test
init|=
name|storeDocs
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Replacing resources ..."
argument_list|)
expr_stmt|;
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|(
literal|"xdb"
argument_list|,
name|COUNT
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating "
operator|+
name|COUNT
operator|+
literal|" files..."
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|generator
operator|.
name|generate
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|generateXQ
argument_list|)
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|test
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
operator|&&
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|test
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
operator|.
name|getURI
argument_list|()
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|is
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction committed ..."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopDB
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Collection
name|storeDocs
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|TransactionManager
name|transact
parameter_list|)
throws|throws
name|Exception
block|{
name|Collection
name|test
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|test
operator|=
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
expr_stmt|;
name|assertNotNull
argument_list|(
name|test
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|CollectionConfigurationManager
name|mgr
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
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
name|test
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|File
argument_list|(
literal|"samples/shakespeare/hamlet.xml"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|test
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
literal|"hamlet.xml"
argument_list|)
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|is
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction commited ..."
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|(
literal|"xdb"
argument_list|,
name|COUNT
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating "
operator|+
name|COUNT
operator|+
literal|" files..."
argument_list|)
expr_stmt|;
name|File
index|[]
name|files
init|=
name|generator
operator|.
name|generate
argument_list|(
name|broker
argument_list|,
name|test
argument_list|,
name|generateXQ
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|File
name|file
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|test
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
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|is
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|releaseAll
argument_list|()
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction commited ..."
argument_list|)
expr_stmt|;
block|}
return|return
name|test
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|boolean
name|checkResource
parameter_list|)
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testRead() ...\n"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkResource
condition|)
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"hamlet.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Resource should have been removed"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
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
name|stopDB
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|void
name|stopDB
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

