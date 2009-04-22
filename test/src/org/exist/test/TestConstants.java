begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2006-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
package|;
end_package

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
name|util
operator|.
name|URIUtils
import|;
end_import

begin_class
specifier|public
class|class
name|TestConstants
block|{
comment|/** 	 * String representing the decoded path: t[e s]tÃ¡ì´ 	 */
specifier|public
specifier|static
specifier|final
name|String
name|DECODED_SPECIAL_NAME
init|=
literal|"t[e s]t\u00E0\uC5F4"
decl_stmt|;
comment|/** 	 * String representing the encoded path: t%5Be%20s%5Dt%C3%A0%EC%97%B4 	 */
specifier|public
specifier|static
specifier|final
name|String
name|SPECIAL_NAME
init|=
name|URIUtils
operator|.
name|urlEncodeUtf8
argument_list|(
name|DECODED_SPECIAL_NAME
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: t[e s]tÃ¡ì´ 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|SPECIAL_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|SPECIAL_NAME
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/test 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/test/test2 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION_URI2
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/test/test2/test3 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION_URI3
init|=
name|TEST_COLLECTION_URI2
operator|.
name|append
argument_list|(
literal|"test3"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/t[e s]tÃ¡ì´ 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|SPECIAL_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
name|SPECIAL_NAME
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/destination 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|DESTINATION_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"destination"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/destination2 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|DESTINATION_COLLECTION_URI2
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"destination2"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: /db/destination3 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|DESTINATION_COLLECTION_URI3
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"destination3"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: test.xml 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_XML_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: test2.xml 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_XML_URI2
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test2.xml"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: test3.xml 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_XML_URI3
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test3.xml"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: t[e s]tÃ¡ì´.xml 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|SPECIAL_XML_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|URIUtils
operator|.
name|urlEncodeUtf8
argument_list|(
literal|"t[es]t\u00E0\uC5F4.xml"
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: binary.txt 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_BINARY_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"binary.txt"
argument_list|)
decl_stmt|;
comment|/** 	 * XmldbURI representing the decoded path: testmodule.xqm 	 */
specifier|public
specifier|static
specifier|final
name|XmldbURI
name|TEST_MODULE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"testmodule.xqm"
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

