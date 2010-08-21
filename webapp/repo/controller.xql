xquery version "1.0";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace xdb = "http://exist-db.org/xquery/xmldb";

let $uri := request:get-uri()
let $context := request:get-context-path()
let $install :=  request:get-parameter('install','')
let $remove :=  request:get-parameter('remove','')
let $path := substring-after($uri, $context)
let $name := replace($uri, '^.*/([^/]+)$', '$1')
return
    if ($path = "/repo/") then
	    <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
    		<redirect url="repo.xml"/>
    	</dispatch>
    else if (ends-with($uri, '.xar')) then

     if($install eq 'true') then
       <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
            <forward url="../install-package.xqy">
                <add-parameter name="url" value="{$uri}"/>
            </forward>
        </dispatch>
     else if ($remove eq 'true') then
       <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
            <forward url="../remove-package.xqy">
                <add-parameter name="name" value="{substring-before($name,'.xar')}"/>
            </forward>
        </dispatch>
     else
        ()     
    else if (ends-with($uri, '.xml')) then
        <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
			<view>
				<forward servlet="XSLTServlet">
					<set-attribute name="xslt.stylesheet" 
						value="{$exist:root}/stylesheets/db2xhtml.xsl"/>
				    <set-attribute name="xslt.output.media-type"
                        value="text/html"/>
                	<set-attribute name="xslt.output.doctype-public"
                	    value="-//W3C//DTD XHTML 1.0 Transitional//EN"/>
                	<set-attribute name="xslt.output.doctype-system"
                	    value="resources/xhtml1-transitional.dtd"/>
				</forward>
			</view>
            <cache-control cache="no"/>
		</dispatch>
    else if ($name = ('default-style.css', 'default-style2.css')) then
        let $newPath := replace($path, '^.*/([^/]+/[^/]+)$', '/$1')
        return
        <dispatch xmlns="http://exist.sourceforge.net/NS/exist">
			<forward url="{$newPath}"/>
			<cache-control cache="yes"/>
		</dispatch>
    else
        <ignore xmlns="http://exist.sourceforge.net/NS/exist">
            <cache-control cache="yes"/>
    	</ignore>