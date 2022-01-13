(function () {
		
	var myDeviceToken = "dLigWPAlRdaHRJm8Yljj_A:APA91bF3mDT5PHOllaGzm2BjsFp9R8CgUcPFv_EJc4nba6zs4DfEzMKv_ZjXtSFnYts4xvDtHrs7PCjR4i1xZbf0o7PikMKdNDw6ZpjM1OXDg4s58OPjArt83GnRb1KtFSxTOstdUTMW"
	fetch("https://k-cpr-210110224946.azurewebsites.net/api/requestPush?token="+myDeviceToken)
	.then(function(response){
		console.log(myDeviceToken);
		console.log(response);
	})	
}());