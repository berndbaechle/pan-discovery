[![Build Status][travis-image]][travis-url]



## PAN Discovery

This program is used to find occurrences of PAN (ie. Credit Card numbers) in files and databases.

It is typically used in the context of PCI-DSS assessments to identify storage of card information.

## Requirements

This application requires a Java 8+ Java Runtime Environment.

The build process will generate two different executable jar artefacts:
* pan-discovery-fs-xxx.jar
* pan-discovery-db-xxx.jar



## Usage from executable jar files

### Use to scan Filesystems

```
java -jar pan-discovery-fs-xxx.jar [--verbose] <folder> [<folder> ...]
```

Execution will create a csv file report in the execution folder like PAN_Discovery_2016-11-11_0956.csv

```
File;Matches;Content Type;Sample Match
./samples/Cards.rtf;1;application/rtf;4783853934638427
./samples/Cards.txt;1;text/plain;4783853934638427
./samples/Cards.pdf;1;application/pdf;4783853934638427
./samples/Cards.zip;1;application/zip;4783853934638427
./samples/Cards.doc;1;application/msword;4783853934638427
./samples/Cards.xls;1;application/vnd.ms-excel;4783853934638427
./samples/Cards.xlsx;1;application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;4783853934638427
./samples/Cards.ods;1;application/vnd.oasis.opendocument.spreadsheet;4783853934638427
./samples/Cards.docx;1;application/vnd.openxmlformats-officedocument.wordprocessingml.document;4783853934638427
./samples/Cards.odt;1;application/vnd.oasis.opendocument.text;4783853934638427
```



### Use to scan a relational Database

To scan a relational database, you need to provide a JDBC driver corresponding to your
database system, and credentials for a user access having at least read privileges
on the contents.

The following example will scan a PostgreSQL database running locally:

```
java \
    -Dloader.path=/home/yk/JDBC/PostgreSQL/postgresql-9.4.1212.jar
    -Dspring.datasource.url=jdbc:postgresql://localhost:5432/chess1 
    -Dspring.datasource.username=db_user 
    -Dspring.datasource.password=db_password
    -jar pan-discovery-db-xxx.jar
```

Execution will create two file reports in the execution folder:
* PAN_Discovery_<db_name>_<date>.csv
* PAN_Discovery_<db_name>_<date>.xlsx

These report files provide details about credit card information
findings in the explored schema.

[travis-image]: https://travis-ci.org/alcibiade/pan-discovery.svg?branch=master
[travis-url]: https://travis-ci.org/alcibiade/pan-discovery


## Build

### Build with Oracle Database support

To build the tool with support for Oracle Database, the **oracle** profile must be explicitly activated:

```
./mvnw -Poracle clean install
```

Note that the Oracle JDBC driver dependency will be downloaded directly from official Oracle JDBC Maven repository.
This repository requires authentication through an oracle.com account.

For Maven to use it at build-time, your credentials must be set in the **~/.m2/settings.xml** file. 
A sample of the file content is provided in [sample-settings.xml](sample-settings.xml).
You only have to update **username** and **password** to your own credentials.


## Use pre-built docker images

### Scan filesystem

The docker image is available on the main docker hub as alcibiade/pan-discovery-fs

```
yk@triton:~$ docker pull alcibiade/pan-discovery-fs
```

Here is an example with a folder to scan and a folder to get the report:

```
yk@triton:~/test$ ls -l
total 8
drwxr-xr-x 2 yk yk 4096 Jun 10 16:31 folder_to_scan
drwxr-xr-x 2 yk yk 4096 Jul 25 20:24 report
```

Now let's run the docker image in an interactive container:

```
yk@triton:~/test$ docker run -it --rm \
    -v $PWD/folder_to_scan:/scanfolder \
    -v $PWD/report:/report \
    
    alcibiade/pan-discovery-fs

    ____  ___    _   __   ____  _       Version 1.0.0-SNAPSHOT
   / __ \/   |  / | / /  / __ \(_)_____________ _   _____  _______  __
  / /_/ / /| | /  |/ /  / / / / / ___/ ___/ __ \ | / / _ \/ ___/ / / /
 / ____/ ___ |/ /|  /  / /_/ / (__  ) /__/ /_/ / |/ /  __/ /  / /_/ /
/_/   /_/  |_/_/ |_/  /_____/_/____/\___/\____/|___/\___/_/   \__, /
                                                             /____/

Starting Scanner v1.0.0-SNAPSHOT on d142ff13e13f with PID 7 (/runtime/pan-discovery-fs-1.0.0-SNAPSHOT.jar started by root in /report)
The following profiles are active: default                     
Results will be logged in PAN_Discovery_2017-07-25_1825.csv
Started Scanner in 7.056 seconds (JVM running for 8.893)
Scanning folder /scanfolder
Found 10 possible PAN occurrences in 10 files in 4 seconds
Report written to PAN_Discovery_2017-07-25_1825.csv
```

Report is available in the local reports folder:

```
yk@triton:~/test$ ls -l report/
total 4
-rw-r--r-- 1 root root 1111 Jul 25 20:25 PAN_Discovery_2017-07-25_1825.csv
yk@triton:~/test$ 
```

### Scan a database


The docker image is available on the main docker hub as alcibiade/pan-discovery-db

```
yk@triton:~$ docker pull alcibiade/pan-discovery-db
```

In this example we will scan the PostgreSQL database on host 'triton':

```
yk@triton:~/test$ docker run -it --rm --net=host \
    -e db_url=jdbc:postgresql://triton:5432/chess1 \
    -e db_user=chess \
    -e db_password=chess \
    -v $PWD/reports:/reports 
    alcibiade/pan-discovery-db

    ____  ___    _   __   ____  _       Version 1.0.0-SNAPSHOT
   / __ \/   |  / | / /  / __ \(_)_____________ _   _____  _______  __
  / /_/ / /| | /  |/ /  / / / / / ___/ ___/ __ \ | / / _ \/ ___/ / / /
 / ____/ ___ |/ /|  /  / /_/ / (__  ) /__/ /_/ / |/ /  __/ /  / /_/ /
/_/   /_/  |_/_/ |_/  /_____/_/____/\___/\____/|___/\___/_/   \__, /
                                                             /____/

Starting Scanner v1.0.0-SNAPSHOT on triton with PID 7 (/runtime/pan-discovery-db-1.0.0-SNAPSHOT.jar started by root in /report)
The following profiles are active: default
Detected database is PostgreSQL / 9.5.6
Started Scanner in 7.93 seconds (JVM running for 9.421)
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.account
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.elo_rating
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.robot_cache
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.chess_comment
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.chess_position
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.chess_move_to_position
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.chess_move
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.chess_game
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.session
[  0%|E:  0h:00min|R:N/A]           0 rows read from public.token
[100%|E:  0h:00min|R:  0h:00min]          15 rows read from public.player
Results: None
Spreadsheet report written to PAN_Discovery_DB_2017-07-25_1842.xlsx
Report written to PAN_Discovery_DB_2017-07-25_1842.csv

```

Report is available in the reports folder:

```
yk@triton:~/test$ ls -l report
total 8
-rw-r--r-- 1 root root   35 Jul 25 20:44 PAN_Discovery_DB_2017-07-25_1844.csv
-rw-r--r-- 1 root root 3947 Jul 25 20:44 PAN_Discovery_DB_2017-07-25_1844.xlsx
```
