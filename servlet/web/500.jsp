<%@page session="false"%>
<%@page isErrorPage="true"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<!-- 
	Error page for HTTP error 500 (General Error).
	Written by Thanh Ba Nguyen <btnguyen2k@gmail.com>
	Copyleft 2012
	-->
	<title>General Error @ DASP</title>
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
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
	<%
	String referer = request.getHeader("Referer");
	String contextPath = getServletContext().getContextPath();
	%>
	<div class="navbar navbar-fixed-top">  
		<div class="navbar-inner">  
			<div class="container-fluid">
				<!--
				<a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">  
					<span class="icon-bar"></span>  
					<span class="icon-bar"></span>  
					<span class="icon-bar"></span>  
				</a>  
				-->
				<a class="brand" href="<%=contextPath%>">DASP</a>  
				<div class="nav-collapse">  
					<ul class="nav">
						<!--
						<li class="active"><a href="#">Home</a></li>
						<li><a href="#about">About</a></li>
						<li><a href="#contact">Contact</a></li>
						-->
					</ul>  
				</div><!--/.nav-collapse -->  
			</div>  
		</div>  
	</div>
	
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="alert alert-error alert-block">
				<h4 class="alert-heading">General Error @ DASP</h4>
				An error occurred! If you feel this to be a real problem,
				please contact the Site Administrator.
			</div>
		</div>
		<div class="row-fluid">
			<div class="alert alert-info alert-block">
				<h5 class="alert-heading">Request:</h5>
				<script language="javascript" type="text/javascript">
				//var re = new RegExp("\\w+:[\\/]+.*?\\/", "g");
				//var url = location.href;
				//var uri = url.replace(re, "/");
				//document.write(uri);
				var url = location.href;
				document.write(url);
				</script>
				
				<p>&nbsp;</p>
				
				<h5>You Came From:</h5>
				<%=referer%>
				
				<p>&nbsp;</p>
				
				<%
				if ( exception != null ) {
				    out.println("<h5 class=\"alert-heading\">Exception:</h5>");
				    out.println("<pre>"+exception+"</pre>");
				    
				    out.println("<p>&nbsp;</p>");
				    
				    out.println("<h5 class=\"alert-heading\">Stacktrace:</h5>");
				    java.io.PrintWriter outstream = new java.io.PrintWriter(out);
				    out.println("<pre>");
					exception.printStackTrace(outstream);
					out.println("</pre>");
					out.println("<p>&nbsp;</p>");
					
					Throwable cause = exception.getCause();						
					while ( cause != null ) {
					    out.println("<h6 class=\"alert-heading\">Cause:</h6>");
					    out.println("<pre>");
						cause.printStackTrace(outstream);
						out.println("</pre>");
						cause = cause.getCause();
					}
				}
				%>
			</div>
		</div>
	</div>
</body>
</html>