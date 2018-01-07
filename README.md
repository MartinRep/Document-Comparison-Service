# Document-Comparison-Service
A JEE Application for Measuring Document Similarity.

## Overview
Java web application that enables two or more documents to be compared for similarity.



## Technologies
- [Java](https://www.java.com/) Java is a set of computer software and specifications developed by Sun Microsystems, which was later acquired by the Oracle Corporation, that provides a system for developing application software and deploying it in a cross-platform computing environment.
- [TomCat](https://tomcat.apache.org/) The Apache Tomcat® software is an open source implementation of the Java Servlet, JavaServer Pages, Java Expression Language and Java WebSocket technologies.
- [JQuery](https://jquery.com/) Query is a cross-platform JavaScript library designed to simplify the client-side scripting of HTML.
- [db4o](https://github.com/lytico/db4o) db4o was an embeddable open source object database for Java and.NET developers. It was developed, commercially licensed and supported by Actian. In October 2014, Actian declined to continue to actively pursue and promote the commercial db4o product offering for new customers.
- [db4o XTEA encryption library](https://sourceforge.net/projects/db4oxtea/) XTEA (eXtended Tiny Encryption Algorithm) support for db4o open source object database. XTEA is a block cipher that operates on a 64-bit block size with a 128-bit key. An extremely tiny but very fast encryption algorithm (with cycles less than 32 rounds).


## Architecture



## Installation

 - Clone this repository preferably with Eclipse as this is saved as Eclipse project.
 - Click on the project in Eclipse Project Explorer and press Alt+Enter for Project properties.
 - Navigate to Java Build Path and Remove broken JRE System Library.
 - Click Add Library.. and select JRE System Library and Click Finish to update project with correct path to library
 - Edit `web.xml` located in /WebContent/WEB-INF folder and change paths for logFile and dbFile.
 - Run Tomcat server (This web application was developed on Tomcat 9.0)
 - Navigate to `localhost:8080/Document-Comparison-Service/` to start using the service.
 - Please note the port as your Tomcat server can be running on different one.

## Usage

- Click on `Choose file` and select file to be compared.
- Note the text box will be populated with file name, you can adjust the title here.
- Click `Compare Document`. This button will be enabled once you select document File and Title.
- You be redirected to Pooling site, where server will be polled for comparison results on regular intervals (Refresh rate can be specified inside `web.xml`)
- After results are available Table with approximate similarity results for every document uploaded will be displayed.  
Note that initial file comparison will display empty table.

## Demo

![Demo](https://github.com/MartinRep/Document-Comparison-Service/blob/master/gitAssets/comparionDemo.gif)
   
 
  