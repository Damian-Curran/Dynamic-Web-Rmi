<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<h1> Dictionary Service</h1>
<body>
${wordDef}

<form method="post" action="${pageContext.request.contextPath}/ClientServlet">
		<table>
			<tr>
				<td></td>
				<td><input type="submit" name="makeQuery" value="Make another query"></td>
			</tr>
		</table>
	</form>
</body>
</html>