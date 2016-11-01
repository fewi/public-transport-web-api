#public-transport-web-api

Project description
----------------------

This application is a small web api for the [public-transport-enabler](https://github.com/schildbach/public-transport-enabler).

I started this project for an IoT Application with the ESP8266. The repository can be found here: [Internet-of-Things-with-ESP8266](https://github.com/fewi/Internet-of-Things-with-ESP8266)


Checkout from GitHub
----------
Don't forget to init and update git submodules for the public-transport-enabler library.

cd vagfr-rest-wrapper

git submodule init

git submodule update

Run the application
-------------

```
./gradlew bootRun
```
Test URL for your Browser: http://localhost:8080/connection?from=6906508&to=6930811&product=T

----------


REST Endpoint description
-------------------

### GET:  /provider
 With this endpoint you can get all existing provider which can be used as optional parameter, default provider is VAG Freiburg (Vagfr)  
 
**Example:** 
/provider

----------

### GET:  /station/suggest
 With this endpoint you can get the station ids which you need to use later.  
 
**Parameter:**
 
 1. q -- Name of station you want to search
 2. (optional) providerName -- Name of the provider, for example: Vagfr
 3. (optional) locationType -- type of the locations, default: ANY, possible values. ANY, STATION, STREET, POI

**Example:** 
/station/suggest?q=Technisches+Rathaus

----------

###GET: /connection
Lists all trips from one station to another with departure time and line number. 

**Please note that only direct connections will be listed**

 **Parameter:**
 
 1. from -- Station id from departure station
 2. to -- Station id from the arrival station
 3. product -- Product you want to use ( T = Tram, B = Bus)
 4. (optional) timeOffset -- Minutes e.g. you need to walk to the station.
 5. (optional) providerName -- Name of the provider, for example: Vagfr
 
**Example:** 
/connection?from=6906508&to=6930811&product=T

----------

###GET: /departure
Lists all departure trains of the given station

 **Parameter:**

 1. from -- Station id from departure station
 2. (optional) providerName -- Name of the provider, for example: Vagfr
 3. (optional) limit -- Limit the result set, default 10

**Example:**
/departure?from=6906508

----------

###GET: /connectionEsp
Get next departure time for your trip. Lightwight for easy processing with the ESP8266  
**Please note that only direct connections will be listed**

 **Parameter:**
 
 1. from -- Station id from departure station
 2. to -- Station id from the arrival station
 3. product -- Product you want to use ( T = Tram, B = Bus)
 4. (optional) timeOffset -- Minutes e.g. you need to walk to the station.
 5. (optional) providerName -- Name of the provider, for example: Vagfr
 
**Example:** 
/connectionEsp?from=6906508&to=6930811&product=T&timeOffset=5

----------

###GET: /departureFHEM
Lists all departure trains of the given station in the format for FHEM. http://forum.fhem.de/index.php/topic,48255.0.html

 **Parameter:**

 1. from -- Station id from departure station
 2. (optional) providerName -- Name of the provider, for example: Vagfr
 3. (optional) limit -- Limit the result set, default 10

**Example:**
/departureFHEM?from=6906508&limit=6

----------

###GET: /connectionRaw
Lists all trips from one station to another with with all data the public-transport-enabler libary delivers.

 **Parameter:**
 
 1. from -- Station id from departure station
 2. to -- Station id from the arrival station
 3. product -- Product you want to use ( T = Tram, B = Bus)
 4. (optional) timeOffset -- Minutes e.g. you need to walk to the station. 
 5. (optional) providerName -- Name of the provider, for example: Vagfr
 
**Example:** 
/connectionRaw?from=6906508&to=6930811&product=T

----------

Run the application in Openshift
-------------
Based on description from Rafal Borowiec the original description you can found here: [https://github.com/kolorobot/openshift-diy-spring-boot-gradle]
### Prerequisite
 
Before we can start building the application, we need to have an OpenShift free account and client tools installed.
 
### Step 1: Create DIY application
 
To create an application using client tools, type the following command:
 
     rhc app create <app-name> diy-0.1
 
This command creates an application *<app-name>* using *DIY* cartridge and clones the repository to *<app-name>* directory.
 
### Step 2: Delete Template Application Source code
 
OpenShift creates a template project that can be freely removed:
 
     git rm -rf .openshift README.md diy misc
 
Commit the changes:
 
     git commit -am "Removed template application source code"
 
### Step 3: Pull Source code from GitHub
 
     git remote add upstream https://github.com/fewi/vagfr-rest-wrapper
     git pull -s recursive -X theirs upstream master
 
### Step 4: Push changes
 
The basic template is ready to be pushed to OpenShift:
 
     git push
 
The initial deployment (build and application startup) will take some time (up to several minutes). Subsequent deployments are a bit faster:
 
     remote: BUILD SUCCESSFUL
     remote: Starting DIY cartridge
     remote: XNIO NIO Implementation Version 3.3.0.Final
     remote: s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
     remote: d.f.vagfr.VagfrRestWrapperApplication    : Started VagfrRestWrapperApplication in 7.114 seconds (JVM running for 8.323)
 
You can now browse to: `http://<app-name>.rhcloud.com/provider` and you should see:
 
     [{"name":"Vmv","aClass":"VmvProvider"},{"name":"Vbb","aClass":"VbbProvider"},{"name":"Sf","aClass":"SfProvider"},{"name":"Gvh","aClass":"GvhProvider"},{"name":"Mvg","aClass":"MvgProvider"},{"name":"Oebb","aClass":"OebbProvider"},{"name":"Mersey","aClass":"MerseyProvider"},{"name":"Bvg","aClass":"BvgProvider"},{"name":"Vao","aClass":"VaoProvider"},{"name":"Vagfr","aClass":"VagfrProvider"},{"name":"Paris","aClass":"ParisProvider"},{"name":"Nasa","aClass":"NasaProvider"},{"name":"Linz","aClass":"LinzProvider"},{"name":"Lu","aClass":"LuProvider"},{"name":"Septa","aClass":"SeptaProvider"},{"name":"Vvs","aClass":"VvsProvider"},{"name":"Vvo","aClass":"VvoProvider"},{"name":"Met","aClass":"MetProvider"},{"name":"Nvv","aClass":"NvvProvider"},{"name":"Tfi","aClass":"TfiProvider"},{"name":"Sydney","aClass":"SydneyProvider"},{"name":"Zvv","aClass":"ZvvProvider"},{"name":"Vrr","aClass":"VrrProvider"},{"name":"Ding","aClass":"DingProvider"},{"name":"Nri","aClass":"NriProvider"},{"name":"Nvbw","aClass":"NvbwProvider"},{"name":"Vms","aClass":"VmsProvider"},{"name":"Stockholm","aClass":"StockholmProvider"},{"name":"Rt","aClass":"RtProvider"},{"name":"Se","aClass":"SeProvider"},{"name":"Jet","aClass":"JetProvider"},{"name":"Bsvag","aClass":"BsvagProvider"},{"name":"Bayern","aClass":"BayernProvider"},{"name":"Vbl","aClass":"VblProvider"},{"name":"Tlem","aClass":"TlemProvider"},{"name":"Vor","aClass":"VorProvider"},{"name":"Vgn","aClass":"VgnProvider"},{"name":"Bvb","aClass":"BvbProvider"},{"name":"Svv","aClass":"SvvProvider"},{"name":"Invg","aClass":"InvgProvider"},{"name":"Avv","aClass":"AvvProvider"},{"name":"Vvm","aClass":"VvmProvider"},{"name":"Pl","aClass":"PlProvider"},{"name":"Vvv","aClass":"VvvProvider"},{"name":"Sncb","aClass":"SncbProvider"},{"name":"Sbb","aClass":"SbbProvider"},{"name":"Dub","aClass":"DubProvider"},{"name":"Bahn","aClass":"BahnProvider"},{"name":"Vbn","aClass":"VbnProvider"},{"name":"Vrn","aClass":"VrnProvider"},{"name":"Vrs","aClass":"VrsProvider"},{"name":"Vgs","aClass":"VgsProvider"},{"name":"Wien","aClass":"WienProvider"},{"name":"Italy","aClass":"ItalyProvider"},{"name":"Ns","aClass":"NsProvider"},{"name":"Dsb","aClass":"DsbProvider"},{"name":"Mvv","aClass":"MvvProvider"},{"name":"Sh","aClass":"ShProvider"},{"name":"Kvv","aClass":"KvvProvider"},{"name":"Vvt","aClass":"VvtProvider"},{"name":"Atc","aClass":"AtcProvider"},{"name":"Paca","aClass":"PacaProvider"},{"name":"Eireann","aClass":"EireannProvider"},{"name":"FrenchSouthWest","aClass":"FrenchSouthWestProvider"},{"name":"Stv","aClass":"StvProvider"},{"name":"Ivb","aClass":"IvbProvider"}]
 
### Under the hood
 
See: [http://blog.codeleak.pl/2015/02/openshift-diy-build-spring-boot.html]
