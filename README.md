# spring-boot-keycloak-curd-service
** spring boot using keycloak for authentication and authorization, PostgreSQL, create keycloak user, create new ROLE assigned existing/new ROLES and assigned GROUP from Spring-Boot Rest API.

spring boot security with keycloak server

1- Download keycloak zip.
2- under bin folder open cmd and type- standalone.bat -Djboss.socket.binding.port-offset=100.
for higher version -- kc.bat start-dev
3- for keycloak server -----  http://localhost:8180
for higher version(> 19) server port ------ http://localhost:8080/

create relam
create clint (used to access from API as Client Id)
create role
create user and assign role it

To Create User via spring-boot Rest API
update client with bellow ROLE and assigned that role to the desired user(who's credentials has been used in 
             spring-boot application)
*************************** CLIENT ROLES ********************
manage-clients
manage-realm
manage-users

******************** USER AS ADMIN ROLE***************************
manage-users
manage-clients
manage-realm