<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <meta name="description" content="DASP Status Page" />
    <meta name="keywords" content="DASP" />
    <meta name="author" content="DASP" />
    <title>DASP Status</title>
    <!-- 
    [:if isset($MODEL.urlTransit):]
        <meta http-equiv="refresh" content="2; url=[:$MODEL.urlTransit:]" />
    [:/if:]
    -->    
    <link rel="stylesheet" type="text/css" href="/thirdparty/bootstrap/css/bootstrap.min.css" />
    <link rel="stylesheet" type="text/css" href="/thirdparty/bootstrap/css/bootstrap-responsive.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style type="text/css">
    body {
	    padding-top: 60px;
	    padding-bottom: 40px;
	}
    </style>
</head>
<body>
	<div class="navbar navbar-fixed-top">
	    <div class="navbar-inner">
	        <div class="container-fluid">
	            <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	                <span class="icon-bar"></span>
	            </a>
	            <a class="brand" href="[:$MODEL.urlHome:]">DASP Status</a>
	        </div>
	    </div>
	</div>
	
	<#include CONTENT_FILE>
	
    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script type="text/javascript" src="/thirdparty/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
