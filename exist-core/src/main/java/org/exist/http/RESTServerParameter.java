begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2013 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
package|;
end_package

begin_comment
comment|/**  * Enumeration of each Parameter  * used by the RESTServer  *   * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_enum
enum|enum
name|RESTServerParameter
block|{
comment|/**      * The results of XPath and XQuery executions      * by the REST Server are cached if the Cache Parameter is set.      * This can be used in the Query String of a GET request      * to release the cached results of a query.      *       * The value of the parameter should be the cached session id.      *       * Contexts: GET      */
name|Release
block|,
comment|/**      * Can be used in the Query String of a GET request      * to provide an XPath to execute. The context of the XPath      * is the resource or collection indicated in the URI.      *       * The value of the parameter should be an XPath expression.      *       * Contexts: GET      */
name|XPath
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request to provide an XQuery      * to execute. The context of the XQuery is the resource or      * collection indicated in the URI.      *       * Contexts: GET, POST      *       * The value of the parameter when used in the query string of a GET      * request should be a valid XQuery.      *       * The value of this parameter used in the body of POST requests      * has the following format:      *       *<exist:query start? = number      *  max? = number      *  cache? = ("yes" | "no")      *  session? = string      *  typed? = ("yes" | "no")      *  (wrap = ("yes" | "no") | enclose = ("yes" | "no"))?      *  encoding? = string      *  method? = string>      *      (exist:text,      *      exist:variables?,      *      exist:properties?)      *</exist:query>      */
name|Query
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request to specify values for      * any XQuery external variables that you wish to bind.      *       * Contexts: GET, POST      *       * The value of this parameter, is an XML element with the format:      *       *<exist:variables>      *      (exist:variable+)      *</exist:variables>      */
name|Variables
block|,
comment|/**      * XML description can be used inside Variables      * in either the Query String of a GET request or      * in the body of a POST request to specify the name      * and value of an external XQuery variable.      *       * Contexts: GET, POST      *       * The value of this parameter, is an XML element with the format:      *       *<exist:variable>      *      (exist:qname,      *      sx:sequence)      *</exist:variable>      *       *<exist:qname>      *      (exist:prefix?,      *       exist:localname,      *       exist:namespace?)      *</exist:qname>      *       *<sx:sequence>      *      (sx:value+)      *</sx:sequence>      *       *<sx:value type? = string>      *      (text() | element())      *</sx:value>      */
name|Variable
block|,
comment|/**      * Can be used in the Query String of a GET request when      * supplying an XPath or XQuery to indicate how many      * results should be returned (if the query returns a sequence      * of items).      *       * Contexts: GET      *       * The value of the parameter should be a number greater than zero.      *       * See Max for POST requests.      */
name|HowMany
block|,
comment|/**      * Can be used in the body of a POST request when      * supplying an XQuery to indicate how many      * results should be returned (if the query returns a sequence      * of items).      *       * Contexts: POST      *       * The value of the parameter should be a number greater than zero.      *       * See HowMany for GET requests.      */
name|Max
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request when supplying an XPath or XQuery      * to  indicate where the result sequence should start from      * (if the query returns a sequence of items)      *       * For GET requests the result subsequence is Start => results<= HowMany      *       * For POST requests the result subsequence is Start => results<= Max      *       * Contexts: GET, POST      *       * The value of the parameter should be a positive number.      */
name|Start
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request when supplying an XPath or XQuery,      * it causes the results of the query to be annotated with data type      * information.      *       * Contexts: GET, POST      *       * The value of the parameter should be either "yes" or "no".      */
name|Typed
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request when supplying an XPath or XQuery,      * it causes the results of the query to be wrapped in an exist:result      * element with some detail about the results.      *       * Contexts: GET, POST      *       * The value of the parameter should be either "yes" or "no".      *       * The format of the wrapper has the format:      *       *<exist:result hits = number      *  start = number      *  count = number      *  session? = string>      *      any*      *</exist:result>      */
name|Wrap
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request when supplying an XPath or XQuery,      * it causes the results of the query to be cached on the server.      *       * This parameter is useful in combination with Start and HowMany/Max      * to enable you to execute a query once, and then retrieve the results      * in pages.      *       * Cached results are assigned a Session ID which you need to use in       * subsequent requests. The Session ID is returned in the      * exist:result/@session attribute if you have used the Wrap parameter,      * and also in the HTTP Response Header X-Session-Id      *       * Contexts: GET, POST      *       * The value of the parameter should be either "yes" or "no".      */
name|Cache
block|,
comment|/**      * Can be used in the Query String of a GET request      * to indicate that an XML result should be indented.      *       * Contexts: GET      *       * The value of the parameter should be either "yes" or "no".      */
name|Indent
block|,
comment|/**      * Can be used in the Query String of a GET request to retrieve      * the Source Code of an XQuery or XProc resource rather than      * executing the resource.      *       * Contexts: GET      *       * The value of the parameter should be either "yes" or "no".      */
name|Source
block|,
comment|/**      * Can be used in either the Query String of a GET request      * or in the body of a POST request when supplying an XPath or XQuery,      * it identified the results of a previous query that were cached on the      * server.      *       * Contexts: GET, POST      *       * The value of the parameter should be a Session ID previously assigned      * by eXist from a request made using the Cache parameter.      */
name|Session
block|,
comment|/**      * Can be used in the Query String of a GET request to enable or       * disable expansion of XSL Processing Instructions by the serializer      * operating on the result of the request.      * It can also be used to specify a specific XSL to be used by the      * serializer.      *       * Contexts: GET      *       * The value of the parameter should be either "yes", "no", or a path      * to an XSL file in the database.      *       * Note - The expansion of XSL Processing Instruction is also enabled or      * disabled in conf.xml.      */
name|XSL
block|,
comment|/**      * Can be used in the Query String of a GET or HEAD request to specify      * the character encoding to be used to form the response.      *       * Contexts: GET, HEAD      */
name|Encoding
block|,
comment|/**      * Enclose is a synonym for Wrap.      * See Wrap.      *       * Contexts: POST      */
name|Enclose
block|,
comment|/**      * Can be used in the body of a POST request when supplying an XQuery,      * it causes the results of the query to be serialized just the supplied      * serializer method.      *       * Contexts: POST      *       * The value of the parameter must be a valid serialization method,      * currently this is one of:      *  "xml", "xhtml", "html", "html5", "json" or "text"      */
name|Method
block|,
comment|/**      * TODO This is not currently implemented.      */
name|Mime
block|,
comment|/**      * Can be used in the body of a POST request when supplying an XQuery,      * it contains the XQuery itself. Typically the XQuery is first placed      * in a CDATA section to escape any non-XML characters.      *       * Contexts: POST      *       *<exist:text>      *<![CDATA[      *<my-query>{current-dateTime()}</my-query>      *  ]]>      *</exist:text>      */
name|Text
block|,
comment|/**      * Can be used in the body of a POST request when supplying an XQuery,      * it contains extra properties for the serializer. See Property.      *       * Contexts: POST      *       *<exist:properties>      *  (exist:property+)      *</exist:properties>      */
name|Properties
block|,
comment|/**      * Can be used in the body of a POST request when supplying an XQuery,      * it contains a property definition for the serializer      *  e.g.<exist:property name="omit-xml-declaration" value="yes"/>      *       * Contexts: POST      *       *<exist:property name = string      *  value = string/>      */
name|Property
block|;
comment|/**      * Get the parameter key that is      * to be used in a URL Query String.      *       * @return The parameter key, suitable for use in a URL      */
specifier|public
name|String
name|queryStringKey
parameter_list|()
block|{
return|return
literal|"_"
operator|+
name|xmlKey
argument_list|()
return|;
block|}
comment|/**      * Get the parameter key that is to      * be used in an XML document as      * either an element or attribute name.      *       * @return The parameter key, suitable for use in an XML document      */
specifier|public
name|String
name|xmlKey
parameter_list|()
block|{
return|return
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

