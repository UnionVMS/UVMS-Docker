Contains requirements to be able to run this on Jenkins.
--------------------------------------------------------
As root user

sudo apt-get install python3-pip

As jenkins user

pip3 install selenium
pip3 install psycopg2
pip3 install xmlrunner

sudo apt-get install google-chrome-stable

Install latest chromedriver : https://sites.google.com/a/chromium.org/chromedriver/downloads


Windows setup
-------------
https://www.python.org/downloads/release/python-362/

pip3 install selenium
pip3 install psycopg2
pip3 install xmlrunner
pip3 install requests

Install latest chromedriver : https://sites.google.com/a/chromium.org/chromedriver/downloads
and add it to the PATH.


Postman/Newman test

Windows

1. Install Node and npm
https://nodejs.org/en/download/

2. Install newman
npm install -g newman

Linux

1. Install Node and npm
sudo apt-get install npm
sudo apt-get install nodejs-legacy

2. Install newman
npm install -g newman

Run on Windows

newman run src\test\resources\QASmoke.postman_collection.json --environment src\test\resources\test_environment --color --reporters junit,cli,html

newman run src\test\resources\SpatialIntegrationTest.postman_collection.json --environment src\test\resources\test_environment --color --reporters junit,cli,html  

newman run src\test\resources\FluxPluginWebService.postman_collection.json  --environment src\test\resources\test_environment --color --reporters junit,cli,html