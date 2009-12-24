begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|UserImpl
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
name|ExternalModule
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
name|Module
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
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AnyURIAttribute
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|AttributeValue
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|attr
operator|.
name|StringAttribute
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ctx
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ctx
operator|.
name|RequestCtx
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ctx
operator|.
name|Subject
import|;
end_import

begin_comment
comment|/* * Source.getKey().toString() needs to be unique: *	potential collision between FileSource and DBSource ?? */
end_comment

begin_comment
comment|/** * This class provides methods for creating an XACML request.  The main methods * are those that return a<code>RequestCtx</code>.  Links are provided to the * relevant constants in<code>XACMLConstants</code> to facilitate policy * writing. * * @see XACMLConstants */
end_comment

begin_class
specifier|public
class|class
name|RequestHelper
block|{
comment|/** 	 * Creates an XACML request for permission to execute an XQuery main module. 	 * The subjects section will contain a subject for the user obtained from the 	 * specified context.  The resource section will be created by the 	 * createQueryResource method.  The action-id will be 	 * {@link XACMLConstants#EXECUTE_QUERY_ACTION execute query}.  The environment 	 * section will be created by createEnvironment, using the access context 	 * of the query context. 	 *   	 * @param context The context for this query 	 * @param source The source of this query 	 * @return A<code>RequestCtx</code> that may be evaluated by the PDP to 	 * determine whether the specified user may execute the query represented by 	 *<code>source</code>. 	 */
specifier|public
name|RequestCtx
name|createQueryRequest
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|XACMLSource
name|source
parameter_list|)
block|{
name|Set
argument_list|<
name|Subject
argument_list|>
name|subjects
init|=
name|createQuerySubjects
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|resourceAttributes
init|=
name|createQueryResource
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|actionAttributes
init|=
name|createBasicAction
argument_list|(
name|XACMLConstants
operator|.
name|EXECUTE_QUERY_ACTION
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|environmentAttributes
init|=
name|createEnvironment
argument_list|(
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RequestCtx
argument_list|(
name|subjects
argument_list|,
name|resourceAttributes
argument_list|,
name|actionAttributes
argument_list|,
name|environmentAttributes
argument_list|)
return|;
block|}
comment|/** 	* Creates a<code>RequestCtx</code> for a request concerning reflective 	* access to Java code from an XQuery.  This handles occurs when a method 	* is being invoked on the class in question. This method creates a 	* request with the following content: 	*<ul> 	* 	*<li>Subjects for the contextModule and user are created with the  	* createQuerySubjects method.</li> 	* 	*<li>Resource attributes are created with the 	*<code>createReflectionResource</code> method.</li> 	* 	*<li>Action attributes are created with the 	*<code>createBasicAction</code> method.  The action-id is 	* {@link XACMLConstants#INVOKE_METHOD_ACTION invoke method}.</li> 	* 	*<li>The {@link XACMLConstants#ACCESS_CONTEXT_ATTRIBUTE} access context  	* attribute is generated for the environment section.</li> 	* 	*</ul> 	* 	* @param context The<code>XQueryContext</code> for the module making the 	* request. 	* @param contextModule The query containing the reflection. 	* @param className The name of the class that is being accessed or loaded. 	* @param methodName The name of the method that is being invoked 	* @return A<code>RequestCtx</code> that represents the access in question. 	*/
specifier|public
name|RequestCtx
name|createReflectionRequest
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Module
name|contextModule
parameter_list|,
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|)
block|{
name|UserImpl
name|user
init|=
name|context
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Subject
argument_list|>
name|subjects
init|=
name|createQuerySubjects
argument_list|(
name|user
argument_list|,
name|contextModule
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|resourceAttributes
init|=
name|createReflectionResource
argument_list|(
name|className
argument_list|,
name|methodName
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|actionAttributes
init|=
name|createBasicAction
argument_list|(
name|XACMLConstants
operator|.
name|INVOKE_METHOD_ACTION
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|environmentAttributes
init|=
name|createEnvironment
argument_list|(
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RequestCtx
argument_list|(
name|subjects
argument_list|,
name|resourceAttributes
argument_list|,
name|actionAttributes
argument_list|,
name|environmentAttributes
argument_list|)
return|;
block|}
comment|/** 	* Creates a<code>RequestCtx</code> for a request concerning access 	* to a function in an XQuery library module.  If the function is 	* from a main module, this method returns null to indicate that. 	* The client should interpret this to mean that the request is 	* granted because access to a main module implies access to its 	* functions. 	* 	*<p> 	* This method creates a request with the following content: 	*<ul> 	* 	*<li>Subjects for the contextModule and user (obtained from the 	* XQueryContext) are created with the createQuerySubjects method.</li> 	* 	*<li>The specified functionModule parameter is used to generate the 	* {@link XACMLConstants#SOURCE_KEY_ATTRIBUTE source-key}, 	* {@link XACMLConstants#SOURCE_TYPE_ATTRIBUTE source-type}, and 	* {@link XACMLConstants#MODULE_CATEGORY_ATTRIBUTE module category} 	* attributes. The functionName parameter is the value of the 	* {@link XACMLConstants#RESOURCE_ID_ATTRIBUTE subject-id} attribute 	* (the local part) and of the  	* {@link XACMLConstants#MODULE_NS_ATTRIBUTE module namespace} 	*  attribute (the namespace URI part).  The  	* {@link XACMLConstants#RESOURCE_CATEGORY_ATTRIBUTE resource-category} 	* attribute is {@link XACMLConstants#FUNCTION_RESOURCE function}. 	*  	*<li>Action attributes are created with the 	*<code>createBasicAction</code> method.  The action is 	* {@link XACMLConstants#CALL_FUNCTION_ACTION call function}.  	* 	*<li>The {@link XACMLConstants#ACCESS_CONTEXT_ATTRIBUTE} access context  	* attribute is generated for the environment section.</li> 	* 	*</ul> 	* 	* @param context The query context. 	* @param contextModule The query making the access. 	* @param functionName The<code>QName</code> of the function being called. 	* @return A<code>RequestCtx</code> that represents the access in question  	*	or<code>null</code> if the function belongs to a main module and 	*	not a library module. 	*/
specifier|public
name|RequestCtx
name|createFunctionRequest
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Module
name|contextModule
parameter_list|,
name|QName
name|functionName
parameter_list|)
block|{
name|String
name|namespaceURI
init|=
name|functionName
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|Module
name|functionModule
init|=
name|context
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|functionModule
operator|==
literal|null
condition|)
block|{
comment|//main module, not a library module, so access to function is always allowed
return|return
literal|null
return|;
block|}
name|UserImpl
name|user
init|=
name|context
operator|.
name|getUser
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Subject
argument_list|>
name|subjects
init|=
name|createQuerySubjects
argument_list|(
name|user
argument_list|,
name|contextModule
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|resourceAttributes
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|MODULE_CATEGORY_ATTRIBUTE
argument_list|,
name|getModuleCategory
argument_list|(
name|functionModule
argument_list|)
argument_list|)
expr_stmt|;
name|XACMLSource
name|moduleSrc
init|=
name|generateModuleSource
argument_list|(
name|functionModule
argument_list|)
decl_stmt|;
name|addSourceAttributes
argument_list|(
name|resourceAttributes
argument_list|,
name|moduleSrc
argument_list|)
expr_stmt|;
name|addValidURIAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|MODULE_NS_ATTRIBUTE
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|,
name|XACMLConstants
operator|.
name|FUNCTION_RESOURCE
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_ID_ATTRIBUTE
argument_list|,
name|functionName
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|actionAttributes
init|=
name|createBasicAction
argument_list|(
name|XACMLConstants
operator|.
name|CALL_FUNCTION_ACTION
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|environmentAttributes
init|=
name|createEnvironment
argument_list|(
name|context
operator|.
name|getAccessContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RequestCtx
argument_list|(
name|subjects
argument_list|,
name|resourceAttributes
argument_list|,
name|actionAttributes
argument_list|,
name|environmentAttributes
argument_list|)
return|;
block|}
comment|/** 	* Creates a<code>Subject</code> for a<code>User</code>. 	* The user's name is the value of the  	* {@link XACMLConstants#SUBJECT_ID_ATTRIBUTE subject-id} attribute.  The 	* subject-category is {@link XACMLConstants#ACCESS_SUBJECT access-subject}. 	* The {@link XACMLConstants#GROUP_ATTRIBUTE group} attribute is a bag 	* containing the name of each group of which the user is a member. 	* 	* @param user The user making the request 	* @return A<code>Subject</code> for use in a<code>RequestCtx</code> 	*/
specifier|public
name|Subject
name|createUserSubject
parameter_list|(
name|UserImpl
name|user
parameter_list|)
block|{
name|AttributeValue
name|value
init|=
operator|new
name|StringAttribute
argument_list|(
name|user
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Attribute
name|attr
init|=
operator|new
name|Attribute
argument_list|(
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|Subject
argument_list|(
name|XACMLConstants
operator|.
name|ACCESS_SUBJECT
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|attr
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	* Creates the basic attributes needed to describe a simple action 	* in a request.  The<code>action</code> parameter is the value of 	* the {@link XACMLConstants#ACTION_ID_ATTRIBUTE action-id} attribute and the  	* {@link XACMLConstants#ACTION_NS_ATTRIBUTE namespace} attribute for the 	* action-id is eXist's XACML 	* {@link XACMLConstants#ACTION_NS action namespace}. 	* 	* @param action The {@link XACMLConstants#ACTION_ID_ATTRIBUTE action-id} 	*	of the action. 	* @return A<code>Set</code> that contains attributes describing the 	*	action for use in a<code>RequestCtx</code> 	*/
specifier|public
name|Set
argument_list|<
name|Attribute
argument_list|>
name|createBasicAction
parameter_list|(
name|String
name|action
parameter_list|)
block|{
if|if
condition|(
name|action
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|addStringAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|ACTION_ID_ATTRIBUTE
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|addValidURIAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|ACTION_NS_ATTRIBUTE
argument_list|,
name|XACMLConstants
operator|.
name|ACTION_NS
argument_list|)
expr_stmt|;
return|return
name|attributes
return|;
block|}
comment|/** 	* Creates a<code>Subject</code> for a<code>Module</code>. 	* If the module is external, its<code>Source</code> is the value of the  	* {@link XACMLConstants#SUBJECT_ID_ATTRIBUTE subject-id} attribute, otherwise, 	* the name of the implementing class is used.  The subject-category is  	* {@link XACMLConstants#CODEBASE_SUBJECT codebase}.  The value of the  	* {@link XACMLConstants#SUBJECT_NS_ATTRIBUTE module namespace} attribute 	* is the namespace URI of the module.  The 	* {@link XACMLConstants#MODULE_CATEGORY_ATTRIBUTE module category} 	* attribute is the type of module, either 	* {@link XACMLConstants#INTERNAL_LIBRARY_MODULE internal} or 	* {@link XACMLConstants#EXTERNAL_LIBRARY_MODULE external}. 	* 	* @param module A query module involved in making the request 	* @return A<code>Subject</code> for use in a<code>RequestCtx</code> 	*/
specifier|public
name|Subject
name|createModuleSubject
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|addValidURIAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|SUBJECT_NS_ATTRIBUTE
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|MODULE_CATEGORY_ATTRIBUTE
argument_list|,
name|getModuleCategory
argument_list|(
name|module
argument_list|)
argument_list|)
expr_stmt|;
name|XACMLSource
name|moduleSrc
init|=
name|generateModuleSource
argument_list|(
name|module
argument_list|)
decl_stmt|;
name|addSourceAttributes
argument_list|(
name|attributes
argument_list|,
name|moduleSrc
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|SUBJECT_ID_ATTRIBUTE
argument_list|,
name|moduleSrc
operator|.
name|createId
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|Subject
argument_list|(
name|XACMLConstants
operator|.
name|CODEBASE_SUBJECT
argument_list|,
name|attributes
argument_list|)
return|;
block|}
comment|/** 	* Creates a<code>Set</code> of<code>Attribute</code>s for a resource 	* representing Java reflection in an XQuery. 	* The {@link XACMLConstants#RESOURCE_CATEGORY_ATTRIBUTE resource-category} 	* attribute is {@link XACMLConstants#METHOD_RESOURCE method}. 	* The {@link XACMLConstants#SOURCE_TYPE_ATTRIBUTE source-type} attribute is 	* {@link XACMLConstants#CLASS_SOURCE_TYPE class} and the 	* {@link XACMLConstants#SOURCE_KEY_ATTRIBUTE source-key} attribute is the 	* name of the class.  The 	* {@link XACMLConstants#RESOURCE_ID_ATTRIBUTE resource-id} attribute is the  	* method name. 	* 	* @param className The name of the Java class 	* @param methodName The name of the method being invoked 	* @return A<code>Set</code> containing the<code>Attribute</code>s 	* describing access to Java code by reflection. 	*/
specifier|public
name|Set
argument_list|<
name|Attribute
argument_list|>
name|createReflectionResource
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|)
block|{
if|if
condition|(
name|className
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Class name cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|methodName
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Method name cannot be null"
argument_list|)
throw|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|resourceAttributes
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|8
argument_list|)
decl_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|,
name|XACMLConstants
operator|.
name|METHOD_RESOURCE
argument_list|)
expr_stmt|;
name|XACMLSource
name|source
init|=
name|XACMLSource
operator|.
name|getInstance
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|addSourceAttributes
argument_list|(
name|resourceAttributes
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_ID_ATTRIBUTE
argument_list|,
name|methodName
argument_list|)
expr_stmt|;
return|return
name|resourceAttributes
return|;
block|}
comment|/** 	 * Creates the Resource section of a request for a main module. 	 *  	 * @param source The source of the query. 	 * @return A<code>Set</code> containing attributes for the specified 	 * query. 	 */
specifier|public
name|Set
argument_list|<
name|Attribute
argument_list|>
name|createQueryResource
parameter_list|(
name|XACMLSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Query source cannot be null"
argument_list|)
throw|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|resourceAttributes
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|addSourceAttributes
argument_list|(
name|resourceAttributes
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_ID_ATTRIBUTE
argument_list|,
name|source
operator|.
name|createId
argument_list|()
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|resourceAttributes
argument_list|,
name|XACMLConstants
operator|.
name|RESOURCE_CATEGORY_ATTRIBUTE
argument_list|,
name|XACMLConstants
operator|.
name|MAIN_MODULE_RESOURCE
argument_list|)
expr_stmt|;
return|return
name|resourceAttributes
return|;
block|}
comment|/** 	* Creates<code>Subject</code>s for the specified user and module.  This is 	* equivalent to putting the<code>Subject</code>s created by the 	*<code>createUserSubject(User user)</code> and 	*<code>createModuleSubject(Module contextModule)</code> methods.  The 	* context module may be null if there is no context module. 	* 	* @param user The user making the access 	* @param contextModule The module involved in the access, if any.  It may 	* be null to indicate the is not an intermediary XQuery module. 	* @return A<code>Set</code> containing a<code>Subject</code> for each 	* the context module if there is one and the user. 	*/
specifier|public
name|Set
argument_list|<
name|Subject
argument_list|>
name|createQuerySubjects
parameter_list|(
name|UserImpl
name|user
parameter_list|,
name|Module
name|contextModule
parameter_list|)
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"User cannot be null"
argument_list|)
throw|;
name|Set
argument_list|<
name|Subject
argument_list|>
name|subjects
init|=
operator|new
name|HashSet
argument_list|<
name|Subject
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|Subject
name|userSubject
init|=
name|createUserSubject
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|subjects
operator|.
name|add
argument_list|(
name|userSubject
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextModule
operator|!=
literal|null
condition|)
block|{
name|Subject
name|moduleSubject
init|=
name|createModuleSubject
argument_list|(
name|contextModule
argument_list|)
decl_stmt|;
name|subjects
operator|.
name|add
argument_list|(
name|moduleSubject
argument_list|)
expr_stmt|;
block|}
return|return
name|subjects
return|;
block|}
comment|/** 	 * Creates the environment section of a request for the given 	 *<code>AccessContext</code>. 	 *  	 * @param accessCtx The context 	 * @return A<code>Set</code> containing one attribute, the 	 * {@link XACMLConstants#ACCESS_CONTEXT_ATTRIBUTE access context} 	 * attribute with the value of the specified access context. 	 */
specifier|public
name|Set
argument_list|<
name|Attribute
argument_list|>
name|createEnvironment
parameter_list|(
name|AccessContext
name|accessCtx
parameter_list|)
block|{
if|if
condition|(
name|accessCtx
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullAccessContextException
argument_list|()
throw|;
name|Set
argument_list|<
name|Attribute
argument_list|>
name|environment
init|=
operator|new
name|HashSet
argument_list|<
name|Attribute
argument_list|>
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|addStringAttribute
argument_list|(
name|environment
argument_list|,
name|XACMLConstants
operator|.
name|ACCESS_CONTEXT_ATTRIBUTE
argument_list|,
name|accessCtx
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|environment
return|;
block|}
comment|/** 	* Generates an<code>XACMLSource</code> for a<code>Module</code> 	* based on its implementing class name (if it is an  	*<code>InternalModule</code>) or its<code>Source</code> 	* (if it is an<code>ExternalModule</code>). 	* 	* @param module the module for which the source should be generated 	* @return an<code>XACMLSource</code> that uniquely defines the source 	* of the given module 	*/
specifier|public
specifier|static
name|XACMLSource
name|generateModuleSource
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Module cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
return|return
name|XACMLSource
operator|.
name|getInstance
argument_list|(
name|module
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
return|return
name|XACMLSource
operator|.
name|getInstance
argument_list|(
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getSource
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Returns the module type for the given XQuery library module.  This 	 * is either 	 * {@link XACMLConstants#INTERNAL_LIBRARY_MODULE internal} or 	 * {@link XACMLConstants#EXTERNAL_LIBRARY_MODULE external} 	 *  	 * @param module The XQuery library module.  If it is null, this method 	 * returns null. 	 * @return null if module is null, the module's category (internal or external) 	 * otherwise 	 */
specifier|public
specifier|static
name|String
name|getModuleCategory
parameter_list|(
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|module
operator|.
name|isInternalModule
argument_list|()
condition|?
name|XACMLConstants
operator|.
name|INTERNAL_LIBRARY_MODULE
else|:
name|XACMLConstants
operator|.
name|EXTERNAL_LIBRARY_MODULE
return|;
block|}
comment|/** 	 * Adds new attributes to the specified<code>Set</code> of attributes 	 * that represent the specified source.  The added attributes are the 	 * {@link XACMLConstants#SOURCE_KEY_ATTRIBUTE source's key} and the 	 * {@link XACMLConstants#SOURCE_TYPE_ATTRIBUTE source's type}. 	 *    	 * @param attributes The<code>Set</code> to which attributes will be 	 * added.  If null, this method does nothing. 	 * @param source The source for which attributes will be added.  It 	 * cannot be null. 	 */
specifier|public
specifier|static
name|void
name|addSourceAttributes
parameter_list|(
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|,
name|XACMLSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Source cannot be null"
argument_list|)
throw|;
name|addStringAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|SOURCE_KEY_ATTRIBUTE
argument_list|,
name|source
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|addStringAttribute
argument_list|(
name|attributes
argument_list|,
name|XACMLConstants
operator|.
name|SOURCE_TYPE_ATTRIBUTE
argument_list|,
name|source
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Adds a new attribute of type string to the specified 	 *<code>Set</code> of attributes.  The new attribute's value is 	 * constructed from the attrValue parameter and is given the id 	 * of the attrID parameter.  	 *  	 * @param attributes The<code>Set</code> to which the new attribute 	 * should be added.  If it is null, this method does nothing. 	 * @param attrID The ID of the new attribute, cannot be null 	 * @param attrValue The value of the new attribute.  It cannot be null. 	 */
specifier|public
specifier|static
name|void
name|addStringAttribute
parameter_list|(
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|,
name|URI
name|attrID
parameter_list|,
name|String
name|attrValue
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|attrID
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Attribute ID cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|attrValue
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Attribute value cannot be null"
argument_list|)
throw|;
name|AttributeValue
name|value
init|=
operator|new
name|StringAttribute
argument_list|(
name|attrValue
argument_list|)
decl_stmt|;
name|Attribute
name|attr
init|=
operator|new
name|Attribute
argument_list|(
name|attrID
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|add
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Adds a new attribute of type anyURI to the specified 	 *<code>Set</code> of attributes.  The new attribute's value is 	 * constructed from the uriString parameter and is given the id 	 * of the attrID parameter.  	 *  	 * @param attributes The<code>Set</code> to which the new attribute 	 * should be added.  If it is null, this method does nothing. 	 * @param attrID The ID of the new attribute, cannot be null 	 * @param uriString The value of the new attribute.  It must parse into a 	 * valid URI and cannot be null. 	 * @throws URISyntaxException if the specified attribute value is not a 	 * valid URI. 	 */
specifier|public
specifier|static
name|void
name|addURIAttribute
parameter_list|(
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|,
name|URI
name|attrID
parameter_list|,
name|String
name|uriString
parameter_list|)
throws|throws
name|URISyntaxException
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|attrID
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Attribute ID cannot be null"
argument_list|)
throw|;
if|if
condition|(
name|uriString
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Attribute value cannot be null"
argument_list|)
throw|;
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|uriString
argument_list|)
decl_stmt|;
name|AttributeValue
name|value
init|=
operator|new
name|AnyURIAttribute
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|Attribute
name|attr
init|=
operator|new
name|Attribute
argument_list|(
name|attrID
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|attributes
operator|.
name|add
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
comment|//wrapper for when the URI is known to be valid, such as when obtained from a source
comment|//that validates the URI or from a constant
specifier|private
specifier|static
name|void
name|addValidURIAttribute
parameter_list|(
name|Set
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|,
name|URI
name|attrID
parameter_list|,
name|String
name|uriString
parameter_list|)
block|{
try|try
block|{
name|addURIAttribute
argument_list|(
name|attributes
argument_list|,
name|attrID
argument_list|,
name|uriString
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
name|RuntimeException
argument_list|(
literal|"URI should never be invalid"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|RequestHelper
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

