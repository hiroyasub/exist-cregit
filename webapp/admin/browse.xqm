module namespace browse="http://exist-db.org/xquery/admin-interface/browse";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace xdb="http://exist-db.org/xquery/xmldb";
declare namespace util="http://exist-db.org/xquery/util";

import module namespace date="http://exist-db.org/xquery/admin-interface/date" at "dates.xqm";

declare function browse:main($user as xs:string, $passwd as xs:string) as element() {
    let $colName := request:request-parameter("collection", "/db"),
        $collection := xdb:collection($colName, $user, $passwd)
    return
        <div class="panel">
            {
                browse:process-action($collection)
            }
            <div class="panel-head">Browsing Collection: {$colName}</div>
            <form method="POST" enctype="multipart/form-data">
                {
                    browse:display-collection($collection)
                }
                <table class="actions">
                    <tr><td colspan="3"><input type="submit" name="action" value="Remove Selected"/></td></tr>
                
                    <tr>
                        <td><input type="submit" name="action" value="Create Collection"/></td>
                        <td>New collection:<br/>
                        <input type="text" name="create" size="40"/></td>
                        <td/>
                    </tr>
                    
                    <tr>
                        <td><input type="submit" name="action" value="Store"/></td>
                        <td>Path to file on server:<br/>
                        <input type="text" name="uri" size="40"/></td>
                        <td>Store as:<br/>
                        <input type="text" name="name" size="20"/></td>
                    </tr>
                    <tr>
                        <td><input type="submit" name="action" value="Upload"/></td>
                        <td><input type="file" size="40" name="upload"/></td>
                        <td>Store as:<br/>
                        <input type="text" name="name" size="20"/></td>
                    </tr>
                </table>
                
                <input type="hidden" name="collection" value="{$colName}"/>
                <input type="hidden" name="panel" value="browse"/>
            </form>
        </div>
};

declare function browse:process-action($collection as object) as element()* {
    let $action := request:request-parameter("action", ())
    return
        util:catch("java.lang.Exception",
            if($action eq "Remove Selected") then
                browse:remove()
            else if($action eq "Create Collection") then
                browse:create-collection($collection)
            else if($action eq "Store") then
                browse:store($collection)
            else if($action eq "Upload") then
                browse:upload($collection)
            else
                (),
            <div class="error">
                An error occurred while processing the action:<br/>
                {$util:exception-message}
            </div>
        )
};

declare function browse:upload($collection as object) as element() {
    let $name := request:request-parameter("name", ()),
        $docName := 
            if($name) then $name 
            else request:get-uploaded-file-name("upload"),
        $file := request:get-uploaded-file("upload")
    return
        <div class="process">
            <ul>
            <li>Storing uploaded content to: {$docName}</li>
                {
                    xdb:store($collection, $docName, $file)
                }
            </ul>
        </div>
};

declare function browse:store($collection as object) as element() {
    let $uri := request:request-parameter("uri", ()),
        $path := if(starts-with($uri, "file:")) then $uri 
            else concat("file:", $uri),
        $docName := request:request-parameter("name", ())
    return
        <div class="process">
            <ul>
                <li>Storing resources from URI: {$path}</li>
                {
                    xdb:store($collection, $docName, xs:anyURI($path))
                }
            </ul>
        </div>
};

declare function browse:remove() as element() {
    let $resources := request:request-parameter("resource", ())
    return
        <div class="process">
            <ul>
                {
                    for $resource in $resources
                    return
                        browse:remove-resource($resource)
                }
            </ul>
        </div>
};

declare function browse:remove-resource($resource as xs:string) as element()* {
    let $doc := doc($resource)
    return
        if($doc) then (
            <li>Removing document: {$resource} ...</li>,
            xdb:remove(util:collection-name($doc), util:document-name($doc))
        ) else (
            <li>Removing collection: {$resource} ...</li>,
            xdb:remove($resource)
        )
};

declare function browse:create-collection($parent as object) as element() {
    let $newcol := request:request-parameter("create", ())
    return
        <div class="process">
            <ul>
            {
                if($newcol) then
                    let $col := xdb:create-collection($parent, $newcol)
                    return
                        <li>Created collection: {util:collection-name($col)}.</li>
                else
                    <li>No name specified for new collection!</li>
            }
            </ul>
        </div>
};

declare function browse:display-collection($collection as object) 
as element() {
    let $colName := util:collection-name($collection)
    return
        <table cellpadding="5" id="browse">
            <tr>
                <th/>
                <th>Name</th>
                <th>Permissions</th>
                <th>Owner</th>
                <th>Group</th>
                <th>Created</th>
                <th>Locked by</th>
            </tr>
            <tr>
                <td/>
                <td><a href="?panel=browse&amp;collection={browse:get-parent-collection($colName)}">Up</a></td>
                <td/>
                <td/>
                <td/>
                <td/>
                <td/>
            </tr>
            {
                browse:display-child-collections($collection),
                browse:display-child-resources($collection)
            }
        </table>
};

declare function browse:display-child-collections($collection as object)
as element()* {
    let $parent := util:collection-name($collection)
    for $child in xdb:get-child-collections($collection)
    let $path := concat($parent, '/', $child),
        $created := xdb:created($path)
    order by $child
    return
        <tr>
            <td><input type="checkbox" name="resource" value="{$path}"/></td>
            <td><a href="?panel=browse&amp;collection={$path}">{$child}</a></td>
            <td class="perm">{xdb:permissions-to-string(xdb:get-permissions($path))}</td>
            <td>{xdb:get-owner($path)}</td>
            <td>{xdb:get-group($path)}</td>
            <td>{date:format-dateTime($created)}</td>
            <td/>
        </tr>
};

declare function browse:display-child-resources($collection as object)
as element()* {
    let $parent := util:collection-name($collection)
    for $child in xdb:get-child-resources($collection)
    order by $child
    return
        <tr>
            <td><input type="checkbox" name="resource" value="{$parent}/{$child}"/></td>
            <td><a target="_new" href="view-source.xql?source={$parent}/{$child}">{$child}</a></td>
            <td class="perm">{xdb:permissions-to-string(xdb:get-permissions($collection, $child))}</td>
            <td>{xdb:get-owner($collection, $child)}</td>
            <td>{xdb:get-group($collection, $child)}</td>
            <td>{date:format-dateTime(xdb:created($collection, $child))}</td>
            <td>{xdb:document-has-lock($collection, $child)}</td>
        </tr>
};

declare function browse:get-parent-collection($path as xs:string) as xs:string {
    if($path eq "/db") then
        $path
    else
        replace($path, "\/[^\/]*$", "")
};
