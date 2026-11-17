# Situação-Problema 2

Validação de cadastro em Java, com uma amostra da [Reuters-21578](https://huggingface.co/datasets/ucirvine/reuters21578).

[Relatório em PDF](Situacao-Problema-2.pdf) · [Código Java](CadastroReuters.java)

Use JDK 11 ou superior. Baixe o [driver SQLite JDBC](https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.46.1.0/sqlite-jdbc-3.46.1.0.jar) e a [API SLF4J](https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar) na pasta do código.

```sh
javac -encoding UTF-8 CadastroReuters.java
java -cp '.:sqlite-jdbc-3.46.1.0.jar:slf4j-api-1.7.36.jar' CadastroReuters
```

No Windows, separe os itens do classpath com ponto e vírgula. Para executar apenas os três casos de validação sem o driver, use `java CadastroReuters --validacao`.
