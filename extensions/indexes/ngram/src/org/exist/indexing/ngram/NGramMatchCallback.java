begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
comment|/**  * Callback interface used by the NGram {@link org.exist.indexing.MatchListener} to report matching  * text sequences. Pass to  * {@link NGramIndexWorker#getMatchListener(org.exist.dom.NodeProxy, NGramMatchCallback)}  * to get informed of matches.  */
end_comment

begin_interface
specifier|public
interface|interface
name|NGramMatchCallback
block|{
comment|/**      * Called by the NGram {@link org.exist.indexing.MatchListener} whenever it encounters      * a match object while traversing the node tree.      *      * @param receiver the receiver to which the MatchListener is currently writing.      * @param matchingText the matching text sequence      * @param node the text node containing the match      */
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

