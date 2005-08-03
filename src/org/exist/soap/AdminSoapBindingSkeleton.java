begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * AdminSoapBindingSkeleton.java  *  * This file was auto-generated from WSDL  * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_class
specifier|public
class|class
name|AdminSoapBindingSkeleton
implements|implements
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
implements|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|wsdl
operator|.
name|Skeleton
block|{
specifier|private
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
name|impl
decl_stmt|;
specifier|private
specifier|static
name|java
operator|.
name|util
operator|.
name|Map
name|_myOperations
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|Hashtable
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
name|_myOperationsList
init|=
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
decl_stmt|;
comment|/**     * Returns List of OperationDesc objects with this name     */
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|List
name|getOperationDescByName
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|methodName
parameter_list|)
block|{
return|return
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
name|methodName
argument_list|)
return|;
block|}
comment|/**     * Returns Collection of OperationDescs     */
specifier|public
specifier|static
name|java
operator|.
name|util
operator|.
name|Collection
name|getOperationDescs
parameter_list|()
block|{
return|return
name|_myOperationsList
return|;
block|}
static|static
block|{
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
name|_oper
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|FaultDesc
name|_fault
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
name|_params
decl_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"base64Binary"
argument_list|)
argument_list|,
name|byte
index|[]
operator|.
expr|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in2"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in3"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in4"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"boolean"
argument_list|)
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"store"
argument_list|,
name|_params
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"store"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"connect"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"connectReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"connect"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"connect"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"connect"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"connect"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"disconnect"
argument_list|,
name|_params
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"disconnect"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"disconnect"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"disconnect"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"disconnect"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"removeCollection"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"removeCollectionReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"boolean"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"removeCollection"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"removeCollection"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"removeCollection"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"removeCollection"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"removeDocument"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"removeDocumentReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"boolean"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"removeDocument"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"removeDocument"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"removeDocument"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"removeDocument"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"createCollection"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"createCollectionReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"boolean"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"createCollection"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"createCollection"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"createCollection"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"createCollection"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in2"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"xupdate"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"xupdateReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"xupdate"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"xupdate"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"xupdate"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"xupdate"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
name|_params
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
index|[]
block|{
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in0"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in1"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"in2"
argument_list|)
argument_list|,
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ParameterDesc
operator|.
name|IN
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"string"
argument_list|)
argument_list|,
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,          }
expr_stmt|;
name|_oper
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|OperationDesc
argument_list|(
literal|"xupdateResource"
argument_list|,
name|_params
argument_list|,
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|""
argument_list|,
literal|"xupdateResourceReturn"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setReturnType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"http://www.w3.org/2001/XMLSchema"
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setElementQName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"xupdateResource"
argument_list|)
argument_list|)
expr_stmt|;
name|_oper
operator|.
name|setSoapAction
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|_myOperationsList
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
if|if
condition|(
name|_myOperations
operator|.
name|get
argument_list|(
literal|"xupdateResource"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|_myOperations
operator|.
name|put
argument_list|(
literal|"xupdateResource"
argument_list|,
operator|new
name|java
operator|.
name|util
operator|.
name|ArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|(
operator|(
name|java
operator|.
name|util
operator|.
name|List
operator|)
name|_myOperations
operator|.
name|get
argument_list|(
literal|"xupdateResource"
argument_list|)
operator|)
operator|.
name|add
argument_list|(
name|_oper
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AdminSoapBindingSkeleton
parameter_list|()
block|{
name|this
operator|.
name|impl
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|AdminSoapBindingImpl
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AdminSoapBindingSkeleton
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
name|impl
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
block|}
specifier|public
name|void
name|store
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|byte
index|[]
name|in1
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in2
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in3
parameter_list|,
name|boolean
name|in4
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|impl
operator|.
name|store
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|,
name|in2
argument_list|,
name|in3
argument_list|,
name|in4
argument_list|)
expr_stmt|;
block|}
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|connect
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|java
operator|.
name|lang
operator|.
name|String
name|ret
init|=
name|impl
operator|.
name|connect
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|void
name|disconnect
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|impl
operator|.
name|disconnect
argument_list|(
name|in0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|removeCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|boolean
name|ret
init|=
name|impl
operator|.
name|removeCollection
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|boolean
name|removeDocument
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|boolean
name|ret
init|=
name|impl
operator|.
name|removeDocument
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|boolean
name|createCollection
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|boolean
name|ret
init|=
name|impl
operator|.
name|createCollection
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|int
name|xupdate
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in2
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|int
name|ret
init|=
name|impl
operator|.
name|xupdate
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|,
name|in2
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
specifier|public
name|int
name|xupdateResource
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|in0
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in1
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|in2
parameter_list|)
throws|throws
name|java
operator|.
name|rmi
operator|.
name|RemoteException
block|{
name|int
name|ret
init|=
name|impl
operator|.
name|xupdateResource
argument_list|(
name|in0
argument_list|,
name|in1
argument_list|,
name|in2
argument_list|)
decl_stmt|;
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

