<!DOCTYPE html> 
<html>
<head>
	<title>sungard mobile</title>
<link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.css" />
<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.js"></script>
<link rel="stylesheet" href="http://demos.jquerymobile.com/1.4.2/theme-classic/theme-classic.css" />
</head>


	<body>

<section id="page1" data-role="page">
  <header data-role="header"  data-theme="b" ><h1>Welcome</h1></header>
  <div data-role="content" class="content">
  <img src="data:image/jpg;base64,${person.byteArrayString}" />
  ${image}
  <br/>
        welcome ${person.name} ${person.email}
  </div>
  <footer data-role="footer" data-theme="b"><h1>Sungard Mobile</h1></footer>
</section>
</body>


</html>