begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Package memtree implements a fast, in-memory DOM tree, which is used by eXist for the  * nodes created inside an XQuery expression.  * The code is an adoption of the tinytree implementation found in Michael H. Kay's  * Saxon. The implementation should be very memory efficient: the data for every node in the document  * node tree is stored in the document object itself, using simple arrays.  *  * @author Wolfgang  * @since 0.9.3  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
package|;
end_package

end_unit

