# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Server Diagram

A diagram of the Server architecture can be found here: [Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWOZVYSnfoccKQCLAwwAIIgQKAM4TMAE0HAARsAkoYMhZkwBzKBACu2GAGI0wKgE8YAJRRakEsFEFIIaYwHcAFkjAdEqUgBaAD4WakoALhgAbQAFAHkyABUAXRgAej0VKAAdNABvLMpTAFsUABoYXCl3aBlKlBLgJAQAX0wKcNgQsLZxKKhbe18oAAobOwdKGwBHPUkwAEoO1nYYHoEhUXEpKK0UMABVbNHjyk3gZYvtyQl10PUosgBRABlnuCSYIqhS1QAxKzxACy32ymGuYlu6x6nT6KCiaD0CAQK2oaw28huuxgIEGghQZzGRIuVyxUKk9zUCiiAEkAHIvKxfH5-SrVCS1KD1GCNZoIGAMpLxMG0OEY0KQnYSKJ4lAE4R6MCeU7ZUkQ8nSqmPQWM57M0W-YBlSrAJWeJIQADW6F1wpgZuVGq2FLugVhq36MEVyouaPhMNCcMi1iGUygNgk2FcKn9lED8GQ6DAUQATAAGdN5QrZNkO82Wm1odroGSaHT6QxGaC8fYwV4QOxuIxeHx+JNBIOsENxRKpDIqKQuNDZ1nGipVZScurtYPdLvor0IRtINCjBtN2bzBzLcXiKlS257A5E1XneRkl1ano6l7vT6Gv4wQEgw3+tYAHndMEPOJJ8nffdMSvI9cXxXwfRVf8hEvERXW1GldSZFlc3HU0C2tW0hRFR1PEA1Qv0CH9NVAyC-TnGBCN6dgog3VdI2jNBY38ZNKO-Oc00zUdUJNfNlULdASzQMttF0AxjB0FBbQ3fRmBbbxfEwFjO16Hs+DeZ4kmeNJ0kHCRhzyXCBLQON52opdG1k9dLKVLcFl3T1VGAuDpSiGQUAQA4UEg0YyIvZ0XOhG9EPU94tL4i1MLcF9QVw0y2KIvcEXrGywAYmN2wCZgqI4mAM3TTBS3LMSq0GGR62GGAAHFxzueS2yUjtmA9LoomiKrNJ0rRx0MjCi3ilr4SiZAHBqsoJHXYYxskOyd3wg8SJxfYwB8vyYIC7E3QeRC7w+FDijQiLjOfIFYvNeaqN-GVvXNcjHIWkCloOaaJtgzaEKeDSHwAKhO19urKC7vyuqJpouaIUninLHNoqbavSpjMtY6HWryriCgByQ2vyTHaT4co8hgIniZJ0mYFbXwiTZQmydpomFAQUArSptCabpsnMfpcdWkhorRMrYxsD0KBsE8+BwNUaaPAUpGVNy3tki6nrTD69Bs057mBoXIawPlXxptGOAJem2alnm5zNuPFbzV827-Kuj7yC+-ajV4oyor+s6nSShLiMe661uAc3JUW665QJA2wft0PHYZZCYFx-GE-HLmyjtEVMaBoiQeq8c7q6X2kqiI29ZQE3JEY5imsL7soE4rMMfHPGhJEitxKMcwPOXdwYAAKQgVdc7TowGaZxqsqpeXYkOft0kx3r+Ki7NReATuoDgCBlygSpE9nWvJ5hmAACsB7QA3+9Xcu5ns4O-cCp7reVW3fWj-3Hd2h8xzd1XotOiKs7vpbG6L8hC3xzstSOedX73y2tST694viJ09snQGPtLqh1BlA0BPsLauiiAYOQ+txyjCjutB2wUoiHFiHwYQ4VMZOy+KQoOaDgYYKHigfONBYAox1hfM+45IzIiwMpbK7Fa7100HzNuVYdCr0TPKWAwBsCi0IM4Vw0sGoiIPqjaIoVNLaQyOoLW5lkogE8ngUYDlFxORDv7WU5ioBElehteCFCfwaXCjFN8ODbEwPsQoyCzjyHbSiHozxf84o+MAXg3EDiXqWJcdeEJ7iwrPGQZnCiPCaLyLwAjKuE8eEhnyoVYSmAgA)

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared tests`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
