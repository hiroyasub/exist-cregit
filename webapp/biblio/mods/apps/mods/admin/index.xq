xquery version "1.0";

(: MODS XRX Application :)

import module namespace style = "http://exist-db.org/mods-style" at "../../../modules/style.xqm";

let $title := 'MODS XRX Application Admin Page'

let $content := 
    <div class="content">
       <p>Welcome to the MODS Application.</p>
       <p>This demonstration application uses eXist with XQuery and XForms to allow you to perform basic database operations CRUDS (Create, Read/Search, Update, Delete and Search) operations on MODS records:
          <ul>
              <li>
                  <a href="../index.xq">Main Index Page</a> 
              </li>
              
              <li>
                  <a href="../views/list-items.xq">List</a> List of all MODS records created with the MODS editor.
              </li>
              <li>
                  <a href="../views/document-types.xq">Create New Record From Template</a> Create a new MODS record using a predefined template
              </li>
              <li>
                  <a href="../edit/edit.xq?id=new">Create New Default Record</a> Create a new MODS record using default tabs
              </li>
              <!--
              <li>
                  <a href="../edit/edit.xq?id=new&amp;user=dan">New in {xmldb:get-current-user()} Home</a> Create a new MODS record using user data collection
              </li>
              -->
              <li>
                  <a href="../edit/edit.xq?id=new&amp;show-all=true">Create New Full Record</a> Create a new MODS record will all tabs
              </li>
              
              
          </ul>
         <!-- 
          <ol>
              <h3>XML Web Services</h3>
              <li>
                  <a href="../edit/all-codes.xq">Code Table Web Service</a> This is a list of all the codes that will be used in the edit forms.
              </li>
              <li>
                  <a href="../schemas/get-enumerated-values.xq">Enums from XML Schema</a> A tool to dump all the XML Schema enumerations
                  and put them in the XForms item/value/label format.
              </li>
         </ol>
         -->
         <ol>
              <h3>Administrative Tools</h3>
              <li>
                  <a href="../admin/list-code-tables.xq">List Code Tables</a> This is a list of all the codes that will be used in the edit forms.
              </li>
              
         </ol>
         
          <ol>
              <h3>Analysis Reports</h3>
              <li>
                  <a href="../analysis/first-level-elements.xq">First Level Elements</a> A report of all of the first level
                  elements used in our sample data for creating new instances.
              </li>
              <li>
                  <a href="../analysis/tab-report.xq">Tab Report</a> A database report of each tab and the path expressions used.
              </li>
              <li>
                  <a href="../analysis/forms-body-metrics.xq">Forms Body Report</a> Analysis of each tab body of the form.
              </li>
              <li>
                  <a href="../analysis/tabs-for-code-table.xq">All Tabs Used By A Code Table</a> Analysis of each tab body and the code tables that use it.
              </li>
              <li>
                  <a href="../views/missing-ids.xq">Missing IDs</a> All MODS records that do not have an ID.
              </li>
              <li>
                  <a href="../views/missing-titles.xq">Missing Titles</a> All MODS records that do not have at lease one title.
              </li>
         </ol>
         
         <ol>
              <h3>Unit Tests</h3>
              <li>
                  <a href="../unit-tests/edit-body-div-attribute-test.xq">Edit Body Files</a> A report of all of the files
                  use to build the body elements.
              </li>
              <li>
                  <a href="../unit-tests/tab-table-test.xq">HTML Table Tabs</a> A tool to test the XQuery mods:tabs-table() function
              </li>
         </ol>
         
         
        </p>
     </div>
     
return
    style:assemble-page($title, $content)