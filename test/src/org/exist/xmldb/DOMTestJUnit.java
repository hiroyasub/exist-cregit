begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 7 aoï¿½t 2004 $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|Element
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
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
name|assertNotNull
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
name|fail
import|;
end_import

begin_comment
comment|/** A test case for accessing DOMS remotely  * @author jmv  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|DOMTestJUnit
extends|extends
name|RemoteDBTest
block|{
specifier|private
specifier|static
name|String
name|name
init|=
literal|"test.xml"
decl_stmt|;
specifier|private
name|Collection
name|rootColl
decl_stmt|;
specifier|private
name|Database
name|database
decl_stmt|;
specifier|private
specifier|static
name|String
name|getBaseURI
parameter_list|()
block|{
return|return
name|getUri
argument_list|()
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|IllegalAccessException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.initdb"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|dbc
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DB_DRIVER
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|dbc
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|rootColl
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|getBaseURI
argument_list|()
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|rootColl
argument_list|)
expr_stmt|;
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|rootColl
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|r
operator|.
name|setContent
argument_list|(
literal|"<?xml-stylesheet type=\"text/xsl\" href=\"test.xsl\"?><!-- Root Comment --><properties><property key=\"type\">Table</property></properties>"
argument_list|)
expr_stmt|;
name|rootColl
operator|.
name|storeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
comment|/** test Update of an existing document through DOM */
annotation|@
name|Test
specifier|public
name|void
name|domUpdate
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XMLResource
name|index
init|=
operator|(
name|XMLResource
operator|)
name|rootColl
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|content
init|=
operator|(
name|String
operator|)
name|index
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
literal|null
decl_stmt|;
name|Element
name|root
init|=
literal|null
decl_stmt|;
name|NodeList
name|nl
init|=
literal|null
decl_stmt|;
name|Node
name|n
init|=
name|index
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|Document
condition|)
block|{
name|doc
operator|=
operator|(
name|Document
operator|)
name|n
expr_stmt|;
name|root
operator|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|doc
operator|=
name|n
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|root
operator|=
operator|(
name|Element
operator|)
name|n
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"RemoteXMLResource unable to return a Document either an Element"
argument_list|)
expr_stmt|;
block|}
name|nl
operator|=
name|doc
operator|.
name|getChildNodes
argument_list|()
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
block|}
name|Element
name|schemaNode
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"schema"
argument_list|)
decl_stmt|;
name|schemaNode
operator|.
name|setAttribute
argument_list|(
literal|"targetNamespace"
argument_list|,
literal|"targetNamespace"
argument_list|)
expr_stmt|;
name|schemaNode
operator|.
name|setAttribute
argument_list|(
literal|"resourceName"
argument_list|,
literal|"filename"
argument_list|)
expr_stmt|;
name|root
operator|.
name|appendChild
argument_list|(
name|schemaNode
argument_list|)
expr_stmt|;
name|index
operator|.
name|setContentAsDOM
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|rootColl
operator|.
name|storeResource
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|index
operator|=
operator|(
name|XMLResource
operator|)
name|rootColl
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|content
operator|=
operator|(
name|String
operator|)
name|index
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|n
operator|=
name|index
operator|.
name|getContentAsDOM
argument_list|()
expr_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|Document
condition|)
block|{
name|doc
operator|=
operator|(
name|Document
operator|)
name|n
expr_stmt|;
name|root
operator|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|doc
operator|=
name|n
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|root
operator|=
operator|(
name|Element
operator|)
name|n
expr_stmt|;
block|}
name|nl
operator|=
name|root
operator|.
name|getChildNodes
argument_list|()
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

