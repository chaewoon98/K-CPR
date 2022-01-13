/*
* Copyright (c) 2015 Samsung Electronics Co., Ltd.
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above
* copyright notice, this list of conditions and the following disclaimer
* in the documentation and/or other materials provided with the
* distribution.
* * Neither the name of Samsung Electronics Co., Ltd. nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
* A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
* OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
* THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

var SAAgent,
    SASocket,
    connectionListener,
    responseTxt = document.getElementById("responseTxt");
var num = 0;
var audio1 = new Audio("../res/media/beep17.mp3"); 
var time = document.getElementById("time").value;
var good = 0;
var bad = 0;
var percent = 0;
var fastAudio = new Audio("../res/media/faster.mp3");

document.getElementById('number').innerHTML= num.toString();

function timer() {       
	
	  good = num-bad;
	  percent = (good/num)*100;	  
      alert("good : "+good.toString() + '\n'+"bad : "+bad.toString()+ '\n' + "percent : "+ Math.floor(percent).toString()+"%" );
       location.replace("../index.html");       
}

console.log(time);
console.log(typeof(time));
   
   switch(time){
      case "0":         
         break;
         
      case "30":            
         setTimeout(timer, 30000);
         break;               
                        
      case "60":
         setTimeout(timer, 60000);                  
         break;
                  
      case "90":         
         setTimeout(timer, 90000);         
         break;
         
      case "120":         
         setTimeout(timer, 120000);         
         break;
   }         
   
   audio1.currentTime = 0.07;   
   audio1.loop = true;
   audio1.play();

/* Make Provider application running in background */
function createHTML(log_string)
{
	// 워치 시간 설정 -> 안드에서 안내하고, 시간 설정 한 후 워치에서 바로 시작하게, 어플 2개,  
}

connectionListener = {
    /* Remote peer agent (Consumer) requests a service (Provider) connection */
    onrequest: function (peerAgent) {

        createHTML("peerAgent: peerAgent.appName<br />" +
                    "is requsting Service conncetion...");

        /* Check connecting peer by appName*/
        if (peerAgent.appName === "HelloAccessoryConsumer") {
            SAAgent.acceptServiceConnectionRequest(peerAgent);
            createHTML("Service connection request accepted.");

        } else {
            SAAgent.rejectServiceConnectionRequest(peerAgent);
            createHTML("Service connection request rejected.");

        }
    },

    /* Connection between Provider and Consumer is established */
    
    onconnect: function (socket) {
        var onConnectionLost,
            dataOnReceive;

        createHTML("Service connection established");

        /* Obtaining socket */
        SASocket = socket;

        onConnectionLost = function onConnectionLost (reason) {
            createHTML("Service Connection disconnected due to following reason:<br />" + reason);
        };

        /* Inform when connection would get lost */
        SASocket.setSocketStatusListener(onConnectionLost);

        dataOnReceive =  function dataOnReceive (channelId, data) {
            
        	var newData;

            if (!SAAgent.channelIds[0]) {
                createHTML("Something goes wrong...NO CHANNEL ID!");
                return;
            }
            
            console.log("here");
            console.log(typeof(data));
                        
            if(data == "5"){
              num++;
              document.getElementById('number').innerHTML= num.toString();
              document.getElementById('ui-page-id').style.backgroundColor= "#056ecd";
              document.getElementById('phrase').innerHTML = "GOOD";
            	
            }
            
            if(data =="6"){              
              bad++;              
              document.getElementById('number').innerHTML= num.toString();
              fastAudio.play();
              document.getElementById('ui-page-id').style.backgroundColor = "#fc2e0c";
              document.getElementById('phrase').innerHTML = "BAD";
            }                                    
        };

//        /* Set listener for incoming data from Consumer */
        SASocket.setDataReceiveListener(dataOnReceive);
    },
    onerror: function (errorCode) {
        createHTML("Service connection error<br />errorCode: " + errorCode);
    }
};

function requestOnSuccess (agents) {
    var i = 0;

    for (i; i < agents.length; i += 1) {
        if (agents[i].role === "PROVIDER") {
            createHTML("Service Provider found!<br />" +
                        "Name: " +  agents[i].name);
            SAAgent = agents[i];
            break;
        }
    }

    /* Set listener for upcoming connection from Consumer */
    SAAgent.setServiceConnectionListener(connectionListener);
};

function requestOnError (e) {
    createHTML("requestSAAgent Error" +
                "Error name : " + e.name + "<br />" +
                "Error message : " + e.message);
};

/* Requests the SAAgent specified in the Accessory Service Profile */
webapis.sa.requestSAAgent(requestOnSuccess, requestOnError);

//rkthreh 센서
var accelerationSensor = tizen.sensorservice.getDefaultSensor("LINEAR_ACCELERATION");
var gyro_x = document.getElementById('gyro_x');

function onGetSuccessCB(sensorData)
{
  console.log("######## Get the gyroscope sensor data ########");
  console.log("x: " + sensorData.x);
  console.log("y: " + sensorData.y);
  console.log("z: " + sensorData.z);
}

function onerrorCB(error)
{
  console.log("Error occurred");
}

function onsuccessCB()
{
  console.log("Sensor start");
  accelerationSensor.getLinearAccelerationSensorData(onGetSuccessCB, onerrorCB);
}

//3축에대한 가속도를 위한 변수
var recentAcc =[0,0,0];
var recentRawAcc =[0,0,0];
var previousAcc =[0,0,0];
var previousRawAcc =[0,0,0];

var recentVel =[0,0,0];
var recentRawVel =[0,0,0];
var previousVel =[0,0,0];
var previousRawVel =[0,0,0];

var recentPos =[0,0,0];
var recentRawPos =[0,0,0];
var previousPos =[0,0,0];
var previousRawPos =[0,0,0];

var depth;

var isFirst = true;

var audio = new Audio('../audio_file.mp3');

function onchangedCB(sensorData) {
	
	//if(isFirst){
//		getRecentRawAcc(recentRawAcc, sensorData);  //현제 가속도 가져옴
//		
//		setRecentAcc(recentAcc, previousRawAcc,recentRawAcc);
//		previousRawAcc = recentRawAcc.slice(); //배열 복사
//	
//		integrate(recentRawVel, previousVel, recentAcc, previousAcc,0.05);
//		previousAcc = recentAcc.slice();
//	
//		runTCE(recentVel, recentRawVel, previousVel, previousRawVel);
//		previousRawVel = recentRawVel.slice();
//	
//		console.log("속도 크기 :" +  excuteDepth(recentVel) * 100);
//					
//		integrate(recentRawPos, previousPos, recentVel, previousVel,0.05);
//		previousVel = recentVel.slice();
//		console.log(recentRawPos);
//		
//		runTCE(recentPos, recentRawPos, previousPos, previousRawPos);
//		previousRawPos = recentRawPos.slice();
//		previousPos = recentPos.slice();
			
		depth = String(sensorData.x)+","+String(sensorData.y)+","+String(sensorData.z);
	    
	    console.log("depth : " + depth);
	    SASocket.sendData(SAAgent.channelIds[0],depth);
	    createHTML("Send massage:<br />" +  depth);
//	}else{
//		getRecentRawAcc(previousRawAcc, sensorData);
//		isFirst = false;
//	}
}

function getRecentRawAcc(recentRawAcc, sensorData){
	recentRawAcc[0] = sensorData.x;
	recentRawAcc[1] = sensorData.y;
	recentRawAcc[2] = sensorData.z;
}

function setRecentAcc(recentAcc, previousRawAcc,recentRawAcc){ //filtering
	recentAcc[0] =  0.3 * previousRawAcc[0] + 0.7 * recentRawAcc[0];
	recentAcc[1] =  0.3 * previousRawAcc[1] + 0.7 * recentRawAcc[1];
	recentAcc[2] =  0.3 * previousRawAcc[2] + 0.7 * recentRawAcc[2];
}

function integrate(recentIntegratingData, previousIntegratedData, recentData, previousData, range){
	recentIntegratingData[0] = previousIntegratedData[0] + (recentData[0]) * range
	recentIntegratingData[1] = previousIntegratedData[1] + (recentData[1]) * range
	recentIntegratingData[2] = previousIntegratedData[2] + (recentData[2]) * range
}

function runTCE(recent, recentRaw, previous, previousRaw){
	recent[0] = 0.95 * (recentRaw[0] + previous[0] - previousRaw[0]);
	recent[1] = 0.95 * (recentRaw[1] + previous[1] - previousRaw[1]);
	recent[2] = 0.95 * (recentRaw[2] + previous[2] - previousRaw[2]);
}

function excuteDepth(position){
	return Math.sqrt(Math.pow(position[0],2) + Math.pow(position[0],2) + Math.pow(position[0],2)); 
}

function handleMotionEvent(event){
	var x = event	
}

//default 100ms
accelerationSensor.start(onsuccessCB);
//accelerationSensor.setChangeListener(onchangedCB, 20, 2000);
accelerationSensor.setChangeListener(onchangedCB, 80);
