# Akka HTTP Bookstore API

This project is an implementation of a REST API for managing a bookstore using Akka HTTP. It supports operations such as creating, retrieving, updating, and deleting books.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Built With](#built-with)


## Getting Started

These instructions will guide you through setting up and running the project on your local machine.

### Prerequisites

Make sure you have the following software installed:

- [Scala](https://www.scala-lang.org/download/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/download.html)
- [Java JDK 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/codinoverse/akka-http-highLevelApi.git

2. Navigate to the directory

   ```bash
   cd projectname
   
3. Install dependencies:
    
    ```bash
   sbt update

### Running the Application

1. To run the application, use the following sbt command
    ```bash
   sbt run
The server will start on http://localhost:8080.


### API Endpoints


**Get All Books**
* URL: /api/book
* Method: GET
* Response: List of all books.

**Get Book By ID**
* URL: /api/book/{id}
* Method: GET
* URL Parameter: id (integer)
* Response: Book details.


**Search Books In Stock**
* URL: /api/book/inventory/{instock}
* Method: GET
* URL Parameter: instock (boolean)
* Response: List of books in stock.

**Create a Book**
* URL: /api/book
* Method: POST
* Request Body: JSON representation of the book.
* Response: Confirmation message with the created book's ID


**Modify Stock**
* URL: /api/book/inventory
* Method: POST
* URL Parameters: id (integer), quantity (integer)
* Response: Updated book details.


**Delete a Book**
* URL: /api/book/{id}
* Method: DELETE
* URL Parameter: id (integer)
* Response: Confirmation message.


### Testing
1. To run the tests, use the following sbt command:
    
    ```bash
   sbt test
   


### Built With
* Akka HTTP - The HTTP library for building the API
* Spray JSON - JSON library for Scala
* Mockito - Mocking framework for unit tests


