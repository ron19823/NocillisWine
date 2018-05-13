Kindly Update the application.properties for db configurations like db 
url, username and password. And do make sure the Database exists by the name
given in the url.

sample mysql script for creating data base :-

CREATE DATABASE `Nocillis` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;

sample script for creating table if spring.jpa.hibernate.ddl-auto setting 
is set to none:-

CREATE TABLE `wine_buying_list` (
  `wine_id` varchar(150) COLLATE utf8_bin NOT NULL,
  `person_id` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`wine_id`),
  UNIQUE KEY `UKjwu46c5cfo7omlw1foonxmbpp` (`wine_id`,`person_id`),
  KEY `person_id_idx` (`person_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

Api details :- 

	url:- POST - http://{server-ip}:{port}/wineTasting/generateBuyingList

	body:- file
	
Sample curl:- curl -X POST \
  http://localhost:8080/wineTasting/generateBuyingList \
  -H 'cache-control: no-cache' \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -H 'postman-token: a46c1106-b863-2ccc-6022-4667d973c8e0' \
  -F file=@person_wine_4.txt
	
