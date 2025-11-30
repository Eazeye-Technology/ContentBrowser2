function loginCookieSend(islogin){
	var thirdDomain = getCookie("thirdDomain");
	var cookieSid = getCookie("cookiesid");
	var cookieSidCheck = getCookie("cookiesidCheck");
	if(cookieSid == cookieSidCheck){
		return;
	}
	if(!isAicai()){
        var url = "http://m.aicai.com/page/public/loginCookie.jsp?vt=5&thirdDomain="+document.domain;
    	if(islogin){
    		url = url+"&loginCookie="+cookieSid;
    	}
        creatScript(url);
	}else{
		if(thirdDomain){
	        var url = "http://"+thirdDomain+"/page/public/loginCookie.jsp?vt=5&loginCookie="+cookieSid;
			creatScript(url);
		}
	}
}

function isAicai(){
	if(document.domain.indexOf("aicai.com") != -1){
		return true;
	}else{
		return false;
	}
}

function creatScript(url){
    var script = document.createElement('script');
    script.setAttribute("type","text/javascript");
    script.setAttribute("src",url);
    document.getElementsByTagName("head")[0].appendChild(script);
}

function getCookie(name){
	var cookieValue = null;
	if (document.cookie && document.cookie != '') { 
		var cookies = document.cookie.split(';');
		for (var i = 0; i < cookies.length; i++) { 
			var cookie = cookies[i];
			if (cookie.substring(0, name.length + 2).trim() == name.trim() + "="){ 
				cookieValue = cookie.substring(name.length + 2, cookie.length); 
				break; 
			} 
		} 
	} 
	return cookieValue;
}
function loginCookieCallback(flag,sid){
	if(flag == 'true'){
		 document.cookie="cookiesidCheck="+sid;
	}
}
loginCookieSend(islogin);