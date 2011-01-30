begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|utils
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
name|config
operator|.
name|ConfigurationException
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
name|Account
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
name|Group
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
name|security
operator|.
name|SecurityManager
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
name|Subject
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
name|internal
operator|.
name|RealmImpl
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
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|internal
operator|.
name|aider
operator|.
name|UserAider
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
name|realm
operator|.
name|Realm
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
name|EXistInputSource
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
name|w3c
operator|.
name|dom
operator|.
name|Attr
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
name|DOMException
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|ConverterFrom1_0
block|{
specifier|private
specifier|final
specifier|static
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PASS
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DIGEST_PASS
init|=
literal|"digest-password"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|USER_ID
init|=
literal|"uid"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|HOME
init|=
literal|"home"
decl_stmt|;
specifier|public
specifier|static
name|void
name|convert
parameter_list|(
name|EXistInputSource
name|is
parameter_list|)
block|{
block|}
specifier|public
specifier|static
name|void
name|convert
parameter_list|(
name|Subject
name|invokingUser
parameter_list|,
name|SecurityManager
name|sm
parameter_list|,
name|Document
name|acl
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|Element
name|docElement
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|acl
operator|!=
literal|null
condition|)
name|docElement
operator|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|docElement
operator|==
literal|null
condition|)
block|{
block|}
else|else
block|{
name|Realm
name|realm
init|=
name|sm
operator|.
name|getRealm
argument_list|(
name|RealmImpl
operator|.
name|ID
argument_list|)
decl_stmt|;
comment|//			int nextGroupId = -1;
comment|//			int nextUserId = -1;
name|Element
name|root
init|=
name|acl
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Attr
name|version
init|=
name|root
operator|.
name|getAttributeNode
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
name|int
name|major
init|=
literal|0
decl_stmt|;
name|int
name|minor
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|numbers
init|=
name|version
operator|.
name|getValue
argument_list|()
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|major
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|minor
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|numbers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|nl
init|=
name|root
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|node
decl_stmt|;
name|Element
name|next
decl_stmt|;
name|Account
name|account
decl_stmt|;
name|NodeList
name|ul
decl_stmt|;
comment|//			String lastId;
name|Group
name|group
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
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
continue|continue;
name|next
operator|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"users"
argument_list|)
condition|)
block|{
comment|//					lastId = next.getAttribute("last-id");
comment|//					try {
comment|//						nextUserId = Integer.parseInt(lastId);
comment|//					} catch (NumberFormatException e) {
comment|//					}
name|ul
operator|=
name|next
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ul
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|ul
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"user"
argument_list|)
condition|)
block|{
name|account
operator|=
name|createAccount
argument_list|(
name|major
argument_list|,
name|minor
argument_list|,
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|realm
operator|.
name|hasAccount
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|realm
operator|.
name|updateAccount
argument_list|(
name|invokingUser
argument_list|,
name|account
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|realm
operator|.
name|addAccount
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|else if
condition|(
name|next
operator|.
name|getTagName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"groups"
argument_list|)
condition|)
block|{
comment|//					lastId = next.getAttribute("last-id");
comment|//					try {
comment|//						nextGroupId = Integer.parseInt(lastId);
comment|//					} catch (NumberFormatException e) {
comment|//					}
name|ul
operator|=
name|next
operator|.
name|getChildNodes
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ul
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|ul
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"group"
argument_list|)
condition|)
block|{
name|group
operator|=
name|createGroup
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|realm
operator|.
name|hasGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|realm
operator|.
name|updateGroup
argument_list|(
name|invokingUser
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|realm
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
comment|/** 	 * Read a account information from the given DOM node 	 *  	 * @param node 	 *            Description of the Parameter 	 *@exception DatabaseConfigurationException 	 *                Description of the Exception 	 * @throws ConfigurationException  	 * @throws PermissionDeniedException  	 * @throws DOMException  	 */
specifier|public
specifier|static
name|Account
name|createAccount
parameter_list|(
name|int
name|majorVersion
parameter_list|,
name|int
name|minorVersion
parameter_list|,
name|Element
name|node
parameter_list|)
throws|throws
name|ConfigurationException
throws|,
name|DOMException
throws|,
name|PermissionDeniedException
block|{
name|String
name|password
init|=
literal|null
decl_stmt|;
name|String
name|digestPassword
init|=
literal|null
decl_stmt|;
name|int
name|id
init|=
operator|-
literal|1
decl_stmt|;
name|XmldbURI
name|home
init|=
literal|null
decl_stmt|;
name|String
name|name
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
comment|//|| name.length() == 0
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"account needs a name"
argument_list|)
throw|;
name|Attr
name|attr
decl_stmt|;
if|if
condition|(
name|majorVersion
operator|==
literal|0
condition|)
block|{
name|attr
operator|=
name|node
operator|.
name|getAttributeNode
argument_list|(
name|PASS
argument_list|)
expr_stmt|;
name|digestPassword
operator|=
name|attr
operator|==
literal|null
condition|?
literal|null
else|:
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|attr
operator|=
name|node
operator|.
name|getAttributeNode
argument_list|(
name|PASS
argument_list|)
expr_stmt|;
name|password
operator|=
name|attr
operator|==
literal|null
condition|?
literal|null
else|:
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|attr
operator|=
name|node
operator|.
name|getAttributeNode
argument_list|(
name|DIGEST_PASS
argument_list|)
expr_stmt|;
name|digestPassword
operator|=
name|attr
operator|==
literal|null
condition|?
literal|null
else|:
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
name|Attr
name|userId
init|=
name|node
operator|.
name|getAttributeNode
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"attribute id is missing"
argument_list|)
throw|;
try|try
block|{
name|id
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|userId
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
literal|"illegal user id: "
operator|+
name|userId
operator|+
literal|" for account "
operator|+
name|name
argument_list|)
throw|;
block|}
name|Attr
name|homeAttr
init|=
name|node
operator|.
name|getAttributeNode
argument_list|(
name|HOME
argument_list|)
decl_stmt|;
name|home
operator|=
name|homeAttr
operator|==
literal|null
condition|?
literal|null
else|:
name|XmldbURI
operator|.
name|create
argument_list|(
name|homeAttr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO: workaround for 'null' admin's password. It should be removed after 6 months (@ 10 July 2010)
if|if
condition|(
name|id
operator|==
literal|1
operator|&&
name|password
operator|==
literal|null
condition|)
name|password
operator|=
literal|""
expr_stmt|;
name|Account
name|new_account
init|=
operator|new
name|UserAider
argument_list|(
name|RealmImpl
operator|.
name|ID
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|new_account
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|new_account
operator|.
name|setHome
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|NodeList
name|gl
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|group
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
name|gl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|group
operator|=
name|gl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|group
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|GROUP
argument_list|)
condition|)
name|new_account
operator|.
name|addGroup
argument_list|(
name|group
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|new_account
return|;
block|}
specifier|public
specifier|static
name|Group
name|createGroup
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
return|return
operator|new
name|GroupAider
argument_list|(
name|RealmImpl
operator|.
name|ID
argument_list|,
name|element
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
return|;
comment|//, Integer.parseInt(element.getAttribute("id")
block|}
block|}
end_class

end_unit

