Kindly Update the application.properties for thread pool size configuration, temp file 
location, server port etc.

Api details :- 

	url:- POST - http://{server-ip}:{port}/wineTasting/generateBuyingList?useMultiThreading={value > zero for multi threaded approach}

	body:- file
	
Sample curl:- curl -X POST \
  http://localhost:8181/wineTasting/generateBuyingList \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -H 'postman-token: a46c1106-b863-2ccc-6022-4667d973c8e0' \
  -F file=@person_wine_4.txt
	
