# Situação-Problema 1 | UNICSUL

Demonstração de busca segura em Java com JDBC e SQLite, usando o registro 7001 da coleção Reuters-21578. O relatório PDF apresenta o cenário, os resultados e a adaptação para o catálogo de livros e filmes.

## Executar

Com JDK 11 ou superior, baixe `sqlite-jdbc-3.46.1.0.jar` e `slf4j-api-1.7.36.jar` do Maven Central e coloque os dois arquivos na pasta do código.

```sh
javac BuscaReuters.java
java -cp '.:sqlite-jdbc-3.46.1.0.jar:slf4j-api-1.7.36.jar' BuscaReuters
```

No Windows, separe os elementos do classpath com ponto e vírgula.

As contagens esperadas são 1 por título, 1 por fonte, 1 no controle concatenado e 0 na busca parametrizada com a entrada de teste. O controle vulnerável roda em uma base descartável em memória.

## Fonte dos dados

[Reuters-21578 — UCI](https://doi.org/10.24432/C52G6M) · [Registro de exemplo](https://huggingface.co/datasets/ucirvine/reuters21578)
