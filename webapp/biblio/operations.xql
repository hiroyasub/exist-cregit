xquery version "1.0";

declare namespace op="http://exist-db.org/xquery/biblio/operations";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace response="http://exist-db.org/xquery/response";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

declare variable $rwx------ := 448;
declare variable $rwxrwx--- := 504;
declare variable $rwxrwxrwx := 511;

declare function op:create-collection($parent as xs:string, $name as xs:string) as element(status) {
    let $collection := xmldb:create-collection($parent, $name) return
        <status id="created">{$collection}</status>
};

declare function op:move-collection($parent as xs:string, $path as xs:string) as element(status) {
    let $null := xmldb:move($parent, $path) return
        <status id="moved" from="{$parent}">{$path}</status>
};

declare function op:remove-collection($collection as xs:string) as element(status) {
    let $null := xmldb:remove($collection) return
        <status id="removed">{$collection}</status>
};

declare function op:update-collection-permissions($collection as xs:string, $restriction as xs:string, $user-group as xs:string?) as element(status) {
    let $null := if($restriction eq "user") then
        (
            (: onlu this user can access, so restrict full access to user :)
            let $current-group := xmldb:get-group($collection) return
                xmldb:set-collection-permissions($collection, request:get-attribute("xquery.user"), $current-group, $rwx------)
        )
        else if($restriction eq "group")then
        (
            (: anyone in the group can access, so restrict full access to group :)
            let $current-owner := xmldb:get-owner($collection) return
                xmldb:set-collection-permissions($collection, $current-owner, $user-group, $rwxrwx---)
        )
        else
        (
            (: anyone can access, so allow full access to everyone :)
            let $current-owner := xmldb:get-owner($collection),
            $current-group := xmldb:get-group($collection) return
                xmldb:set-collection-permissions($collection, $current-owner, $current-group, $rwxrwxrwx)
        )
    return
        <status id="permissions">{$restriction}{if($restriction eq "group")then(concat(": ", $user-group))else()}</status>
};

declare function op:unknown-action($action as xs:string) {
        response:set-status-code(403),
        <p>Unknown action: {$action}.</p>
};

let $action := request:get-parameter("action", ()),
$collection := request:get-parameter("collection", ())
return
    if($action eq "create-collection")then
        op:create-collection($collection, request:get-parameter("name",()))
    else if($action eq "move-collection")then
        op:move-collection($collection, request:get-parameter("path",()))
    else if($action eq "remove-collection")then
        op:remove-collection($collection)
    else if($action eq "update-collection-permissions")then
        op:update-collection-permissions($collection, request:get-parameter("restriction", ()), request:get-parameter("userGroup",()))
    else
        op:unknown-action($action)