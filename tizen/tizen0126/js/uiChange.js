(function () {
   var time = document.getElementById("time").value;   
   var audio1 = new Audio("../res/media/beep17.mp3");   
   var fastAudio = new Audio("../res/media/faster.mp3");
   var num = 0;
   var sum = 0;
   var good = 0;
   var bad = 0;
   var percent = 0;
   
   //가속도 센서
   var accelerationSensor = tizen.sensorservice.getDefaultSensor("LINEAR_ACCELERATION");
   var gyro_x = document.getElementById('gyro_x');

   // 3축 가속도 변수
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
   var avoidDuplicationCount=0;
   
   // 추가된 부분
   var position = [];
   var peak = 0;
   var second = 0;
   var peakSecond = 0;
   var peakToPeakSecond = 0;
   var listSize = 18;
   
   var nowTime = 0;
   var nextTime = 0;
   var k=0;
   
   console.log(time);
   
   document.getElementById('number').innerHTML="0";                  
   
   function timer() {            
      alert("good : "+good.toString() + '\n'+"bad : "+bad.toString()+ '\n' + "percent : "+ percent.toString()+"%" );
       location.replace("../index.html");       
   }
   
   
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
   
   setInterval(function(){
	   
	  document.getElementById('number').innerHTML= num.toString();	  	  
	   
   }, 600);
   
//푸쉬 부분 시작   
function onchangedCB(sensorData) {
  
	var depth = String(sensorData.x)+","+String(sensorData.y)+","+String(sensorData.z);            
	
   recentSVM = getSVM(sensorData);
   addSVMData(SVMdataList, recentSVM);
   
//   //findPeakSVM(SVMdataList);
//   
//   getRecentRawAcc(recentRawAcc, sensorData);  //현제 가속도 가져옴
//   
//   setRecentAcc(recentAcc, previousRawAcc,recentRawAcc);
//   previousRawAcc = recentRawAcc.slice(); //배열 복사
//   
//   integrate(recentRawVel, previousVel, recentAcc, previousAcc,0.02);
//   previousAcc = recentAcc.slice();
//   
//   runTCE(recentVel, recentRawVel, previousVel, previousRawVel);
//   previousRawVel = recentRawVel.slice();
//            
//   integrate(recentRawPos, previousPos, recentVel, previousVel,0.02);
//   previousVel = recentVel.slice();
//      
//   runTCE(recentPos, recentRawPos, previousPos, previousRawPos);
   
//   //previousRawPos = recentRawPos.slice();
//   //previousPos = recentPos.slice();
//         
//   depth = excuteDepth(recentPos);
//      
//   push(depth, 0.05);
//   console.log(position);
//      
//      
   if(findPeakSVM(SVMdataList) > 0){
           num++;
           k++;
           console.log("된다");                                      
           
           document.getElementById('ui-page-id').style.backgroundColor= "#056ecd";
	       document.getElementById('phrase').innerHTML = "GOOD";
	       
           if(k%2 == 1){
        	   nowTime = new Date();        	   
           } else{
        	   nextTime = new Date();
        	   
        	   console.log(nextTime.getTime());
        	   console.log(nowTime.getTime());
        	   console.log(nextTime.getTime()-nowTime.getTime());
        	   
        	   if(nextTime.getTime()-nowTime.getTime() > 600){
        		   document.getElementById('ui-page-id').style.backgroundColor = "#fc2e0c";
        	         document.getElementById('phrase').innerHTML = "BAD";        	         
        	         fastAudio.play();
        	   } 
           }
           
    }else{
          console.log("안된다");
                    
    }           
}


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

function getRecentRawAcc(recentRawAcc, sensorData){
   recentRawAcc[0] = sensorData.x;
   recentRawAcc[1] = sensorData.y;
   recentRawAcc[2] = sensorData.z;
}

function setRecentAcc(recentAcc, previousRawAcc,recentRawAcc){ //filtering
   recentAcc[0] =  0.3 * previousRawAcc[0] + 0.7 * recentRawAcc[0];
   recentAcc[1] =  0.3 * previousRawAcc[1] + 0.7 * recentRawAcc[1];
   recentAcc[2] =  0.3 * previousRawAcc[2] + 0.7 * recentRawAcc[2];
   previousRawAcc = recentAcc.slice();
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
   return Math.sqrt(Math.pow(position[0],2) + Math.pow(position[0],2) + Math.pow(position[0],2)) *100; 
}

function handleMotionEvent(event){
   var x = event;
}

function count(){
   return position.length;
}

function push(recentDepth, second){
   position.push(recentDepth);
   
   this.second += second;
   
   if(position.length > listSize){
        position.shift(); // 맨 앞 배열 요소 지우기
    }
   
}

function findPeak(){
   
   if(count() < listSize){
      console.log("1");
      return -1;
   }
   
   if(isDescentPattern(listSize/2) && isRisingPattern(listSize/2)){
      console.log("2");
        if(peak != position[listSize/2] && position[listSize/2] > 2){
           console.log("3");
            peak = position[listSize/2];
            peakToPeakSecond = second - peakSecond;
            peakSecond = second;            
            return peakToPeakSecond;
        }
    }
   console.log("4");
    return -1;
}

function isRisingPattern(toNum){
   var out = 0;

    for( counter = 0; counter < toNum; counter++){
        if(position[counter] > position[counter + 1]){
            ++out;
            if(out > 1) return false;
        }
    }

    return true;
}

function isDescentPattern(toNum){
   var out = 0;
   
    for(counter = toNum; counter < count() - 1; counter++){
        if(position[counter] < position[counter + 1]){
           ++out;
            if(out > 1) return false;
        }
    }
   return true;
}


/*
 *  만보기
 */1
var recentSVM;
var SVMdataList = [];
var peakList = [];
const SVMdataListSize = 5; //홀수 넣어주기
const peakListSize = 5;

var Threshold = 25;

//recentSVM = getSVM(sensorData);
//addSVMData(SVMdataList, recentSVM);

function getSVM(sensorData){
   return Math.sqrt(Math.pow(sensorData.x,2) + Math.pow(sensorData.y,2) + Math.pow(sensorData.z,2))
}

function addSVMData(SVMdataList, recentSVM){
   SVMdataList.push(recentSVM);
   
   if(SVMdataList.length > SVMdataListSize ){
      SVMdataList.shift();
   }   
}
var a = 0;
function findPeakSVM(SVMDataList){   
   
   var investigatedIndex = Math.floor(SVMDataList.length/2);

   if(SVMDataList[investigatedIndex] < Threshold) return -1; //피크없음 1-1
   
   
   
   
   for(var i = 1; i < SVMDataList.length/2 ; i++){
      if(SVMDataList[investigatedIndex - i] > SVMDataList[investigatedIndex - i + 1]){
         return -1;
      }
      
      if(SVMDataList[investigatedIndex + i - 1] < SVMDataList[investigatedIndex + i]){
         return -1;
      }
      
   }   
   
   console.log(a++);
   return SVMDataList[investigatedIndex];
}

function isDramaValue(SVMdata,index){
   
}



//default 100ms
accelerationSensor.start(onsuccessCB);
//accelerationSensor.setChangeListener(onchangedCB, 20, 2000);
accelerationSensor.setChangeListener(onchangedCB, 20);

}());