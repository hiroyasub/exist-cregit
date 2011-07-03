define("ace/theme/pastel_on_dark",["require","exports","module"],function(a,b,c){var d=a("pilot/dom"),e=".ace-pastel-on-dark .ace_editor {\n  border: 2px solid rgb(159, 159, 159);\n}\n\n.ace-pastel-on-dark .ace_editor.ace_focus {\n  border: 2px solid #327fbd;\n}\n\n.ace-pastel-on-dark .ace_gutter {\n  width: 50px;\n  background: #e8e8e8;\n  color: #333;\n  overflow : hidden;\n}\n\n.ace-pastel-on-dark .ace_gutter-layer {\n  width: 100%;\n  text-align: right;\n}\n\n.ace-pastel-on-dark .ace_gutter-layer .ace_gutter-cell {\n  padding-right: 6px;\n}\n\n.ace-pastel-on-dark .ace_print_margin {\n  width: 1px;\n  background: #e8e8e8;\n}\n\n.ace-pastel-on-dark .ace_scroller {\n  background-color: #2c2828;\n}\n\n.ace-pastel-on-dark .ace_text-layer {\n  cursor: text;\n  color: #8f938f;\n}\n\n.ace-pastel-on-dark .ace_cursor {\n  border-left: 2px solid #A7A7A7;\n}\n\n.ace-pastel-on-dark .ace_cursor.ace_overwrite {\n  border-left: 0px;\n  border-bottom: 1px solid #A7A7A7;\n}\n \n.ace-pastel-on-dark .ace_marker-layer .ace_selection {\n  background: rgba(221, 240, 255, 0.20);\n}\n\n.ace-pastel-on-dark .ace_marker-layer .ace_step {\n  background: rgb(198, 219, 174);\n}\n\n.ace-pastel-on-dark .ace_marker-layer .ace_bracket {\n  margin: -1px 0 0 -1px;\n  border: 1px solid rgba(255, 255, 255, 0.25);\n}\n\n.ace-pastel-on-dark .ace_marker-layer .ace_active_line {\n  background: rgba(255, 255, 255, 0.031);\n}\n\n       \n.ace-pastel-on-dark .ace_invisible {\n  color: rgba(255, 255, 255, 0.25);\n}\n\n.ace-pastel-on-dark .ace_keyword {\n  color:#757ad8;\n}\n\n.ace-pastel-on-dark .ace_keyword.ace_operator {\n  color:#797878;\n}\n\n.ace-pastel-on-dark .ace_constant {\n  color:#4fb7c5;\n}\n\n.ace-pastel-on-dark .ace_constant.ace_language {\n  \n}\n\n.ace-pastel-on-dark .ace_constant.ace_library {\n  \n}\n\n.ace-pastel-on-dark .ace_constant.ace_numeric {\n  \n}\n\n.ace-pastel-on-dark .ace_invalid {\n  \n}\n\n.ace-pastel-on-dark .ace_invalid.ace_illegal {\n  color:#F8F8F8;\nbackground-color:rgba(86, 45, 86, 0.75);\n}\n\n.ace-pastel-on-dark .ace_invalid.ace_deprecated {\n  text-decoration:underline;\nfont-style:italic;\ncolor:#D2A8A1;\n}\n\n.ace-pastel-on-dark .ace_support {\n  color:#9a9a9a;\n}\n\n.ace-pastel-on-dark .ace_support.ace_function {\n  color:#aeb2f8;\n}\n\n.ace-pastel-on-dark .ace_function.ace_buildin {\n  \n}\n\n.ace-pastel-on-dark .ace_string {\n  color:#66a968;\n}\n\n.ace-pastel-on-dark .ace_string.ace_regexp {\n  color:#E9C062;\n}\n\n.ace-pastel-on-dark .ace_comment {\n  color:#656865;\n}\n\n.ace-pastel-on-dark .ace_comment.ace_doc {\n  color:A6C6FF;\n}\n\n.ace-pastel-on-dark .ace_comment.ace_doc.ace_tag {\n  color:A6C6FF;\n}\n\n.ace-pastel-on-dark .ace_variable {\n  color:#bebf55;\n}\n\n.ace-pastel-on-dark .ace_variable.ace_language {\n  color:#bebf55;\n}\n\n.ace-pastel-on-dark .ace_xml_pe {\n  color:#494949;\n}";d.importCssString(e),b.cssClass="ace-pastel-on-dark"})