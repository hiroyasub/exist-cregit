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
name|realm
operator|.
name|openid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
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
name|AbstractAccount
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
name|SubjectAccreditedImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|Identifier
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"account"
argument_list|)
specifier|public
class|class
name|AccountImpl
extends|extends
name|SubjectAccreditedImpl
block|{
name|Identifier
name|_identifier
init|=
literal|null
decl_stmt|;
specifier|protected
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ConfigurationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"www.google.com"
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getAuthority
argument_list|()
argument_list|)
condition|)
return|return
name|uri
operator|.
name|getQuery
argument_list|()
operator|.
name|replace
argument_list|(
literal|"id="
argument_list|,
literal|""
argument_list|)
operator|+
literal|"@google.com"
return|;
return|return
name|id
operator|.
name|replace
argument_list|(
literal|"https://"
argument_list|,
literal|"/"
argument_list|)
return|;
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractAccount
name|account
parameter_list|,
name|Identifier
name|identifier
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|account
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
name|_identifier
operator|=
name|identifier
expr_stmt|;
block|}
comment|//	@Override
comment|//	public void setPassword(String passwd) {
comment|//	}
comment|//
comment|//	@Override
comment|//	public String getPassword() {
comment|//		return null;
comment|//	}
comment|//
comment|//	@Override
comment|//	public XmldbURI getHome() {
comment|//		// TODO Auto-generated method stub
comment|//		return null;
comment|//	}
comment|//
comment|//	@Override
comment|//	public String getDigestPassword() {
comment|//		return null;
comment|//	}
comment|//
comment|//	//TODO: find a place to construct 'full' name
comment|//	public String getName_() {
comment|//            String name = "";
comment|//
comment|//            Set<AXSchemaType> metadataKeys = getMetadataKeys();
comment|//
comment|//            if(metadataKeys.contains(AXSchemaType.FIRSTNAME)) {
comment|//                name += getMetadataValue(AXSchemaType.FIRSTNAME);
comment|//            }
comment|//
comment|//            if(metadataKeys.contains(AXSchemaType.LASTNAME)) {
comment|//                if(name.length()> 0 ) {
comment|//                    name += " ";
comment|//                }
comment|//                name += getMetadataValue(AXSchemaType.LASTNAME);
comment|//            }
comment|//
comment|//            if(name.length() == 0) {
comment|//                name += getMetadataValue(AXSchemaType.FULLNAME);
comment|//            }
comment|//
comment|//            if(name.length() == 0) {
comment|//                name = _identifier.getIdentifier();
comment|//            }
comment|//
comment|//            return name;
comment|//	}
comment|//
comment|//    @Override
comment|//    public Group addGroup(Group group) throws PermissionDeniedException {
comment|//
comment|//        if(group == null){
comment|//            return null;
comment|//        }
comment|//
comment|//        Account user = getDatabase().getSubject();
comment|//
comment|//
comment|//        if(!((user != null&& user.hasDbaRole()) || ((GroupImpl)group).isMembersManager(user))){
comment|//                throw new PermissionDeniedException("not allowed to change group memberships");
comment|//        }
comment|//
comment|//        if(!groups.contains(group)) {
comment|//            groups.add(group);
comment|//
comment|//            if(SecurityManager.DBA_GROUP.equals(name)) {
comment|//                hasDbaRole = true;
comment|//            }
comment|//        }
comment|//
comment|//        return group;
comment|//    }
block|}
end_class

end_unit

