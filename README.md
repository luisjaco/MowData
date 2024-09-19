# MowData
MowData is a backend Java program which is designed to query and update a local PostgreSQL database for a landscaping business owner. This program uses Java Database Connectivity to make maintaining a client database easy and simplifies many common PostgreSQL actions for users.

## Getting Started
> [!NOTE]
> MowData requires a PostgreSQL database to be running while in use. Ensure the database is empty before first starting.
* Once a database is established, the port number, database name, username, and password will be required.
* To run MowData, initialize a `Menu` from the `tools` package in `Main.java` and use`menu.start()`. The `.start()` method can be used either with or without parameters. If there are no parameters, the user will be prompted for the parameters each time the program runs.
* MowData will create the required tables in the database if they aren't present. It will also prompt the user if they would like sample data inserted. The schema diagram can be viewed [here](https://dbdiagram.io/d/mowdata-66d0ffc6eef7e08f0e2d21bc).

## Usage

```java
package com.github.luisjaco;

import com.github.luisjaco.tools.Menu;

public class Main {
    public static void main(String[] args) {
        //Initialize a tools.Menu.
        Menu menu = new Menu();

        //Use either of the following methods:

        //1. Start menu with parameters.
        menu.start(5432, "mowdata", "postgres", "password");

        //2. Start menu without parameters.
        //This will prompt the user for the missing parameters.
        menu.start();
    }
}
```

### License
[MIT](https://choosealicense.com/licenses/mit/)