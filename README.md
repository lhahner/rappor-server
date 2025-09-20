# Differential privacy with RAPPOR: Server Integration
This project was part of the course "Practical Course for Computer Security and privacy"
and demonstrates a prototype which enables users to send anonymized data to the server
and to decode the data.

### 1.1 How to start the server?
> **WARNING**  
> The native server will never keep your data persistence in the database.
> After shutdown all data is lost. To enable persistence check;
> *How to make Db persistent after Run-Time?*

To start the server application run the bash script resided in 
the root directory of the program:
```
./setup.sh
```

The script is interactive and will let you know if any dependencies are 
missing.

### 1.2 How to use the HTTP endpoints?
In general the HTTP points are mostly for data transfer to the database and back, for the
current version I have listed below all required ones.

| Endpoint             | Method | Parameter                               | Description                                                       |
|----------------------|--------|-----------------------------------------|-------------------------------------------------------------------|
 | `/parameters/`       | GET    | `mode`,`profile`,`name`                 | Recieve Parameters of a certain profile or mode.                  |
| `/parameters/`       | POST   | `profile`                               | Recieve Parameters of a certain profile or insert if not present. |
| `/healthdata/upload` | POST   | -                                       | Insert the given report to the database.                          |
| `/healthdata/{id}`   | GET    | -                                       | Recieve a Healthdata entity with a certain Id.                    |
| `/healthdata/decode` | GET    | `cohort`,`profile`,`maxNumberOfReports` | Run the decode pipline and recieve a probability report.          |

### 1.3 How to make the DB persistent after Run-Time?
To keep your data in the database after shut down you need to adjust the docker file.
Remove the following line from `docker-compose.yml`.

````
    # Critical: keep data in memory so every start is a fresh init
    tmpfs:
      - /var/lib/postgresql/data:rw
    # Don’t restart automatically; it's for test runs
    restart: "no"
````
and switch `restart` to "yes".