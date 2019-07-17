begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|ngram
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
name|NodeProxy
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
name|serializer
operator|.
name|Receiver
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Callback interface used by the NGram {@link org.exist.indexing.MatchListener} to report matching  * text sequences. Pass to  * {@link NGramIndexWorker#getMatchListener(org.exist.storage.DBBroker, org.exist.dom.persistent.NodeProxy, NGramMatchCallback)}  * to get informed of matches.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NGramMatchCallback
block|{
comment|/**      * Called by the NGram {@link org.exist.indexing.MatchListener} whenever it encounters      * a match object while traversing the node tree.      *      * @param receiver the receiver to which the MatchListener is currently writing.      * @param matchingText the matching text sequence      * @param node the text node containing the match      *      * @throws XPathException if a query error occurs      * @throws SAXException if a parse error occurs      */
specifier|public
name|void
name|match
parameter_list|(
name|Receiver
name|receiver
parameter_list|,
name|String
name|matchingText
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
function_decl|;
block|}
end_interface

end_unit

