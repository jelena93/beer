# beer

Beer recommendation Clojure project was generated using Leiningen, Ring and Compojure library, Buddy for security, Korma for database access, Migratus for database migrations, Clojure programming language for backend, Liberator for Rest Web Services and Bootstrap framework and Javascript for frontend development. Application also has expert system, written rules for beer recommendation, which is implemented using Clara rules.

[Leiningen][1] is build automation and dependency management tool for Clojure.

[1]: https://github.com/technomancy/leiningen

[Ring][2] is a Clojure web applications library. By abstracting the details of HTTP into a simple, unified API, Ring allows web applications to be constructed of modular components that can be shared among a variety of applications, web servers, and web frameworks.

[2]: https://github.com/ring-clojure/ring

[Compojure][3] is a small routing library for Ring that allows web applications to be composed of small, independent parts.

[3]: https://github.com/weavejester/compojure

[Korma][4] is one of the most popular SQL DSL for Clojure. It uses a macro-based DSL to represent queries as if they were Clojure forms.

[4]: https://github.com/korma/Korma

[Migratus][5] is a Leiningen plugin general migration framework, with an implementation for database migrations, for the 
[Migratus][6] library. Migratus library is a general migration framework, with implementations for migrations as SQL scripts or general Clojure code.

[5]: https://github.com/yogthos/migratus-lein
[6]: https://github.com/yogthos/migratus

[Buddy-auth][7] is a module of Buddy secuirty library that provides authentication and authorization facilites for ring and ring based web applications.

[7]: https://github.com/funcool/buddy-auth

[Liberator][8] is a Clojure library for building RESTful applications.

[8]: https://github.com/clojure-liberator/liberator/

[Clara rules][9] - forward-chaining rules engine written in Clojure with Java interoperability. It aims to simplify code with a developer-centric approach to expert systems.

[9]: https://github.com/cerner/clara-rules


## Prerequisites

You will need Leiningen 1.7.0 or above and MySql installed.

## Running

These are steps for running the application:

1. Login to the MySQL server and create database beer with the following command:

	CREATE DATABASE beer DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

You can change database configuration in conf/db-config.edn: 
    
    {:db "beer" :user "admin" :password "admin"}

2. Navigate to your project directory and execute the following command in the command line for database migrations:

    lein migratus migrate

You can change configuration for database migration in conf/migratus-config.edn: 
    
    {:classname "com.mysql.jdbc.Driver" :subprotocol "mysql" :subname "//localhost/beer" :user "admin" :password "admin"}

3. To start a web server for the application, run:

	lein ring server

Finally, login as admin:

    username: admin
    password: admin

Or register your own user account.

## About the Project

The aim of this project was to create an online expert system for beer recommendation.

This project consists of the following frontend pages:

If the user is logged in either as admin or user:

1.1. Login and register page - it is a user starting point. The user has to be logged in either as admin or user to access other pages. If the user has no accout he can register a new one.

1.2. User profile - shows user information. User can change his username, name, surname, email or password. Password is hashed using [bcrypt-clj][10] 

[10]: https://github.com/zjhmale/bcrypt-clj

1.3. Logout - by logging out system is destroying user session and is redirected to login page.

1.4. Beer recommendation - User is asked a few questions and based on his answer, more questions are generated and finally system recommends beer style and all beers which meet conditions based on his answers. 

If the user is logged in as admin:

2.1. Home page - statistics [google chart tools][11] - users likes and comments per beer.

[11]: https://developers.google.com/chart/

2.2. Searh beer styles - search by style id or name (Rest Api), by clicking on one of the styles admin is redirected to the page of that style, which has more detailed information on selected style.

2.3. Style page - Displays style's name, description and all beers of that style. Admin can edit description and delete beers on this page.

2.4. Add beer - Admin can add new beer to the system, by providing new beer name, origin (domestic, imported), price (expensive, cheap), style (by choosing one of the existing beer styles), picture (either url or by uploading image), alcohol (in %), country and info. All the information is required. Validation exists in fronted - [jQuery Validation Plugin][12] and in backend - [Structural validation library][13]

[12]: https://jqueryvalidation.org/
[13]: https://github.com/funcool/struct

2.5. Searh beers - search by beer id, name, beer style, alcohol, manufacturer or country (Rest Api), by clicking on one of the beers admin is redirected to the page of that beer, which has more detailed information and where he can delete or edit selected beer.

2.6. Beer page - displays beer's data. Admin can edit all of beer's information or delete beer. Also admin can delete user comments on that beer.

If the user is logged in as user:

3.1. Home page - displays all beers in system. User can click on one of the beers to see more information on that beer.

3.2. Beer page - displays information on selected beer. User can also like the beer or dislike if he already liked it, also he can leave his comment on that beer.

[12]: https://github.com/zjhmale/bcrypt-clj

The following dependencies were added to the project:

                [org.clojure/clojure "1.8.0"]
                [compojure "1.5.2"]
                [selmer "1.10.7"]
                [ring-server "0.4.0"]
                [org.clojure/java.jdbc "0.6.1"]
                [com.cerner/clara-rules "0.14.0"]
                [buddy/buddy-auth "1.4.1"]
                [bcrypt-clj "0.3.3"]
                [mysql/mysql-connector-java "5.1.6"]
                [liberator "0.10.0"]
                [ring/ring-json "0.4.0"]
                [migratus "0.8.28"]
                [korma/korma "0.4.3"]
                [funcool/struct "1.0.0"]

The project was developed as part of the assignment for the course Software Engineering Tools and Methodology on Master's studies - Software Engineering and Computer Sciences at the Faculty of Organization Sciences, University of Belgrade, Serbia. 
