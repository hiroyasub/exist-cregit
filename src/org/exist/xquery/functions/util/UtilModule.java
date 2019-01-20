begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2013 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|xquery
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
name|xquery
operator|.
name|functions
operator|.
name|inspect
operator|.
name|InspectFunction
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
import|;
end_import

begin_comment
comment|/**  * Module function definitions for util module.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author ljo  * @author Andrzej Taramina (andrzej@chaeron.com)  * @author Adam retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|UtilModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/util"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"util"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2004-09-12"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"pre eXist-1.0"
decl_stmt|;
specifier|public
name|boolean
name|evalDisabled
init|=
literal|false
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|BuiltinFunctions
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|BuiltinFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BuiltinFunctions
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|BuiltinFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BuiltinFunctions
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|BuiltinFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BuiltinFunctions
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|BuiltinFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BuiltinFunctions
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|BuiltinFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|InspectFunction
operator|.
name|SIGNATURE_DEPRECATED
argument_list|,
name|InspectFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|moduleDescriptionSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|registeredModuleSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|registeredModulesSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|mapModuleSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|unmapModuleSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|mappedModuleSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|mappedModulesSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|moduleInfoSig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ModuleInfo
operator|.
name|moduleInfoWithURISig
argument_list|,
name|ModuleInfo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Expand
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Expand
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Expand
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Expand
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DescribeFunction
operator|.
name|signature
argument_list|,
name|DescribeFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunDoctype
operator|.
name|signature
argument_list|,
name|FunDoctype
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL
index|[
literal|0
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL
index|[
literal|1
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL
index|[
literal|2
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_WITH_CONTEXT
index|[
literal|0
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_WITH_CONTEXT
index|[
literal|1
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_INLINE
index|[
literal|0
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_INLINE
index|[
literal|1
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_ASYNC
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_AND_SERIALIZE
index|[
literal|0
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_AND_SERIALIZE
index|[
literal|1
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Eval
operator|.
name|FS_EVAL_AND_SERIALIZE
index|[
literal|2
index|]
argument_list|,
name|Eval
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Compile
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Compile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Compile
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Compile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Compile
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Compile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DocumentNameOrId
operator|.
name|docIdSignature
argument_list|,
name|DocumentNameOrId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DocumentNameOrId
operator|.
name|docNameSignature
argument_list|,
name|DocumentNameOrId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DocumentNameOrId
operator|.
name|absoluteResourceIdSignature
argument_list|,
name|DocumentNameOrId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DocumentNameOrId
operator|.
name|resourceByAbsoluteIdSignature
argument_list|,
name|DocumentNameOrId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CollectionName
operator|.
name|signature
argument_list|,
name|CollectionName
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|LogFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|LogFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|LogFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|LogFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|LogFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|LogFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|LogFunction
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|LogFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CatchFunction
operator|.
name|signature
argument_list|,
name|CatchFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ExclusiveLockFunction
operator|.
name|signature
argument_list|,
name|ExclusiveLockFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SharedLockFunction
operator|.
name|signature
argument_list|,
name|SharedLockFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Collations
operator|.
name|signature
argument_list|,
name|Collations
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SystemProperty
operator|.
name|signature
argument_list|,
name|SystemProperty
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionFunction
operator|.
name|signature
argument_list|,
name|FunctionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CallFunction
operator|.
name|signature
argument_list|,
name|CallFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|NodeId
operator|.
name|signature
argument_list|,
name|NodeId
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetNodeById
operator|.
name|signature
argument_list|,
name|GetNodeById
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeys
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|IndexKeys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeys
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|IndexKeys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeys
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|IndexKeys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeyOccurrences
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|IndexKeyOccurrences
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeyOccurrences
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|IndexKeyOccurrences
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeyDocuments
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|IndexKeyDocuments
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexKeyDocuments
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|IndexKeyDocuments
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|IndexType
operator|.
name|signature
argument_list|,
name|IndexType
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|QNameIndexLookup
operator|.
name|signature
argument_list|,
name|QNameIndexLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Serialize
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Serialize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Serialize
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Serialize
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryDoc
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|BinaryDoc
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryDoc
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|BinaryDoc
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryDoc
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|BinaryDoc
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryToString
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|BinaryToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryToString
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|BinaryToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryToString
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|BinaryToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BinaryToString
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|BinaryToString
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Profile
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Profile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Profile
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Profile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PrologFunctions
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|PrologFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PrologFunctions
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|PrologFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PrologFunctions
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|PrologFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PrologFunctions
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|PrologFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SystemTime
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|SystemTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SystemTime
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|SystemTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SystemTime
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|SystemTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RandomFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|RandomFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RandomFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|RandomFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RandomFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|RandomFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunUnEscapeURI
operator|.
name|signature
argument_list|,
name|FunUnEscapeURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UUID
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|UUID
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UUID
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|UUID
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DeepCopyFunction
operator|.
name|signature
argument_list|,
name|DeepCopyFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetSequenceType
operator|.
name|signature
argument_list|,
name|GetSequenceType
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Parse
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Parse
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Parse
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Parse
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|NodeXPath
operator|.
name|signature
argument_list|,
name|NodeXPath
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Hash
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Hash
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Hash
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Hash
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetFragmentBetween
operator|.
name|signature
argument_list|,
name|GetFragmentBetween
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BaseConverter
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|BaseConverter
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BaseConverter
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|BaseConverter
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Wait
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Wait
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Base64Functions
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Base64Functions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Base64Functions
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Base64Functions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Base64Functions
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|Base64Functions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BaseConversionFunctions
operator|.
name|FNS_INT_TO_OCTAL
argument_list|,
name|BaseConversionFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|BaseConversionFunctions
operator|.
name|FNS_OCTAL_TO_INT
argument_list|,
name|BaseConversionFunctions
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|LineNumber
operator|.
name|signature
argument_list|,
name|LineNumber
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
static|static
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|functions
argument_list|,
operator|new
name|FunctionComparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|static
name|QName
name|EXCEPTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"exception"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|EXCEPTION_MESSAGE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"exception-message"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|ERROR_CODE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"error-code"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
name|UtilModule
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|evalDisabledParamList
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|getParameter
argument_list|(
literal|"evalDisabled"
argument_list|)
decl_stmt|;
if|if
condition|(
name|evalDisabledParamList
operator|!=
literal|null
operator|&&
operator|!
name|evalDisabledParamList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|strEvalDisabled
init|=
name|evalDisabledParamList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|strEvalDisabled
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|evalDisabled
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|strEvalDisabled
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|declareVariable
argument_list|(
name|EXCEPTION_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|declareVariable
argument_list|(
name|EXCEPTION_MESSAGE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|declareVariable
argument_list|(
name|ERROR_CODE_QNAME
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for various utility extension functions."
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
specifier|public
name|boolean
name|isEvalDisabled
parameter_list|()
block|{
return|return
name|evalDisabled
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
specifier|final
name|XQueryContext
name|xqueryContext
parameter_list|,
specifier|final
name|boolean
name|keepGlobals
parameter_list|)
block|{
if|if
condition|(
operator|!
name|keepGlobals
condition|)
block|{
name|mGlobalVariables
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|,
name|keepGlobals
argument_list|)
expr_stmt|;
block|}
specifier|static
name|FunctionSignature
name|functionSignature
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|FunctionReturnSequenceType
name|returnType
parameter_list|,
specifier|final
name|FunctionParameterSequenceType
modifier|...
name|paramTypes
parameter_list|)
block|{
return|return
name|FunctionDSL
operator|.
name|functionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|NAMESPACE_URI
argument_list|)
argument_list|,
name|description
argument_list|,
name|returnType
argument_list|,
name|paramTypes
argument_list|)
return|;
block|}
specifier|static
name|FunctionSignature
index|[]
name|functionSignatures
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|FunctionReturnSequenceType
name|returnType
parameter_list|,
specifier|final
name|FunctionParameterSequenceType
index|[]
index|[]
name|variableParamTypes
parameter_list|)
block|{
return|return
name|FunctionDSL
operator|.
name|functionSignatures
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|NAMESPACE_URI
argument_list|)
argument_list|,
name|description
argument_list|,
name|returnType
argument_list|,
name|variableParamTypes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

