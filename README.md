# docker-narwassco
This project is for web-gis of Narok Water and Sewerage Services Company in Kenya

This docker-compose.yml includes the following container;
* mapserver: mapserver container. it provides map services to our web application.
* tiles: tile server on apache. it provides TMS layer for basemap.
* tomcat: application server. it provides Web-GIS application.

But this docker-compose does not include PostgreSQL/PostGIS.
You must install PostGIS outside of docker container. Docker container will access to PostGIS on host computer by  "host.docker.internal" as host name.

If you changed source code of Web-GIS application under webapp folder, please use eclipse to make gisapp.war file under build folder. ./build/gisapp.war file will bind to tomcat webapps folder.

this git repository does not includes tile data and base map data because data size will be too large. so please copy tile data and base map data under mapserver folder.

all of source code and images belong to Narok Water and Sewerage Services Co., Ltd. in Kenya.