begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
operator|.
name|xquery
package|;
end_package

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
name|dom
operator|.
name|persistent
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
name|storage
operator|.
name|md
operator|.
name|Meta
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
name|md
operator|.
name|MetaData
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
name|md
operator|.
name|Metas
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|value
operator|.
name|StringValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
operator|.
name|MDStorageManager
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  * @author Casey Jordan<casey.jordan@jorsek.com>  */
end_comment

begin_class
specifier|public
class|class
name|PairSet
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|QName
name|NAME
init|=
operator|new
name|QName
argument_list|(
literal|"set-value"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|NAME_URL
init|=
operator|new
name|QName
argument_list|(
literal|"set-value-by-url"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Set document's key/value pair."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RETURN
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"Key/value pair UUID"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
name|NAME
argument_list|,
name|DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"document"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The document."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"key"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The key."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The value."
argument_list|)
block|}
argument_list|,
name|RETURN
argument_list|)
block|,
comment|//		new FunctionSignature(
comment|//				NAME,
comment|//				DESCRIPTION,
comment|//				new SequenceType[] {
comment|//					 new FunctionParameterSequenceType("document", Type.ITEM, Cardinality.EXACTLY_ONE, "The document."),
comment|//					 new FunctionParameterSequenceType("key", Type.STRING, Cardinality.EXACTLY_ONE, "The key."),
comment|//					 new FunctionParameterSequenceType("value", Type.DOCUMENT, Cardinality.EXACTLY_ONE, "The value.")
comment|//				},
comment|//				RETURN
comment|//			),
operator|new
name|FunctionSignature
argument_list|(
name|NAME_URL
argument_list|,
name|DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"document"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The document's URL."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"key"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The key."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The value."
argument_list|)
block|}
argument_list|,
name|RETURN
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|PairSet
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Metas
name|metas
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|instanceof
name|DocumentImpl
condition|)
block|{
name|metas
operator|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getMetas
argument_list|(
operator|(
operator|(
name|DocumentImpl
operator|)
name|args
index|[
literal|0
index|]
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unsupported type "
operator|+
name|args
index|[
literal|0
index|]
operator|.
name|getItemType
argument_list|()
argument_list|)
throw|;
if|else if
condition|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|NAME_URL
argument_list|)
condition|)
name|metas
operator|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getMetas
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|metas
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"No metadata found."
argument_list|)
throw|;
name|Meta
name|meta
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|instanceof
name|DocumentImpl
condition|)
name|meta
operator|=
name|metas
operator|.
name|put
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
else|else
name|meta
operator|=
name|metas
operator|.
name|put
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|ValueSequence
name|returnSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|returnSeq
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|meta
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|returnSeq
return|;
block|}
block|}
end_class

end_unit

