begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|SymbolTable
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
name|User
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
name|DBBroker
import|;
end_import

begin_class
specifier|public
class|class
name|StaticContext
block|{
specifier|protected
specifier|static
specifier|final
name|String
index|[]
index|[]
name|internalFunctions
init|=
block|{
block|{
literal|"substring"
block|,
literal|"org.exist.xpath.FunSubstring"
block|}
block|,
block|{
literal|"substring-before"
block|,
literal|"org.exist.xpath.FunSubstringBefore"
block|}
block|,
block|{
literal|"substring-after"
block|,
literal|"org.exist.xpath.FunSubstringAfter"
block|}
block|,
block|{
literal|"normalize-space"
block|,
literal|"org.exist.xpath.FunNormalizeString"
block|}
block|,
block|{
literal|"concat"
block|,
literal|"org.exist.xpath.FunConcat"
block|}
block|,
block|{
literal|"starts-with"
block|,
literal|"org.exist.xpath.FunStartsWith"
block|}
block|,
block|{
literal|"ends-with"
block|,
literal|"org.exist.xpath.FunEndsWith"
block|}
block|,
block|{
literal|"contains"
block|,
literal|"org.exist.xpath.FunContains"
block|}
block|,
block|{
literal|"not"
block|,
literal|"org.exist.xpath.FunNot"
block|}
block|,
block|{
literal|"position"
block|,
literal|"org.exist.xpath.FunPosition"
block|}
block|,
block|{
literal|"last"
block|,
literal|"org.exist.xpath.FunLast"
block|}
block|,
block|{
literal|"count"
block|,
literal|"org.exist.xpath.FunCount"
block|}
block|,
block|{
literal|"string-length"
block|,
literal|"org.exist.xpath.FunStrLength"
block|}
block|,
block|{
literal|"boolean"
block|,
literal|"org.exist.xpath.FunBoolean"
block|}
block|,
block|{
literal|"string"
block|,
literal|"org.exist.xpath.FunString"
block|}
block|,
block|{
literal|"number"
block|,
literal|"org.exist.xpath.FunNumber"
block|}
block|,
block|{
literal|"true"
block|,
literal|"org.exist.xpath.FunTrue"
block|}
block|,
block|{
literal|"false"
block|,
literal|"org.exist.xpath.FunFalse"
block|}
block|,
block|{
literal|"sum"
block|,
literal|"org.exist.xpath.FunSum"
block|}
block|,
block|{
literal|"floor"
block|,
literal|"org.exist.xpath.FunFloor"
block|}
block|,
block|{
literal|"ceiling"
block|,
literal|"org.exist.xpath.FunCeiling"
block|}
block|,
block|{
literal|"round"
block|,
literal|"org.exist.xpath.FunRound"
block|}
block|,
block|{
literal|"name"
block|,
literal|"org.exist.xpath.FunName"
block|}
block|,
block|{
literal|"local-name"
block|,
literal|"org.exist.xpath.FunLocalName"
block|}
block|,
block|{
literal|"namespace-uri"
block|,
literal|"org.exist.xpath.FunNamespaceURI"
block|}
block|,
block|{
literal|"match-any"
block|,
literal|"org.exist.xpath.FunKeywordMatchAny"
block|}
block|,
block|{
literal|"match-all"
block|,
literal|"org.exist.xpath.FunKeywordMatchAll"
block|}
block|,
block|{
literal|"id"
block|,
literal|"org.exist.xpath.FunId"
block|}
block|,
block|{
literal|"lang"
block|,
literal|"org.exist.xpath.FunLang"
block|}
block|}
decl_stmt|;
specifier|private
name|HashMap
name|namespaces
decl_stmt|;
specifier|private
name|HashMap
name|functions
decl_stmt|;
specifier|private
name|User
name|user
decl_stmt|;
specifier|public
name|StaticContext
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|loadDefaults
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|declareNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
operator|||
name|uri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null argument passed to declareNamespace"
argument_list|)
throw|;
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getURIForPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|namespaces
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|clearNamespaces
parameter_list|()
block|{
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|loadDefaults
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getClassForFunction
parameter_list|(
name|String
name|fnName
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|functions
operator|.
name|get
argument_list|(
name|fnName
argument_list|)
return|;
block|}
specifier|public
name|User
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
specifier|private
name|void
name|loadDefaults
parameter_list|()
block|{
name|SymbolTable
name|syms
init|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
decl_stmt|;
name|String
index|[]
name|prefixes
init|=
name|syms
operator|.
name|defaultPrefixList
argument_list|()
decl_stmt|;
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|(
name|prefixes
operator|.
name|length
argument_list|)
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
name|prefixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|namespaces
operator|.
name|put
argument_list|(
name|prefixes
index|[
name|i
index|]
argument_list|,
name|syms
operator|.
name|getDefaultNamespace
argument_list|(
name|prefixes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|functions
operator|=
operator|new
name|HashMap
argument_list|(
name|internalFunctions
operator|.
name|length
argument_list|)
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
name|internalFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|functions
operator|.
name|put
argument_list|(
name|internalFunctions
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|,
name|internalFunctions
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

