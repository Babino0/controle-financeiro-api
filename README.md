# Controle Financeiro API

API REST para controle financeiro pessoal: contas, categorias e transações.
Projeto de estudo em Java com Spring Boot.

A versão em Java puro, com as regras de negócio, está em
[controle-financeiro-java](https://github.com/Babino0/controle-financeiro-java).

## Tecnologias

- Java 21
- Spring Boot (Spring Web)
- Spring Data JPA com Hibernate
- H2 (banco em memória)
- Maven

## Como rodar

Requisito: JDK 21.

```
.\mvnw.cmd spring-boot:run
```

No Linux e no macOS, use `./mvnw spring-boot:run`. A API sobe em `http://localhost:8080`.

## Persistência

As entidades são mapeadas com JPA. As tabelas são criadas automaticamente no H2
ao iniciar, e os dados são apagados quando a aplicação para. O PostgreSQL
será adotado nas próximas etapas.

## Endpoints: categorias

| Método | Caminho | Descrição | Resposta |
| --- | --- | --- | --- |
| POST | `/categorias` | Cria uma categoria | 201 |
| GET | `/categorias` | Lista as categorias | 200 |
| GET | `/categorias/{id}` | Busca por id | 200 ou 404 |
| DELETE | `/categorias/{id}` | Remove por id | 204 |

## Status do projeto

- [x] CRUD de Categoria
- [ ] Conta e Transação
- [ ] Regras de negócio (saldo, transferência)
- [ ] PostgreSQL
- [ ] Autenticação com JWT
- [ ] Testes automatizados
- [ ] Docker e deploy