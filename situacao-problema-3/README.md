# Situação-Problema 3

Refatoração do catálogo em modelo, serviço e DAO. A demonstração em terminal usa o registro 7001 da [Reuters-21578](https://huggingface.co/datasets/ucirvine/reuters21578).

[Relatório em PDF](Situacao-Problema-3.pdf) · [Código Java](CatalogoReuters.java)

Com JDK 17 ou superior, execute a verificação do serviço sem banco.

```sh
javac -encoding UTF-8 CatalogoReuters.java
java CatalogoReuters --validacao
```

Para rodar o CRUD e a busca, baixe o [driver SQLite JDBC](https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.46.1.0/sqlite-jdbc-3.46.1.0.jar) e a [API SLF4J](https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar) para a pasta do código.

```sh
java -cp '.:sqlite-jdbc-3.46.1.0.jar:slf4j-api-1.7.36.jar' CatalogoReuters
```

No Windows, use ponto e vírgula entre os itens do classpath. A base SQLite em memória é recriada a cada execução.
