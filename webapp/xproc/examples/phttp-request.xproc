<?xml version="1.0" encoding="UTF-8"?>
<p:pipeline xmlns:p="http://www.w3.org/ns/xproc"
            xmlns:c="http://www.w3.org/ns/xproc-step"
            name="pipeline">

<p:http-request name="http-get">  
<p:input port="source">
  <p:inline>
    <c:request xmlns:c="http://www.w3.org/ns/xproc-step" 
               href="http://tests.xproc.org/service/fixed-xml" 
               method="get"/>
  </p:inline>
</p:input>
</p:http-request>

</p:pipeline>