xquery version "1.0";

module namespace config="http://exist-db.org/mods/config";

declare variable $config:mods-root := "/db/mods";
declare variable $config:search-app-root := "/db/org/library/apps/mods/search";
declare variable $config:edit-app-root := "/db/org/library/apps/mods/edit";
declare variable $config:force-lower-case-usernames as xs:boolean := true();

declare variable $config:mods-temp-collection := "/db/org/library/apps/mods/temp";

(: email invitation settings :)
declare variable $config:send-notification-emails := false();
declare variable $config:smtp-server := "smtp.yourdomain.com";
declare variable $config:smtp-from-address := "exist@yourdomain.com";

(:~ Credentials for the dba admin user :)
declare variable $config:dba-credentials := ("admin", ());
