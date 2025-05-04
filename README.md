create table in postgres

- tbl_m_role
- tbl_m_employee
- tbl_tr_user
- tbl_tr_travelexpense
- tbl_m_status_tracking
- tbl_m_map_status (for list status approved and reimburse or finance settlement)
- tbl_expense_detail (to be) to insert, update, and fetch the cost for each trip

THE ERD
![ERD-TravelExpenses](https://github.com/lukmnh/spring-customJDBC/assets/86191995/057f9408-5669-400e-8d60-043fe184fb07)

the project spring include { instal on https://start.spring.io/ }

- choose project maven
- language java
- Spring Boot version {adjust to newest version}
- packaging jar
- java version 21

this project for inserted Form TravelRequest on company using tech :
Springboot JDBC for the backend (server-side),
ReactJS (the UI) for frontend (not yet implemented).

in module included feat:
register employee,
login using spring security (not yet implemented),
form for insert travel request,
find the history travel request

# REDIS
In the future, i will use Redis caching, install Docker, set up Redis according to application.properties, and try if the logic works.
