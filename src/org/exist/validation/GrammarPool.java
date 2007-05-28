begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|grammars
operator|.
name|XMLGrammarPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|grammars
operator|.
name|Grammar
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|xni
operator|.
name|grammars
operator|.
name|XMLGrammarDescription
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xerces
operator|.
name|util
operator|.
name|XMLGrammarPoolImpl
import|;
end_import

begin_comment
comment|/**  *  Wrapper around the Xerces XMLGrammarPoolImpl, so debugging of  * actions can be monitored. Javadoc copied from xml.apache.org.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  *  * @see org.apache.xerces.xni.grammars.XMLGrammarPool  * @see org.apache.xerces.util.XMLGrammarPoolImpl  * @see org.apache.xerces.xni.grammars.Grammar  * @see org.apache.xerces.xni.grammars.XMLGrammarDescription  */
end_comment

begin_class
specifier|public
class|class
name|GrammarPool
implements|implements
name|XMLGrammarPool
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GrammarPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XMLGrammarPool
name|pool
decl_stmt|;
comment|/**  Constructs a grammar pool with a default number of buckets. */
specifier|public
name|GrammarPool
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing GrammarPool."
argument_list|)
expr_stmt|;
name|pool
operator|=
operator|new
name|XMLGrammarPoolImpl
argument_list|()
expr_stmt|;
block|}
comment|/**  Constructs a grammar pool with a default number of buckets.           The supplied grammar pool is reused */
specifier|public
name|GrammarPool
parameter_list|(
name|XMLGrammarPool
name|pool
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing GrammarPool using supplied pool."
argument_list|)
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      *   Retrieve the initial known set of grammars. this method is called      * by a validator before the validation starts. the application can provide       * an initial set of grammars available to the current validation attempt.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#retrieveInitialGrammarSet(String)      *       * @param   type  The type of the grammar, from the       *          org.apache.xerces.xni.grammars.Grammar interface.      * @return  The set of grammars the validator may put in its "bucket"      */
specifier|public
name|Grammar
index|[]
name|retrieveInitialGrammarSet
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Retrieve initial grammarset ("
operator|+
name|type
operator|+
literal|")."
argument_list|)
expr_stmt|;
name|Grammar
index|[]
name|grammars
init|=
name|pool
operator|.
name|retrieveInitialGrammarSet
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Found "
operator|+
name|grammars
operator|.
name|length
operator|+
literal|" grammars."
argument_list|)
expr_stmt|;
return|return
name|grammars
return|;
block|}
comment|/**      *  Return the final set of grammars that the validator ended up with.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#cacheGrammars(String,Grammar[])      *       * @param type      The type of the grammars being returned      * @param grammar   an array containing the set of grammars being       *                  returned; order is not significant.      */
specifier|public
name|void
name|cacheGrammars
parameter_list|(
name|String
name|type
parameter_list|,
name|Grammar
index|[]
name|grammar
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Cache "
operator|+
name|grammar
operator|.
name|length
operator|+
literal|" grammars ("
operator|+
name|type
operator|+
literal|")."
argument_list|)
expr_stmt|;
name|pool
operator|.
name|cacheGrammars
argument_list|(
name|type
argument_list|,
name|grammar
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Allows the XMLGrammarPool to store grammars when its       * cacheGrammars(String, Grammar[]) method is called. This is the default       * state of the object.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#unlockPool      */
specifier|public
name|void
name|unlockPool
parameter_list|()
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Unlock grammarpool."
argument_list|)
expr_stmt|;
name|pool
operator|.
name|unlockPool
argument_list|()
expr_stmt|;
block|}
comment|/**      *   This method requests that the application retrieve a grammar       * corresponding to the given GrammarIdentifier from its cache. If it       * cannot do so it must return null; the parser will then call the       * EntityResolver. An application must not call its EntityResolver itself       * from this method; this may result in infinite recursions.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#retrieveGrammar(XMLGrammarDescription)      *      * @param xgd    The description of the Grammar being requested.      * @return       the Grammar corresponding to this description or null       *               if no such Grammar is known.      */
specifier|public
name|Grammar
name|retrieveGrammar
parameter_list|(
name|XMLGrammarDescription
name|xgd
parameter_list|)
block|{
if|if
condition|(
name|xgd
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"XMLGrammarDescription is null"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|xgd
operator|.
name|getNamespace
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Retrieve grammar for namespace '"
operator|+
name|xgd
operator|.
name|getNamespace
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xgd
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Retrieve grammar for publicId '"
operator|+
name|xgd
operator|.
name|getPublicId
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
block|}
return|return
name|pool
operator|.
name|retrieveGrammar
argument_list|(
name|xgd
argument_list|)
return|;
block|}
comment|/**      *  Causes the XMLGrammarPool not to store any grammars when the       * cacheGrammars(String, Grammar[[]) method is called.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#lockPool      */
specifier|public
name|void
name|lockPool
parameter_list|()
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Lock grammarpool."
argument_list|)
expr_stmt|;
name|pool
operator|.
name|lockPool
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Removes all grammars from the pool.      *      * @see org.apache.xerces.xni.grammars.XMLGrammarPool#clear      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Clear grammarpool."
argument_list|)
expr_stmt|;
name|pool
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

